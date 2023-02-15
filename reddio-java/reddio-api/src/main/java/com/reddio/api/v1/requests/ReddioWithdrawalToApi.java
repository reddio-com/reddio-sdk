package com.reddio.api.v1.requests;

import com.reddio.api.v1.rest.*;

import java.util.concurrent.CompletableFuture;

public class ReddioWithdrawalToApi implements SignedReddioApiRequest<WithdrawalToMessage, ResponseWrapper<WithdrawalToResponse>> {

    private ReddioRestClient localRestClient;
    private WithdrawalToMessage request;

    @Override
    public ResponseWrapper<WithdrawalToResponse> send() {
        return null;
    }

    @Override
    public CompletableFuture<ResponseWrapper<WithdrawalToResponse>> sendAsync() {
        return null;
    }

    @Override
    public WithdrawalToMessage getRequest() {
        return this.request;
    }

    @Override
    public Signature getSignature() {
        return this.request.getSignature();
    }
}
