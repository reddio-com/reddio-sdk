package com.reddio.api.v1.rest;

import junit.framework.TestCase;
import org.junit.Assert;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class DefaultReddioRestClientTest extends TestCase {
    public void testGetRecord() throws ExecutionException, InterruptedException {
        DefaultReddioRestClient client = DefaultReddioRestClient.testnet();
        ResponseWrapper<GetRecordResponse> response = client.getRecord(
                GetRecordMessage.of("0x6736f7449da3bf44bf0f7bdd6463818e1ef272641d43021e8bca17b32ec2df0", 300523)).get();
        Assert.assertEquals(GetRecordResponse.SequenceRecord.SEQUENCE_STATUS_ACCEPTED, response.data.get(0).status);
    }

    public void testOrderList() throws ExecutionException, InterruptedException {
        DefaultReddioRestClient client = DefaultReddioRestClient.testnet();
        CompletableFuture<ResponseWrapper<OrderListResponse>> future = client.orderList(OrderListMessage.of("0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
                "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5"));
        ResponseWrapper<OrderListResponse> result = future.get();
        Assert.assertEquals("OK", result.status);
    }
}