package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Symbol {
    @JsonProperty("base_token_asset_id")
    private String BaseTokenAssetId;

    @JsonProperty("quote_token_asset_id")
    private String QuoteTokenAssetId;

    @JsonProperty("base_token_contract_addr")
    private String BaseTokenContractAddr;

    @JsonProperty("quote_token_contract_addr")
    private String QuoteTokenContractAddr;

    @JsonProperty("base_token_name")
    private String BaseTokenName;

    @JsonProperty("quote_token_name")
    private String QuoteTokenName;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("token_id")
    private String tokenId;
}
