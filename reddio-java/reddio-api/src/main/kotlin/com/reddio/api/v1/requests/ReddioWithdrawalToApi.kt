package com.reddio.api.v1.requests

import com.reddio.ReddioException
import com.reddio.api.v1.QuantizedHelper
import com.reddio.api.v1.ReddioClient
import com.reddio.api.v1.StarkExSigner
import com.reddio.api.v1.requests.polling.RecordPoller
import com.reddio.api.v1.rest.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture

class ReddioWithdrawalToApi private constructor(
    private val localRestClient: ReddioRestClient, private val request: WithdrawalToMessage
) : SignedReddioApiRequest<WithdrawalToMessage, ResponseWrapper<WithdrawalToResponse>> {
    override fun call(): ResponseWrapper<WithdrawalToResponse> {
        return unwrapCompletionException {
            this.callAsync().join()
        }
    }

    override fun callAsync(): CompletableFuture<ResponseWrapper<WithdrawalToResponse>> {
        return this.localRestClient.withdrawalTo(this.request)
    }

    override fun getRequest(): WithdrawalToMessage {
        return request
    }

    override fun getSignature(): Signature {
        return request.getSignature()
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
            this.localRestClient, this.request.getStarkKey(), response.getData().getSequenceId()
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
            RecordPoller(this.localRestClient, this.request.getStarkKey(), response.getData().getSequenceId())
        }.thenComposeAsync { it.pollAsync(*desiredRecordStatus) }
    }

    companion object {

        private val defaultDesiredRecordStatus = arrayOf(
            RecordStatus.SubmittedToReddio,
            RecordStatus.AcceptedByReddio,
            RecordStatus.FailedOnReddio,
        )

        @JvmStatic
        fun build(localRestClient: ReddioRestClient, request: WithdrawalToMessage): ReddioWithdrawalToApi {
            return ReddioWithdrawalToApi(localRestClient, request)
        }

        /**
         * Build the request for withdrawal asset.
         *
         * @param restClient the reddio rest client
         * @param starkPrivateKey the stark private key for signing the request
         * @param amount the amount of asset to withdraw, use 1 for ERC721
         * @param contractAddress the contract address of the asset, use literal "ETH" for ETH
         * @param tokenId the token id of the ERC721, use empty string for ETH and ERC20
         * @param type the type of the asset, use literal "ERC20" for ERC20, "ERC721" for ERC721, "ETH" for ETH
         * @param receiver the eth address to send the asset to
         * @param expirationTimeStamp the timestamp when the request expires in seconds, max value is 4194303L
         */
        fun withdrawal(
            restClient: ReddioRestClient,
            starkPrivateKey: String,
            amount: String,
            contractAddress: String,
            tokenId: String,
            type: String,
            receiver: String,
            expirationTimeStamp: Long
        ): ReddioWithdrawalToApi {
            val quantizedHelper = QuantizedHelper(restClient)
            val starkExSigner = StarkExSigner(starkPrivateKey)
            val starkKey = starkExSigner.getStarkKey()
            val message = runBlocking {
                val quantizedAmount = quantizedHelper.quantizedAmount(amount, type, contractAddress).toString()
                val assetId = AssetVaultHelper.getAssetId(restClient, contractAddress, tokenId, type)
                val vaultsIds = AssetVaultHelper.getVaultsIds(restClient, assetId, starkKey, receiver)

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

                WithdrawalToMessage.of(
                    contractAddress,
                    assetId,
                    starkKey,
                    quantizedAmount,
                    tokenId,
                    nonce,
                    vaultsIds.senderVaultId,
                    receiver,
                    vaultsIds.receiverVaultId,
                    expirationTimeStamp,
                    signature
                )
            }
            return build(restClient, message)
        }

        /**
         * Build the request for withdrawal of ETH.
         *
         * @param restClient the rest client
         * @param starkPrivateKey the stark private key for signing the request
         * @param amount the amount of ETH to withdraw
         * @param receiver the eth address to send the ETH to
         * @param expirationTimestamp the timestamp when the request expires in seconds, max value is 4194303L
         */
        @JvmStatic
        fun withdrawalETH(
            restClient: ReddioRestClient,
            starkPrivateKey: String,
            amount: String,
            receiver: String,
            expirationTimestamp: Long
        ): ReddioWithdrawalToApi {
            return withdrawal(
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

        /**
         * Build the request for withdrawal of ERC20.
         *
         * @param restClient the rest client
         * @param starkPrivateKey the stark private key for signing the request
         * @param amount the amount of ETH to withdraw
         * @param contractAddress the contract address of the ERC20
         * @param receiver the eth address to send the ETH to
         * @param expirationTimestamp the timestamp when the request expires in seconds, max value is 4194303L
         */
        @JvmStatic
        fun withdrawalERC20(
            restClient: ReddioRestClient,
            starkPrivateKey: String,
            amount: String,
            contractAddress: String,
            receiver: String,
            expirationTimestamp: Long
        ): ReddioWithdrawalToApi {
            return withdrawal(
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

        /**
         * Build the request for withdrawal of ERC721/ERC721M.
         *
         * @param restClient the rest client
         * @param starkPrivateKey the stark private key for signing the request
         * @param contractAddress the contract address of the ERC721
         * @param tokenId the token id of the ERC721/ERC721M
         * @param tokenType the token type of the ERC721/ERC721M to withdrawal, use [ReddioClient.TOKEN_TYPE_ERC721] for ERC721 and [ReddioClient.TOKEN_TYPE_ERC721M] for ERC721M
         * @param receiver the eth address to send the ERC721/ERC721M to
         * @param expirationTimestamp the timestamp when the request expires in seconds, max value is 4194303L
         */
        @JvmStatic
        fun withdrawalERC721(
            restClient: ReddioRestClient,
            starkPrivateKey: String,
            contractAddress: String,
            tokenId: String,
            tokenType: String,
            receiver: String,
            expirationTimestamp: Long
        ): ReddioWithdrawalToApi {
            if (ReddioClient.TOKEN_TYPE_ERC721M != tokenType && ReddioClient.TOKEN_TYPE_ERC721 != tokenType) {
                throw ReddioException("tokenType must be ERC721 or ERC721M for ERC721/ERC721M withdrawal")
            }

            return withdrawal(
                restClient,
                starkPrivateKey,
                "1",
                contractAddress,
                tokenId,
                tokenType,
                receiver,
                expirationTimestamp
            )
        }

    }
}