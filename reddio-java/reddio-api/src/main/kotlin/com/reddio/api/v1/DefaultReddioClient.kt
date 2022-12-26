package com.reddio.api.v1

import com.reddio.ReddioException
import com.reddio.api.v1.rest.*
import com.reddio.crypto.CryptoService
import com.reddio.sign.BizMessageSHA3
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.web3j.utils.Convert
import java.math.BigInteger
import java.time.Duration
import java.time.Instant
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.toKotlinDuration

class DefaultReddioClient(
    private val restClient: ReddioRestClient
) : ReddioClient {

    private val quantizedHelper = QuantizedHelper(restClient);


    override fun getRecord(starkKey: String?, sequenceId: Long): CompletableFuture<ResponseWrapper<GetRecordResponse>> {
        return restClient.getRecord(GetRecordMessage.of(starkKey, sequenceId))
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
                    if (GetRecordResponse.SequenceRecord.SEQUENCE_STATUS_ACCEPTED == record.getData()[0].getStatus()) {
                        result = record
                        break
                    }
                    if (GetRecordResponse.SequenceRecord.SEQUENCE_STATUS_FAILED == record.getData()[0].getStatus()) {
                        throw TransferFailedException("transfer failed", record.getData())
                    }
                    delay(interval.toKotlinDuration())
                }
                result
            }
        }
    }

    override fun withStarkExSigner(starkExSigner: StarExSigner): ReddioClient.WithStarkExSigner {
        return DefaultWithStarkExSigner(restClient, starkExSigner)
    }

    override fun withStarkExSigner(starkPrivateKey: String): ReddioClient.WithStarkExSigner {
        return DefaultWithStarkExSigner(restClient, StarExSigner.buildWithPrivateKey(starkPrivateKey))
    }

    inner class DefaultWithStarkExSigner(
        private val restClient: ReddioRestClient,
        private val starkExSigner: StarExSigner,
    ) : ReddioClient.WithStarkExSigner {

        override fun withdrawal(
            starkKey: String,
            amount: String,
            contractAddress: String,
            tokenId: String,
            type: String,
            receiver: String,
            expirationTimeStamp: Long
        ): CompletableFuture<ResponseWrapper<WithdrawalToResponse>> {
            return CompletableFuture.supplyAsync {
                runBlocking {
                    val quantizedAmount = quantizedHelper.quantizedAmount(amount, type, contractAddress).toString()
                    val assetId = getAssetId(contractAddress, tokenId, type)
                    val vaultsIds = getVaultsIds(assetId, starkKey, receiver)
                    val senderVaultId = vaultsIds.senderVaultId
                    val receiverVaultId = vaultsIds.receiverVaultId
                    val nonce = restClient.getNonce(GetNonceMessage.of(starkKey)).await().getData().getNonce()
                    val signature = starkExSigner.signTransferMessage(
                        quantizedAmount, nonce, senderVaultId, assetId, receiverVaultId, receiver, expirationTimeStamp
                    )
                    restClient.withdrawalTo(
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
                    ).await()
                }
            }
        }

        override fun order(
            starkKey: String,
            price: String,
            amount: String,
            tokenAddress: String,
            tokenId: String,
            marketplaceUuid: String,
            tokenType: String,
            orderType: OrderType
        ): CompletableFuture<ResponseWrapper<OrderResponse>> {
            return CompletableFuture.supplyAsync {
                runBlocking {
                    val orderInfoResponse = restClient.orderInfo(
                        OrderInfoMessage.of(
                            starkKey, "ETH:ETH", String.format("%s:%s:%s", tokenType, tokenAddress, tokenId)
                        )
                    ).await()

                    val vaultIds = orderInfoResponse.data.getVaultIds()
                    val quoteToken = orderInfoResponse.data.assetIds[1]
                    // hard coded format ETH on layer2 (price * (10 **decimals) / quantum)
                    val amountBuy =
                        Convert.toWei((price.toDouble() * amount.toDouble()).toString(), Convert.Unit.MWEI).toLong()
                            .toString()
                    val formatPrice = Convert.toWei(price, Convert.Unit.MWEI).toString()

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
                    if (orderType == OrderType.BUY) {
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
            orderType: OrderType,
            baseTokenType: String,
            baseTokenContract: String,
            marketplaceUuid: String
        ): CompletableFuture<ResponseWrapper<OrderResponse>> {
            return CompletableFuture.supplyAsync {
                runBlocking {
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
                    if (orderType == OrderType.BUY) {
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
                    restClient.order(orderMessage).await()
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
            orderType: OrderType,
            marketplaceUuid: String,
            payInfo: BizMessage.PayInfo,
            signPayInfoPrivateKey: String,
        ): CompletableFuture<ResponseWrapper<OrderResponse>> {

            return CompletableFuture.supplyAsync {
                runBlocking {
                    val orderInfoResponse = restClient.orderInfo(
                        OrderInfoMessage.of(
                            starkKey,
                            String.format(
                                "%s:%s",
                                ReddioClient.TOKEN_TYPE_ERC20,
                                ReddioClient.RUSD_TESTNET_CONTRACT_ADDRESS
                            ),
                            String.format("%s:%s:%s", contractType, contractAddress, tokenId)
                        )
                    ).await()


                    val vaultIds = orderInfoResponse.data.getVaultIds()
                    val quoteToken = orderInfoResponse.data.assetIds[1]
                    val quantizedPrice = quantizedHelper.quantizedAmount(
                        price,
                        ReddioClient.TOKEN_TYPE_ERC20,
                        ReddioClient.RUSD_TESTNET_CONTRACT_ADDRESS
                    )
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
                    if (orderType == OrderType.BUY) {
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

                    // sign pay info
                    val bizMessage = BizMessage.of(payInfo, "")
                    val hash = BizMessageSHA3.getBizMessageHash(bizMessage, orderInfoResponse.data.nonce)
                    val sign = CryptoService.sign(
                        BigInteger(
                            signPayInfoPrivateKey.replace("0x", "").lowercase(),
                            16
                        ), hash, null
                    )

                    // append pay info
                    if (OrderType.BUY == orderType) {
                        orderMessage.setStopLimitTimeInForce(OrderMessage.STOP_LIMIT_TIME_IN_FORCE_IOC)
                        orderMessage.setPayment(
                            OrderMessage.Payment.of(
                                payInfo,
                                orderInfoResponse.data.nonce,
                                Signature.of(
                                    "0x" + sign.r,
                                    "0x" + sign.s,
                                    starkKey
                                )
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
            orderType: OrderType
        ): CompletableFuture<ResponseWrapper<OrderResponse>> {
            return order(
                starkKey, contractType, contractAddress, tokenId, price, amount, orderType, "ETH", "eth", ""
            )
        }

        override fun buyNFTWithPayInfo(
            starkKey: String,
            contractType: String,
            contractAddress: String,
            tokenId: String,
            price: String,
            amount: String,
            marketplaceUuid: String,
            payInfo: BizMessage.PayInfo,
            signPayInfoPrivateKey: String
        ): CompletableFuture<ResponseWrapper<OrderResponse>> {
            return orderWithPayInfo(
                starkKey,
                contractType,
                contractAddress,
                tokenId,
                price,
                amount,
                OrderType.BUY,
                marketplaceUuid,
                payInfo,
                signPayInfoPrivateKey
            )
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
                OrderType.SELL,
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
            type: String,
            receiver: String,
            expirationTimeStamp: Long
        ): CompletableFuture<ResponseWrapper<TransferResponse>> {
            return CompletableFuture.supplyAsync {
                runBlocking {
                    val assetId = getAssetId(contractAddress, tokenId, type)

                    val quantizedAmount = quantizedHelper.quantizedAmount(amount, type, contractAddress).toString()
                    val vaultsIds = getVaultsIds(assetId, starkKey, receiver)
                    val senderVaultId = vaultsIds.senderVaultId
                    val receiverVaultId = vaultsIds.receiverVaultId
                    val nonce = restClient.getNonce(GetNonceMessage.of(starkKey)).await().getData().getNonce()
                    val signature = starkExSigner.signTransferMessage(
                        quantizedAmount, nonce, senderVaultId, assetId, receiverVaultId, receiver, expirationTimeStamp
                    )
                    restClient.transfer(
                        TransferMessage.of(
                            assetId,
                            starkKey,
                            quantizedAmount,
                            nonce,
                            senderVaultId,
                            receiver,
                            receiverVaultId,
                            expirationTimeStamp,
                            signature
                        )
                    ).await()
                }
            }
        }

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
        fun testnet(): DefaultReddioClient = DefaultReddioClient(DefaultReddioRestClient.testnet())

        private data class VaultIds(val senderVaultId: String, val receiverVaultId: String)
    }
}

