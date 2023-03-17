package com.reddio.api.v1;

import com.reddio.api.v1.rest.Signature;
import org.junit.Assert;
import org.junit.Test;

public class SignerTest {
    @Test
    public void testSignTransferMessage() {
        StarkExSigner signer = StarkExSigner.buildWithPrivateKey("0xa7b68cf2ee72b2a0789914daa8ae928aec21b6b0bf020e394833f4c732d99d");
        Signature signature = signer.signTransferMessage("1", 59, "23400424", "0x1c9c7dee2be5015eddff167dda36f319f6bfcd4bd41f9d5cb49fe164bc58cb1", "23400425", "0x7865bc66b610d6196a7cbeb9bf066c64984f6f06b5ed3b6f5788bd9a6cb099c", 4194303L);
        Assert.assertEquals("0x7b191774b10a208331d716ab4fe0ecd24b430d0142bdd123a14d243abf626b1", signature.getR());
        Assert.assertEquals("0x21f3a32d5779668d66af7f9f161d90afb3765c3a6326b6397f73ea346f94e5d", signature.getS());
    }
}
