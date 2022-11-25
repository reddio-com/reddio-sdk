package com.reddio.api.v1

import com.reddio.api.v1.rest.GetContractInfoMessage
import com.reddio.api.v1.rest.ReddioRestClient
import kotlinx.coroutines.future.await
import kotlin.math.pow

class QuantizedHelper(private val restClient: ReddioRestClient) {

    /**
     * quantizedAmount = (amount * 10^decimals) / quantum
     *
     * @param amount amount to be converted, for example, use 0.0013 here for converting 0.0013 eth
     * @param type token type, available values: ETH, ERC20
     * @param contractAddress, use literal ETH for ETH, and hash address for ERC20
     */
    suspend fun quantizedAmount(
        amount: String, type: String, contractAddress: String
    ): Long {
        val contractInfo = restClient.getContractInfo(GetContractInfoMessage.of(type, contractAddress)).await()
        val quantizedAmount =
            (amount.toDouble() * 10.0.pow(contractInfo.data.decimals.toDouble()) / contractInfo.data.quantum).toLong()
        return quantizedAmount
    }

    /**
     * nonQuantizedAmount = amount * 10^decimals
     *
     * @param amount amount to be converted, for example, use 0.0013 here for converting 0.0013 eth
     * @param type token type, available values: ETH, ERC20
     * @param contractAddress, use literal ETH for ETH, and hash address for ERC20
     */
    suspend fun nonQuantizedAmount(
        amount: String, type: String, contractAddress: String
    ): Long {
        val contractInfo = restClient.getContractInfo(GetContractInfoMessage.of(type, contractAddress)).await()
        val nonQuantizedAmount = (amount.toDouble() * 10.0.pow(contractInfo.data.decimals.toDouble())).toLong()
        return nonQuantizedAmount
    }
}