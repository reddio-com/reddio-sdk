package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class OrderListResponse {
    @JsonProperty("total")
    public Long total;
    @JsonProperty("list")
    public List<Order> list;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class Order {
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
        public String FeeRate;
        @JsonProperty("token_type")
        public String TokenType;
        @JsonProperty("token_id")
        public String TokenId;
        @JsonProperty("display_price")
        public String DisplayPrice;
    }


}


