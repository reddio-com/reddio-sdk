package com.reddio.api.v1.requests.polling;

import com.reddio.IntegrationTest;
import com.reddio.api.v1.rest.DefaultReddioRestClient;
import com.reddio.api.v1.rest.Order;
import com.reddio.api.v1.rest.OrderState;
import com.reddio.api.v1.rest.ReddioRestClient;
import com.reddio.exception.ReddioException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.concurrent.CompletionException;

import static org.junit.Assert.*;

public class OrderPollerTest {

    @Test
    @Category(IntegrationTest.class)
    public void testPollOrder_AlreadyInDesiredState() {
        final ReddioRestClient restClient = DefaultReddioRestClient.testnet();
        final OrderPoller poller = new OrderPoller(restClient, 304282L);
        final Order order = poller.poll(OrderState.Filled);
        assertNotNull(order);
        assertEquals(OrderState.Filled, order.getOrderState());
    }

    @Test(expected = ReddioException.class)
    @Category(IntegrationTest.class)
    public void testPollOrder_NeverGetDesiredState() {
        final ReddioRestClient restClient = DefaultReddioRestClient.testnet();
        final OrderPoller poller = new OrderPoller(restClient, 304282L);
        poller.poll(OrderState.ConditionallyCanceled);
        fail();
    }
}