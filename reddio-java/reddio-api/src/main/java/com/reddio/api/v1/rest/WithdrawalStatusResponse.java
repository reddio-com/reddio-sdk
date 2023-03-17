package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


public class WithdrawalStatusResponse extends ArrayList<WithdrawalStatusResponse.WithdrawalStatusRecord> {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class WithdrawalStatusRecord {
        @JsonProperty("contract_address")
        private String contractAddress;

        @JsonProperty("asset_id")
        private String assetId;

        @JsonProperty("token_id")
        private String tokenId;

        @JsonProperty("type")
        private String type;

        @JsonProperty("asset_type")
        private String assetType;

        @JsonProperty("display_value")
        private String displayValue;

        @JsonProperty("symbol")
        private String symbol;

        @JsonProperty("amount")
        private Long amount;

    }
}
