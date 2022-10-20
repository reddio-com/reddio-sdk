package com.reddio.api.v1.rest;

import junit.framework.TestCase;
import org.junit.Assert;

import java.util.concurrent.ExecutionException;

public class DefaultReddioRestClientTest extends TestCase {


    public void testGetRecord() throws ExecutionException, InterruptedException {
        DefaultReddioRestClient client = DefaultReddioRestClient.testnet();
        ResponseWrapper<GetRecordResponse> response = client.getRecord(
                GetRecordMessage.of("0x6736f7449da3bf44bf0f7bdd6463818e1ef272641d43021e8bca17b32ec2df0", 300523)).get();
        Assert.assertEquals(GetRecordResponse.SequenceRecord.SEQUENCE_STATUS_ACCEPTED, response.data.get(0).status);
    }
}