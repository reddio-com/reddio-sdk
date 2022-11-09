package com.reddio.api.v1;

import com.reddio.api.v1.rest.*;

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

    CompletableFuture<ResponseWrapper<WithdrawalToResponse>> withdrawal(
            String starkKey,
            String privateKey,
            String amount,
            String contractAddress,
            String tokenId,
            String type,
            String receiver,
            long expirationTimeStamp
    );

    CompletableFuture<ResponseWrapper<OrderResponse>> order(
            String privateKey,
            String starkKey,
            String price,
            String amount,
            String tokenAddress,
            String tokenId,
            String marketplaceUuid,
            String tokenType,
            OrderType orderType
    );
}
