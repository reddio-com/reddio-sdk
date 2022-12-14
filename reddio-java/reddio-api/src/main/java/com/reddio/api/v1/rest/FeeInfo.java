package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class FeeInfo {
    @JsonProperty("fee_limit")
    public Long feeLimit;
    @JsonProperty("token_id")
    public String tokenId;
    @JsonProperty("source_vault_id")
    public Long sourceVaultId;
}
