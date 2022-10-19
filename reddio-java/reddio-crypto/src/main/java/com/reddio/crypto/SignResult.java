package com.reddio.crypto;

import com.sun.jna.Structure;

import java.nio.ByteBuffer;

@Structure.FieldOrder({"r", "s"})
public class SignResult extends Structure implements Structure.ByValue {
    public ByteBuffer r;
    public ByteBuffer s;

    public SignResult() {
        super();
        r = ByteBuffer.allocateDirect(CryptoService.Reddio.STRING_MAX_SIZE);
        s = ByteBuffer.allocateDirect(CryptoService.Reddio.STRING_MAX_SIZE);
    }
}

