package com.reddio.crypto;

import com.sun.jna.Structure;

@Structure.FieldOrder({"public_key", "msg_hash", "r", "s"})
public class VerifySignatureMessage extends Structure implements Structure.ByValue {
    public String public_key;
    public String msg_hash;
    public String r;
    public String s;
}
