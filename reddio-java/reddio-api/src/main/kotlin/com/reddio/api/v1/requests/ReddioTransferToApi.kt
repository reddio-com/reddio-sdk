package com.reddio.api.v1.requests

import com.reddio.api.v1.QuantizedHelper
import com.reddio.api.v1.ReddioClient
import com.reddio.api.v1.StarkExSigner
import com.reddio.api.v1.requests.polling.RecordPoller
import com.reddio.api.v1.rest.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture

class ReddioTransferToApi private constructor(
    private val localRestClient: ReddioRestClient, private val request: TransferMessage
) : SignedReddioApiRequest<TransferMessage, ResponseWrapper<TransferResponse>> {
    override fun call(): ResponseWrapper<TransferResponse> {
        return this.callAsync().join()
    }

    override fun callAsync(): CompletableFuture<ResponseWrapper<TransferResponse>> {
        return this.localRestClient.transfer(this.request)
    }

    override fun getRequest(): TransferMessage {
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

    companion object {
        @JvmStatic
        fun build(localRestClient: ReddioRestClient, request: TransferMessage): ReddioTransferToApi {
            return ReddioTransferToApi(localRestClient, request)
        }

        /**
         * Build the request for transfer asset.
         *
         * @param restClient the reddio rest client
         * @param starkPrivateKey the stark private key for signing the request
         * @param amount the amount of asset to transfer, use 1 for ERC721
         * @param contractAddress the contract address of the asset to transfer
         * @param tokenId the token id of the asset to transfer, use empty string for ETH and ERC20
         * @param tokenType the token type of the asset to transfer, use literal "ERC20" for ERC20 and "ERC721" for ERC721
         * @param receiver the receiver's stark key
         * @param expirationTimestamp the expiration timestamp of the request in seconds, max value is 4194303L
         */
        @JvmStatic
        fun transfer(
            restClient: ReddioRestClient,
            starkPrivateKey: String,
            amount: String,
            contractAddress: String,
            tokenId: String,
            tokenType: String,
            receiver: String,
            expirationTimestamp: Long,
        ): ReddioTransferToApi {

            val quantizedHelper = QuantizedHelper(restClient)
            val starkExSigner = StarkExSigner(starkPrivateKey)
            val starkKey = starkExSigner.getStarkKey()

            val message = runBlocking {
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
                TransferMessage.of(
                    assetId,
                    starkKey,
                    quantizedAmount,
                    nonce,
                    vaultsIds.senderVaultId,
                    receiver,
                    vaultsIds.receiverVaultId,
                    expirationTimestamp,
                    signature
                )
            }
            return build(restClient, message)
        }

        @JvmStatic
        fun transferETH(
            restClient: ReddioRestClient,
            starkPrivateKey: String,
            amount: String,
            receiver: String,
            expirationTimestamp: Long,
        ): ReddioTransferToApi {
            return transfer(
                restClient,
                starkPrivateKey,
                amount,
                "ETH",
                "",
                ReddioClient.TOKEN_TYPE_ETH,
                receiver,
                expirationTimestamp
            )
        }

        @JvmStatic
        fun transferERC20(
            restClient: ReddioRestClient,
            starkPrivateKey: String,
            amount: String,
            contractAddress: String,
            receiver: String,
            expirationTimestamp: Long,
        ): ReddioTransferToApi {
            return transfer(
                restClient,
                starkPrivateKey,
                amount,
                contractAddress,
                "",
                ReddioClient.TOKEN_TYPE_ERC20,
                receiver,
                expirationTimestamp
            )
        }

        @JvmStatic
        fun transferERC721(
            restClient: ReddioRestClient,
            starkPrivateKey: String,
            contractAddress: String,
            tokenId: String,
            receiver: String,
            expirationTimestamp: Long
        ): ReddioTransferToApi {
            return transfer(
                restClient,
                starkPrivateKey,
                "1",
                contractAddress,
                tokenId,
                ReddioClient.TOKEN_TYPE_ERC721,
                receiver,
                expirationTimestamp
            )
        }
    }
}