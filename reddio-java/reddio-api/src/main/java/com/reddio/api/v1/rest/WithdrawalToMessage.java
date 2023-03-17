package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class WithdrawalToMessage {
    @JsonProperty("contract_address")
    private String contractAddress;
    @JsonProperty("asset_id")
    private String assetId;
    @JsonProperty("stark_key")
    private String starkKey;
    @JsonProperty("amount")
    private String amount;
    @JsonProperty("token_id")
    private String tokenId;
    @JsonProperty("nonce")
    private Long nonce;
    @JsonProperty("vault_id")
    private String vaultId;
    @JsonProperty("receiver")
    private String receiver;
    @JsonProperty("receiver_vault_id")
    private String receiverVaultId;
    @JsonProperty("expiration_timestamp")
    private Long expirationTimestamp;
    @JsonProperty("signature")
    private Signature signature;
}
