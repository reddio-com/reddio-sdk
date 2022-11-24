package com.reddio.gas;

public interface GasFeeEstimator {
    SuggestedGasFee fetchSuggestedGasFee(GasOption gasOption);
}
