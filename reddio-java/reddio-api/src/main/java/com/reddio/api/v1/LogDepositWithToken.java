package com.reddio.api.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class LogDepositWithToken {
    public String depositorEthKey;
    public String starkKey;
    public String vaultId;
    public String assetType;
    public String tokenId;
    public String assetId;
    public String nonQuantizedAmount;
    public String quantizedAmount;
}
