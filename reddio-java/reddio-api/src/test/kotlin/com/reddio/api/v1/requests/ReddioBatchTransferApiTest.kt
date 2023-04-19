package com.reddio.api.v1.requests

import com.reddio.IntegrationTest
import com.reddio.api.v1.ReddioClient
import com.reddio.api.v1.rest.DefaultReddioRestClient
import com.reddio.api.v1.rest.GetBalancesMessage
import com.reddio.fixtures.Fixtures
import com.reddio.fixtures.StarkKeysPool
import org.junit.Assert.*
import org.junit.Test
import org.junit.experimental.categories.Category


class ReddioBatchTransferApiTest {
    @Test
    @Category(IntegrationTest::class)
    fun testBatchTransfer() {
        val (sender, _) = Fixtures.fetchStarkKeysWhichOwnERC721OnLayer2()
        val receiver = StarkKeysPool.starkKeysFromPoolButExpect(sender.starkKey)
        val restClient = DefaultReddioRestClient.testnet()
        val balances = restClient.getBalances(
            GetBalancesMessage.of(
                sender.starkKey, Fixtures.ReddioTestERC721ContractAddress, null, null
            )
        ).join().data.list.filter { it.balanceAvailable > 0 }

        val itemsToTransfer = balances.map {
            ReddioBatchTransferApi.Companion.TransferItem.transferERC721(
                it.contractAddress,
                it.tokenId,
                it.type,
                receiver.starkKey,
                ReddioClient.MAX_EXPIRATION_TIMESTAMP
            )
        }
        val result = ReddioBatchTransferApi.batchTransfer(
            restClient, sender.starkPrivateKey, itemsToTransfer
        ).call()
        assertEquals("OK", result.status)
        println("sequence id: " + result.data.sequenceId)
    }
}