package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class GetBalancesResponse  {
    @JsonProperty("list")
    public List<BalanceRecord> list;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class BalanceRecord {
        @JsonProperty("asset_id")
        public String assetId;
        @JsonProperty("contract_address")
        public String contractAddress;
        @JsonProperty("balance_available")
        public Long balanceAvailable;
        @JsonProperty("type")
        public String type;
        @JsonProperty("decimals")
        public Long decimals;
        @JsonProperty("symbol")
        public String symbol;
        @JsonProperty("quantum")
        public String quantum;
        @JsonProperty("displayValue")
        public String displayValue;
        @JsonProperty("token_id")
        public String tokenId;
    }
}
