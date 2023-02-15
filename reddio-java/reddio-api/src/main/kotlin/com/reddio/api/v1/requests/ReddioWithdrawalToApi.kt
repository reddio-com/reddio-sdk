package com.reddio.api.v1.requests

import com.reddio.api.v1.QuantizedHelper
import com.reddio.api.v1.ReddioClient
import com.reddio.api.v1.StarkExSigner
import com.reddio.api.v1.rest.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture

class ReddioWithdrawalToApi private constructor(
    private val localRestClient: ReddioRestClient, private val request: WithdrawalToMessage
) : SignedReddioApiRequest<WithdrawalToMessage, ResponseWrapper<WithdrawalToResponse>> {
    override fun call(): ResponseWrapper<WithdrawalToResponse> {
        return this.callAsync().get()
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

    companion object {
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

        @JvmStatic
        fun withdrawalERC721(
            restClient: ReddioRestClient,
            starkPrivateKey: String,
            contractAddress: String,
            tokenId: String,
            receiver: String,
            expirationTimestamp: Long
        ): ReddioWithdrawalToApi {
            return withdrawal(
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