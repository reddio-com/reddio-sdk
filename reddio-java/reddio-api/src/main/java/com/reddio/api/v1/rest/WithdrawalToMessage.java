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
    public String ContractAddress;
    @JsonProperty("asset_id")
    public String AssetId;
    @JsonProperty("stark_key")
    public String StarkKey;
    @JsonProperty("amount")
    public String Amount;
    @JsonProperty("token_id")
    public String TokenId;
    @JsonProperty("nonce")
    public Long Nonce;
    @JsonProperty("vault_id")
    public String VaultId;
    @JsonProperty("receiver")
    public String Receiver;
    @JsonProperty("receiver_vault_id")
    public String ReceiverVaultId;
    @JsonProperty("expiration_timestamp")
    public Long ExpirationTimestamp;
    @JsonProperty("signature")
    public Signature Signature;
}
