package com.reddio.gas

/**
 * CodefiNetworkEstimator fetch the suggested gas price from Codefi's API: https://gas-api.metaswap.codefi.network/,
 * which also used by MataMask.
 */
class CodefiNetworkEstimator(chainId: Long) : GasFeeEstimator {
    private val client = CodefiNetworkGasAPIClient(chainId)
    override fun fetchSuggestedGasFee(gasOption: GasOption): SuggestedGasFee {
        val response = client.suggestedGasFees()
        val entry = when (gasOption) {
            GasOption.Low -> response.low
            GasOption.Market -> response.medium
            GasOption.Aggressive -> response.high
        }
        return SuggestedGasFee.of(
            gasOption,
            entry.suggestedMaxPriorityFeePerGas,
            entry.suggestedMaxFeePerGas,
        )
    }
}