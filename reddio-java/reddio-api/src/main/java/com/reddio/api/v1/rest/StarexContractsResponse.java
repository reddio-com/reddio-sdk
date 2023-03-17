package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class StarexContractsResponse {
    @JsonProperty("mainnet")
    private String Mainnet;
    @JsonProperty("testnet")
    private String Testnet;
}
