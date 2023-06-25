package com.reddio.api.v3.requests

import com.reddio.api.misc.ReddioApiRequest
import com.reddio.api.misc.unwrapCompletionException
import com.reddio.api.v1.rest.ResponseWrapper
import com.reddio.api.v3.rest.GetBalancesMessage
import com.reddio.api.v3.rest.GetBalancesResponse
import com.reddio.api.v3.rest.ReddioRestClient
import java.util.concurrent.CompletableFuture

class ReddioGetBalancesApi private constructor(
    private val localRestClient: ReddioRestClient, private val request: GetBalancesMessage
)

    : ReddioApiRequest<GetBalancesMessage, ResponseWrapper<GetBalancesResponse>> {

    override fun call(): ResponseWrapper<GetBalancesResponse> {
        return unwrapCompletionException {
            this.callAsync().join()
        }
    }

    override fun callAsync(): CompletableFuture<ResponseWrapper<GetBalancesResponse>> {
        return this.localRestClient.getBalances(this.request)
    }

    override fun getRequest(): GetBalancesMessage {
        return this.request
    }

    companion object {
        @JvmStatic
        fun build(localRestClient: ReddioRestClient, request: GetBalancesMessage): ReddioGetBalancesApi {
            return ReddioGetBalancesApi(localRestClient, request)
        }

        @JvmStatic
        @JvmOverloads
        fun getaBalance(
            localRestClient: ReddioRestClient,
            starkKey: String,
            contractAddress: String = "",
            type: String = "",
            page: Long = 1,
            limit: Long = 100
        ): ReddioGetBalancesApi {
            val request = GetBalancesMessage.of(starkKey, page, limit, contractAddress, type)
            return build(localRestClient, request)
        }
    }
}