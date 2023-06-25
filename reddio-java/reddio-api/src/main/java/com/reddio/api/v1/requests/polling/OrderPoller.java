package com.reddio.api.v1.requests.polling;

import com.reddio.api.misc.UnwrapCompletionExceptionKt;
import com.reddio.api.v1.rest.Order;
import com.reddio.api.v1.rest.OrderState;
import com.reddio.api.v1.rest.ReddioRestClient;
import com.reddio.api.v1.rest.ResponseWrapper;
import com.reddio.exception.ReddioException;
import lombok.SneakyThrows;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * OrderPoller is a helper class to keep polling the order, until reach the desired
 * {@link com.reddio.api.v1.rest.OrderState}, or throw an exception when exceed the max attempts.
 * <p>
 *
 * @author strrl
 */
public class OrderPoller {

    public final static Duration DEFAULT_WAIT = Duration.ofMillis(100);
    public final static int DEFAULT_MAX_ATTEMPTS = 3;
    private static final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
    private final ReddioRestClient restClient;
    private final Long sequenceId;

    public OrderPoller(ReddioRestClient restClient, Long sequenceId) {
        this.restClient = restClient;
        this.sequenceId = sequenceId;
    }

    /**
     * Get the sequence id of the order.
     *
     * @return the sequence id of the order.
     */
    public Long getSequenceId() {
        return this.sequenceId;
    }

    /**
     * Poll the order until it reach the desired {@link com.reddio.api.v1.rest.OrderState}, with default wait time and max attempts.
     *
     * @param desiredOrderState An array of desired {@link com.reddio.api.v1.rest.OrderState}.
     * @return The order with desired {@link com.reddio.api.v1.rest.OrderState}.
     * @throws CompletionException the polling exceed the max attempts.
     */
    public Order poll(OrderState... desiredOrderState) {
        return UnwrapCompletionExceptionKt.unwrapCompletionException(() -> this.pollAsync(desiredOrderState).join());
    }

    /**
     * Poll the order until it reach the desired {@link com.reddio.api.v1.rest.OrderState}.
     *
     * @param wait              the wait time between each poll.
     * @param maxAttempts       the max attempts to poll.
     * @param desiredOrderState An array of desired {@link com.reddio.api.v1.rest.OrderState}.
     * @return The order with desired {@link com.reddio.api.v1.rest.OrderState}.
     * @throws CompletionException the polling exceed the max attempts.
     */
    public Order poll(Duration wait, Integer maxAttempts, OrderState... desiredOrderState) {
        return UnwrapCompletionExceptionKt.unwrapCompletionException(() -> this.pollAsync(wait, maxAttempts, desiredOrderState).join());
    }

    /**
     * Poll the order with custom stop condition.
     *
     * @param wait          the wait time between each poll.
     * @param maxAttempts   the max attempts to poll.
     * @param stopCondition the stop condition.
     * @return The order with desired {@link com.reddio.api.v1.rest.OrderState}.
     * @throws CompletionException the polling exceed the max attempts.
     */
    public Order poll(Duration wait, Integer maxAttempts, Function<Order, Boolean> stopCondition) {
        return UnwrapCompletionExceptionKt.unwrapCompletionException(() -> this.pollAsync(wait, maxAttempts, stopCondition).join());
    }

    /**
     * Poll the order asynchronously, until it reach the desired {@link com.reddio.api.v1.rest.OrderState}, with default wait time and max attempts.
     *
     * @param desiredOrderState An array of desired {@link com.reddio.api.v1.rest.OrderState}.
     * @return A {@link CompletableFuture} of the order with desired {@link com.reddio.api.v1.rest.OrderState}.
     */
    public CompletableFuture<Order> pollAsync(OrderState... desiredOrderState) {
        return this.pollAsync(DEFAULT_WAIT, DEFAULT_MAX_ATTEMPTS, desiredOrderState);
    }

    /**
     * Poll the order asynchronously, until it reach the desired {@link com.reddio.api.v1.rest.OrderState}.
     *
     * @param wait              the wait time between each poll.
     * @param maxAttempts       the max attempts to poll.
     * @param desiredOrderState An array of desired {@link com.reddio.api.v1.rest.OrderState}.
     * @return A {@link CompletableFuture} of the order with desired {@link com.reddio.api.v1.rest.OrderState}.
     */
    public CompletableFuture<Order> pollAsync(Duration wait, Integer maxAttempts, OrderState... desiredOrderState) {
        if (desiredOrderState.length == 0) {
            throw new ReddioException("Desired order state is required when polling the order.");
        }
        Set<OrderState> set = new HashSet<>(Arrays.asList(desiredOrderState));
        return this.pollAsync(wait, maxAttempts, order -> order != null && order.getOrderState() != null && set.contains(order.getOrderState()));
    }

    /**
     * Poll the order asynchronously with custom stop condition.
     *
     * @param wait          the wait time between each poll.
     * @param maxAttempts   the max attempts to poll.
     * @param stopCondition the stop condition.
     * @return A {@link CompletableFuture} of the order with desired {@link com.reddio.api.v1.rest.OrderState}.
     * @throws CompletionException the polling exceed the max attempts.
     */
    public CompletableFuture<Order> pollAsync(Duration wait, Integer maxAttempts, Function<Order, Boolean> stopCondition) {
        CompletableFuture<Order> result = new CompletableFuture<>();
        final PollingTask task = new PollingTask(this.restClient, this.sequenceId, wait, maxAttempts, stopCondition, 0, result);
        // TODO: make the task cancellable
        executorService.schedule(task, wait.toMillis(), TimeUnit.MILLISECONDS);
        return result;
    }


    private static final class PollingTask implements Runnable {
        private final ReddioRestClient restClient;
        private final Long sequenceId;
        private final Duration wait;
        private final Integer maxAttempts;
        private final Function<Order, Boolean> stopCondition;
        private final Integer currentAttempt;
        private final CompletableFuture<Order> resultHolder;

        public PollingTask(ReddioRestClient restClient, Long sequenceId, Duration wait, Integer maxAttempts, Function<Order, Boolean> stopCondition, Integer currentAttempt, CompletableFuture<Order> resultHolder) {
            this.restClient = restClient;
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
            Order order = null;
            try {
                final ResponseWrapper<Order> response = this.restClient.getOrder(this.sequenceId).get();
                if (response.getData() == null) {
                    // did not get any data, schedule next poll
                    scheduleNextPoll();
                    return;
                }
                order = response.getData();
            } catch (Throwable ignored) {
                // omit any exception, schedule next poll
                scheduleNextPoll();
                return;
            }

            if (this.stopCondition.apply(order)) {
                this.resultHolder.complete(order);
                return;
            }

            scheduleNextPoll();
        }

        private void scheduleNextPoll() {
            if (this.currentAttempt >= this.maxAttempts) {
                this.resultHolder.completeExceptionally(new ReddioException("Polling order exceeds max attempts, max attempts: " + this.maxAttempts + ", sequence id: " + this.sequenceId));
                return;
            }
            final PollingTask nextTask = new PollingTask(this.restClient, this.sequenceId, this.wait, this.maxAttempts, this.stopCondition, this.currentAttempt + 1, this.resultHolder);
            executorService.schedule(nextTask, this.wait.toMillis(), TimeUnit.MILLISECONDS);
        }
    }
}
