package com.reddio.api.v1.requests.polling;

import com.reddio.api.v1.rest.DefaultReddioRestClient;
import com.reddio.api.v1.rest.RecordStatus;
import com.reddio.api.v1.rest.ReddioRestClient;
import com.reddio.api.v1.rest.SequenceRecord;
import org.junit.Test;

import java.util.concurrent.CompletionException;

import static org.junit.Assert.*;

public class RecordPollerTest {

    @Test
    public void testPollRecord_AlreadyInDesiredStatus() {
        final ReddioRestClient restClient = DefaultReddioRestClient.testnet();
        final RecordPoller poller = new RecordPoller(restClient, "0x1ccc27877014bc1a81919fc855ebbd1b874603283c9ea93397d970b0704e581", 304282L);
        final SequenceRecord record = poller.poll(RecordStatus.AcceptedByReddio);
        assertNotNull(record);
        assertEquals(RecordStatus.AcceptedByReddio, record.getStatus());
    }

    @Test(expected = CompletionException.class)
    public void testPollRecord_NeverGetDesiredStatus() {
        final ReddioRestClient restClient = DefaultReddioRestClient.testnet();
        final RecordPoller poller = new RecordPoller(restClient, "0x1ccc27877014bc1a81919fc855ebbd1b874603283c9ea93397d970b0704e581", 304282L);
        poller.poll(RecordStatus.FailedOnReddio);
        fail();
    }
}