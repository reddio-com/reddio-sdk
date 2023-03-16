package com.reddio.api.v1.rest;

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
    @JsonProperty("contract_address")
    private String contractAddress;
    @JsonProperty("limit")
    private Long limit;
    @JsonProperty("page")
    private Long page;
}
