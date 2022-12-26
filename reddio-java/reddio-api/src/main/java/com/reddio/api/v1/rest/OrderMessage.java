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

    public static final String STOP_LIMIT_TIME_IN_FORCE_GTC = "GTC";
    public static final String STOP_LIMIT_TIME_IN_FORCE_IOC = "IOC";
    public static final String STOP_LIMIT_TIME_IN_FORCE_FOK = "FOK";


    @JsonProperty("amount")
    public String amount;
    @JsonProperty("amount_buy")
    public String amountBuy;
    @JsonProperty("amount_sell")
    public String amountSell;
    @JsonProperty("token_buy")
    public String tokenBuy;
    @JsonProperty("token_sell")
    public String tokenSell;
    @JsonProperty("base_token")
    public String baseToken;
    @JsonProperty("quote_token")
    public String quoteToken;
    @JsonProperty("vault_id_buy")
    public String vaultIdBuy;
    @JsonProperty("vault_id_sell")
    public String vaultIdSell;
    @JsonProperty("expiration_timestamp")
    public Long expirationTimestamp;
    @JsonProperty("nonce")
    public Long nonce;
    @JsonProperty("signature")
    public Signature signature;
    @JsonProperty("account_id")
    public String accountId;
    @JsonProperty("direction")
    public Long direction;
    @JsonProperty("fee_info")
    public FeeInfo feeInfo;
    @JsonProperty("price")
    public String price;

    @JsonProperty("stark_key")
    public String starkKey;

    @JsonProperty("stop_limit_time_in_force")
    public String stopLimitTimeInForce;

    @JsonProperty("payment")
    public Payment payment;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class Payment {

        @JsonProperty("pay_info")
        public com.reddio.api.v1.rest.Payment.PayInfo payInfo;

        @JsonProperty("nonce")
        public Long nonce;

        @JsonProperty("signature")
        public Signature signature;
    }
}
