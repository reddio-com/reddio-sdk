package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class TransferMessage {
    @JsonProperty("asset_id")
    public String assetId;
    @JsonProperty("stark_key")
    public String starkKey;
    @JsonProperty("amount")
    public String amount;
    @JsonProperty("nonce")
    public Long nonce;
    @JsonProperty("vault_id")
    public String vaultId;
    @JsonProperty("receiver")
    public String receiver;
    @JsonProperty("receiver_vault_id")
    public String receiverVaultId;
    @JsonProperty("expiration_timestamp")
    public Long expirationTimestamp;
    @JsonProperty("signature")
    public Signature signature;

}
