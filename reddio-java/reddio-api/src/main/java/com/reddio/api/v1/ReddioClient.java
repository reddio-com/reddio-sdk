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

        /**
         * Transfer ETH/ERC20/ERC721/ERC721M on layer2.
         *
         * @param starkKey            The stark key of the sender.
         * @param amount              The amount of the token to transfer.
         * @param contractAddress     The contract address of the token to transfer.
         * @param tokenId             The token id of the token to transfer. Use empty string if the token is ETH/ERC20.
         * @param tokenType           The token type of the token to transfer. Use {@link #TOKEN_TYPE_ETH}, {@link #TOKEN_TYPE_ERC20}, {@link #TOKEN_TYPE_ERC721} or {@link #TOKEN_TYPE_ERC721M}.
         * @param receiver            The stark key of the receiver.
         * @param expirationTimeStamp The expiration time stamp of the transfer in seconds, max value is 4194303L.
         */
        CompletableFuture<ResponseWrapper<TransferResponse>> transfer(String starkKey, String amount, String contractAddress, String tokenId, String tokenType, String receiver, long expirationTimeStamp);

        /**
         * Transfer ETH on layer2.
         *
         * @param amount              The amount of the token to transfer.
         * @param receiver            The stark key of the receiver.
         * @param expirationTimeStamp The expiration time stamp of the transfer in seconds, max value is 4194303L.
         */
        CompletableFuture<ResponseWrapper<TransferResponse>> transferETH(String amount, String receiver, long expirationTimeStamp);


        /**
         * Transfer ERC20 on layer2.
         *
         * @param amount              The amount of the token to transfer.
         * @param contractAddress     The contract address of the token to transfer.
         * @param receiver            The stark key of the receiver.
         * @param expirationTimeStamp The expiration time stamp of the transfer in seconds, max value is 4194303L.
         */
        CompletableFuture<ResponseWrapper<TransferResponse>> transferERC20(String amount, String contractAddress, String receiver, long expirationTimeStamp);

        /**
         * Transfer ERC721/ERC721M on layer2.
         *
         * @param contractAddress     The contract address of the token to transfer.
         * @param tokenId             The token id of the token to transfer.
         * @param tokenType           The token type of the token to transfer. Use {@link #TOKEN_TYPE_ERC721} or {@link #TOKEN_TYPE_ERC721M}.
         * @param receiver            The stark key of the receiver.
         * @param expirationTimeStamp The expiration time stamp of the transfer in seconds, max value is 4194303L.
         */
        CompletableFuture<ResponseWrapper<TransferResponse>> transferERC721(String contractAddress, String tokenId, String tokenType, String receiver, long expirationTimeStamp);

        @Deprecated
        WithdrawalToMessage withdrawalMessage(String amount, String contractAddress, String tokenId, String type, String receiver, long expirationTimeStamp);

        @Deprecated
        WithdrawalToMessage withdrawalETHMessage(String amount, String receiver, long expirationTimeStamp);

        @Deprecated
        WithdrawalToMessage withdrawalERC20Message(String amount, String contractAddress, String receiver, long expirationTimeStamp);

        @Deprecated
        WithdrawalToMessage withdrawalERC721Message(String contractAddress, String tokenId, String receiver, long expirationTimeStamp);

        @Deprecated
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

        /**
         * Withdraw ETH/ERC20/ERC721/ERC721M on layer2.
         *
         * @param amount              The amount of the token to withdraw.
         * @param contractAddress     The contract address of the token to withdraw.
         * @param tokenId             The token id of the token to withdraw. Use empty string if the token is ETH/ERC20.
         * @param type                The token type of the token to withdraw. Use {@link #TOKEN_TYPE_ETH}, {@link #TOKEN_TYPE_ERC20}, {@link #TOKEN_TYPE_ERC721} or {@link #TOKEN_TYPE_ERC721M}.
         * @param receiver            The stark key of the receiver.
         * @param expirationTimeStamp The expiration time stamp of the withdrawal in seconds, max value is 4194303L.
         */
        CompletableFuture<ResponseWrapper<WithdrawalToResponse>> withdrawal(String amount, String contractAddress, String tokenId, String type, String receiver, long expirationTimeStamp);

        /**
         * Withdraw ETH on layer2.
         *
         * @param amount              The amount of the token to withdraw.
         * @param receiver            The stark key of the receiver.
         * @param expirationTimeStamp The expiration time stamp of the withdrawal in seconds, max value is 4194303L.
         */
        CompletableFuture<ResponseWrapper<WithdrawalToResponse>> withdrawalETH(String amount, String receiver, long expirationTimeStamp);

        /**
         * Withdraw ERC20 on layer2.
         *
         * @param amount              The amount of the token to withdraw.
         * @param contractAddress     The contract address of the token to withdraw.
         * @param receiver            The stark key of the receiver.
         * @param expirationTimeStamp The expiration time stamp of the withdrawal in seconds, max value is 4194303L.
         */
        CompletableFuture<ResponseWrapper<WithdrawalToResponse>> withdrawalERC20(String amount, String contractAddress, String receiver, long expirationTimeStamp);

        /**
         * Withdraw ERC721/ERC721M on layer2.
         *
         * @param contractAddress     The contract address of the token to withdraw.
         * @param tokenId             The token id of the token to withdraw.
         * @param tokenType           The token type of the token to withdraw. Use {@link #TOKEN_TYPE_ERC721} or {@link #TOKEN_TYPE_ERC721M}.
         * @param receiver            The stark key of the receiver.
         * @param expirationTimeStamp The expiration time stamp of the withdrawal in seconds, max value is 4194303L.
         */
        CompletableFuture<ResponseWrapper<WithdrawalToResponse>> withdrawalERC721(String contractAddress, String tokenId, String tokenType, String receiver, long expirationTimeStamp);

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
