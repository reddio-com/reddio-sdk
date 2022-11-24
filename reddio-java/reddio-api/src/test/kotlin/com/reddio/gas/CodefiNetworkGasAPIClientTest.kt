package com.reddio.gas

import org.junit.Test

import org.junit.Assert.*

class CodefiNetworkGasAPIClientTest {

    @Test
    fun suggestedGasFees() {
        val client = CodefiNetworkGasAPIClient(5L)
        val response = client.suggestedGasFees()
        assertNotNull(response)
    }
}