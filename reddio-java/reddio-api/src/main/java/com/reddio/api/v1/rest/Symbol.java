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
    public String BaseTokenAssetId;
    @JsonProperty("quote_token_asset_id")

    public String QuoteTokenAssetId;
    @JsonProperty("base_token_contract_addr")

    public String BaseTokenContractAddr;
    @JsonProperty("quote_token_contract_addr")

    public String QuoteTokenContractAddr;
    @JsonProperty("base_token_name")

    public String BaseTokenName;
    @JsonProperty("quote_token_name")

    public String QuoteTokenName;
}
