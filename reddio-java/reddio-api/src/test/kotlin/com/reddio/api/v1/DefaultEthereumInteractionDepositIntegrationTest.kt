package com.reddio.api.v1

import com.reddio.IntegrationTest
import com.reddio.api.v1.rest.DefaultReddioRestClient
import com.reddio.fixtures.Fixtures
import com.reddio.gas.GasOption
import mu.KotlinLogging
import org.junit.Test
import org.junit.experimental.categories.Category


private val logger = KotlinLogging.logger {}

@Category(IntegrationTest::class)
class DefaultEthereumInteractionDepositIntegrationTest {

    @Test
    fun testDepositETH() {
        val depositAmount = "0.0007"
        val (toDeposit, ethOwnership) = Fixtures.fetchStarkKeysWhichOwnETHOnLayer1(depositAmount)
        val ethereumInteraction = DefaultEthereumInteraction.build(
            DefaultReddioRestClient.testnet(),
            DefaultEthereumInteraction.GOERIL_ID,
            Fixtures.fetchETHJsonRPCNode(),
            toDeposit.ethPrivateKey
        )
        val logDeposit = ethereumInteraction.depositETH(
            toDeposit.starkKey, depositAmount, GasOption.Market
        ).join()
        logger.info { logDeposit }
    }

    @Test
    fun testDepositERC20() {
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

    @Test
    fun testDepositERC721() {
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
}