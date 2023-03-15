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
                restClient, DefaultEthereumInteraction.GOERIL_ID, Fixtures.fetchETHJsonRPCNode(), account.ethPrivateKey
            )
            val contractInfo: ResponseWrapper<GetContractInfoResponse> = restClient.getContractInfo(
                GetContractInfoMessage.of(
                    "ETH", "ETH"
                )
            ).join()
            val assetType = contractInfo.data.getAssetType()

            val log = ethereumInteraction.withdrawETHOrERC20(
                account.ethAddress, assetType, GasOption.Market
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
            val contractInfo: ResponseWrapper<GetContractInfoResponse> = restClient.getContractInfo(
                GetContractInfoMessage.of(
                    ReddioClient.TOKEN_TYPE_ERC20,
                    record.contractAddress
                )
            ).join()
            val assetType = contractInfo.data.getAssetType()

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
            val contractInfo: ResponseWrapper<GetContractInfoResponse> = restClient.getContractInfo(
                GetContractInfoMessage.of(
                    ReddioClient.TOKEN_TYPE_ERC721,
                    record.contractAddress
                )
            ).join()
            val assetType = contractInfo.data.getAssetType()

            val log = ethereumInteraction.withdrawalERC721(
                account.ethAddress, assetType, record.tokenId, GasOption.Market
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
            val contractInfo: ResponseWrapper<GetContractInfoResponse> = restClient.getContractInfo(
                GetContractInfoMessage.of(
                    ReddioClient.TOKEN_TYPE_ERC721M,
                    record.contractAddress
                )
            ).join()
            val assetType = contractInfo.data.getAssetType()

            val log = ethereumInteraction.withdrawalERC721M(
                account.ethAddress, assetType, record.tokenId, GasOption.Market
            ).join()
            logger.info {
                "ERC721M deposit: $log"
            }
        } catch (e: FixtureException) {
            Assume.assumeNoException("Skipping test: No ETH withdrawal fixtures found.", e)
        }
    }
}