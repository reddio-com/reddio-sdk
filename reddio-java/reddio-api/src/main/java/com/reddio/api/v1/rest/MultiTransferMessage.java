package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.reddio.exception.ReddioException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class MultiTransferMessage {
    @JsonProperty("transfers")
    private List<TransferMessage> transfers;

    @JsonProperty("signature")
    private Signature signature;

    public static MultiTransferMessage of(List<TransferMessage> transfers) {
        if (transfers == null || transfers.isEmpty()) {
            throw new ReddioException("transfers must not be null or empty");
        }
        return new MultiTransferMessage(transfers, transfers.get(0).getSignature());
    }
}
