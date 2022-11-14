package com.reddio.api.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class LogDeposit {
    public String depositorEthKey;
    public String starkKey;
    public String vaultId;
    public String assetType;
    public String nonQuantizedAmount;
    public String quantizedAmount;
//    public Map<String, Object> raw;
}
