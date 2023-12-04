package com.reddio.api.v1.requests.polling;

import com.reddio.IntegrationTest;
import com.reddio.api.v1.rest.DefaultReddioRestClient;
import com.reddio.api.v1.rest.RecordStatus;
import com.reddio.api.v1.rest.ReddioRestClient;
import com.reddio.api.v1.rest.SequenceRecord;
import com.reddio.exception.ReddioException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

@Ignore
public class RecordPollerTest {

    @Test
    @Category(IntegrationTest.class)
    public void testPollRecord_AlreadyInDesiredStatus() {
        final ReddioRestClient restClient = DefaultReddioRestClient.testnet();
        final RecordPoller poller = new RecordPoller(restClient, "0x1ccc27877014bc1a81919fc855ebbd1b874603283c9ea93397d970b0704e581", 304282L);
        final SequenceRecord record = poller.poll(RecordStatus.AcceptedByReddio);
        assertNotNull(record);
        assertEquals(RecordStatus.AcceptedByReddio, record.getStatus());
    }

    @Test(expected = ReddioException.class)
    @Category(IntegrationTest.class)
    public void testPollRecord_NeverGetDesiredStatus() {
        final ReddioRestClient restClient = DefaultReddioRestClient.testnet();
        final RecordPoller poller = new RecordPoller(restClient, "0x1ccc27877014bc1a81919fc855ebbd1b874603283c9ea93397d970b0704e581", 304282L);
        poller.poll(RecordStatus.FailedOnReddio);
        fail();
    }
}