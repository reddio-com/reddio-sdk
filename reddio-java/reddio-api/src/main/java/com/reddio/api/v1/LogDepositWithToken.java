package com.reddio.api.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class LogDepositWithToken {
    private String depositorEthKey;
    private String starkKey;
    private String vaultId;
    private String assetType;
    private String tokenId;
    private String assetId;
    private String nonQuantizedAmount;
    private String quantizedAmount;
}
