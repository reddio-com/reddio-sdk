package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class OrderInfoResponse {
    @JsonProperty("fee_rate")
    public String feeRate;
    @JsonProperty("base_token")
    public String baseToken;
    @JsonProperty("fee_token")
    public String feeToken;
    @JsonProperty("lower_limit")
    public Long lowerLimit;
    @JsonProperty("nonce")
    public Long nonce;
    @JsonProperty("contracts")
    public List<Contract> contracts;
    @JsonProperty("vault_ids")
    public List<String> vaultIds;
    @JsonProperty("asset_ids")
    public List<String> assetIds;
}
