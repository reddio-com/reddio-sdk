package com.reddio.sign;

import com.reddio.api.v1.rest.Payment;
import com.reddio.api.v1.rest.Signature;
import com.reddio.crypto.CryptoService;

import java.math.BigInteger;

public class PaymentSign {
    public static Signature sign(String privateKeyString, String orderId, long nonce) {
        Payment payment = Payment.of(Payment.PayInfo.of(orderId), "");
        BigInteger hash = PaymentSHA3.getPaymentHash(payment, nonce);
        BigInteger privateKey = new BigInteger(privateKeyString.replace("0x", ""), 16);
        com.reddio.crypto.Signature signature = CryptoService.sign(privateKey, hash, null);
        BigInteger publicKey = CryptoService.getPublicKey(privateKey);
        return Signature.of(
                "0x" + signature.getR(),
                "0x" + signature.getS(),
                "0x" + publicKey.toString(16)
        );
    }

    public static boolean verify(
            String orderId,
            String state,
            long nonce,
            String publicKey,
            String r,
            String s
    ) {
        Payment payment = Payment.of(Payment.PayInfo.of(orderId), state);
        return verify(payment, nonce, publicKey, r, s);
    }

    public static boolean verify(
            Payment payment,
            long nonce,
            String publicKey,
            String r,
            String s
    ) {
        BigInteger hash = PaymentSHA3.getPaymentHash(payment, nonce);
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
