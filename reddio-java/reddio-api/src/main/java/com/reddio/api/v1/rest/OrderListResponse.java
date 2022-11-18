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
    public Long total;
    public List<Order> list;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class Order {
        @JsonProperty("order_id")
        public String orderId;
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
        @JsonProperty("token_id")
        public String TokenId;
        @JsonProperty("display_price")
        public String DisplayPrice;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class Symbol {
        @JsonProperty("base_token_asset_id")
        public String baseTokenAssetId;
        @JsonProperty("quote_token_asset_id")
        public String quoteTokenAssetId;
        @JsonProperty("base_token_contract_addr")
        public String baseTokenContractAddr;
        @JsonProperty("quote_token_contract_addr")
        public String quoteTokenContractAddr;
        @JsonProperty("base_token_name")
        public String baseTokenName;
        @JsonProperty("quote_token_name")
        public String quoteTokenName;
    }
}


