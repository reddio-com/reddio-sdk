package com.reddio.api.v1.requests

import com.reddio.IntegrationTest
import com.reddio.api.v1.rest.DefaultReddioRestClient
import com.reddio.fixtures.Fixtures
import com.reddio.fixtures.StarkKeysPool
import org.junit.Assert.*
import org.junit.Test
import org.junit.experimental.categories.Category

class ReddioMultiTransferApiTest {
    @Test
    @Category(IntegrationTest::class)
    fun testMultiTransfer() {
        val restClient = DefaultReddioRestClient.testnet()

        val transferAmount = "0.00017"
        val (sender, _) = Fixtures.fetchStarkKeysWhichOwnETHOnLayer2(transferAmount)
        val receiver = StarkKeysPool.starkKeysFromPoolButExpect(sender.starkKey)

        val result = ReddioMultiTransferApi.multiTransfer(
            restClient, listOf(
                ReddioTransferToApi.transferETH(
                    restClient, sender.starkPrivateKey, transferAmount, receiver.starkKey, 4194303L
                ),
                ReddioTransferToApi.transferETH(
                    restClient, receiver.starkPrivateKey, transferAmount, sender.starkKey, 4194303L
                ),
            )
        ).callAndPollRecord()
        println(result)
    }
}