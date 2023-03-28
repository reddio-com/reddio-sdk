package com.reddio.api.v1

import com.reddio.IntegrationTest
import com.reddio.api.v1.rest.DefaultReddioRestClient
import com.reddio.fixtures.Fixtures
import com.reddio.gas.GasOption
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import mu.KotlinLogging

import org.junit.Before
import org.junit.Test
import org.junit.experimental.categories.Category
import java.util.concurrent.locks.ReentrantLock


private val logger = KotlinLogging.logger {}

@Category(IntegrationTest::class)
class DefaultEthereumInteractionWatchDepositEventIntegrationTest {

    @Category(IntegrationTest::class)
    class WatchDeposit {
        @Before
        fun depositERC20() {
            val depositAmount = "0.03"
            val (toDeposit, erC20Ownership) = Fixtures.fetchStarkKeysWhichOwnERC20OnLayer1(balance = depositAmount)
            val ethereumInteraction = DefaultEthereumInteraction.build(
                DefaultReddioRestClient.testnet(),
                DefaultEthereumInteraction.GOERIL_ID,
                Fixtures.fetchETHJsonRPCNode(),
                toDeposit.ethPrivateKey
            )
            val logDeposit = ethereumInteraction.depositERC20(
                Fixtures.ReddioTestERC20ContractAddress, toDeposit.starkKey, depositAmount, GasOption.Market
            ).join()
            logger.info { logDeposit }
        }

        // timeout >= 16 * blocktime = 16 * 15 = 240 seconds
        @Test(timeout = 300 * 10000)
        fun testWatchDeposit() {
            val ethereumInteraction = DefaultEthereumInteraction.build(
                DefaultReddioRestClient.testnet(),
                DefaultEthereumInteraction.GOERIL_ID,
                Fixtures.fetchETHJsonRPCNode(),
                "0x0"
            )

            val mutex = Mutex(true)
            val disposable = ethereumInteraction.watchDeposit {
                logger.info { "Deposit event: ${it.component1()}" }
                logger.info { "block: ${it.component2()}" }
                mutex.unlock()
            }

            runBlocking {
                mutex.lock()
            }
            disposable.dispose()
        }
    }

    @Category(IntegrationTest::class)
    class WatchNftDeposit {
        @Before
        fun depositERC721() {
            val (toDeposit, erC721Ownership) = Fixtures.fetchStarkKeysWhichOwnERC721OnLayer1()
            val ethereumInteraction = DefaultEthereumInteraction.build(
                DefaultReddioRestClient.testnet(),
                DefaultEthereumInteraction.GOERIL_ID,
                Fixtures.fetchETHJsonRPCNode(),
                toDeposit.ethPrivateKey
            )
            val logNftDeposit = ethereumInteraction.depositERC721(
                erC721Ownership.contractAddress,
                erC721Ownership.tokenId,
                toDeposit.starkKey,
                GasOption.Market
            ).join()
            logger.info { logNftDeposit }
        }

        // timeout >= 16 * blocktime = 16 * 15 = 240 seconds
        @Test(timeout = 300 * 10000)
        fun testWatchNftDeposit() {
            val ethereumInteraction = DefaultEthereumInteraction.build(
                DefaultReddioRestClient.testnet(),
                DefaultEthereumInteraction.GOERIL_ID,
                Fixtures.fetchETHJsonRPCNode(),
                "0x0"
            )
            val mutex = Mutex(true)

            val disposable = ethereumInteraction.watchNftDeposit() {
                logger.info { "Deposit event: ${it.component1()}" }
                logger.info { "block: ${it.component2()}" }
                mutex.unlock()
            }
            runBlocking {
                mutex.lock()
            }
            disposable.dispose()
        }
    }


}