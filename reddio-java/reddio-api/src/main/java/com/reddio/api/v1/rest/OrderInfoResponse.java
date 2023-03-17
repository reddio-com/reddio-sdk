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
    private String feeRate;
    @JsonProperty("base_token")
    private String baseToken;
    @JsonProperty("fee_token")
    private String feeToken;
    @JsonProperty("lower_limit")
    private Long lowerLimit;
    @JsonProperty("nonce")
    private Long nonce;
    @JsonProperty("contracts")
    private List<Contract> contracts;
    @JsonProperty("vault_ids")
    private List<String> vaultIds;
    @JsonProperty("asset_ids")
    private List<String> assetIds;
}
