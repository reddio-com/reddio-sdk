package com.reddio.crypto;

import com.sun.jna.Library;
import com.sun.jna.Native;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class CryptoService {
    public interface Reddio extends Library {
        String JNALib = "reddio";
        Reddio instance = Native.load(JNALib, Reddio.class);
        int STRING_MAX_SIZE = 65;

        String explain(int errno);

        int sign(SignDocument document, SignResult ret);
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
            throw new RuntimeException(Reddio.instance.explain(errno));
        }
        return new Signature(StandardCharsets.UTF_8.decode(ret.r).toString().trim(), StandardCharsets.UTF_8.decode(ret.s).toString().trim());
    }
}
