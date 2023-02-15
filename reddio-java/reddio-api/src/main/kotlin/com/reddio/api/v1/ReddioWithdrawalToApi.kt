package com.reddio.api.v1

import com.reddio.api.v1.requests.SignedReddioApiRequest
import com.reddio.api.v1.rest.*
import java.util.concurrent.CompletableFuture

class ReddioWithdrawalToApi private constructor(
    private val localRestClient: ReddioRestClient,
    private val request: WithdrawalToMessage
) : SignedReddioApiRequest<WithdrawalToMessage?, ResponseWrapper<WithdrawalToResponse?>?> {
    override fun send(): ResponseWrapper<WithdrawalToResponse?>? {
        return null
    }

    override fun sendAsync(): CompletableFuture<ResponseWrapper<WithdrawalToResponse?>?>? {
        return null
    }

    override fun getRequest(): WithdrawalToMessage {
        return request
    }

    override fun getSignature(): Signature {
        return request.getSignature()
    }

    companion object {
        fun build(localRestClient: ReddioRestClient, request: WithdrawalToMessage): ReddioWithdrawalToApi {
            return ReddioWithdrawalToApi(localRestClient, request)
        }
    }
}