package com.reddio.api.v1

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.reddio.IntegrationTest
import com.reddio.api.v1.requests.ReddioWithdrawalToApi.Companion.withdrawalERC721
import com.reddio.api.v1.rest.DefaultReddioRestClient
import com.reddio.crypto.CryptoService
import com.reddio.exception.ReddioBusinessException
import com.reddio.exception.ReddioErrorCode
import com.reddio.fixtures.Fixtures
import mu.KotlinLogging
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.junit.experimental.categories.Category
import java.util.concurrent.CompletionException
import java.util.concurrent.ExecutionException

private val logger = KotlinLogging.logger {}

@Category(IntegrationTest::class)
@Ignore
class DefaultReddioClientIntegrationTest {

    @Test
    @Category(IntegrationTest::class)
    fun testWithdrawalGoerliETH() {
        val withdrawalAmount = "0.000003"
        val (sender, ethOwnership) = Fixtures.fetchStarkKeysWhichOwnETHOnLayer2(withdrawalAmount)

        val client = DefaultReddioClient.testnet()
        val clientWithSigner =
            client.withStarkExSigner(sender.starkPrivateKey)
        val future = clientWithSigner.withdrawal(
            sender.starkKey,
            withdrawalAmount,
            "ETH",
            "",
            ReddioClient.TOKEN_TYPE_ETH,
            sender.ethAddress,
            ReddioClient.MAX_EXPIRATION_TIMESTAMP
        )
        val result = future.get()
        Assert.assertEquals("OK", result.status)
        logger.info { ObjectMapper().writeValueAsString(result) }
    }

    @Test
    @Category(IntegrationTest::class)
    fun testWithdrawalNTFERC721() {
        val client = DefaultReddioClient.testnet()
        val clientWithSigner =
            client.withStarkExSigner("0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d")
        val future = clientWithSigner.withdrawal(
            "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
            "1",
            DefaultEthereumInteractionTest.REDDIO721_CONTRACT_ADDRESS,
            "1022",
            "ERC721",
            "0x76f2Fc7ed90039d986e3eb4DB294f05E160c8F03",
            4194303L
        )
        val result = future.get()
        Assert.assertEquals("OK", result.status)
        println(ObjectMapper().writeValueAsString(result))
    }

    @Test
    @Category(IntegrationTest::class)
    fun testWithdrawalNTFERC721M() {
        val client = DefaultReddioClient.testnet()
        val clientWithSigner =
            client.withStarkExSigner("0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d")
        val future = clientWithSigner.withdrawal(
            "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
            "1",
            DefaultEthereumInteractionTest.REDDIO721M_CONTRACT_ADDRESS,
            "7",
            "ERC721M",
            "0x76f2Fc7ed90039d986e3eb4DB294f05E160c8F03",
            4194303L
        )
        val result = future.get()
        Assert.assertEquals("OK", result.status)
        println(ObjectMapper().writeValueAsString(result))
    }

    @Test
    @Category(IntegrationTest::class)
    @Throws(
        ExecutionException::class, InterruptedException::class, JsonProcessingException::class
    )
    @Ignore("invalid api key")
    fun testMints() {
        val client = DefaultReddioClient.testnet("<truncated-api-key>")
        val future = client.mints(
            "0x113536494406bc039586c1ad9b8f51af664d6ef8",
            "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
            1
        )
        val result = future.get()
        println(ObjectMapper().writeValueAsString(result))
        Assert.assertEquals("OK", result.status)
    }

    @Test
    @Category(IntegrationTest::class)
    fun testMintWithInvalidAmount() {
        val client = DefaultReddioClient.testnet("rk-1236d5fc-f4c1-4a19-a2ff-9c29e3a70e37")
        try {
            val future = client.mints(
                "0x113536494406bc039586c1ad9b8f51af664d6ef8",
                "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
                -1
            )
            future.join()
            Assert.fail()
        } catch (e: CompletionException) {
            Assert.assertEquals(ReddioErrorCode.MintAmountInvalid, (e.cause as ReddioBusinessException?)!!.errorCode)
        }
    }

    @Test
    @Category(IntegrationTest::class)
    @Throws(
        ExecutionException::class, InterruptedException::class, JsonProcessingException::class
    )
    fun testMintMintAgainWithSameTokenId() {
        val client = DefaultReddioClient.testnet("rk-1236d5fc-f4c1-4a19-a2ff-9c29e3a70e37")
        val tokenIds: MutableList<Long> = java.util.ArrayList()
        tokenIds.add(300L)
        try {
            val future = client.mints(
                "0x113536494406bc039586c1ad9b8f51af664d6ef8",
                "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
                tokenIds
            )
            future.join()
            Assert.fail()
        } catch (e: CompletionException) {
            Assert.assertEquals(ReddioErrorCode.TokenIDInvalid, (e.cause as ReddioBusinessException?)!!.errorCode)
        }
    }


    @Test
    @Category(IntegrationTest::class)
    fun testWithdrawalForNotSuchToken() {
        val senderPrivateKey = CryptoService.getRandomPrivateKey()
        val receiverPrivateKey = CryptoService.getRandomPrivateKey()
        try {
            withdrawalERC721(
                DefaultReddioRestClient.testnet(),
                "0x" + senderPrivateKey.toString(16),
                "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5",
                "9999999",
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