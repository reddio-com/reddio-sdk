package com.reddio.api.v1.requests

import com.reddio.api.v1.QuantizedHelper
import com.reddio.api.v1.ReddioClient
import com.reddio.api.v1.StarkExSigner
import com.reddio.api.v1.requests.polling.RecordPoller
import com.reddio.api.v1.rest.*
import com.reddio.sign.BatchTransferSign
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture

class ReddioBatchTransferApi private constructor(
    private val localRestClient: ReddioRestClient, private val request: BatchTransferMessage

) : SignedReddioApiRequest<BatchTransferMessage, ResponseWrapper<BatchTransferResponse>> {
    override fun call(): ResponseWrapper<BatchTransferResponse> {
        return unwrapCompletionException {
            this.callAsync().join()
        }
    }

    override fun callAsync(): CompletableFuture<ResponseWrapper<BatchTransferResponse>> {
        return this.localRestClient.batchTransfer(this.request)
    }

    override fun getRequest(): BatchTransferMessage {
        return this.request
    }

    override fun getSignature(): Signature {
        return this.request.getSignature()
    }

    /**
     * Call the request and poll the record until it reaches one of the desired status, which are SubmittedToReddio, AcceptedByReddio, FailedOnReddio by default.
     */
    fun callAndPollRecord(): SequenceRecord {
        return callAndPollRecord(*defaultDesiredRecordStatus)
    }

    /**
     * Call the request and poll the record until it reaches one of the desired status.
     */
    fun callAndPollRecord(vararg desiredRecordStatus: RecordStatus): SequenceRecord {
        val response = this.call()
        return RecordPoller(
            this.localRestClient, null, response.data.sequenceId
        ).poll(*desiredRecordStatus)
    }

    /**
     * Call the request and poll the record until it reaches one of the desired status asynchronously, which are SubmittedToReddio, AcceptedByReddio, FailedOnReddio by default.
     */
    fun callAndPollRecordAsync(): CompletableFuture<SequenceRecord> {
        return callAndPollRecordAsync(*defaultDesiredRecordStatus)
    }

    /**
     * Call the request and poll the record until it reaches one of the desired status asynchronously.
     */
    fun callAndPollRecordAsync(vararg desiredRecordStatus: RecordStatus): CompletableFuture<SequenceRecord> {
        return this.callAsync().thenApplyAsync { response ->
            RecordPoller(this.localRestClient, null, response.getData().getSequenceId())
        }.thenComposeAsync { it.pollAsync(*desiredRecordStatus) }
    }


    data class TransferItem(
        val amount: String,
        val contractAddress: String,
        val tokenId: String,
        val tokenType: String,
        val receiver: String,
        val expirationTimestamp: Long,
    ) {
        fun asSignedBatchTransferItem(restClient: ReddioRestClient, starkPrivateKey: String): BatchTransferItem {
            val quantizedHelper = QuantizedHelper(restClient)
            val starkExSigner = StarkExSigner(starkPrivateKey)
            val starkKey = starkExSigner.getStarkKey()
            return runBlocking {
                val quantizedAmount = quantizedHelper.quantizedAmount(amount, tokenType, contractAddress).toString()
                val assetId = AssetVaultHelper.getAssetId(restClient, contractAddress, tokenId, tokenType)
                val vaultsIds = AssetVaultHelper.getVaultsIds(restClient, assetId, starkKey, receiver)
                val nonce = restClient.getNonce(GetNonceMessage.of(starkKey)).await().getData().getNonce()
                val signature = starkExSigner.signTransferMessage(
                    quantizedAmount,
                    nonce,
                    vaultsIds.senderVaultId,
                    assetId,
                    vaultsIds.receiverVaultId,
                    receiver,
                    expirationTimestamp
                )
                BatchTransferItem.of(
                    assetId,
                    starkKey,
                    amount,
                    nonce,
                    vaultsIds.senderVaultId,
                    receiver,
                    vaultsIds.receiverVaultId,
                    expirationTimestamp,
                    signature
                )
            }

        }

        companion object {

            /**
             * Build a transfer item in batch transfer for transfer ETH.
             *
             * @param amount the amount of ETH to transfer
             * @param receiver the receiver's stark key
             * @param expirationTimestamp the expiration timestamp of the request in seconds, max value is 4194303L
             */
            @JvmStatic
            fun transferETH(
                amount: String,
                receiver: String,
                expirationTimestamp: Long,
            ): TransferItem {
                return TransferItem(
                    amount, "ETH", "", ReddioClient.TOKEN_TYPE_ETH, receiver, expirationTimestamp
                )
            }

            /**
             * Build a batch transfer item in batch transfer for transfer ERC20.
             *
             * @param amount the amount of ERC20 to transfer
             * @param contractAddress the contract address of the ERC20 to transfer
             * @param receiver the receiver's stark key
             * @param expirationTimestamp the expiration timestamp of the request in seconds, max value is 4194303L
             */
            @JvmStatic
            fun transferERC20(
                amount: String,
                contractAddress: String,
                receiver: String,
                expirationTimestamp: Long,
            ): TransferItem {
                return TransferItem(
                    amount, contractAddress, "", ReddioClient.TOKEN_TYPE_ERC20, receiver, expirationTimestamp
                )
            }

            /**
             * Build a transfer item in batch transfer for transfer ERC721/ERC721M.
             *
             * @param contractAddress the contract address of the ERC721/ERC721M to transfer
             * @param tokenId the token id of the ERC721/ERC721M to transfer
             * @param tokenType the token type of the ERC721/ERC721M to transfer, use [ReddioClient.TOKEN_TYPE_ERC721] for ERC721 and [ReddioClient.TOKEN_TYPE_ERC721M] for ERC721M
             * @param receiver the receiver's stark key
             * @param expirationTimestamp the expiration timestamp of the request in seconds, max value is 4194303L
             */
            @JvmStatic
            fun transferERC721(
                contractAddress: String,
                tokenId: String,
                tokenType: String,
                receiver: String,
                expirationTimestamp: Long
            ): TransferItem {
                return TransferItem(
                    "1", contractAddress, tokenId, tokenType, receiver, expirationTimestamp
                )
            }
        }
    }

    companion object {

        private val defaultDesiredRecordStatus = arrayOf(
            RecordStatus.SubmittedToReddio,
            RecordStatus.AcceptedByReddio,
            RecordStatus.FailedOnReddio,
        )

        @JvmStatic
        fun build(localRestClient: ReddioRestClient, request: BatchTransferMessage): ReddioBatchTransferApi {
            return ReddioBatchTransferApi(localRestClient, request)
        }

        /**
         * Build a batch transfer request for transfer purpose.
         *
         * @param localRestClient the reddio rest client
         * @param senderStarkPrivateKey the sender's stark private key
         * @param transfers the list of transfer items
         */
        @JvmStatic
        fun batchTransfer(
            localRestClient: ReddioRestClient,
            senderStarkPrivateKey: String,
            transfers: List<TransferItem>,
        ): ReddioBatchTransferApi {
            return batchTransfer(localRestClient, senderStarkPrivateKey, transfers, null)
        }

        /**
         * Build a batch transfer request for batch order purpose.
         *
         * @param localRestClient the reddio rest client
         * @param senderStarkPrivateKey the sender's stark private key
         * @param transfers the list of transfer items
         * @param baseTokenTransferSeqId the sequence id of the base token transfer transaction
         */
        @JvmStatic
        fun batchTransfer(
            localRestClient: ReddioRestClient,
            senderStarkPrivateKey: String,
            transfers: List<TransferItem>,
            baseTokenTransferSeqId: Long?,
        ): ReddioBatchTransferApi {
            val signer = StarkExSigner(senderStarkPrivateKey)
            val starkPublicKey = signer.getStarkKey()
            val batchTransferMessage = runBlocking {
                val nonce = localRestClient.getNonce(
                    GetNonceMessage.of(
                        starkPublicKey
                    )
                ).await().data.nonce
                val result = BatchTransferMessage.of(
                    transfers.map {
                        it.asSignedBatchTransferItem(localRestClient, senderStarkPrivateKey)
                    },
                    starkPublicKey,
                    nonce,
                    null,
                    baseTokenTransferSeqId,
                )
                result.signature = BatchTransferSign.sign(senderStarkPrivateKey, result)
                result
            }
            return build(localRestClient, batchTransferMessage)
        }
    }
}