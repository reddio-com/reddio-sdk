package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
     * Amount of tokens mint on layer 2.
     * <p>
     * Can not be used with {@link #tokenIds}
     */
    @JsonProperty("amount")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String amount;

    /**
     * Specified token ids to mint.
     * <p>
     * Can not be used with {@link #amount}
     */
    @JsonProperty("token_ids")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public String tokenIds;

    public static final String tokenIdsAsString(List<Long> tokenIds) {
        return String.join(",", tokenIds.stream().map(Object::toString).toArray(String[]::new));
    }

    public static MintsMessage of(String contractAddress, String starkKey, String amount) {
        return new MintsMessage(contractAddress, starkKey, amount, "");
    }
}
