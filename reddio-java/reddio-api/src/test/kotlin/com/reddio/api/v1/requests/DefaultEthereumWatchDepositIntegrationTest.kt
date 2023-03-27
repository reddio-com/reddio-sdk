package com.reddio.api.v1.requests

import com.reddio.api.v1.DefaultEthereumInteraction
import com.reddio.api.v1.rest.DefaultReddioRestClient
import org.junit.Test

class DefaultEthereumWatchDepositIntegrationTest {
    @Test
    fun testFailedToUninstall() {
        val ei = DefaultEthereumInteraction.build(
            DefaultReddioRestClient.testnet(),
            DefaultEthereumInteraction.GOERIL_ID,
            "https://goerli.infura.io/v3/b9ecd12c80674d0eb4f183544995b1cf",
            "4044e466d3a4db6e91f262713de47f08e8e21ac598df1e441dc0e116b078282b"
        )
        ei.watchDeposit( { (event, block) ->
            println("event: $event")
            println("block: $block")
        })
        Thread.sleep(60 * 60 * 1000)
    }
}