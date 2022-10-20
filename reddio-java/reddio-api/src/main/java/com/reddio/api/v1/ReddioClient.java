package com.reddio.api.v1;

import com.reddio.api.v1.rest.GetRecordResponse;
import com.reddio.api.v1.rest.ResponseWrapper;
import com.reddio.api.v1.rest.TransferResponse;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public interface ReddioClient {
    CompletableFuture<ResponseWrapper<TransferResponse>> transfer(
            String starkKey,
            String privateKey,
            String amount,
            String contractAddress,
            String tokenId,
            String type,
            String receiver,
            long expirationTimeStamp
    );

    CompletableFuture<ResponseWrapper<GetRecordResponse>> getRecord(String starkKey, long sequenceId);

    CompletableFuture<ResponseWrapper<GetRecordResponse>> waitingTransferGetApproved(String starkKey, long sequenceId);

    CompletableFuture<ResponseWrapper<GetRecordResponse>> waitingTransferGetApproved(String starkKey, long sequenceId, Duration interval, Duration deadline, AtomicBoolean shouldStop);
}
