package com.reddio.api.v1.requests

import com.reddio.api.v1.StarkExSigner
import com.reddio.api.v1.requests.polling.OrderPoller
import com.reddio.api.v1.rest.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

class ReddioCancelOrderApi private constructor(
    private val localRestClient: ReddioRestClient, private val orderId: Long, private val request: CancelOrderMessage
) : SignedReddioApiRequest<CancelOrderMessage, ResponseWrapper<CancelOrderResponse>> {

    override fun call(): ResponseWrapper<CancelOrderResponse> {
        return unwrapCompletionException {
            callAsync().join()
        }
    }

    override fun callAsync(): CompletableFuture<ResponseWrapper<CancelOrderResponse>> {
        return localRestClient.cancelOrder(orderId, request);
    }

    override fun getRequest(): CancelOrderMessage {
        return this.request
    }

    override fun getSignature(): Signature {
        return this.request.signature
    }

    /**
     * Call the API and poll for the status of original order with given order id until it is cancelled.
     */
    fun callAndPollOrder(): Order {
        val ignored = call()
        return OrderPoller(
            this.localRestClient, this.orderId
        ).poll(*defaultDesiredOrderState)
    }

    /**
     * Call the API and poll for the status of original order with given order id until it is cancelled.
     */
    fun callAndPollOrderAsync(): CompletableFuture<Order> {
        return this.callAsync().thenApplyAsync { _ ->
            OrderPoller(this.localRestClient, orderId)
        }.thenComposeAsync { it.pollAsync(*defaultDesiredOrderState) }
    }

    companion object {
        val defaultDesiredOrderState = arrayOf(OrderState.Canceled)

        @JvmStatic
        fun build(
            localRestClient: ReddioRestClient, orderId: Long, request: CancelOrderMessage
        ): ReddioCancelOrderApi = ReddioCancelOrderApi(
            localRestClient, orderId, request
        )

        @JvmStatic
        fun cancelOrder(
            localRestClient: ReddioRestClient, starkPrivateKey: String, orderId: Long
        ): ReddioCancelOrderApi {
            val starkExSigner = StarkExSigner(starkPrivateKey)
            val starkKey = starkExSigner.getStarkKey()
            val signature = starkExSigner.signCancelOrderMsg(orderId)
            val request = CancelOrderMessage.of(starkKey, signature);
            return build(localRestClient, orderId, request)
        }
    }
}
