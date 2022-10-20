package com.reddio.api.v1

import com.reddio.api.v1.rest.*
import com.reddio.crypto.CryptoService
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import java.lang.RuntimeException
import java.math.BigInteger
import java.time.Duration
import java.time.Instant
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.toKotlinDuration

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
                    privateKey, amount, nonce, senderVaultId, assetId, receiverVaultId, receiver, expirationTimeStamp
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

    override fun getRecord(starkKey: String?, sequenceId: Long): CompletableFuture<ResponseWrapper<GetRecordResponse>> {
        return restClient.getRecord(GetRecordMessage.of(starkKey, sequenceId))
    }

    override fun waitingTransferGetApproved(
        starkKey: String, sequenceId: Long
    ): CompletableFuture<ResponseWrapper<GetRecordResponse>> {
        val neverStop = AtomicBoolean(false);
        return waitingTransferGetApproved(
            starkKey, sequenceId,
            Duration.ofSeconds(5),
            Duration.ofSeconds(60),
            neverStop,
        )
    }

    override fun waitingTransferGetApproved(
        starkKey: String, sequenceId: Long, interval: Duration, deadline: Duration, shouldStop: AtomicBoolean
    ): CompletableFuture<ResponseWrapper<GetRecordResponse>> {
        val startTime = Instant.now()
        return CompletableFuture.supplyAsync {
            val result: ResponseWrapper<GetRecordResponse>
            runBlocking {
                while (true) {
                    if (shouldStop.get()) {
                        throw InterruptedException("cancelled")
                    }
                    if (Thread.interrupted()) {
                        throw InterruptedException("cancelled")
                    }
                    if (startTime.plus(deadline).isBefore(Instant.now())) {
                        throw InterruptedException("timed out")
                    }
                    val record = restClient.getRecord(GetRecordMessage.of(starkKey, sequenceId)).await()
                    if (GetRecordResponse.SequenceRecord.SEQUENCE_STATUS_ACCEPTED == record.getData()[0].getStatus()) {
                        result = record
                        break
                    }
                    if (GetRecordResponse.SequenceRecord.SEQUENCE_STATUS_FAILED == record.getData()[0].getStatus()) {
                        throw RuntimeException("transfer failed")
                    }
                    delay(interval.toKotlinDuration())
                }
                result
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
            BigInteger(privateKey.lowercase().replace("0x", ""), 16), CryptoService.getTransferMsgHash(
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
        fun mainnet(): DefaultReddioClient = DefaultReddioClient(DefaultReddioRestClient.mainnet())

        @JvmStatic
        fun testnet(): DefaultReddioClient = DefaultReddioClient(DefaultReddioRestClient.testnet())

        private data class VaultIds(val senderVaultId: String, val receiverVaultId: String)
    }
}
