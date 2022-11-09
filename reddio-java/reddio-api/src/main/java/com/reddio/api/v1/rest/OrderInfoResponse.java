package com.reddio.api.v1.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class OrderInfoResponse {
    public String feeRate;
    public String baseToken;
    public String feeToken;
    public Long lowerLimit;
    public Long nonce;
    public List<Contract> contracts;
    public List<String> vaultIds;
    public List<String> assetIds;
}
