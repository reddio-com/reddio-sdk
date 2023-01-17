package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class MintsMessage {
    /**
     * Contract address of token (ERC20 or ERC721)
     */
    @JsonProperty("contract_address")
    public String contractAddress;

    /**
     * A unique key that identifies the user in the off-chain state
     */
    @JsonProperty("stark_key")
    public String starkKey;

    /**
     * Amount of tokens mint on layer 2
     */
    @JsonProperty("amount")
    public String amount;
}
