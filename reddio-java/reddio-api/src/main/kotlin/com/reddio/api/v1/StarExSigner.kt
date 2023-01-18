package com.reddio.api.v1

import com.reddio.api.v1.rest.Signature
import com.reddio.crypto.CryptoService
import java.math.BigInteger

class StarExSigner(private val privateKey: String) {
    fun signTransferMessage(
        amount: String,
        nonce: Long,
        senderVaultId: String,
        token: String,
        receiverVaultId: String,
        receiverPublicKey: String,
        expirationTimestamp: Long = 4194303L,
    ): Signature {
        val result = CryptoService.sign(
            BigInteger(privateKey.toLowerCase().replace("0x", ""), 16), CryptoService.getTransferMsgHash(
                amount.toLong(),
                nonce,
                senderVaultId.toLong(),
                BigInteger(token.toLowerCase().replace("0x", ""), 16),
                receiverVaultId.toLong(),
                BigInteger(receiverPublicKey.toLowerCase().replace("0x", ""), 16),
                expirationTimestamp,
                null
            ), null
        )
        return Signature.of("0x${result.r}", "0x${result.s}")
    }

    fun signOrderMsgWithFee(
        vaultIdSell: String,
        vaultIdBuy: String,
        amountSell: String,
        amountBuy: String,
        tokenSell: String,
        tokenBuy: String,
        nonce: Long,
        expirationTimestamp: Long = 4194303L,
        feeToken: String,
        feeSourceVaultId: Long,
        feeLimit: Long,
    ): Signature {
        val hash = CryptoService.getLimitOrderMsgHashWithFee(
            vaultIdSell.toLong(),
            vaultIdBuy.toLong(),
            amountSell.toLong(),
            amountBuy.toLong(),
            BigInteger(tokenSell.toLowerCase().replace("0x", ""), 16),
            BigInteger(tokenBuy.toLowerCase().replace("0x", ""), 16),
            nonce,
            expirationTimestamp,
            BigInteger(feeToken.toLowerCase().replace("0x", ""), 16),
            feeSourceVaultId,
            feeLimit
        )
        val result = CryptoService.sign(BigInteger(privateKey.toLowerCase().replace("0x", ""), 16), hash, null);
        return Signature.of("0x${result.r}", "0x${result.s}")
    }

    fun signCancelOrderMsg(
        orderId: Long
    ): Signature {
        val hash = CryptoService.getCancelOrderMsgHash(orderId)
        val result = CryptoService.sign(BigInteger(privateKey.toLowerCase().replace("0x", ""), 16), hash, null);
        return Signature.of("0x${result.r}", "0x${result.s}")
    }

    companion object {
        @JvmStatic
        fun buildWithPrivateKey(privateKey: String): StarExSigner {
            return StarExSigner(privateKey)
        }
    }
}