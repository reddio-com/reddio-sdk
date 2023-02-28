package com.reddio.api.v1

import com.reddio.ReddioException
import com.reddio.api.v1.rest.*
import com.reddio.sign.PaymentSign
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.Instant
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

class DefaultReddioClient(
    private val restClient: ReddioRestClient
) : ReddioClient {

    private val quantizedHelper = QuantizedHelper(restClient);


    override fun getOrder(orderId: Long): CompletableFuture<ResponseWrapper<Order>> {
        return CompletableFuture.supplyAsync {
            runBlocking {
                restClient.getOrder(orderId).await()
            }
        }
    }

    override fun getRecord(starkKey: String?, sequenceId: Long): CompletableFuture<ResponseWrapper<GetRecordResponse>> {
        return restClient.getRecord(GetRecordMessage.of(starkKey, sequenceId))
    }

    override fun getRecordBySignature(r: String, s: String): CompletableFuture<ResponseWrapper<GetRecordResponse>> {
        return restClient.getRecordBySignature(Signature.of(r, s))
    }

    override fun getRecordBySignature(signature: Signature): CompletableFuture<ResponseWrapper<GetRecordResponse>> {
        return restClient.getRecordBySignature(signature)
    }

    override fun listRecords(
        starkKey: String?,
        limit: Long?,
        page: Long?,
        contractAddress: String?
    ): CompletableFuture<ResponseWrapper<ListRecordsResponse>> {
        return this.restClient.listRecords(ListRecordsMessage.of(starkKey, limit, page, contractAddress))
    }

    override fun listRecords(sequenceIds: MutableList<Long>?): CompletableFuture<ResponseWrapper<ListRecordsResponse>> {
        return this.restClient.listRecords(ListRecordsMessage.of(sequenceIds))
    }

    override fun getTxn(sequenceId: Long): CompletableFuture<ResponseWrapper<GetTxnResponse>> {
        return restClient.getTxn(GetTxnMessage.of(sequenceId))
    }

    override fun waitingTransferGetApproved(
        starkKey: String, sequenceId: Long
    ): CompletableFuture<ResponseWrapper<GetRecordResponse>> {
        val neverStop = AtomicBoolean(false);
        return waitingTransferGetApproved(
            starkKey, sequenceId,
            Duration.ofSeconds(5),
            Duration.ofSeconds(60),
            neverStop,
        )
    }

    override fun waitingTransferGetApproved(
        starkKey: String, sequenceId: Long, interval: Duration, deadline: Duration, shouldStop: AtomicBoolean
    ): CompletableFuture<ResponseWrapper<GetRecordResponse>> {
        val startTime = Instant.now()
        return CompletableFuture.supplyAsync {
            val result: ResponseWrapper<GetRecordResponse>
            runBlocking {
                while (true) {
                    if (shouldStop.get()) {
                        throw InterruptedException("cancelled")
                    }
                    if (Thread.interrupted()) {
                        throw InterruptedException("cancelled")
                    }
                    if (startTime.plus(deadline).isBefore(Instant.now())) {
                        throw InterruptedException("timed out")
                    }
                    val record = restClient.getRecord(GetRecordMessage.of(starkKey, sequenceId)).await()
                    if (RecordStatus.AcceptedByReddio == record.getData()[0].getStatus()) {
                        result = record
                        break
                    }
                    if (RecordStatus.FailedOnReddio == record.getData()[0].getStatus()) {
                        throw TransferFailedException("transfer failed", record.getData())
                    }
                    delay(interval.toMillis())
                }
                result
            }
        }
    }

    override fun mints(
        contractAddress: String, starkKey: String, amount: Long
    ): CompletableFuture<ResponseWrapper<MintResponse>> {
        return restClient.mints(MintsMessage.of(contractAddress, starkKey, amount.toString()))
    }

    override fun mints(
        contractAddress: String, starkKey: String, tokenIds: List<Long>
    ): CompletableFuture<ResponseWrapper<MintResponse>> {
        return restClient.mints(MintsMessage.of(contractAddress, starkKey, "", MintsMessage.tokenIdsAsString(tokenIds)))
    }

    override fun withdrawalStatus(
        stage: String, ethAddress: String
    ): CompletableFuture<ResponseWrapper<WithdrawalStatusResponse>> {
        return this.restClient.withdrawalStatus(WithdrawalStatusMessage.of(stage, ethAddress))
    }

    override fun withStarkExSigner(starkExSigner: StarkExSigner): ReddioClient.WithStarkExSigner {
        return DefaultWithStarkExSigner(restClient, starkExSigner)
    }

    override fun withStarkExSigner(starkPrivateKey: String): ReddioClient.WithStarkExSigner {
        return DefaultWithStarkExSigner(restClient, StarkExSigner.buildWithPrivateKey(starkPrivateKey))
    }

    inner class DefaultWithStarkExSigner(
        private val restClient: ReddioRestClient,
        private val starkExSigner: StarkExSigner,
    ) : ReddioClient.WithStarkExSigner {
        override fun close() {
            // noop
        }

        override fun withdrawalMessage(
            amount: String,
            contractAddress: String,
            tokenId: String,
            type: String,
            receiver: String,
            expirationTimeStamp: Long
        ): WithdrawalToMessage {
            val starkKey = starkExSigner.getStarkKey()
            return runBlocking {
                val quantizedAmount = quantizedHelper.quantizedAmount(amount, type, contractAddress).toString()
                val assetId = getAssetId(contractAddress, tokenId, type)
                val vaultsIds = getVaultsIds(assetId, starkKey, receiver)
                val senderVaultId = vaultsIds.senderVaultId
                val receiverVaultId = vaultsIds.receiverVaultId
                val nonce = restClient.getNonce(GetNonceMessage.of(starkKey)).await().getData().getNonce()
                val signature = starkExSigner.signTransferMessage(
                    quantizedAmount, nonce, senderVaultId, assetId, receiverVaultId, receiver, expirationTimeStamp
                )
                WithdrawalToMessage.of(
                    contractAddress,
                    assetId,
                    starkKey,
                    quantizedAmount,
                    tokenId,
                    nonce,
                    senderVaultId,
                    receiver,
                    receiverVaultId,
                    expirationTimeStamp,
                    signature
                )
            }
        }

        override fun withdrawalETHMessage(
            amount: String, receiver: String, expirationTimeStamp: Long
        ): WithdrawalToMessage {
            return withdrawalMessage(
                amount, "ETH", "", ReddioClient.TOKEN_TYPE_ETH, receiver, expirationTimeStamp
            )
        }

        override fun withdrawalERC20Message(
            amount: String, contractAddress: String, receiver: String, expirationTimeStamp: Long
        ): WithdrawalToMessage {
            return withdrawalMessage(
                amount, contractAddress, "", ReddioClient.TOKEN_TYPE_ERC20, receiver, expirationTimeStamp
            )
        }

        override fun withdrawalERC721Message(
            contractAddress: String, tokenId: String, receiver: String, expirationTimeStamp: Long
        ): WithdrawalToMessage {
            return withdrawalMessage(
                "1", contractAddress, tokenId, ReddioClient.TOKEN_TYPE_ERC721, receiver, expirationTimeStamp
            )
        }

        override fun withdrawal(withdrawalToMessage: WithdrawalToMessage): CompletableFuture<ResponseWrapper<WithdrawalToResponse>> {
            return restClient.withdrawalTo(withdrawalToMessage);
        }

        override fun withdrawal(
            starkKey: String,
            amount: String,
            contractAddress: String,
            tokenId: String,
            type: String,
            receiver: String,
            expirationTimeStamp: Long
        ): CompletableFuture<ResponseWrapper<WithdrawalToResponse>> {
            return this.withdrawal(
                amount, contractAddress, tokenId, type, receiver, expirationTimeStamp
            )
        }

        override fun withdrawal(
            amount: String,
            contractAddress: String,
            tokenId: String,
            type: String,
            receiver: String,
            expirationTimeStamp: Long
        ): CompletableFuture<ResponseWrapper<WithdrawalToResponse>> {
            val message = this.withdrawalMessage(
                amount, contractAddress, tokenId, type, receiver, expirationTimeStamp
            )
            return this.withdrawal(message)
        }

        override fun withdrawalETH(
            amount: String, receiver: String, expirationTimeStamp: Long
        ): CompletableFuture<ResponseWrapper<WithdrawalToResponse>> {
            return this.withdrawal(
                amount,
                "ETH",
                "",
                ReddioClient.TOKEN_TYPE_ETH,
                receiver,
                expirationTimeStamp,
            )
        }

        override fun withdrawalERC20(
            amount: String, contractAddress: String, receiver: String, expirationTimeStamp: Long
        ): CompletableFuture<ResponseWrapper<WithdrawalToResponse>> {
            return this.withdrawal(
                amount,
                contractAddress,
                "",
                ReddioClient.TOKEN_TYPE_ERC20,
                receiver,
                expirationTimeStamp,
            )
        }

        override fun withdrawalERC721(
            contractAddress: String,
            tokenId: String,
            tokenType: String,
            receiver: String,
            expirationTimeStamp: Long
        ): CompletableFuture<ResponseWrapper<WithdrawalToResponse>> {
            if (ReddioClient.TOKEN_TYPE_ERC721M != tokenType && ReddioClient.TOKEN_TYPE_ERC721 != tokenType) {
                throw ReddioException("tokenType must be ERC721 or ERC721M for ERC721/ERC721M withdrawal")
            }

            return this.withdrawal(
                "1",
                contractAddress,
                tokenId,
                tokenType,
                receiver,
                expirationTimeStamp,
            )
        }


        override fun order(
            starkKey: String,
            price: String,
            amount: String,
            tokenAddress: String,
            tokenId: String,
            marketplaceUuid: String,
            tokenType: String,
            orderType: OrderBehavior
        ): CompletableFuture<ResponseWrapper<OrderResponse>> {
            return CompletableFuture.supplyAsync {
                runBlocking {
                    val orderMessage = orderMessage(
                        starkKey,
                        "ETH",
                        "ETH",
                        tokenType,
                        tokenAddress,
                        tokenId,
                        price,
                        amount,
                        orderType
                    )
                    restClient.order(orderMessage).await()
                }
            }
        }

        override fun order(
            starkKey: String,
            contractType: String,
            contractAddress: String,
            tokenId: String,
            price: String,
            amount: String,
            orderType: OrderBehavior,
            baseTokenType: String,
            baseTokenContract: String,
            marketplaceUuid: String,
        ): CompletableFuture<ResponseWrapper<OrderResponse>> {
            return CompletableFuture.supplyAsync {
                runBlocking {
                    val orderMessage = orderMessage(
                        starkKey,
                        baseTokenType,
                        baseTokenContract,
                        contractType,
                        contractAddress,
                        tokenId,
                        price,
                        amount,
                        orderType
                    )
                    restClient.order(orderMessage).await()
                }
            }

        }

        private suspend fun orderMessage(
            starkKey: String,
            baseTokenType: String,
            baseTokenContract: String,
            contractType: String,
            contractAddress: String,
            tokenId: String,
            price: String,
            amount: String,
            orderType: OrderBehavior
        ): OrderMessage {
            val orderInfoResponse = restClient.orderInfo(
                OrderInfoMessage.of(
                    starkKey,
                    String.format("%s:%s", baseTokenType, baseTokenContract),
                    String.format("%s:%s:%s", contractType, contractAddress, tokenId)
                )
            ).await()


            val vaultIds = orderInfoResponse.data.getVaultIds()
            val quoteToken = orderInfoResponse.data.assetIds[1]
            val quantizedPrice = quantizedHelper.quantizedAmount(price, baseTokenType, baseTokenContract)
            val formatPrice = quantizedPrice.toString()
            val amountBuy = (quantizedPrice.toDouble() * amount.toDouble()).toLong().toString()

            val orderMessage = OrderMessage()
            orderMessage.amount = amount;
            orderMessage.baseToken = orderInfoResponse.data.getBaseToken()
            orderMessage.quoteToken = quoteToken
            orderMessage.price = formatPrice
            orderMessage.starkKey = starkKey
            orderMessage.expirationTimestamp = 4194303;
            orderMessage.nonce = orderInfoResponse.data.nonce;
            orderMessage.feeInfo = FeeInfo.of(
                (orderInfoResponse.data.feeRate.toDouble() * amountBuy.toDouble()).toLong(),
                orderInfoResponse.data.feeToken,
                vaultIds[0].toLong()
            )
            if (orderType == OrderBehavior.BUY) {
                orderMessage.direction = OrderMessage.DIRECTION_BID
                orderMessage.tokenSell = orderInfoResponse.data.baseToken
                orderMessage.tokenBuy = quoteToken
                orderMessage.amountSell = amountBuy
                orderMessage.amountBuy = amount
                orderMessage.vaultIdBuy = vaultIds[1]
                orderMessage.vaultIdSell = vaultIds[0]
            } else {
                orderMessage.direction = OrderMessage.DIRECTION_ASK
                orderMessage.tokenSell = quoteToken
                orderMessage.tokenBuy = orderInfoResponse.data.baseToken
                orderMessage.amountSell = amount
                orderMessage.amountBuy = amountBuy
                orderMessage.vaultIdBuy = vaultIds[0]
                orderMessage.vaultIdSell = vaultIds[1]
            }
            orderMessage.signature = starkExSigner.signOrderMsgWithFee(
                orderMessage.vaultIdSell,
                orderMessage.vaultIdBuy,
                orderMessage.amountSell,
                orderMessage.amountBuy,
                orderMessage.tokenSell,
                orderMessage.tokenBuy,
                orderMessage.nonce,
                orderMessage.expirationTimestamp,
                orderMessage.feeInfo.tokenId,
                orderMessage.feeInfo.sourceVaultId,
                orderMessage.feeInfo.feeLimit
            )
            return orderMessage
        }

        override fun cancelOrder(
            starkKey: String, orderId: Long
        ): CompletableFuture<ResponseWrapper<CancelOrderResponse>> {
            return CompletableFuture.supplyAsync {
                runBlocking {
                    val signature = starkExSigner.signCancelOrderMsg(orderId)
                    restClient.cancelOrder(orderId, CancelOrderMessage.of(starkKey, signature)).await()
                }
            }
        }

        override fun orderWithPayInfo(
            starkKey: String,
            contractType: String,
            contractAddress: String,
            tokenId: String,
            price: String,
            amount: String,
            orderType: OrderBehavior,
            marketplaceUuid: String,
            payInfo: Payment.PayInfo,
            signPayInfoPrivateKey: String,
            baseTokenType: String,
            baseTokenAddress: String
        ): CompletableFuture<ResponseWrapper<OrderResponse>> {

            return CompletableFuture.supplyAsync {
                runBlocking {

                    val orderMessage = orderMessage(
                        starkKey,
                        baseTokenType,
                        baseTokenAddress,
                        contractType,
                        contractAddress,
                        tokenId,
                        price,
                        amount,
                        orderType
                    )

                    val orderInfoResponse = restClient.orderInfo(
                        OrderInfoMessage.of(
                            starkKey, String.format(
                                "%s:%s", baseTokenType, baseTokenAddress
                            ), String.format("%s:%s:%s", contractType, contractAddress, tokenId)
                        )
                    ).await()

                    // append pay info
                    if (OrderBehavior.BUY == orderType) {
                        orderMessage.setStopLimitTimeInForce(OrderMessage.STOP_LIMIT_TIME_IN_FORCE_IOC)
                        val sign = PaymentSign.sign(
                            signPayInfoPrivateKey, payInfo.orderId, orderInfoResponse.data.nonce
                        )
                        orderMessage.setPayment(
                            OrderMessage.Payment.of(
                                payInfo, orderInfoResponse.data.nonce, sign
                            )
                        )
                    }
                    restClient.order(orderMessage).await()
                }
            }
        }

        override fun orderWithEth(
            starkKey: String,
            contractType: String,
            contractAddress: String,
            tokenId: String,
            price: String,
            amount: String,
            orderType: OrderBehavior
        ): CompletableFuture<ResponseWrapper<OrderResponse>> {
            return order(
                starkKey, contractType, contractAddress, tokenId, price, amount, orderType, "ETH", "eth", ""
            )
        }

        override fun buyNFTWithPayInfoBaseTokenRUSD(
            starkKey: String,
            contractType: String,
            contractAddress: String,
            tokenId: String,
            price: String,
            amount: String,
            marketplaceUuid: String,
            payInfo: Payment.PayInfo,
            signPayInfoPrivateKey: String
        ): CompletableFuture<ResponseWrapper<OrderResponse>> {
            return orderWithPayInfo(
                starkKey,
                contractType,
                contractAddress,
                tokenId,
                price,
                amount,
                OrderBehavior.BUY,
                marketplaceUuid,
                payInfo,
                signPayInfoPrivateKey,
                ReddioClient.TOKEN_TYPE_ERC20,
                ReddioClient.RUSD_TESTNET_CONTRACT_ADDRESS
            )
        }

        override fun buyNFTWithETHOrderTypeIOC(
            starkKey: String,
            contractType: String,
            contractAddress: String,
            tokenId: String,
            price: String,
            amount: String,
            marketplaceUuid: String
        ): CompletableFuture<ResponseWrapper<OrderResponse>> {
            return CompletableFuture.supplyAsync {
                runBlocking {
                    val orderMessage = orderMessage(
                        starkKey,
                        "ETH",
                        "ETH",
                        contractType,
                        contractAddress,
                        tokenId,
                        price,
                        amount,
                        OrderBehavior.BUY
                    )

                    // setup stop limit order as IOC
                    orderMessage.setStopLimitTimeInForce(OrderMessage.STOP_LIMIT_TIME_IN_FORCE_IOC)
                    restClient.order(orderMessage).await()
                }
            }
        }

        override fun sellNFTWithRUSD(
            starkKey: String,
            contractType: String,
            contractAddress: String,
            tokenId: String,
            price: String,
            amount: String,
            marketplaceUuid: String
        ): CompletableFuture<ResponseWrapper<OrderResponse>> {
            return order(
                starkKey,
                contractType,
                contractAddress,
                tokenId,
                price,
                amount,
                OrderBehavior.SELL,
                ReddioClient.TOKEN_TYPE_ERC20,
                ReddioClient.RUSD_TESTNET_CONTRACT_ADDRESS,
                marketplaceUuid
            )
        }

        override fun transfer(
            starkKey: String,
            amount: String,
            contractAddress: String,
            tokenId: String,
            tokenType: String,
            receiver: String,
            expirationTimeStamp: Long
        ): CompletableFuture<ResponseWrapper<TransferResponse>> {
            return CompletableFuture.supplyAsync {
                runBlocking {

                    val quantizedAmount = quantizedHelper.quantizedAmount(amount, tokenType, contractAddress).toString()
                    val assetId = getAssetId(contractAddress, tokenId, tokenType)
                    val vaultsIds = getVaultsIds(assetId, starkKey, receiver)
                    val nonce = restClient.getNonce(GetNonceMessage.of(starkKey)).await().getData().getNonce()

                    val signature = starkExSigner.signTransferMessage(
                        quantizedAmount,
                        nonce,
                        vaultsIds.senderVaultId,
                        assetId,
                        vaultsIds.receiverVaultId,
                        receiver,
                        expirationTimeStamp
                    )
                    restClient.transfer(
                        TransferMessage.of(
                            assetId,
                            starkKey,
                            quantizedAmount,
                            nonce,
                            vaultsIds.senderVaultId,
                            receiver,
                            vaultsIds.receiverVaultId,
                            expirationTimeStamp,
                            signature
                        )
                    ).await()
                }
            }
        }

        override fun transferETH(
            amount: String, receiver: String, expirationTimeStamp: Long
        ): CompletableFuture<ResponseWrapper<TransferResponse>> {
            val starkKey = this.starkExSigner.getStarkKey();
            return this.transfer(
                starkKey, amount, "ETH", "", ReddioClient.TOKEN_TYPE_ETH, receiver, expirationTimeStamp
            )
        }

        override fun transferERC20(
            amount: String, contractAddress: String, receiver: String, expirationTimeStamp: Long
        ): CompletableFuture<ResponseWrapper<TransferResponse>> {
            val starkKey = this.starkExSigner.getStarkKey();
            return this.transfer(
                starkKey, amount, contractAddress, "", ReddioClient.TOKEN_TYPE_ERC20, receiver, expirationTimeStamp
            )
        }

        override fun transferERC721(
            contractAddress: String,
            tokenId: String,
            tokenType: String,
            receiver: String,
            expirationTimeStamp: Long
        ): CompletableFuture<ResponseWrapper<TransferResponse>> {
            if (ReddioClient.TOKEN_TYPE_ERC721M != tokenType && ReddioClient.TOKEN_TYPE_ERC721 != tokenType) {
                throw ReddioException("tokenType must be ERC721 or ERC721M for ERC721/ERC721M transfer")
            }

            val starkKey = this.starkExSigner.getStarkKey();
            return this.transfer(
                starkKey,
                "1",
                contractAddress,
                tokenId,
                tokenType,
                receiver,
                expirationTimeStamp
            )
        }

    }

    override fun close() {
        // noop
    }

    private suspend fun getAssetId(
        contractAddress: String,
        tokenId: String,
        type: String,
    ): String {
        val contractInfo =
            restClient.getContractInfo(GetContractInfoMessage.of(type, contractAddress)).await().getData()
        val result =
            restClient.getAssetId(GetAssetIdMessage.of(contractAddress, type, tokenId, contractInfo.quantum)).await()
        return result.getData().getAssetId()
    }

    private suspend fun getVaultsIds(assetId: String, starkKey: String, receiver: String): VaultIds {
        val result = restClient.getVaultId(GetVaultIdMessage.of(assetId, listOf(starkKey, receiver))).await()
        return VaultIds(result.getData().vaultIds[0], result.getData().vaultIds[1])
    }

    companion object {

        @JvmStatic
        fun mainnet(): DefaultReddioClient = DefaultReddioClient(DefaultReddioRestClient.mainnet())

        @JvmStatic
        fun mainnet(apiKey: String): DefaultReddioClient = DefaultReddioClient(DefaultReddioRestClient.mainnet(apiKey))

        @JvmStatic
        fun testnet(): DefaultReddioClient = DefaultReddioClient(DefaultReddioRestClient.testnet())

        @JvmStatic
        fun testnet(apiKey: String): DefaultReddioClient = DefaultReddioClient(DefaultReddioRestClient.testnet(apiKey))

        private data class VaultIds(val senderVaultId: String, val receiverVaultId: String)
    }
}
