package com.reddio.api.v1

import com.reddio.api.v1.rest.*
import com.reddio.crypto.CryptoService
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import java.math.BigInteger
import java.util.concurrent.CompletableFuture

class DefaultReddioClient(private val restClient: ReddioRestClient) : ReddioClient {

    override fun transfer(
        starkKey: String,
        privateKey: String,
        amount: String,
        contractAddress: String,
        tokenId: String,
        type: String,
        receiver: String,
        expirationTimeStamp: Long
    ): CompletableFuture<ResponseWrapper<TransferResponse>> {
        return CompletableFuture.supplyAsync {
            runBlocking {
                val assetId = getAssetId(contractAddress, tokenId, type)
                val vaultsIds = getVaultsIds(assetId, starkKey, receiver)
                val senderVaultId = vaultsIds.senderVaultId
                val receiverVaultId = vaultsIds.receiverVaultId
                val nonce = restClient.getNonce(GetNonceMessage.of(starkKey)).await().getData().getNonce()
                val signature = signTransferMessage(
                    privateKey,
                    amount,
                    nonce,
                    senderVaultId,
                    assetId,
                    receiverVaultId,
                    receiver,
                    expirationTimeStamp
                )
                restClient.transfer(
                    TransferMessage.of(
                        assetId,
                        starkKey,
                        amount,
                        nonce,
                        senderVaultId,
                        receiver,
                        receiverVaultId,
                        expirationTimeStamp,
                        signature
                    )
                ).await()
            }
        }
    }

    private suspend fun getAssetId(
        contractAddress: String,
        tokenId: String,
        type: String,
    ): String {
        val result = restClient.getAssetId(GetAssetIdMessage.of(contractAddress, type, tokenId)).await()
        return result.getData().getAssetId()
    }

    private suspend fun getVaultsIds(assetId: String, starkKey: String, receiver: String): VaultIds {
        val result = restClient.getVaultId(GetVaultIdMessage.of(assetId, listOf(starkKey, receiver))).await()
        return VaultIds(result.getData().vaultIds[0], result.getData().vaultIds[1])
    }

    internal fun signTransferMessage(
        privateKey: String,
        amount: String,
        nonce: Long,
        senderVaultId: String,
        token: String,
        receiverVaultId: String,
        receiverPublicKey: String,
        expirationTimestamp: Long = 4194303L,
    ): Signature {
        val result = CryptoService.sign(
            BigInteger(privateKey.lowercase().replace("0x", ""), 16),
            CryptoService.getTransferMsgHash(
                amount.toLong(),
                nonce,
                senderVaultId.toLong(),
                BigInteger(token.lowercase().replace("0x", ""), 16),
                receiverVaultId.toLong(),
                BigInteger(receiverPublicKey.lowercase().replace("0x", ""), 16),
                expirationTimestamp,
                null
            ), null
        )
        return Signature.of("0x${result.r}", "0x${result.s}")
    }


    companion object {
        @JvmStatic
        fun mainnet(): DefaultReddioClient =
            DefaultReddioClient(DefaultReddioRestClient.mainnet())

        @JvmStatic
        fun testnet(): DefaultReddioClient =
            DefaultReddioClient(DefaultReddioRestClient.testnet())

        private data class VaultIds(val senderVaultId: String, val receiverVaultId: String)
    }
}
