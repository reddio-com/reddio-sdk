package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @deprecated use {@link com.reddio.api.v3.rest.ReddioRestClient#getBalances(com.reddio.api.v3.rest.GetBalancesMessage)} as instead.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Deprecated
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
