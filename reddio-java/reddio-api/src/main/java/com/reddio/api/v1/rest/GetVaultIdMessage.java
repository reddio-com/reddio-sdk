package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class GetVaultIdMessage {
    @JsonProperty("asset_id")
    private String assetId;
    @JsonProperty("stark_keys")
    private List<String> starkKeys;
}
