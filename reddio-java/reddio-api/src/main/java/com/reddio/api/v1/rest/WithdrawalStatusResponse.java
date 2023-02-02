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
        public String contractAddress;

        @JsonProperty("asset_id")
        public String assetId;

        @JsonProperty("token_id")
        public String tokenId;

        @JsonProperty("type")
        public String type;

        @JsonProperty("asset_type")
        public String assetType;

        @JsonProperty("display_value")
        public String displayValue;

        @JsonProperty("symbol")
        public String symbol;

        @JsonProperty("amount")
        public Long amount;

    }
}
