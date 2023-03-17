package com.reddio.api.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class LogDeposit {
    private String depositorEthKey;
    private String starkKey;
    private String vaultId;
    private String assetType;
    private String nonQuantizedAmount;
    private String quantizedAmount;
//    public Map<String, Object> raw;
}
