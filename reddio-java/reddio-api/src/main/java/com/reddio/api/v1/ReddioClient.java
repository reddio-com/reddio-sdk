package com.reddio.api.v1;

import com.reddio.api.v1.rest.*;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public interface ReddioClient extends AutoCloseable {

    String TOKEN_TYPE_ETH = "ETH";
    String TOKEN_TYPE_ERC20 = "ERC20";
    String TOKEN_TYPE_ERC721 = "ERC721";
    String TOKEN_TYPE_ERC721M = "ERC721M";

    String RUSD_TESTNET_CONTRACT_ADDRESS = "0x241f280f13Ff42bbd884d039804c1E5648392A4B";


    interface WithStarkExSigner extends AutoCloseable {

        CompletableFuture<ResponseWrapper<TransferResponse>> transfer(String starkKey, String amount, String contractAddress, String tokenId, String tokenType, String receiver, long expirationTimeStamp);

        CompletableFuture<ResponseWrapper<TransferResponse>> transferETH(String amount, String receiver, long expirationTimeStamp);

        CompletableFuture<ResponseWrapper<TransferResponse>> transferERC20(String amount, String contractAddress, String receiver, long expirationTimeStamp);

        CompletableFuture<ResponseWrapper<TransferResponse>> transferERC721(String contractAddress, String tokenId, String receiver, long expirationTimeStamp);

        CompletableFuture<ResponseWrapper<TransferResponse>> transferERC721M(String contractAddress, String tokenId, String receiver, long expirationTimeStamp);


        WithdrawalToMessage withdrawalMessage(String amount, String contractAddress, String tokenId, String type, String receiver, long expirationTimeStamp);

        WithdrawalToMessage withdrawalETHMessage(String amount, String receiver, long expirationTimeStamp);

        WithdrawalToMessage withdrawalERC20Message(String amount, String contractAddress, String receiver, long expirationTimeStamp);

        WithdrawalToMessage withdrawalERC721Message(String contractAddress, String tokenId, String receiver, long expirationTimeStamp);

        CompletableFuture<ResponseWrapper<WithdrawalToResponse>> withdrawal(WithdrawalToMessage withdrawalToMessage);

        /**
         * @param starkKey
         * @param amount
         * @param contractAddress
         * @param tokenId
         * @param type
         * @param receiver
         * @param expirationTimeStamp
         * @return
         * @deprecated Use {@link #withdrawal(String, String, String, String, String, long)} instead.
         */
        @Deprecated
        CompletableFuture<ResponseWrapper<WithdrawalToResponse>> withdrawal(String starkKey, String amount, String contractAddress, String tokenId, String type, String receiver, long expirationTimeStamp);

        CompletableFuture<ResponseWrapper<WithdrawalToResponse>> withdrawal(String amount, String contractAddress, String tokenId, String type, String receiver, long expirationTimeStamp);

        CompletableFuture<ResponseWrapper<WithdrawalToResponse>> withdrawalETH(String amount, String receiver, long expirationTimeStamp);

        CompletableFuture<ResponseWrapper<WithdrawalToResponse>> withdrawalERC20(String amount, String contractAddress, String receiver, long expirationTimeStamp);

        CompletableFuture<ResponseWrapper<WithdrawalToResponse>> withdrawalERC721(String contractAddress, String tokenId, String receiver, long expirationTimeStamp);

        CompletableFuture<ResponseWrapper<WithdrawalToResponse>> withdrawalERC721M(String contractAddress, String tokenId, String receiver, long expirationTimeStamp);

        @Deprecated
        CompletableFuture<ResponseWrapper<OrderResponse>> order(String starkKey, String price, String amount, String tokenAddress, String tokenId, String marketplaceUuid, String tokenType, OrderBehavior orderType);

        CompletableFuture<ResponseWrapper<OrderResponse>> order(String starkKey, String contractType, String contractAddress, String tokenId, String price, String amount, OrderBehavior orderType, String baseTokenType, String baseTokenContract, String marketplaceUuid);

        CompletableFuture<ResponseWrapper<CancelOrderResponse>> cancelOrder(String starkKey, long orderId);

        CompletableFuture<ResponseWrapper<OrderResponse>> orderWithPayInfo(String starkKey, String contractType, String contractAddress, String tokenId, String price, String amount, OrderBehavior orderType, String marketplaceUuid, Payment.PayInfo payInfo, String signPayInfoPrivateKey, String baseTokenType, String baseTokenAddress);

        CompletableFuture<ResponseWrapper<OrderResponse>> orderWithEth(String starkKey, String contractType, String contractAddress, String tokenId, String price, String amount, OrderBehavior orderType);

        CompletableFuture<ResponseWrapper<OrderResponse>> buyNFTWithPayInfoBaseTokenRUSD(String starkKey, String contractType, String contractAddress, String tokenId, String price, String amount, String marketplaceUuid, Payment.PayInfo payInfo, String signPayInfoPrivateKey);

        CompletableFuture<ResponseWrapper<OrderResponse>> buyNFTWithETHOrderTypeIOC(String starkKey, String contractType, String contractAddress, String tokenId, String price, String amount, String marketplaceUuid);

        CompletableFuture<ResponseWrapper<OrderResponse>> sellNFTWithRUSD(String starkKey, String contractType, String contractAddress, String tokenId, String price, String amount, String marketplaceUuid);
    }


    CompletableFuture<ResponseWrapper<Order>> getOrder(long orderId);

    CompletableFuture<ResponseWrapper<GetRecordResponse>> getRecord(String starkKey, long sequenceId);

    CompletableFuture<ResponseWrapper<GetRecordResponse>> getRecordBySignature(String r, String s);

    CompletableFuture<ResponseWrapper<GetRecordResponse>> getRecordBySignature(Signature signature);

    CompletableFuture<ResponseWrapper<ListRecordsResponse>> listRecords(String starkKey, Long limit, Long page, String contractAddress);

    CompletableFuture<ResponseWrapper<ListRecordsResponse>> listRecords(List<Long> sequenceIds);

    CompletableFuture<ResponseWrapper<GetTxnResponse>> getTxn(long sequenceId);

    CompletableFuture<ResponseWrapper<GetRecordResponse>> waitingTransferGetApproved(String starkKey, long sequenceId);

    CompletableFuture<ResponseWrapper<GetRecordResponse>> waitingTransferGetApproved(String starkKey, long sequenceId, Duration interval, Duration deadline, AtomicBoolean shouldStop);

    CompletableFuture<ResponseWrapper<MintResponse>> mints(String contractAddress, String starkKey, long amount);

    CompletableFuture<ResponseWrapper<MintResponse>> mints(String contractAddress, String starkKey, List<Long> tokenIds);

    CompletableFuture<ResponseWrapper<WithdrawalStatusResponse>> withdrawalStatus(String stage, String ethAddress);

    WithStarkExSigner withStarkExSigner(StarkExSigner starkExSigner);

    WithStarkExSigner withStarkExSigner(String starkPrivateKey);

}
