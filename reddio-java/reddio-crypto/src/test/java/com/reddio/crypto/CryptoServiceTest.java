package com.reddio.crypto;

import junit.framework.TestCase;
import org.junit.Assert;

import java.math.BigInteger;

public class CryptoServiceTest extends TestCase {

    public void testSign() {
        BigInteger privateKey = new BigInteger("3c1e9550e66958296d11b60f8e8e7a7ad990d07fa65d5f7652c4a6c87d4e3cc", 16);
        BigInteger msgHash = new BigInteger("397e76d1667c4454bfb83514e120583af836f8e32a516765497823eabe16a3f", 16);
        Signature signature = CryptoService.sign(privateKey, msgHash, null);
        Assert.assertEquals("173fd03d8b008ee7432977ac27d1e9d1a1f6c98b1a2f05fa84a21c84c44e882", signature.r);
        Assert.assertEquals("4b6d75385aed025aa222f28a0adc6d58db78ff17e51c3f59e259b131cd5a1cc", signature.s);
    }
}