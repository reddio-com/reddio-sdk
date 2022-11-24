package com.reddio.gas

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request

class CodefiNetworkGasAPIClient(private val chainId: Long) {
    private val httpClient = OkHttpClient.Builder().build()
    private val objectMapper = jacksonObjectMapper()


    fun suggestedGasFees(): SuggestedGasFeesResponse {
        val request = Request.Builder()
            .url("https://gas-api.metaswap.codefi.network/networks/${chainId}/suggestedGasFees")
            .build();
        val response = httpClient.newCall(request).execute()
        val jsonString = response.body!!.string()
        return objectMapper.readValue(jsonString, SuggestedGasFeesResponse::class.java)
    }

}

data class SuggestedGasFeesResponse(
    val low: SuggestedGasFeeEntry,
    val medium: SuggestedGasFeeEntry,
    val high: SuggestedGasFeeEntry,
    val estimatedBaseFee: String,
    val networkCongestion: Double,
    val latestPriorityFeeRange: List<String>,
    val historicalPriorityFeeRange: List<String>,
    val historicalBaseFeeRange: List<String>,
    val priorityFeeTrend: String,
    val baseFeeTrend: String,
)

data class SuggestedGasFeeEntry(
    val suggestedMaxPriorityFeePerGas: String,
    val suggestedMaxFeePerGas: String,
    val minWaitTimeEstimate: Long,
    val maxWaitTimeEstimate: Long,
)