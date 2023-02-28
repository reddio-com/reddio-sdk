package com.reddio.api.v1.requests

import com.reddio.IntegrationTest
import com.reddio.api.v1.ReddioClient
import com.reddio.api.v1.rest.DefaultReddioRestClient
import com.reddio.api.v1.rest.RecordStatus
import org.junit.Assert.*
import org.junit.Test
import org.junit.experimental.categories.Category

class ReddioTransferToApiTest {
    @Test
    @Category(IntegrationTest::class)
    fun testTransferERC721() {
        val restClient = DefaultReddioRestClient.testnet()
        val response = ReddioTransferToApi.transferERC721(
            restClient,
            "0xa7b68cf2ee72b2a0789914daa8ae928aec21b6b0bf020e394833f4c732d99d",
            "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5",
            "497",
            ReddioClient.TOKEN_TYPE_ERC721,
            "0x7865bc66b610d6196a7cbeb9bf066c64984f6f06b5ed3b6f5788bd9a6cb099c",
            4194303L
        ).call()
        assertEquals("OK", response.status)
    }

    @Test
    @Category(IntegrationTest::class)
    fun testTransferERC721ThenPolling() {
        val restClient = DefaultReddioRestClient.testnet()
        val record = ReddioTransferToApi.transferERC721(
            restClient,
            "0xa7b68cf2ee72b2a0789914daa8ae928aec21b6b0bf020e394833f4c732d99d",
            "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5",
            "497",
            ReddioClient.TOKEN_TYPE_ERC721,
            "0x7865bc66b610d6196a7cbeb9bf066c64984f6f06b5ed3b6f5788bd9a6cb099c",
            4194303L
        ).callAndPollRecord(RecordStatus.FailedOnReddio)
        // it should be fail because sender does not hold NFT with token id 497
        assertEquals(RecordStatus.FailedOnReddio, record.getStatus())
        println(record.toString())
    }

}