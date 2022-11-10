package com.reddio.crypto;

import com.sun.jna.Library;
import com.sun.jna.Native;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class CryptoService {
    public interface Reddio extends Library {
        String JNALib = "reddio";
        Reddio instance = Native.load(JNALib, Reddio.class);
        int STRING_MAX_SIZE = 65;

        String explain(int errno);

        int sign(SignDocument document, SignResult ret);

        int get_transfer_msg_hash(TransferMsg msg, ByteBuffer ret);

        int get_limit_order_msg_hash_with_fee(LimitOrderMsgWithFee msg, ByteBuffer ret);

        int get_random_private_key(ByteBuffer ret);

        int get_public_key(String privateKey, ByteBuffer ret);
    }

    public static Signature sign(BigInteger privateKey, BigInteger msgHash, BigInteger seed) {
        SignDocument document = new SignDocument();
        document.private_key = privateKey.toString(16);
        document.msg_hash = msgHash.toString(16);
        if (seed == null) {
            document.seed = null;
        } else {
            document.seed = seed.toString(16);
        }
        SignResult ret = new SignResult();
        int errno = Reddio.instance.sign(document, ret);
        if (errno != 0) {
            throw new ReddioCryptoException(Reddio.instance.explain(errno));
        }
        return new Signature(StandardCharsets.UTF_8.decode(ret.r).toString().trim(), StandardCharsets.UTF_8.decode(ret.s).toString().trim());
    }

    public static BigInteger getTransferMsgHash(
            long amount,
            long nonce,
            long senderVaultId,
            BigInteger token,
            long receiverVaultId,
            BigInteger receiverPublicKey,
            long expirationTimestamp,
            BigInteger condition
    ) {
        TransferMsg msg = new TransferMsg();
        msg.amount = String.valueOf(amount);
        msg.nonce = String.valueOf(nonce);
        msg.sender_vault_id = String.valueOf(senderVaultId);
        msg.token = token.toString(16);
        msg.receiver_vault_id = String.valueOf(receiverVaultId);
        msg.receiver_public_key = receiverPublicKey.toString(16);
        msg.expiration_time_stamp = String.valueOf(expirationTimestamp);
        if (condition == null) {
            msg.condition = null;
        } else {
            msg.condition = condition.toString(16);
        }
        ByteBuffer ret = ByteBuffer.allocateDirect(Reddio.STRING_MAX_SIZE);
        int errno = Reddio.instance.get_transfer_msg_hash(msg, ret);
        if (errno != 0) {
            throw new ReddioCryptoException(Reddio.instance.explain(errno));
        }
        return new BigInteger(StandardCharsets.UTF_8.decode(ret).toString().trim(), 16);
    }

    public static BigInteger getLimitOrderMsgHashWithFee(
            long vaultSell,
            long vaultBuy,
            long amountSell,
            long amountBuy,
            BigInteger token_sell,
            BigInteger token_buy,
            long nonce,
            long expirationTimestamp,
            BigInteger feeToken,
            long feeVaultId,
            long feeLimit
    ) {
        LimitOrderMsgWithFee msg = new LimitOrderMsgWithFee();
        msg.vault_sell = String.valueOf(vaultSell);
        msg.vault_buy = String.valueOf(vaultBuy);
        msg.amount_sell = String.valueOf(amountSell);
        msg.amount_buy = String.valueOf(amountBuy);
        msg.token_sell = token_sell.toString(16);
        msg.token_buy = token_buy.toString(16);
        msg.nonce = String.valueOf(nonce);
        msg.expiration_time_stamp = String.valueOf(expirationTimestamp);
        msg.fee_token = feeToken.toString(16);
        msg.fee_vault_id = String.valueOf(feeVaultId);
        msg.fee_limit = String.valueOf(feeLimit);
        ByteBuffer ret = ByteBuffer.allocateDirect(Reddio.STRING_MAX_SIZE);
        int errno = Reddio.instance.get_limit_order_msg_hash_with_fee(msg, ret);
        if (errno != 0) {
            throw new ReddioCryptoException(Reddio.instance.explain(errno));
        }
        return new BigInteger(StandardCharsets.UTF_8.decode(ret).toString().trim(), 16);
    }

    public static BigInteger getPublicKey(BigInteger privateKey) {
        ByteBuffer ret = ByteBuffer.allocateDirect(Reddio.STRING_MAX_SIZE);
        int errno = Reddio.instance.get_public_key(privateKey.toString(16), ret);
        if (errno != 0) {
            throw new ReddioCryptoException(Reddio.instance.explain(errno));
        }
        return new BigInteger(StandardCharsets.UTF_8.decode(ret).toString().trim(), 16);
    }
    public static BigInteger getRandomPrivateKey() {
        ByteBuffer ret = ByteBuffer.allocateDirect(Reddio.STRING_MAX_SIZE);
        int errno = Reddio.instance.get_random_private_key(ret);
        if (errno != 0) {
            throw new ReddioCryptoException(Reddio.instance.explain(errno));
        }
        return new BigInteger(StandardCharsets.UTF_8.decode(ret).toString().trim(), 16);
    }
}
