package com.reddio.crypto;

import com.sun.jna.Structure;

@Structure.FieldOrder({
        "vault_sell",
        "vault_buy",
        "amount_sell",
        "amount_buy",
        "token_sell",
        "token_buy",
        "nonce",
        "expiration_time_stamp",
        "fee_token",
        "fee_vault_id",
        "fee_limit"
})
public class LimitOrderMsgWithFee extends Structure implements Structure.ByValue {
    public String vault_sell;
    public String vault_buy;
    public String amount_sell;
    public String amount_buy;
    public String token_sell;
    public String token_buy;
    public String nonce;
    public String expiration_time_stamp;
    public String fee_token;
    public String fee_vault_id;
    public String fee_limit;
}
