package com.reddio.api.v3.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class GetBalancesMessage {

    @JsonProperty("stark_key")
    private String starkKey;

    @JsonProperty("page")
    private Long page;

    @JsonProperty("limit")
    private Long limit;

    @JsonProperty("contract_address")
    private String contractAddress;

    @JsonProperty("type")
    private String type;
}
