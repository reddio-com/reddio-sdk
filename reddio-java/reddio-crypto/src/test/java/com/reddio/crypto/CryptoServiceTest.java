package com.reddio.crypto;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class CryptoServiceTest {
    @Test
    public void testSign() {
        BigInteger privateKey = new BigInteger("3c1e9550e66958296d11b60f8e8e7a7ad990d07fa65d5f7652c4a6c87d4e3cc", 16);
        BigInteger msgHash = new BigInteger("397e76d1667c4454bfb83514e120583af836f8e32a516765497823eabe16a3f", 16);
        Signature signature = CryptoService.sign(privateKey, msgHash, null);
        Assert.assertEquals("173fd03d8b008ee7432977ac27d1e9d1a1f6c98b1a2f05fa84a21c84c44e882", signature.r);
        Assert.assertEquals("4b6d75385aed025aa222f28a0adc6d58db78ff17e51c3f59e259b131cd5a1cc", signature.s);
    }

    @Test
    public void testVerify() {
        boolean result = CryptoService.verify(CryptoService.getPublicKey(
                        new BigInteger("3c1e9550e66958296d11b60f8e8e7a7ad990d07fa65d5f7652c4a6c87d4e3cc", 16)),
                new BigInteger("397e76d1667c4454bfb83514e120583af836f8e32a516765497823eabe16a3f", 16),
                new Signature("173fd03d8b008ee7432977ac27d1e9d1a1f6c98b1a2f05fa84a21c84c44e882", "4b6d75385aed025aa222f28a0adc6d58db78ff17e51c3f59e259b131cd5a1cc"));
        Assert.assertEquals(true, result);
    }

    @Test

    public void testGetTransferMsgHash() {
        BigInteger hash = CryptoService.getTransferMsgHash(
                2154549703648910716L,
                1L,
                34L,
                new BigInteger("3003a65651d3b9fb2eff934a4416db301afd112a8492aaf8d7297fc87dcd9f4", 16),
                21L
                , new BigInteger("5fa3383597691ea9d827a79e1a4f0f7949435ced18ca9619de8ab97e661020", 16),
                438953L,
                null
        );
        Assert.assertEquals("6366b00c218fb4c8a8b142ca482145e8513c78e00faa0de76298ba14fc37ae7", hash.toString(16));
    }

    @Test
    public void testGetLimitOrderMsgHashWithFee() {
        BigInteger hash = CryptoService.getLimitOrderMsgHashWithFee(
                21,
                27,
                2154686749748910716L,
                1470242115489520459L,
                new BigInteger("5fa3383597691ea9d827a79e1a4f0f7989c35ced18ca9619de8ab97e661020", 16),
                new BigInteger("774961c824a3b0fb3d2965f01471c9c7734bf8dbde659e0c08dca2ef18d56a", 16),
                0,
                438953,
                new BigInteger("70bf591713d7cb7150523cf64add8d49fa6b61036bba9f596bd2af8e3bb86f9", 16),

                593128169,
                7
        );
        Assert.assertEquals("2a6c0382404920ebd73c1cbc319cd38974e7e255e00394345e652b0ce2cefbd", hash.toString(16));
    }

    @Test
    public void testGetCancelOrderMsgHash() {
        BigInteger hash = CryptoService.getCancelOrderMsgHash(233);
        Assert.assertEquals("2d97ce4376a8cec568b243857eafd329fd90afca2437a3368f34884eed53fd3", hash.toString(16));
    }

    @Test
    public void testRandomPrivateKey() {
        BigInteger privateKey = CryptoService.getRandomPrivateKey();
        BigInteger publicKey = CryptoService.getPublicKey(privateKey);
        System.out.println("privateKey: " + privateKey.toString(16));
        System.out.println("publicKey: " + publicKey.toString(16));
    }

    @Test
    public void testGetPrivateKeyFromEthSignature() {
        BigInteger ethSignature = new BigInteger("21fbf0696d5e0aa2ef41a2b4ffb623bcaf070461d61cf7251c74161f82fec3a4370854bc0a34b3ab487c1bc021cd318c734c51ae29374f2beb0e6f2dd49b4bf41c", 16);
        BigInteger starkPrivateKey = CryptoService.getPrivateKeyFromEthSignature(ethSignature);
        Assert.assertEquals("766f11e90cd7c7b43085b56da35c781f8c067ac0d578eabdceebc4886435bda", starkPrivateKey.toString(16));
    }
}