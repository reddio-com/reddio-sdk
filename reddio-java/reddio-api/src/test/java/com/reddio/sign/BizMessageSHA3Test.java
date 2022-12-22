package com.reddio.sign;

import com.reddio.api.v1.rest.BizMessage;
import com.reddio.crypto.CryptoService;
import com.reddio.crypto.Signature;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class BizMessageSHA3Test {

    @Test
    public void testGetBizMessageHash1() {
        BizMessage bizMessage = BizMessage.of(
                BizMessage.PayInfo.of(0),
                ""
        );
        long nonce = 0;
        assertEquals("a864605cfa7f106dec7ce9fbffa6230e26eddd92375bbe268df99967c27587", BizMessageSHA3.getBizMessageHash(bizMessage, nonce).toString(16));
    }

    @Test
    public void testGetBizMessageHash2() {
        BizMessage bizMessage = BizMessage.of(
                BizMessage.PayInfo.of(233),
                "OK"
        );
        long nonce = 2333;
        assertEquals("7aab5b275be161d65ed7232cae8fb6da5d0185f340d5cb1a47ec34132d87f4a", BizMessageSHA3.getBizMessageHash(bizMessage, nonce).toString(16));
    }

    @Test
    public void testSignBizMessageHash() {
        BizMessage bizMessage = BizMessage.of(
                BizMessage.PayInfo.of(233),
                "OK"
        );
        long nonce = 2333;
        BigInteger hash = BizMessageSHA3.getBizMessageHash(bizMessage, nonce);

        Signature sign = CryptoService.sign(new BigInteger("3c1e9550e66958296d11b60f8e8e7a7ad990d07fa65d5f7652c4a6c87d4e3cc", 16), hash, null);
        assertEquals("53600cc40bda73aa80d24522bd7f026790d431d2a66268119fda738939ff2e1", sign.r);
        assertEquals("796414771b9cddc8d1502bea8a4594673256bc9b0357e51ffad0e76c93b6e37", sign.s);
    }
}