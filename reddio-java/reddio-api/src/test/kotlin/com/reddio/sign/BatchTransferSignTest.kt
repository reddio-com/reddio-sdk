package com.reddio.sign

import com.reddio.api.v1.StarkExSigner
import com.reddio.api.v1.rest.*
import org.junit.Test
import java.util.Arrays

class BatchTransferSignTest {
    @Test
    fun roundtripWithBaseTokenTransferSeqId() {
        val starkPrivateKey = "6106fd77a881c5bffabc35b4fa827250de8b2a9d1b84ce64353c1774c36168"
        val starkKey = "0x2408a415d92f4921ac099c3eac16eb1fd3d175a2334c3a007e4d3538c8aaeb6"
        val restClient = DefaultReddioRestClient.testnet()
        val assetId = restClient.getAssetId(GetAssetIdMessage.of("ETH", "ETH", "", null)).join().data.assetId
        val vaultIds =
            restClient.getVaultId(GetVaultIdMessage.of(assetId, listOf(starkKey, "0x0"))).join().data.vaultIds
        val batchTransferMessage = BatchTransferMessage.of(
            listOf(
                BatchTransferItem.of(
                    assetId,
                    starkKey,
                    "10000",
                    114515L,
                    vaultIds[0],
                    "0x0",
                    vaultIds[1],
                    4194303L,
                    null
                )
            ),
            starkKey,
            114514L,
            null,
            1919810L
        )
        val signer = StarkExSigner.buildWithPrivateKey(starkPrivateKey)
        val publicKey = signer.getStarkKey()
        for (item in batchTransferMessage.transfers) {
            val signature = signer.signTransferMessage(
                item.amount,
                item.nonce,
                item.vaultId,
                item.assetId,
                item.receiverVaultId,
                item.receiver,
            )
            item.signature = Signature.of(signature.r, signature.s, publicKey)
        }
        val signature = BatchTransferSign.sign(starkPrivateKey, batchTransferMessage)
        BatchTransferSign.verify(publicKey, batchTransferMessage, signature.r, signature.s)
    }

    @Test
    fun roundtripWithoutBaseTokenTransferSeqId() {
        val starkPrivateKey = "6106fd77a881c5bffabc35b4fa827250de8b2a9d1b84ce64353c1774c36168"
        val starkKey = "0x2408a415d92f4921ac099c3eac16eb1fd3d175a2334c3a007e4d3538c8aaeb6"
        val restClient = DefaultReddioRestClient.testnet()
        val assetId = restClient.getAssetId(GetAssetIdMessage.of("ETH", "ETH", "", null)).join().data.assetId
        val vaultIds =
            restClient.getVaultId(GetVaultIdMessage.of(assetId, listOf(starkKey, "0x0"))).join().data.vaultIds
        val batchTransferMessage = BatchTransferMessage.of(
            listOf(
                BatchTransferItem.of(
                    assetId,
                    starkKey,
                    "10000",
                    114515L,
                    vaultIds[0],
                    "0x0",
                    vaultIds[1],
                    4194303L,
                    null
                )
            ),
            starkKey,
            114514L,
            null,
            null
        )
        val signer = StarkExSigner.buildWithPrivateKey(starkPrivateKey)
        val publicKey = signer.getStarkKey()
        for (item in batchTransferMessage.transfers) {
            val signature = signer.signTransferMessage(
                item.amount,
                item.nonce,
                item.vaultId,
                item.assetId,
                item.receiverVaultId,
                item.receiver,
            )
            item.signature = Signature.of(signature.r, signature.s, publicKey)
        }
        val signature = BatchTransferSign.sign(starkPrivateKey, batchTransferMessage)
        BatchTransferSign.verify(publicKey, batchTransferMessage, signature.r, signature.s)
    }


}