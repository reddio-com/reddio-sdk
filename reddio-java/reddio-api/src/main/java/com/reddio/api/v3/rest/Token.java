package com.reddio.api.v3.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Token {
    @JsonProperty("token_id")
    private String tokenId;

    @JsonProperty("token_uri")
    private String tokenUri;

    @JsonProperty("asset_id")
    private String assetId;
}
