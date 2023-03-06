package com.reddio.api.v1

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.reddio.IntegrationTest
import com.reddio.api.v1.requests.ReddioOrderApi.Companion.orderWithETH
import com.reddio.api.v1.requests.ReddioWithdrawalToApi.Companion.withdrawalERC721
import com.reddio.api.v1.rest.*
import com.reddio.api.v1.rest.GetBalancesResponse.BalanceRecord
import com.reddio.crypto.CryptoService
import com.reddio.exception.ReddioBusinessException
import com.reddio.exception.ReddioErrorCode
import com.reddio.fixtures.Fixtures
import mu.KotlinLogging
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category
import java.util.concurrent.CompletionException
import java.util.concurrent.ExecutionException
import java.util.stream.Collectors

private val logger = KotlinLogging.logger {}

@Category(IntegrationTest::class)
class DefaultReddioClientIntegrationTest {

    @Test
    @Category(IntegrationTest::class)
    fun testWithdrawalGoerliETH() {
        val withdrawalAmount = "0.000003"
        val (sender, ethOwnership) = Fixtures.fetchStarkKeysWhichOwnedETH(withdrawalAmount)

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
    fun testOrder() {
        val client = DefaultReddioClient.testnet()
        val restClient = DefaultReddioRestClient.testnet()
        val balancesFuture = restClient.getBalances(
            GetBalancesMessage.of(
                "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
                DefaultEthereumInteractionTest.REDDIO721_CONTRACT_ADDRESS,
                10L,
                1L
            )
        )
        val balances = balancesFuture.get()
        Assert.assertEquals("OK", balances.status)
        val toSell = balances.data.getList().stream().filter { it: BalanceRecord -> it.balanceAvailable > 0 }
            .collect(Collectors.toList())[0]
        val clientWithSigner =
            client.withStarkExSigner("0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d")
        val future = clientWithSigner.order(
            "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
            "0.013",
            "1",
            DefaultEthereumInteractionTest.REDDIO721_CONTRACT_ADDRESS,
            toSell.tokenId,
            "11ed793a-cc11-4e44-9738-97165c4e14a7",
            "ERC721",
            OrderBehavior.SELL
        )
        val result = future.get()
        println(ObjectMapper().writeValueAsString(result))
        Assert.assertEquals("OK", result.status)
    }

    @Test
    @Category(IntegrationTest::class)

    fun testGetOrder() {
        val client = DefaultReddioClient.testnet()
        val getOrderResponse = client.getOrder(304282).get()
        Assert.assertEquals("OK", getOrderResponse.status)
        val data = getOrderResponse.data
        Assert.assertEquals(304282, data.getOrderId())
        Assert.assertEquals("0x1ccc27877014bc1a81919fc855ebbd1b874603283c9ea93397d970b0704e581", data.getStarkKey())
        Assert.assertEquals("1000", data.getPrice())
        Assert.assertEquals(0, data.getDirection())
        Assert.assertEquals("1", data.getAmount())
        Assert.assertEquals("0", data.getUnFilled())
        Assert.assertEquals(
            "0x352f9ffd821a525051de2d71126113505a7b0a73d98dbc0ac0ff343cfbdef5e", data.getSymbol().baseTokenAssetId
        )
        Assert.assertEquals(
            "0x22d8810dfe28c2c083463d64b886b7e7fbe2b455c9a03ea2f0afd1457abd57d", data.getSymbol().quoteTokenAssetId
        )
        Assert.assertEquals("eth", data.getSymbol().baseTokenContractAddr)
        Assert.assertEquals("0x941661bd1134dc7cc3d107bf006b8631f6e65ad5", data.getSymbol().quoteTokenContractAddr)
        Assert.assertEquals("ETH", data.getSymbol().baseTokenName)
        Assert.assertEquals("REDDIO721", data.getSymbol().quoteTokenName)
        Assert.assertEquals("ERC721", data.getSymbol().getTokenType())
        Assert.assertEquals("1026", data.getSymbol().getTokenId())
        Assert.assertEquals("200", data.getFeeRate())
        Assert.assertEquals("ERC721", data.getTokenType())
        Assert.assertEquals("1026", data.getTokenId())
        Assert.assertEquals("0.001", data.getDisplayPrice())
        Assert.assertEquals(OrderState.Filled, data.getOrderState())
    }

    @Test
    @Category(IntegrationTest::class)

    fun testListOrders() {
        val reddioClient: ReddioClient = DefaultReddioClient.testnet()
        val wrapper =
            reddioClient.listRecords("0x6736f7449da3bf44bf0f7bdd6463818e1ef272641d43021e8bca17b32ec2df0", 3L, 2L, null)
                .get()
        Assert.assertEquals("OK", wrapper.status)
        Assert.assertEquals(3, wrapper.data.pageSize)
        Assert.assertEquals(2, wrapper.data.currentPage)
        Assert.assertEquals(3, wrapper.data.list.size.toLong())
    }

    @Test
    @Category(IntegrationTest::class)

    fun testListOrdersWithSequenceIds() {
        val reddioClient: ReddioClient = DefaultReddioClient.testnet()
        val sequenceIds: MutableList<Long> = ArrayList()
        sequenceIds.add(303531L)
        sequenceIds.add(303530L)
        val wrapper = reddioClient.listRecords(sequenceIds).get()
        Assert.assertEquals("OK", wrapper.status)
        Assert.assertEquals(2, wrapper.data.list.size.toLong())
        Assert.assertEquals(2, wrapper.data.total)
        Assert.assertEquals(303531L, wrapper.data.list[0].getSequenceId())
        Assert.assertEquals(303530L, wrapper.data.list[1].getSequenceId())
    }

    @Test
    @Category(IntegrationTest::class)

    fun testOrderWithERC20() {
        val client = DefaultReddioClient.testnet()
        val restClient = DefaultReddioRestClient.testnet()
        val balancesFuture = restClient.getBalances(
            GetBalancesMessage.of(
                "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
                DefaultEthereumInteractionTest.REDDIO721_CONTRACT_ADDRESS,
                100L,
                1L
            )
        )
        val balances = balancesFuture.get()
        Assert.assertEquals("OK", balances.status)
        val toSell = balances.data.getList().stream().filter { it: BalanceRecord -> it.balanceAvailable > 0 }
            .collect(Collectors.toList())[0]
        val clientWithSigner =
            client.withStarkExSigner("0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d")
        val future = clientWithSigner.order(
            "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
            "ERC721",
            DefaultEthereumInteractionTest.REDDIO721_CONTRACT_ADDRESS,
            toSell.tokenId,
            "0.013",
            "1",
            OrderBehavior.SELL,
            "ERC20",
            DefaultEthereumInteractionTest.RDD20_CONTRACT_ADDRESS,
            ""
        )
        val result = future.get()
        println(ObjectMapper().writeValueAsString(result))
        Assert.assertEquals("OK", result.status)
    }

    @Test
    @Category(IntegrationTest::class)

    fun testSellOrderWithRUSD() {
        val client = DefaultReddioClient.testnet()
        val restClient = DefaultReddioRestClient.testnet()
        val clientWithSigner =
            client.withStarkExSigner("0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d")
        val future = clientWithSigner.sellNFTWithRUSD(
            "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
            "ERC721",
            DefaultEthereumInteractionTest.REDDIO721_CONTRACT_ADDRESS,
            "1210",
            "0.013",
            "1",
            ""
        )
        val result = future.get()
        println(ObjectMapper().writeValueAsString(result))
        Assert.assertEquals("OK", result.status)
    }

    @Test
    @Category(IntegrationTest::class)

    fun testBuyOrderWithPayInfoBaseTokenRUSD() {
        val client = DefaultReddioClient.testnet()
        val restClient = DefaultReddioRestClient.testnet()
        val clientWithSigner =
            client.withStarkExSigner("5f6fbfbcd995e20f94a768193c42060f7e626e6ae8042cacc15e82031087a55")
        val future = clientWithSigner.buyNFTWithPayInfoBaseTokenRUSD(
            "0x13a69a1b7a5f033ee2358ebb8c28fd5a6b86d42e30a61845d655d3c7be4ad0e",
            "ERC721",
            DefaultEthereumInteractionTest.REDDIO721_CONTRACT_ADDRESS,
            "1209",
            "0.013",
            "1",
            "",
            Payment.PayInfo.of("123456789"),
            "0x1a35ffa8bafc5c6656271bcae1f847bb6201705d7e2895c413cfb7d757a3111"
        )
        val result = future.get()
        println(ObjectMapper().writeValueAsString(result))
        Assert.assertEquals("OK", result.status)
    }


    @Test
    @Category(IntegrationTest::class)
    fun testCancelOrder() {
        val client = DefaultReddioClient.testnet()
        val withStarkExSigner =
            client.withStarkExSigner("0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d")
        val future =
            withStarkExSigner.cancelOrder("0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4", 303590)
        val result = future.get()
        println(ObjectMapper().writeValueAsString(result))
        Assert.assertEquals("OK", result.status)
    }

    @Test
    @Category(IntegrationTest::class)
    @Throws(
        ExecutionException::class, InterruptedException::class, JsonProcessingException::class
    )
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

    @Test
    @Category(IntegrationTest::class)
    fun testOrderForNoSuchToken() {
        val buyerPrivateKey = CryptoService.getRandomPrivateKey()
        val receiverPrivateKey = CryptoService.getRandomPrivateKey()
        try {
            orderWithETH(
                DefaultReddioRestClient.testnet(),
                "0x" + buyerPrivateKey.toString(16),
                "ERC721",
                "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5",
                "99999",
                "0.01",
                "1",
                OrderBehavior.BUY
            ).callAndPollOrder()
        } catch (t: Throwable) {
            Assert.assertEquals(ReddioErrorCode.NotSuchToken, (t as ReddioBusinessException).errorCode)
        }
    }
}