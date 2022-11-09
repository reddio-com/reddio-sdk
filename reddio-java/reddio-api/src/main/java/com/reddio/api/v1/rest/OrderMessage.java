package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class OrderMessage {
    public static final Long DIRECTION_ASK = 0L;
    public static final Long DIRECTION_BID = 1L;

    @JsonProperty("amount")
    public String Amount;
    @JsonProperty("amount_buy")
    public String AmountBuy;
    @JsonProperty("amount_sell")
    public String AmountSell;
    @JsonProperty("token_buy")
    public String TokenBuy;
    @JsonProperty("token_sell")
    public String TokenSell;
    @JsonProperty("base_token")
    public String BaseToken;
    @JsonProperty("vault_id_buy")
    public String VaultIdBuy;
    @JsonProperty("vault_id_sell")
    public String VaultIdSell;
    @JsonProperty("expiration_timestamp")
    public Long ExpirationTimestamp;
    @JsonProperty("nonce")
    public Long Nonce;
    @JsonProperty("signature")
    public Signature signature;
    @JsonProperty("account_id")
    public String AccountId;
    @JsonProperty("direction")
    public Long Direction;
    @JsonProperty("fee_info")
    public FeeInfo feeInfo;
}
