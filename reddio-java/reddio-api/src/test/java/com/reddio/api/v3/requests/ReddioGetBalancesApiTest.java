package com.reddio.api.v3.requests;

import com.reddio.api.compose.MultiVersionRestClient;
import com.reddio.api.v1.rest.ResponseWrapper;
import com.reddio.api.v3.rest.GetBalancesResponse;
import org.junit.Test;

public class ReddioGetBalancesApiTest {

    @Test
    public void testGetBalances() {
        ResponseWrapper<GetBalancesResponse> result = ReddioGetBalancesApi.getaBalance(MultiVersionRestClient.v3().testnet(), "0x59f20cc0304b4d7f772363839191ad64c49e9f69de60cf23aa5f56e7005c945").call();
        System.out.println(result);
    }
}
