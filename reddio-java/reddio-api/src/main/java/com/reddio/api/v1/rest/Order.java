package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Order {
    @JsonProperty("order_id")
    public Long orderId;

    @JsonProperty("stark_key")
    public String starkKey;

    @JsonProperty("price")
    public String price;

    @JsonProperty("direction")
    public Long direction;

    @JsonProperty("amount")
    public String amount;

    @JsonProperty("un_filled")
    public String unFilled;

    @JsonProperty("symbol")
    public Symbol symbol;


    @JsonProperty("fee_rate")
    public String feeRate;

    @JsonProperty("token_type")
    public String tokenType;

    @JsonProperty("token_id")
    public String tokenId;

    @JsonProperty("display_price")
    public String displayPrice;

    @JsonProperty("order_state")
    public OrderState orderState;

    @JsonProperty("resp")
    public String resp;
}
