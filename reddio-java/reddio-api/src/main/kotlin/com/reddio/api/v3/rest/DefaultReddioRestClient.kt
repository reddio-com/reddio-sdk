package com.reddio.api.v3.rest

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.reddio.api.misc.EnsureSuccess
import com.reddio.api.misc.ReddioApiKeyInterceptor
import com.reddio.api.misc.ReddioUAInterceptor
import com.reddio.api.misc.ToCompletableFutureCallback
import com.reddio.api.v1.rest.ResponseWrapper
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.CompletableFuture


class DefaultReddioRestClient(
    private val baseEndpoint: String,
    private val apiKey: String,
    private val httpClient: OkHttpClient,
) : ReddioRestClient {

    constructor(baseUrl: String, apiKey: String) : this(
        baseUrl, apiKey, OkHttpClient.Builder().addInterceptor(
            ReddioUAInterceptor.create()
        ).addInterceptor(ReddioApiKeyInterceptor.create(apiKey)).build()
    )

    constructor(baseUrl: String) : this(baseUrl, "")

    override fun getBalances(getBalancesMessage: GetBalancesMessage): CompletableFuture<ResponseWrapper<GetBalancesResponse>> {
        val builder = ("$baseEndpoint/v3/balances").toHttpUrl().newBuilder()
        builder.addQueryParameter("stark_key", getBalancesMessage.starkKey!!)
        if (getBalancesMessage.contractAddress != null) {
            builder.addQueryParameter("contract_address", getBalancesMessage.contractAddress)
        }
        if (getBalancesMessage.limit != null) {
            builder.addQueryParameter("limit", getBalancesMessage.limit.toString())
        }
        if (getBalancesMessage.page != null) {
            builder.addQueryParameter("page", getBalancesMessage.page.toString())
        }
        if (getBalancesMessage.type != null) {
            builder.addQueryParameter("type", getBalancesMessage.type.toString())
        }
        val endpoint: HttpUrl = builder.build()
        val request = Request.Builder().url(endpoint).get().build()
        val call = this.httpClient.newCall(request)
        return ToCompletableFutureCallback.asFuture(
            call, object : TypeReference<ResponseWrapper<GetBalancesResponse>>() {}, objectMapper
        ).thenApply {
            EnsureSuccess.ensureSuccess(
                it, "endpoint", endpoint.toString()
            )
        }
    }

    companion object {
        private val objectMapper = jacksonObjectMapper()
        const val MAINNET_API_ENDPOINT = "https://api.reddio.com"
        const val TESTNET_API_ENDPOINT = "https://api-dev.reddio.com"

        @JvmStatic
        fun mainnet(): DefaultReddioRestClient {
            return DefaultReddioRestClient(DefaultReddioRestClient.MAINNET_API_ENDPOINT)
        }

        @JvmStatic
        fun mainnet(apiKey: String): DefaultReddioRestClient {
            return DefaultReddioRestClient(DefaultReddioRestClient.MAINNET_API_ENDPOINT, apiKey)
        }

        @JvmStatic
        fun testnet(): DefaultReddioRestClient {
            return DefaultReddioRestClient(DefaultReddioRestClient.TESTNET_API_ENDPOINT)
        }

        @JvmStatic
        fun testnet(apiKey: String): DefaultReddioRestClient {
            return DefaultReddioRestClient(DefaultReddioRestClient.TESTNET_API_ENDPOINT, apiKey)
        }

    }
}