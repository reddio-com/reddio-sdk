package com.reddio.api.v1.requests

import com.reddio.api.v1.requests.polling.RecordPoller
import com.reddio.api.v1.rest.*
import java.util.concurrent.CompletableFuture

class ReddioMultiTransferApi private constructor(
    private val localRestClient: ReddioRestClient, private val request: MultiTransferMessage
) : SignedReddioApiRequest<MultiTransferMessage, ResponseWrapper<MultiTransferResponse>> {
    override fun call(): ResponseWrapper<MultiTransferResponse> {
        return unwrapCompletionException {
            this.callAsync().join()
        }
    }

    override fun callAsync(): CompletableFuture<ResponseWrapper<MultiTransferResponse>> {
        return this.localRestClient.multiTransfer(this.request)
    }

    override fun getRequest(): MultiTransferMessage {
        return this.request
    }

    override fun getSignature(): Signature {
        return this.request.signature
    }

    fun callAndPollRecord(): SequenceRecord {
        return callAndPollRecord(*defaultDesiredRecordStatus)
    }

    fun callAndPollRecord(vararg desiredRecordStatus: RecordStatus): SequenceRecord {
        val response = this.call()
        return RecordPoller(
            this.localRestClient, null, response.data.sequenceId
        ).poll(*desiredRecordStatus)
    }

    fun callAndPollRecordAsync(): CompletableFuture<SequenceRecord> {
        return callAndPollRecordAsync(*defaultDesiredRecordStatus)
    }

    fun callAndPollRecordAsync(vararg desiredRecordStatus: RecordStatus): CompletableFuture<SequenceRecord> {
        return this.callAsync().thenApplyAsync { response ->
            RecordPoller(this.localRestClient, null, response.getData().getSequenceId())
        }.thenComposeAsync { it.pollAsync(*desiredRecordStatus) }
    }

    companion object {
        private val defaultDesiredRecordStatus = arrayOf(
            RecordStatus.SubmittedToReddio,
            RecordStatus.AcceptedByReddio,
            RecordStatus.FailedOnReddio,
        )

        @JvmStatic
        fun build(
            localRestClient: ReddioRestClient, multiTransferMessage: MultiTransferMessage
        ): ReddioMultiTransferApi {
            return ReddioMultiTransferApi(localRestClient, multiTransferMessage)
        }

        @JvmStatic
        fun multiTransfer(
            localRestClient: ReddioRestClient, transfers: List<ReddioTransferToApi>
        ): ReddioMultiTransferApi {
            return build(localRestClient, MultiTransferMessage.of(transfers.map { it.request }))
        }

        @JvmStatic
        fun multiTransferWithTransfersMessage(
            localRestClient: ReddioRestClient, transfers: List<TransferMessage>
        ): ReddioMultiTransferApi {
            return build(localRestClient, MultiTransferMessage.of(transfers))
        }
    }
}