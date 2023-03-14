package com.reddio.api.v1

import com.reddio.IntegrationTest
import com.reddio.api.v1.rest.DefaultReddioRestClient
import com.reddio.api.v1.rest.GetContractInfoMessage
import com.reddio.api.v1.rest.GetContractInfoResponse
import com.reddio.api.v1.rest.ResponseWrapper
import com.reddio.fixtures.FixtureException
import com.reddio.fixtures.Fixtures
import com.reddio.gas.GasOption
import mu.KotlinLogging
import org.junit.Assume
import org.junit.Test
import org.junit.experimental.categories.Category


private val logger = KotlinLogging.logger {}

@Category(IntegrationTest::class)
class DefaultEthereumWithdrawalInteractionTest {
    @Test
    fun testWithdrawETH() {
        try {
            val (account, record) = Fixtures.fetchStarkKeysWhichCouldWithdrawalETHOnLayer1()
            val restClient = DefaultReddioRestClient.testnet()
            val ethereumInteraction = DefaultEthereumInteraction.build(
                restClient,
                DefaultEthereumInteraction.GOERIL_ID,
                Fixtures.fetchETHJsonRPCNode(),
                account.ethPrivateKey
            )
            val contractInfo: ResponseWrapper<GetContractInfoResponse> = restClient.getContractInfo(
                GetContractInfoMessage.of(
                    "ETH",
                    "ETH"
                )
            ).join()
            val assetType = contractInfo.data.getAssetType()

            val log = ethereumInteraction.withdrawETHOrERC20(
                account.ethAddress,
                assetType,
                GasOption.Market
            ).join()
            logger.info {
                "ETH deposit: $log"
            }
        } catch (e: FixtureException) {
            Assume.assumeNoException("Skipping test: No ETH withdrawal fixtures found.", e)
        }
    }

}