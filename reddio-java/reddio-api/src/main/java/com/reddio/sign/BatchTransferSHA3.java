package com.reddio.sign;

import com.fasterxml.jackson.core.io.NumberInput;
import com.reddio.api.v1.rest.BatchTransferItem;
import com.reddio.api.v1.rest.BatchTransferMessage;
import com.reddio.api.v1.rest.Signature;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * <p>
 *
 * @author strrl
 * @date 2023/4/18 16:01
 */
public class BatchTransferSHA3 {

    // 8 MiB is enough for about signing maximum 60,000~ transfer one batch.
    // Maybe use ByteArrayOutputStream in the future if required.
    public static final int BYTEBUFFER_SIZE = 8 * 1024 * 1024;

    public static BigInteger getBatchTransferHash(BatchTransferMessage message) {
        ByteBuffer buffer = ByteBuffer.allocate(BYTEBUFFER_SIZE);

        for (BatchTransferItem item : message.getTransfers()) {
            // for each transfer item
            Signature signature = Objects.requireNonNull(item.getSignature(), "signature is required for signing batch transfer");
            String r = Objects.requireNonNull(signature.getR(), "r is required for signing batch transfer");
            String s = Objects.requireNonNull(signature.getS(), "s is required for signing batch transfer");
            buffer.put(r.getBytes(StandardCharsets.UTF_8));
            buffer.put(s.getBytes(StandardCharsets.UTF_8));
        }

        // nonce is required
        buffer.put(Numeric.toBytesPadded(BigInteger.valueOf(Objects.requireNonNull(message.getNonce(), "nonce is required for signing batch transfer")), 32));

        // baseTokenTransferSeqId is optional
        if (message.getBaseTokenTransferSeqId() != null) {
            buffer.put(Numeric.toBytesPadded(BigInteger.valueOf(message.getBaseTokenTransferSeqId()), 32));
        }

        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        byte[] hashBytes = Hash.sha3(bytes);
        BigInteger hash = Numeric.toBigInt(hashBytes);
        BigInteger result = hash.mod(PaymentSHA3.CurveOrder);
        return result;
    }
}
