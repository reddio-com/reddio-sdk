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
    public String contractAddress;
    @JsonProperty("type")
    public String type;
    @JsonProperty("token_id")
    public String tokenId;
    @JsonProperty("quantum")
    public Long quantum;
}
