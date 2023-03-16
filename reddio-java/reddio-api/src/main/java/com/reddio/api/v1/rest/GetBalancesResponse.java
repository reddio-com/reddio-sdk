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
public class GetBalancesResponse {
    @JsonProperty("list")
    private List<BalanceRecord> list;

    @JsonProperty("total")
    private Long total;

    @JsonProperty("current_page")
    private Long currentPage;

    @JsonProperty("page_size")
    private Long pageSize;

    @JsonProperty("total_page")
    private Long totalPage;

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

        @JsonProperty("type")
        private String type;

        @JsonProperty("decimals")
        private Long decimals;

        @JsonProperty("symbol")
        private String symbol;

        @JsonProperty("quantum")
        private String quantum;

        @JsonProperty("display_value")
        private String displayValue;

        @JsonProperty("token_id")
        private String tokenId;
    }
}
