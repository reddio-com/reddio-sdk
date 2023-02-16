package com.reddio.api.v1.requests.polling;

import com.reddio.ReddioException;
import com.reddio.api.v1.rest.*;
import lombok.SneakyThrows;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * RecordPoller is a helper class to keep polling the record, until reach the desired {@link RecordStatus}, or throw an
 * exception when exceed the max attempts.
 * <p>
 *
 * @author strrl
 */
public class RecordPoller {

    private static final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
    public final static Duration DEFAULT_WAIT = Duration.ofMillis(100);
    public final static int DEFAULT_MAX_ATTEMPTS = 3;

    private final ReddioRestClient restClient;
    private final String starkKey;
    private final Long sequenceId;

    public RecordPoller(ReddioRestClient restClient, String starkKey, Long sequenceId) {
        this.restClient = restClient;
        this.starkKey = starkKey;
        this.sequenceId = sequenceId;
    }

    /**
     * Get the stark key of the record.
     *
     * @return the stark key of the record.
     */
    public String getStarkKey() {
        return starkKey;
    }

    /**
     * Get the sequence id of the record.
     *
     * @return the sequence id of the record.
     */
    public Long getSequenceId() {
        return sequenceId;
    }

    /**
     * Poll the record until it reach the desired {@link RecordStatus}, with default wait time and max attempts.
     *
     * @param targetRecordStatus An array of desired {@link RecordStatus}.
     * @return The record with desired {@link RecordStatus}.
     * @throws CompletionException the polling exceed the max attempts.
     */
    public SequenceRecord poll(RecordStatus... targetRecordStatus) {
        return this.pollAsync(targetRecordStatus).join();
    }

    /**
     * Poll the record until it reach the desired {@link RecordStatus}.
     *
     * @param wait               The wait time between each polling.
     * @param maxAttempts        The max attempts to poll.
     * @param targetRecordStatus An array of desired {@link RecordStatus}.
     * @return The record with desired {@link RecordStatus}.
     * @throws CompletionException the polling exceed the max attempts.
     */
    public SequenceRecord poll(Duration wait, Integer maxAttempts, RecordStatus... targetRecordStatus) {
        return this.pollAsync(wait, maxAttempts, targetRecordStatus).join();
    }

    /**
     * Poll the record with custom stop condition.
     *
     * @param wait          The wait time between each polling.
     * @param maxAttempts   The max attempts to poll.
     * @param stopCondition The stop condition function, if the condition function returns true, the polling will stop.
     * @return The record with desired {@link RecordStatus}.
     * @throws CompletionException the polling exceed the max attempts.
     */
    public SequenceRecord poll(Duration wait, Integer maxAttempts, Function<SequenceRecord, Boolean> stopCondition) {
        return this.pollAsync(wait, maxAttempts, stopCondition).join();
    }

    /**
     * Poll the record asynchronously, until it reach the desired {@link RecordStatus}, with default wait time and max attempts.
     *
     * @param desiredRecordStatus An array of desired {@link RecordStatus}.
     * @return A {@link CompletableFuture} of the record with desired {@link RecordStatus}.
     */
    public CompletableFuture<SequenceRecord> pollAsync(RecordStatus... desiredRecordStatus) {
        return this.pollAsync(DEFAULT_WAIT, DEFAULT_MAX_ATTEMPTS, desiredRecordStatus);
    }

    /**
     * Poll the record asynchronously, until it reach the desired {@link RecordStatus}.
     *
     * @param wait                The wait time between each polling.
     * @param maxAttempts         The max attempts to poll.
     * @param desiredRecordStatus An array of desired {@link RecordStatus}.
     * @return A {@link CompletableFuture} of the record with desired {@link RecordStatus}.
     */

    public CompletableFuture<SequenceRecord> pollAsync(Duration wait, Integer maxAttempts, RecordStatus... desiredRecordStatus) {
        if (desiredRecordStatus.length == 0) {
            throw new ReddioException("Desired record status is required when polling the record.");
        }

        Set<RecordStatus> set = new HashSet<>(Arrays.asList(desiredRecordStatus));
        return this.pollAsync(wait, maxAttempts, record -> record != null && record.getStatus() != null && set.contains(record.getStatus()));
    }

    /**
     * Poll the record asynchronously with custom stop condition.
     *
     * @param wait          The wait time between each polling.
     * @param maxAttempts   The max attempts to poll.
     * @param stopCondition The stop condition function, if the condition function returns true, the polling will stop.
     * @return A {@link CompletableFuture} of the record with desired {@link RecordStatus}.
     */
    public CompletableFuture<SequenceRecord> pollAsync(Duration wait, Integer maxAttempts, Function<SequenceRecord, Boolean> stopCondition) {
        CompletableFuture<SequenceRecord> result = new CompletableFuture<>();
        final PollingTask task = new PollingTask(this.restClient, this.starkKey, this.sequenceId, wait, maxAttempts, stopCondition, 0, result);
        // TODO: make the task cancellable
        executorService.schedule(task, wait.toMillis(), TimeUnit.MILLISECONDS);
        return result;
    }

    private static final class PollingTask implements Runnable {

        private final ReddioRestClient restClient;
        private final String starkKey;
        private final Long sequenceId;
        private final Duration wait;
        private final Integer maxAttempts;
        private final Function<SequenceRecord, Boolean> stopCondition;
        private final Integer currentAttempt;
        private final CompletableFuture<SequenceRecord> resultHolder;

        public PollingTask(ReddioRestClient restClient, String starkKey, Long sequenceId, Duration wait, Integer maxAttempts, Function<SequenceRecord, Boolean> stopCondition, Integer currentAttempt, CompletableFuture<SequenceRecord> resultHolder) {
            this.restClient = restClient;
            this.starkKey = starkKey;
            this.sequenceId = sequenceId;
            this.wait = wait;
            this.maxAttempts = maxAttempts;
            this.stopCondition = stopCondition;
            this.currentAttempt = currentAttempt;
            this.resultHolder = resultHolder;
        }

        @Override
        @SneakyThrows
        public void run() {
            SequenceRecord record = null;
            try {
                final ResponseWrapper<GetRecordResponse> response = this.restClient.getRecord(GetRecordMessage.of(this.starkKey, this.sequenceId)).get();
                // We suppose that there would only return 1 record with specified starkKey and sequenceId.
                if (response.getData() == null || response.getData().isEmpty()) {
                    // did not get any data, schedule next poll
                    scheduleNextPoll();
                    return;
                }
                record = response.getData().get(0);
            } catch (Throwable ignored) {
                // omit any exception, schedule next poll
                scheduleNextPoll();
                return;
            }

            if (this.stopCondition.apply(record)) {
                this.resultHolder.complete(record);
                return;
            }

            scheduleNextPoll();
        }

        private void scheduleNextPoll() {
            if (this.currentAttempt >= this.maxAttempts) {
                this.resultHolder.completeExceptionally(new ReddioException("Polling records exceeds max attempts"));
                return;
            }
            final PollingTask nextTask = new PollingTask(this.restClient, this.starkKey, this.sequenceId, this.wait, this.maxAttempts, this.stopCondition, this.currentAttempt + 1, this.resultHolder);
            executorService.schedule(nextTask, this.wait.toMillis(), TimeUnit.MILLISECONDS);
        }
    }

}
