package com.reddio.gas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SuggestedGasFee {
    private GasOption gasOption;
    /**
     * the max priority fee per gas in GWei.
     */
    public String suggestedMaxPriorityFeePerGas;
    /**
     * the max fee per gas in GWei.
     */
    public String suggestedMaxFeePerGas;
}
