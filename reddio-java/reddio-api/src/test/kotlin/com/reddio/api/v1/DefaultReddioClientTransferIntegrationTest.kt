package com.reddio.api.v1

import com.fasterxml.jackson.databind.ObjectMapper
import com.reddio.IntegrationTest
import com.reddio.api.v1.requests.ReddioTransferToApi
import com.reddio.api.v1.rest.DefaultReddioRestClient
import com.reddio.crypto.CryptoService
import com.reddio.exception.ReddioBusinessException
import com.reddio.exception.ReddioErrorCode
import com.reddio.fixtures.Fixtures
import com.reddio.fixtures.StarkKeysPool
import mu.KotlinLogging
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category

private val logger = KotlinLogging.logger {}

@Category(IntegrationTest::class)
class DefaultReddioClientTransferIntegrationTest {

    @Test
    fun testTransferETH() {
        val transferAmount = "0.02"
        val (sender, ethOwnership) = Fixtures.fetchStarkKeysWhichOwnedETH(transferAmount)
        val receiver = StarkKeysPool.starkKeysFromPoolButExpect(sender.starkKey)
        logger.info {
            "transfer ETH fixtures prepared, sender: ${sender.starkKey}, receiver: ${receiver.starkKey}, eth balance: ${ethOwnership.balance}, transfer eth amount: $transferAmount"
        }

        val client = DefaultReddioClient.testnet()
        val clientWithSigner = client.withStarkExSigner(sender.starkPrivateKey)
        val result = clientWithSigner.transferETH(
            transferAmount, receiver.starkKey, ReddioClient.MAX_EXPIRATION_TIMESTAMP
        ).join()
        Assert.assertEquals("OK", result.status)
        logger.info {
            "transfer requested, response: ${ObjectMapper().writeValueAsString(result)}"
        }
    }

    @Test
    fun testTransferERC20() {
        val transferAmount = "0.02"
        val (sender, erc20Ownership) = Fixtures.fetchStarkKeysWhichOwnedERC20(
            amount = transferAmount
        )
        val receiver = StarkKeysPool.starkKeysFromPoolButExpect(sender.starkKey)
        logger.info {
            "transfer ETH fixtures prepared, sender: ${sender.starkKey}, receiver: ${receiver.starkKey}, ERC20 contract address:${erc20Ownership.contractAddress}, ERC20 balance: ${erc20Ownership.balance}, transfer ERC20 amount: $transferAmount"
        }

        val client = DefaultReddioClient.testnet()
        val clientWithSigner = client.withStarkExSigner(sender.starkPrivateKey)
        val result = clientWithSigner.transferERC20(
            transferAmount, erc20Ownership.contractAddress, receiver.starkKey, ReddioClient.MAX_EXPIRATION_TIMESTAMP
        ).join()
        Assert.assertEquals("OK", result.status)
        logger.info {
            "transfer requested, response: ${ObjectMapper().writeValueAsString(result)}"
        }
    }

    @Test
    fun testTransferERC721() {
        val (sender, erc721Ownership) = Fixtures.fetchStarkKeysWhichOwnedERC721()
        val receiver = StarkKeysPool.starkKeysFromPoolButExpect(sender.starkKey)
        logger.info {
            "transfer ETH fixtures prepared, sender: ${sender.starkKey}, receiver: ${receiver.starkKey}, ERC721 contract address:${erc721Ownership.contractAddress}, ERC721 token id: ${erc721Ownership.tokenId}"
        }

        val client = DefaultReddioClient.testnet()
        val clientWithSigner = client.withStarkExSigner(sender.starkPrivateKey)
        val result = clientWithSigner.transferERC721(
            erc721Ownership.contractAddress,
            erc721Ownership.tokenId,
            ReddioClient.TOKEN_TYPE_ERC721,
            receiver.starkKey,
            ReddioClient.MAX_EXPIRATION_TIMESTAMP,
        ).join()
        Assert.assertEquals("OK", result.status)
        logger.info {
            "transfer requested, response: ${ObjectMapper().writeValueAsString(result)}"
        }
    }


    @Test
    fun testTransfer() {
        val (sender, erc721Ownership) = Fixtures.fetchStarkKeysWhichOwnedERC721()
        val receiver = StarkKeysPool.starkKeysFromPoolButExpect(sender.starkKey)
        logger.info {
            "transfer ERC721 fixtures prepared, sender: ${sender.starkKey}, receiver: ${receiver.starkKey}, contractAddress: ${erc721Ownership.contractAddress} tokenId: ${erc721Ownership.tokenId}"
        }

        val client = DefaultReddioClient.testnet()
        val clientWithSigner = client.withStarkExSigner(sender.starkPrivateKey)
        val result = clientWithSigner.transfer(
            sender.starkKey,
            "1",
            erc721Ownership.contractAddress,
            erc721Ownership.tokenId,
            ReddioClient.TOKEN_TYPE_ERC721,
            receiver.starkKey,
            ReddioClient.MAX_EXPIRATION_TIMESTAMP
        ).join()
        Assert.assertEquals("OK", result.status)
        logger.info {
            "transfer requested, response: ${ObjectMapper().writeValueAsString(result)}"
        }
    }

    @Test(timeout = 1000 * 20)
    @Category(IntegrationTest::class)
    fun testTransferThenWaitingRecordGetApproved() {
        val (sender, erc721Ownership) = Fixtures.fetchStarkKeysWhichOwnedERC721()
        val receiver = StarkKeysPool.starkKeysFromPoolButExpect(sender.starkKey)
        logger.info {
            "transfer ERC721 fixtures prepared, sender: ${sender.starkKey}, receiver: ${receiver.starkKey}, contractAddress: ${erc721Ownership.contractAddress} tokenId: ${erc721Ownership.tokenId}"
        }

        val client = DefaultReddioClient.testnet()
        val clientWithSigner = client.withStarkExSigner(sender.starkPrivateKey)
        val result = clientWithSigner.transfer(
            sender.starkKey,
            "1",
            erc721Ownership.contractAddress,
            erc721Ownership.tokenId,
            ReddioClient.TOKEN_TYPE_ERC721,
            receiver.starkKey,
            ReddioClient.MAX_EXPIRATION_TIMESTAMP
        ).join()
        Assert.assertEquals("OK", result.status)

        val waitingResult = client.waitingTransferGetApproved(
            sender.starkKey, result.data.getSequenceId()
        ).join()

        Assert.assertEquals("OK", waitingResult.status)
    }

    @Test
    fun testTransferForNoSuchToken() {
        val senderPrivateKey = CryptoService.getRandomPrivateKey()
        val receiverPrivateKey = CryptoService.getRandomPrivateKey()
        try {
            ReddioTransferToApi.transferERC721(
                DefaultReddioRestClient.testnet(),
                "0x" + senderPrivateKey.toString(16),
                "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5",
                // the token id does not exist
                "-1",
                "ERC721",
                "0x" + receiverPrivateKey.toString(16),
                4194303L
            ).callAndPollRecord()
            Assert.fail()
        } catch (t: Throwable) {
            Assert.assertEquals(ReddioErrorCode.NotSuchToken, (t as ReddioBusinessException).errorCode)
        }
    }
}