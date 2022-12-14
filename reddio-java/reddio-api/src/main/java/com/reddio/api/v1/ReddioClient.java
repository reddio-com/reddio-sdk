package com.reddio.api.v1;

import com.reddio.api.v1.rest.*;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public interface ReddioClient {

    String TOKEN_TYPE_ETH = "ETH";
    String TOKEN_TYPE_ERC20 = "ERC20";
    String TOKEN_TYPE_ERC721 = "ERC721";

    String RUSD_TESTNET_CONTRACT_ADDRESS = "0x241f280f13Ff42bbd884d039804c1E5648392A4B";


    interface WithStarkExSigner {

        CompletableFuture<ResponseWrapper<TransferResponse>> transfer(String starkKey, String amount, String contractAddress, String tokenId, String type, String receiver, long expirationTimeStamp);

        CompletableFuture<ResponseWrapper<WithdrawalToResponse>> withdrawal(String starkKey, String amount, String contractAddress, String tokenId, String type, String receiver, long expirationTimeStamp);

        @Deprecated
        CompletableFuture<ResponseWrapper<OrderResponse>> order(String starkKey, String price, String amount, String tokenAddress, String tokenId, String marketplaceUuid, String tokenType, OrderType orderType);

        CompletableFuture<ResponseWrapper<OrderResponse>> order(String starkKey, String contractType, String contractAddress, String tokenId, String price, String amount, OrderType orderType, String baseTokenType, String baseTokenContract, String marketplaceUuid);

        CompletableFuture<ResponseWrapper<CancelOrderResponse>> cancelOrder(String starkKey, long orderId);

        CompletableFuture<ResponseWrapper<OrderResponse>> orderWithPayInfo(
                String starkKey,
                String contractType,
                String contractAddress,
                String tokenId,
                String price,
                String amount,
                OrderType orderType,
                String marketplaceUuid,
                Payment.PayInfo payInfo,
                String signPayInfoPrivateKey
        );

        CompletableFuture<ResponseWrapper<OrderResponse>> orderWithEth(String starkKey, String contractType, String contractAddress, String tokenId, String price, String amount, OrderType orderType);

        CompletableFuture<ResponseWrapper<OrderResponse>> buyNFTWithPayInfo(
                String starkKey,
                String contractType,
                String contractAddress,
                String tokenId,
                String price,
                String amount,
                String marketplaceUuid,
                Payment.PayInfo payInfo,
                String signPayInfoPrivateKey
        );

        CompletableFuture<ResponseWrapper<OrderResponse>> sellNFTWithRUSD(
                String starkKey,
                String contractType,
                String contractAddress,
                String tokenId,
                String price,
                String amount,
                String marketplaceUuid
        );
    }


    CompletableFuture<ResponseWrapper<GetRecordResponse>> getRecord(String starkKey, long sequenceId);

    CompletableFuture<ResponseWrapper<GetRecordResponse>> waitingTransferGetApproved(String starkKey, long sequenceId);

    CompletableFuture<ResponseWrapper<GetRecordResponse>> waitingTransferGetApproved(String starkKey, long sequenceId, Duration interval, Duration deadline, AtomicBoolean shouldStop);

    WithStarkExSigner withStarkExSigner(StarExSigner starkExSigner);

    WithStarkExSigner withStarkExSigner(String starkPrivateKey);

}
