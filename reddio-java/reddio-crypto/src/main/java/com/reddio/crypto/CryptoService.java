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
}
