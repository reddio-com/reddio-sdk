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
    private String amount;
    @JsonProperty("amount_buy")
    private String amountBuy;
    @JsonProperty("amount_sell")
    private String amountSell;
    @JsonProperty("token_buy")
    private String tokenBuy;
    @JsonProperty("token_sell")
    private String tokenSell;
    @JsonProperty("base_token")
    private String baseToken;
    @JsonProperty("quote_token")
    private String quoteToken;
    @JsonProperty("vault_id_buy")
    private String vaultIdBuy;
    @JsonProperty("vault_id_sell")
    private String vaultIdSell;
    @JsonProperty("expiration_timestamp")
    private Long expirationTimestamp;
    @JsonProperty("nonce")
    private Long nonce;
    @JsonProperty("signature")
    private Signature signature;
    @JsonProperty("account_id")
    private String accountId;
    @JsonProperty("direction")
    private Long direction;
    @JsonProperty("fee_info")
    private FeeInfo feeInfo;
    @JsonProperty("price")
    private String price;
    @JsonProperty("stark_key")
    private String starkKey;
    @JsonProperty("stop_limit_time_in_force")
    private String stopLimitTimeInForce;
    @JsonProperty("payment")
    private Payment payment;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class Payment {

        @JsonProperty("pay_info")
        private com.reddio.api.v1.rest.Payment.PayInfo payInfo;
        @JsonProperty("nonce")
        private Long nonce;
        @JsonProperty("signature")
        private Signature signature;
    }
}
