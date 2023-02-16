package com.reddio.api.v1.requests.polling;

import com.reddio.ReddioException;
import com.reddio.api.v1.rest.*;
import lombok.SneakyThrows;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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

    public String getStarkKey() {
        return starkKey;
    }

    public Long getSequenceId() {
        return sequenceId;
    }

    public SequenceRecord poll(RecordStatus... targetRecordStatus){
        return this.pollAsync(targetRecordStatus).join();
    }

    public SequenceRecord poll(Duration wait, Integer maxAttempts, RecordStatus... targetRecordStatus) {
        return this.pollAsync(wait, maxAttempts, targetRecordStatus).join();
    }

    public SequenceRecord poll(Duration wait, Integer maxAttempts, Function<SequenceRecord, Boolean> stopCondition) {
        return this.pollAsync(wait, maxAttempts, stopCondition).join();
    }
    public CompletableFuture<SequenceRecord> pollAsync(RecordStatus... desiredRecordStatus){
        return this.pollAsync(DEFAULT_WAIT, DEFAULT_MAX_ATTEMPTS, desiredRecordStatus);
    }

    public CompletableFuture<SequenceRecord> pollAsync(Duration wait, Integer maxAttempts, RecordStatus... desiredRecordStatus) {
        Set<RecordStatus> set = new HashSet<>(Arrays.asList(desiredRecordStatus));
        return this.pollAsync(wait, maxAttempts, record -> record != null && record.getStatus() != null && set.contains(null));
    }

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
