package com.reddio.gas

import org.web3j.tx.gas.ContractEIP1559GasProvider
import org.web3j.utils.Convert
import java.math.BigInteger

class StaticGasLimitSuggestionPriceGasProvider(
    private val chainId: Long,
    private val gasOption: GasOption,
    private val staticGasLimit: BigInteger,
) : ContractEIP1559GasProvider {
    private val gasFeeEstimator = CodefiNetworkEstimator(chainId)

    override fun getGasPrice(contractFunc: String?): BigInteger {
        return getMaxFeePerGas(contractFunc);
    }

    override fun getGasPrice(): BigInteger {
        return getMaxFeePerGas(null);
    }

    override fun getGasLimit(contractFunc: String?): BigInteger {
        return staticGasLimit
    }

    override fun getGasLimit(): BigInteger {
        return staticGasLimit
    }

    override fun isEIP1559Enabled(): Boolean {
        return true
    }

    override fun getChainId(): Long {
        return chainId
    }

    override fun getMaxFeePerGas(contractFunc: String?): BigInteger {
        val suggestedGasFee = this.gasFeeEstimator.fetchSuggestedGasFee(gasOption)
        return Convert.toWei(suggestedGasFee.getSuggestedMaxFeePerGas(), Convert.Unit.GWEI).toBigInteger()
    }

    override fun getMaxPriorityFeePerGas(contractFunc: String?): BigInteger {
        val suggestedGasFee = this.gasFeeEstimator.fetchSuggestedGasFee(gasOption)
        return Convert.toWei(suggestedGasFee.getSuggestedMaxPriorityFeePerGas(), Convert.Unit.GWEI).toBigInteger()
    }

    companion object {
        val DEFAULT_GAS_LIMIT = 1000_000L.toBigInteger();
    }
}