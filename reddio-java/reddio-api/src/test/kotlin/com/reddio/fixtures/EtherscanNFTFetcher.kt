package com.reddio.fixtures

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request

class EtherscanNFTFetcher {
    companion object {

        data class EtherscanTokenTransferEvents(
            val message: String,
            val result: List<TransferEvent>,
            val status: String,
        )

        data class TransferEvent(
            val blockNumber: String,
            val timeStamp: String,
            val hash: String,
            val nonce: String,
            val blockHash: String,
            val from: String,
            val contractAddress: String,
            val to: String,
            val tokenID: String,
            val tokenName: String,
            val tokenSymbol: String,
            val tokenDecimal: String,
            val transactionIndex: String,
            val gas: String,
            val gasPrice: String,
            val gasUsed: String,
            val cumulativeGasUsed: String,
            val input: String,
            val confirmations: String,
        )

        private val mapper = jacksonObjectMapper()
        private val httpClient = OkHttpClient()

        fun fetchEtherscanAPIKey(): String {
            return System.getenv("INTEGRATION_TEST_ETHERSCAN_API_KEY") ?: ""
        }

        fun listERC721OwnedByEthAddress(
            ethAddress: String, contractAddress: String = Fixtures.ReddioTestERC721ContractAddress
        ): List<Fixtures.Companion.ERC721Ownership> {
            val url =
                "https://api-goerli.etherscan.io/api?module=account&action=tokennfttx&contractaddress=$contractAddress&address=$ethAddress&startblock=0&endblock=9999999999&sort=asc&apikey=${fetchEtherscanAPIKey()}"

            val req = Request.Builder().get().url(url).build()
            val response: EtherscanTokenTransferEvents = httpClient.newCall(req).execute().let { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    mapper.readValue(body, EtherscanTokenTransferEvents::class.java)
                } else {
                    throw FixtureException("Failed to fetch ERC721 ownership from Etherscan")
                }
            }

            val ownerERC721s: MutableMap<String, Fixtures.Companion.ERC721Ownership> = mutableMapOf()
            response.result.forEach {
                if (it.to == ethAddress) {
                    ownerERC721s[it.tokenID] = Fixtures.Companion.ERC721Ownership(
                        ethAddress,
                        contractAddress,
                        it.tokenID
                    )
                } else if (it.from == ethAddress) {
                    ownerERC721s.remove(it.tokenID)
                }
            }

            return ownerERC721s.values.toList()
        }
    }
}