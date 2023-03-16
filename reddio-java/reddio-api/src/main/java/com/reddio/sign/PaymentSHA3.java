package com.reddio.sign;

import com.reddio.api.v1.rest.Payment;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PaymentSHA3 {
    public static final BigInteger CurveOrder = new BigInteger("3618502788666131213697322783095070105526743751716087489154079457884512865583");

    public static final int BYTEBUFFER_SIZE = 4096;

    public static BigInteger getPaymentHash(Payment message, long nonce) {
        byte[] orderIdBytes = String.format("%s", message.getPayInfo().getOrderId()).getBytes(StandardCharsets.UTF_8);
        byte[] stateBytes32 = message.getState().getBytes(StandardCharsets.UTF_8);
        byte[] nonceBytes = Numeric.toBytesPadded(BigInteger.valueOf(nonce), 32);

        ByteBuffer buffer = ByteBuffer.allocate(BYTEBUFFER_SIZE);
        buffer.put(orderIdBytes);
        buffer.put(stateBytes32);
        buffer.put(nonceBytes);
        buffer.flip();

        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        byte[] hashBytes = Hash.sha3(bytes);
        BigInteger hash = Numeric.toBigInt(hashBytes);
        BigInteger result = hash.mod(CurveOrder);
        return result;
    }
}
