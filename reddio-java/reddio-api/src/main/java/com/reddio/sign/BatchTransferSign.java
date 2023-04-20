package com.reddio.sign;

import com.reddio.api.v1.rest.BatchTransferMessage;
import com.reddio.api.v1.rest.Payment;
import com.reddio.api.v1.rest.Signature;
import com.reddio.crypto.CryptoService;

import java.math.BigInteger;

/**
 * <p>
 *
 * @author strrl
 * @date 2023/4/18 16:01
 */
public class BatchTransferSign {
    public static Signature sign(String privateKeyString, BatchTransferMessage message) {
        BigInteger hash = BatchTransferSHA3.getBatchTransferHash(message);
        BigInteger privateKey = new BigInteger(privateKeyString.replace("0x", ""), 16);
        com.reddio.crypto.Signature signature = CryptoService.sign(privateKey, hash, null);
        BigInteger publicKey = CryptoService.getPublicKey(privateKey);
        return Signature.of(
                "0x" + signature.getR(),
                "0x" + signature.getS(),
                "0x" + publicKey.toString(16)
        );
    }

    public static boolean verify(String publicKey, BatchTransferMessage message, String r, String s) {
        BigInteger hash = BatchTransferSHA3.getBatchTransferHash(message);
        return CryptoService.verify(
                new BigInteger(publicKey.replace("0x", ""), 16),
                hash,
                new com.reddio.crypto.Signature(
                        r,
                        s
                )
        );
    }
}
