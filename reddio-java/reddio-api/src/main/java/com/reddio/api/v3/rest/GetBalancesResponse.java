package com.reddio.api.v3.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


public class GetBalancesResponse extends ArrayList<GetBalancesResponse.BalanceRecord> {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class BalanceRecord {

        @JsonProperty("asset_id")
        private String assetId;

        @JsonProperty("contract_address")
        private String contractAddress;

        @JsonProperty("balance_available")
        private Long balanceAvailable;

        @JsonProperty("balance_frozen")
        private Long balanceFrozen;

        @JsonProperty("withdraw_frozen")
        private Long withdrawFrozen;

        @JsonProperty("type")
        private String type;

        @JsonProperty("decimals")
        private Long decimals;

        @JsonProperty("symbol")
        private String symbol;

        @JsonProperty("quantum")
        private String quantum;

        @JsonProperty("available_tokens")
        private List<Token> availableTokens;

        @JsonProperty("frozen_tokens")
        private List<Token> frozenTokens;

        @JsonProperty("withdraw_frozen_tokens")
        private List<Token> withdrawFrozenTokens;

        @JsonProperty("base_uri")
        private String baseUri;
    }
}
