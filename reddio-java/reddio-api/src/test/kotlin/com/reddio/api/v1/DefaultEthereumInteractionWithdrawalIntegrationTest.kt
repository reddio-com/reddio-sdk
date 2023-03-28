package com.reddio.api.v1

import com.reddio.IntegrationTest
import com.reddio.api.v1.rest.DefaultReddioRestClient
import com.reddio.fixtures.FixtureException
import com.reddio.fixtures.Fixtures
import com.reddio.gas.GasOption
import mu.KotlinLogging
import org.junit.Assume
import org.junit.Test
import org.junit.experimental.categories.Category


private val logger = KotlinLogging.logger {}

@Category(IntegrationTest::class)
class DefaultEthereumInteractionWithdrawalIntegrationTest {
    @Test
    fun testWithdrawETH() {
        try {
            val (account, record) = Fixtures.fetchStarkKeysWhichCouldWithdrawalETHOnLayer1()
            val restClient = DefaultReddioRestClient.testnet()
            val ethereumInteraction = DefaultEthereumInteraction.build(
                restClient, DefaultEthereumInteraction.GOERIL_ID, Fixtures.fetchETHJsonRPCNode(), account.ethPrivateKey
            )

            val log = ethereumInteraction.withdrawalETH(
                account.ethAddress, GasOption.Market
            ).join()
            logger.info {
                "ETH deposit: $log"
            }
        } catch (e: FixtureException) {
            Assume.assumeNoException("Skipping test: No ETH withdrawal fixtures found.", e)
        }
    }

    @Test
    fun testWithdrawERC20() {
        try {
            val (account, record) = Fixtures.fetchStarkKeysWhichCouldWithdrawalERC20OnLayer1()
            val restClient = DefaultReddioRestClient.testnet()
            val ethereumInteraction = DefaultEthereumInteraction.build(
                restClient, DefaultEthereumInteraction.GOERIL_ID, Fixtures.fetchETHJsonRPCNode(), account.ethPrivateKey
            )
            val log = ethereumInteraction.withdrawalERC20(
                account.ethAddress, record.contractAddress, GasOption.Market
            ).join()
            logger.info {
                "ERC20 deposit: $log"
            }
        } catch (e: FixtureException) {
            Assume.assumeNoException("Skipping test: No ETH withdrawal fixtures found.", e)
        }
    }

    @Test
    fun testWithdrawERC721() {
        try {
            val (account, record) = Fixtures.fetchStarkKeysWhichCouldWithdrawalERC721OnLayer1()
            val restClient = DefaultReddioRestClient.testnet()
            val ethereumInteraction = DefaultEthereumInteraction.build(
                restClient, DefaultEthereumInteraction.GOERIL_ID, Fixtures.fetchETHJsonRPCNode(), account.ethPrivateKey
            )

            val log = ethereumInteraction.withdrawalERC721(
                account.ethAddress, record.contractAddress, record.tokenId, GasOption.Market
            ).join()
            logger.info {
                "ERC721 deposit: $log"
            }
        } catch (e: FixtureException) {
            Assume.assumeNoException("Skipping test: No ETH withdrawal fixtures found.", e)
        }
    }

    @Test
    fun testWithdrawERC721M() {
        try {
            val (account, record) = Fixtures.fetchStarkKeysWhichCouldWithdrawalERC721MOnLayer1()
            val restClient = DefaultReddioRestClient.testnet()
            val ethereumInteraction = DefaultEthereumInteraction.build(
                restClient, DefaultEthereumInteraction.GOERIL_ID, Fixtures.fetchETHJsonRPCNode(), account.ethPrivateKey
            )

            val log = ethereumInteraction.withdrawalERC721M(
                account.ethAddress, record.contractAddress, record.tokenId, GasOption.Market
            ).join()
            logger.info {
                "ERC721M deposit: $log"
            }
        } catch (e: FixtureException) {
            Assume.assumeNoException("Skipping test: No ETH withdrawal fixtures found.", e)
        }
    }
}