package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class BatchTransferMessage {

    @JsonProperty("transfers")
    private List<BatchTransferItem> transfers;
    @JsonProperty("stark_key")
    private String starkKey;
    @JsonProperty("nonce")
    private Long nonce;
    @JsonProperty("signature")
    private Signature signature;
    @JsonProperty("base_token_transfer_seq_id")
    private Long baseTokenTransferSeqId;

}
