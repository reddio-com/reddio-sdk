package com.reddio.crypto;

import com.sun.jna.Structure;

@Structure.FieldOrder({"private_key", "msg_hash", "seed"})
public class SignDocument extends Structure implements Structure.ByValue{
    public String private_key;
    public String msg_hash;
    public String seed;
}
