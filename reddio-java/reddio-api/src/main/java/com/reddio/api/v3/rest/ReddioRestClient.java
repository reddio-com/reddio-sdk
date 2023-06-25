package com.reddio.api.v3.rest;

import com.reddio.api.v1.rest.ResponseWrapper;

import java.util.concurrent.CompletableFuture;

public interface ReddioRestClient {

    CompletableFuture<ResponseWrapper<GetBalancesResponse>> getBalances(GetBalancesMessage getBalancesMessage);
}
