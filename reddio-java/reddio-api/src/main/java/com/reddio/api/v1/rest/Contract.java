package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Contract {
    @JsonProperty("contract_address")
    private String contractAddress;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("decimals")
    private Long decimals;
    @JsonProperty("type")
    private String type;
    @JsonProperty("quantum")
    private Long quantum;
    @JsonProperty("asset_type")
    private String assetType;
}
