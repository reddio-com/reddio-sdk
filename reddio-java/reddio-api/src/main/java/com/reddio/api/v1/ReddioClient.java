package com.reddio.api.v1;

import com.reddio.api.v1.rest.ResponseWrapper;
import com.reddio.api.v1.rest.TransferResponse;

import java.util.concurrent.CompletableFuture;

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
}
