package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class GetAssetIdMessage {
    @JsonProperty("contract_address")
    private String contractAddress;
    @JsonProperty("type")
    private String type;
    @JsonProperty("token_id")
    private String tokenId;
    @JsonProperty("quantum")
    private Long quantum;
}
