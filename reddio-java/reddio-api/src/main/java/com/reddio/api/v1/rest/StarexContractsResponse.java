package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class StarexContractsResponse {
    @JsonProperty("mainnet")
    public String Mainnet;
    @JsonProperty("testnet")
    public String Testnet;
}
