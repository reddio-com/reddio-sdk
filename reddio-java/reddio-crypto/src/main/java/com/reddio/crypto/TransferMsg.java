package com.reddio.crypto;

import com.sun.jna.Structure;

@Structure.FieldOrder({"amount", "nonce", "sender_vault_id", "token", "receiver_vault_id", "receiver_public_key", "expiration_time_stamp", "condition"})
public class TransferMsg extends Structure implements Structure.ByValue {
    /**
     * amount in decimal string
     */
    public String amount;
    /**
     * nonce in decimal string
     */
    public String nonce;
    /**
     * sender_vault_id in decimal string
     */
    public String sender_vault_id;
    /**
     * token in hex string
     */
    public String token;
    /**
     * receiver_vault_id in decimal string
     */
    public String receiver_vault_id;
    /**
     * receiver_public_key in hex string
     */
    public String receiver_public_key;
    /**
     * expiration_time_stamp in decimal string
     */
    public String expiration_time_stamp;
    /**
     * condition in hex string, could be null.
     */
    public String condition;
}
