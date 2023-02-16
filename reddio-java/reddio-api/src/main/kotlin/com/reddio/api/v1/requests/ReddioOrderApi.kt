package com.reddio.api.v1.requests

import com.reddio.api.v1.OrderBehavior
import com.reddio.api.v1.QuantizedHelper
import com.reddio.api.v1.ReddioClient
import com.reddio.api.v1.StarkExSigner
import com.reddio.api.v1.requests.polling.OrderPoller
import com.reddio.api.v1.requests.polling.RecordPoller
import com.reddio.api.v1.rest.*
import com.reddio.api.v1.rest.Payment.PayInfo
import com.reddio.sign.PaymentSign
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture

class ReddioOrderApi private constructor(
    private val localRestClient: ReddioRestClient, private val request: OrderMessage
) : SignedReddioApiRequest<OrderMessage, ResponseWrapper<OrderResponse>> {
    override fun call(): ResponseWrapper<OrderResponse> {
        return this.callAsync().join()
    }

    override fun callAsync(): CompletableFuture<ResponseWrapper<OrderResponse>> {
        return this.localRestClient.order(this.request)
    }

    override fun getRequest(): OrderMessage {
        return this.request
    }

    override fun getSignature(): Signature {
        return this.request.getSignature()
    }

    /**
     * Call the request and poll the record until it reaches one of the desired status.
     */
    fun callAndPollRecord(vararg desiredRecordStatus: RecordStatus): SequenceRecord {
        val response = this.call()
        return RecordPoller(
            this.localRestClient, this.request.getStarkKey(), response.getData().getSequenceId()
        ).poll(*desiredRecordStatus)
    }

    /**
     * Call the request and poll the record until it reaches one of the desired status asynchronously.
     */
    fun callAndPollRecordAsync(vararg desiredRecordStatus: RecordStatus): CompletableFuture<SequenceRecord> {
        return this.callAsync().thenApplyAsync { response ->
            RecordPoller(this.localRestClient, this.request.getStarkKey(), response.getData().getSequenceId())
        }.thenComposeAsync { it.pollAsync(*desiredRecordStatus) }
    }

    /**
     * Call the request and poll the order until it reaches one of the order state.
     */
    fun callAndPollOrder(vararg desiredOrderState: OrderState): Order {
        val response = this.call()
        return OrderPoller(
            this.localRestClient, response.getData().getSequenceId()
        ).poll(*desiredOrderState)
    }

    /**
     * Call the request and poll the order until it reaches one of the desired order state asynchronously.
     */
    fun callAndPollOrderAsync(vararg desiredOrderState: OrderState): CompletableFuture<Order> {
        return this.callAsync().thenApplyAsync { response ->
            OrderPoller(this.localRestClient, response.getData().getSequenceId())
        }.thenComposeAsync { it.pollAsync(*desiredOrderState) }
    }

    companion object {
        @JvmStatic
        fun build(
            localRestClient: ReddioRestClient, request: OrderMessage
        ): ReddioOrderApi {
            return ReddioOrderApi(localRestClient, request)
        }

        @JvmStatic
        fun order(
            localRestClient: ReddioRestClient,
            starkPrivateKey: String,
            tokenType: String,
            contractAddress: String,
            tokenId: String,
            price: String,
            amount: String,
            orderBehavior: OrderBehavior,
            baseTokenType: String,
            baseTokenContract: String,
            marketplaceUuid: String,
        ): ReddioOrderApi {
            val quantizedHelper = QuantizedHelper(localRestClient)
            val starkExSigner = StarkExSigner(starkPrivateKey)
            val starkKey = starkExSigner.getStarkKey()

            val message = runBlocking {
                orderMessage(
                    localRestClient,
                    quantizedHelper,
                    starkExSigner,
                    starkKey,
                    baseTokenType,
                    baseTokenContract,
                    tokenType,
                    contractAddress,
                    tokenId,
                    price,
                    amount,
                    orderBehavior
                )
            }
            return build(localRestClient, message)
        }

        @JvmStatic
        fun orderWithETH(
            localRestClient: ReddioRestClient,
            starkPrivateKey: String,
            tokenType: String,
            contractAddress: String,
            tokenId: String,
            price: String,
            amount: String,
            orderBehavior: OrderBehavior,
        ): ReddioOrderApi {
            return order(
                localRestClient,
                starkPrivateKey,
                tokenType,
                contractAddress,
                tokenId,
                price,
                amount,
                orderBehavior,
                "ETH",
                "ETH",
                "",
            )
        }

        @JvmStatic
        fun orderWithPayInfo(
            localRestClient: ReddioRestClient,
            starkPrivateKey: String,
            tokenType: String,
            contractAddress: String,
            tokenId: String,
            price: String,
            amount: String,
            orderBehavior: OrderBehavior,
            marketplaceUuid: String,
            payInfo: Payment.PayInfo,
            signPayInfoPrivateKey: String,
            baseTokenType: String,
            baseTokenAddress: String,
        ): ReddioOrderApi {
            val quantizedHelper = QuantizedHelper(localRestClient)
            val starkExSigner = StarkExSigner(starkPrivateKey)
            val starkKey = starkExSigner.getStarkKey()

            val message = runBlocking {
                orderMessage(
                    localRestClient,
                    quantizedHelper,
                    starkExSigner,
                    starkKey,
                    baseTokenType,
                    baseTokenAddress,
                    tokenType,
                    contractAddress,
                    tokenId,
                    price,
                    amount,
                    orderBehavior,
                )
            }
            val orderInfoResponse = runBlocking {
                localRestClient.orderInfo(
                    OrderInfoMessage.of(
                        starkKey, String.format(
                            "%s:%s", baseTokenType, baseTokenAddress
                        ), String.format("%s:%s:%s", tokenType, contractAddress, tokenId)
                    )
                ).await()
            }
            // append pay info
            if (OrderBehavior.BUY == orderBehavior) {
                message.setStopLimitTimeInForce(OrderMessage.STOP_LIMIT_TIME_IN_FORCE_IOC)
                val sign = PaymentSign.sign(
                    signPayInfoPrivateKey, payInfo.orderId, orderInfoResponse.data.nonce
                )
                message.setPayment(
                    OrderMessage.Payment.of(
                        payInfo, orderInfoResponse.data.nonce, sign
                    )
                )
            }

            return build(localRestClient, message)
        }

        @JvmStatic
        fun buyNFTWithPayInfoBaseTokenRUSD(
            localRestClient: ReddioRestClient,
            starkPrivateKey: String,
            tokenType: String,
            contractAddress: String,
            tokenId: String,
            price: String,
            marketplaceUuid: String,
            payInfo: PayInfo,
            signPayInfoPrivateKey: String
        ): ReddioOrderApi {
            return orderWithPayInfo(
                localRestClient,
                starkPrivateKey,
                tokenType,
                contractAddress,
                tokenId,
                price,
                "1",
                OrderBehavior.BUY,
                marketplaceUuid,
                payInfo,
                signPayInfoPrivateKey,
                ReddioClient.TOKEN_TYPE_ERC20,
                ReddioClient.RUSD_TESTNET_CONTRACT_ADDRESS
            )
        }

        @JvmStatic
        fun buyNFTWithETHOrderTypeIOC(
            localRestClient: ReddioRestClient,
            starkPrivateKey: String,
            tokenType: String,
            contractAddress: String,
            tokenId: String,
            price: String,
            marketplaceUuid: String
        ): ReddioOrderApi {
            val quantizedHelper = QuantizedHelper(localRestClient)
            val starkExSigner = StarkExSigner(starkPrivateKey)
            val starkKey = starkExSigner.getStarkKey()

            val message = runBlocking {
                orderMessage(
                    localRestClient,
                    quantizedHelper,
                    starkExSigner,
                    starkKey,
                    "ETH",
                    "ETH",
                    tokenType,
                    contractAddress,
                    tokenId,
                    price,
                    "1",
                    OrderBehavior.BUY,
                )
            }
            // setup stop limit order as IOC
            message.setStopLimitTimeInForce(OrderMessage.STOP_LIMIT_TIME_IN_FORCE_IOC)

            return build(localRestClient, message)
        }

        @JvmStatic
        fun sellNFTWithRUSD(
            localRestClient: ReddioRestClient,
            starkPrivateKey: String,
            tokenType: String,
            contractAddress: String,
            tokenId: String,
            price: String,
            marketplaceUuid: String
        ): ReddioOrderApi {
            return order(
                localRestClient,
                starkPrivateKey,
                tokenType,
                contractAddress,
                tokenId,
                price,
                "1",
                OrderBehavior.SELL,
                ReddioClient.TOKEN_TYPE_ERC20,
                ReddioClient.RUSD_TESTNET_CONTRACT_ADDRESS,
                marketplaceUuid
            )
        }

        private suspend fun orderMessage(
            restClient: ReddioRestClient,
            quantizedHelper: QuantizedHelper,
            starkExSigner: StarkExSigner,
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
    }

}