package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class GetContractInfoResponse {
    @JsonProperty("quantum")
    private Long quantum;
    @JsonProperty("count")
    private Long count;
    @JsonProperty("type")
    private String type;
    @JsonProperty("decimals")
    private String decimals;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("total_supply")
    private String totalSupply;
    @JsonProperty("asset_type")
    private String assetType;
    @JsonProperty("asset_info")
    private String assetInfo;
    @JsonProperty("id")
    private Long id;
    @JsonProperty("belongs_to")
    private String belongsTo;
    @JsonProperty("contract_uuid")
    private String contractUuid;
    @JsonProperty("chain_status")
    private String chainStatus;
    @JsonProperty("contract_address")
    private String contractAddress;
}
