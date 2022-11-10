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
    public Long quantum;
    @JsonProperty("count")
    public Long count;
    @JsonProperty("type")
    public String type;
    @JsonProperty("decimals")
    public String decimals;
    @JsonProperty("symbol")
    public String symbol;
    @JsonProperty("total_supply")
    public String totalSupply;
    @JsonProperty("asset_type")
    public String assetType;
    @JsonProperty("asset_info")
    public String assetInfo;
    @JsonProperty("id")
    public Long id;
    @JsonProperty("belongs_to")
    public String belongsTo;
    @JsonProperty("contract_uuid")
    public String contractUuid;
    @JsonProperty("chain_status")
    public String chainStatus;
    @JsonProperty("contract_address")
    public String contractAddress;
}
