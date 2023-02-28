package com.reddio.gas

import com.reddio.IntegrationTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.Ignore
import org.junit.experimental.categories.Category

class CodefiNetworkGasAPIClientTest {

    @Test
    @Category(IntegrationTest::class)
    fun suggestedGasFees() {
        val client = CodefiNetworkGasAPIClient(5L)
        val response = client.suggestedGasFees()
        assertNotNull(response)
    }
}