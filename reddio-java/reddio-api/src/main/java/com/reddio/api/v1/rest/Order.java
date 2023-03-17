package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Order {
    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("stark_key")
    private String starkKey;

    @JsonProperty("price")
    private String price;

    @JsonProperty("direction")
    private Long direction;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("un_filled")
    private String unFilled;

    @JsonProperty("symbol")
    private Symbol symbol;

    @JsonProperty("fee_rate")
    private String feeRate;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("token_id")
    private String tokenId;

    @JsonProperty("display_price")
    private String displayPrice;

    @JsonProperty("order_state")
    private OrderState orderState;

    @JsonProperty("resp")
    private String resp;

    @JsonProperty("order_created_at")
    private Instant orderCreatedAt;
}
