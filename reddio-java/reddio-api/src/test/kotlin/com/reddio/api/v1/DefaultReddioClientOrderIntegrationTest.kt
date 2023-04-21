package com.reddio.api.v1

import com.reddio.IntegrationTest
import com.reddio.api.v1.requests.ReddioOrderApi
import com.reddio.api.v1.requests.polling.OrderPoller
import com.reddio.api.v1.rest.DefaultReddioRestClient
import com.reddio.api.v1.rest.GetBalancesMessage
import com.reddio.api.v1.rest.OrderState
import com.reddio.api.v1.rest.Payment
import com.reddio.crypto.CryptoService
import com.reddio.exception.ReddioBusinessException
import com.reddio.exception.ReddioErrorCode
import com.reddio.fixtures.Fixtures
import com.reddio.fixtures.StarkKeysPool
import mu.KotlinLogging
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category
import java.time.Instant


private val logger = KotlinLogging.logger {}

@Category(IntegrationTest::class)
class DefaultReddioClientOrderIntegrationTest {

    @Test
    fun testTradeNFTWithETH() {
        val price = "0.0013"
        val (seller, erC721Ownership) = Fixtures.fetchStarkKeysWhichOwnERC721OnLayer2()
        val buyer = StarkKeysPool.starkKeysFromPoolButExpect(seller.starkKey)

        val restClient = DefaultReddioRestClient.testnet()
        val result = restClient.getBalances(GetBalancesMessage.of(buyer.starkKey, "ETH", null, null)).join()
        Assert.assertTrue(result.data.list.isNotEmpty())
        // make sure that the buyer has enough balance to pay for the NFT
        Assert.assertTrue(result.data.list[0].displayValue.toDouble() > price.toDouble())

        logger.info {
            "fixture prepared for trade(order sell then buy) NFT with ETH, seller: ${seller.starkKey}, buyer: ${buyer.starkKey}, price: $price, erC721Ownership: $erC721Ownership"
        }

        val client = DefaultReddioClient.testnet()
        val sellOrder = with(client.withStarkExSigner(seller.starkPrivateKey)) {
            orderWithEth(
                seller.starkKey,
                ReddioClient.TOKEN_TYPE_ERC721,
                erC721Ownership.contractAddress,
                erC721Ownership.tokenId,
                price,
                "1",
                OrderBehavior.SELL
            ).join()
        }
        Assert.assertEquals("OK", sellOrder.status)
        logger.info { "sell order created: $sellOrder" }

        val buyOrder = with(client.withStarkExSigner(buyer.starkPrivateKey)) {
            orderWithEth(
                buyer.starkKey,
                ReddioClient.TOKEN_TYPE_ERC721,
                erC721Ownership.contractAddress,
                erC721Ownership.tokenId,
                price,
                "1",
                OrderBehavior.BUY
            ).join()
        }
        Assert.assertEquals("OK", buyOrder.status)
        logger.info { "buy order created: $buyOrder" }

        val order = OrderPoller(DefaultReddioRestClient.testnet(), buyOrder.data.sequenceId).poll(
            OrderState.Canceled, OrderState.Filled
        )
        Assert.assertEquals(OrderState.Filled, order.getOrderState())

        logger.info { "order filled: $order" }
    }

    @Test
    fun testTradeNFTWithERC20() {
        val price = "0.0013"
        val (seller, erC721Ownership) = Fixtures.fetchStarkKeysWhichOwnERC721OnLayer2()
        val buyer = StarkKeysPool.starkKeysFromPoolButExpect(seller.starkKey)

        val restClient = DefaultReddioRestClient.testnet()
        val result = restClient.getBalances(
            GetBalancesMessage.of(
                buyer.starkKey, Fixtures.ReddioTestERC20ContractAddress, null, null
            )
        ).join()
        Assert.assertTrue(result.data.list.isNotEmpty())
        // make sure that the buyer has enough balance to pay for the NFT
        Assert.assertTrue(result.data.list[0].displayValue.toDouble() > price.toDouble())

        logger.info {
            "fixture prepared for trade(order sell then buy) NFT with ERC20, seller: ${seller.starkKey}, buyer: ${buyer.starkKey}, erc20 contract address: ${Fixtures.ReddioTestERC20ContractAddress}, price: $price, erC721Ownership: $erC721Ownership"
        }

        val client = DefaultReddioClient.testnet()
        val sellOrder = with(client.withStarkExSigner(seller.starkPrivateKey)) {
            order(
                seller.starkKey,
                ReddioClient.TOKEN_TYPE_ERC721,
                erC721Ownership.contractAddress,
                erC721Ownership.tokenId,
                price,
                "1",
                OrderBehavior.SELL,
                ReddioClient.TOKEN_TYPE_ERC20,
                Fixtures.ReddioTestERC20ContractAddress,
                ""
            ).join()
        }
        Assert.assertEquals("OK", sellOrder.status)
        logger.info { "sell order created: $sellOrder" }

        val buyOrder = with(client.withStarkExSigner(buyer.starkPrivateKey)) {
            order(
                buyer.starkKey,
                ReddioClient.TOKEN_TYPE_ERC721,
                erC721Ownership.contractAddress,
                erC721Ownership.tokenId,
                price,
                "1",
                OrderBehavior.BUY,
                ReddioClient.TOKEN_TYPE_ERC20,
                Fixtures.ReddioTestERC20ContractAddress,
                ""
            ).join()
        }
        Assert.assertEquals("OK", buyOrder.status)
        logger.info { "buy order created: $buyOrder" }

        val order = OrderPoller(DefaultReddioRestClient.testnet(), buyOrder.data.sequenceId).poll(
            OrderState.Canceled, OrderState.Filled
        )
        Assert.assertEquals(OrderState.Filled, order.getOrderState())

        logger.info { "order filled: $order" }
    }

    @Test
    fun testTradeNFTWithPayment() {
        val price = "0.0013"
        val (seller, erC721Ownership) = Fixtures.fetchStarkKeysWhichOwnERC721OnLayer2()
        val buyer = StarkKeysPool.starkKeysFromPoolButExpect(seller.starkKey)

        logger.info {
            "fixture prepared for trade(order sell then buy) NFT with ERC20, seller: ${seller.starkKey}, buyer: ${buyer.starkKey}, erc20 contract address: ${Fixtures.ReddioTestERC20ContractAddress}, price: $price, erC721Ownership: $erC721Ownership"
        }

        val client = DefaultReddioClient.testnet()
        val sellOrder = with(client.withStarkExSigner(seller.starkPrivateKey)) {
            sellNFTWithRUSD(
                seller.starkKey,
                ReddioClient.TOKEN_TYPE_ERC721,
                erC721Ownership.contractAddress,
                erC721Ownership.tokenId,
                price,
                "1",
                ""
            ).join()
        }
        Assert.assertEquals("OK", sellOrder.status)
        logger.info { "sell order created: $sellOrder" }

        val buyOrder = with(client.withStarkExSigner(buyer.starkPrivateKey)) {
            buyNFTWithPayInfoBaseTokenRUSD(
                buyer.starkKey,
                ReddioClient.TOKEN_TYPE_ERC721,
                erC721Ownership.contractAddress,
                erC721Ownership.tokenId,
                price,
                "1",
                "",
                Payment.PayInfo.of(Instant.now().toString()),
                StarkKeysPool.paymentSignerStarkPrivateKey()
            ).join()
        }
        Assert.assertEquals("OK", buyOrder.status)
        logger.info { "buy order created: $buyOrder" }

        val order = OrderPoller(DefaultReddioRestClient.testnet(), buyOrder.data.sequenceId).poll(
            OrderState.Canceled, OrderState.Filled
        )
        Assert.assertEquals(OrderState.Filled, order.getOrderState())

        logger.info { "order filled: $order" }
    }

    @Test
    fun testCancelOrder() {
        val price = "0.0013"
        val (seller, erC721Ownership) = Fixtures.fetchStarkKeysWhichOwnERC721OnLayer2()
        val client = DefaultReddioClient.testnet()
        val sellOrder = with(client.withStarkExSigner(seller.starkPrivateKey)) {
            orderWithEth(
                seller.starkKey,
                ReddioClient.TOKEN_TYPE_ERC721,
                erC721Ownership.contractAddress,
                erC721Ownership.tokenId,
                price,
                "1",
                OrderBehavior.SELL
            ).join()
        }
        Assert.assertEquals("OK", sellOrder.status)

        logger.info { "cancel sell order: ${sellOrder.data}" }

        val cancelResult = with(client.withStarkExSigner(seller.starkPrivateKey)) {
            cancelOrder(seller.starkKey, sellOrder.data.sequenceId).join()
        }
        Assert.assertEquals("OK", cancelResult.status)
    }

    @Test
    fun testOrderForNoSuchToken() {
        val buyerPrivateKey = CryptoService.getRandomPrivateKey()
        try {
            ReddioOrderApi.orderWithETH(
                DefaultReddioRestClient.testnet(),
                "0x" + buyerPrivateKey.toString(16),
                "ERC721",
                "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5",
                "-1",
                "0.01",
                "1",
                OrderBehavior.BUY
            ).callAndPollOrder()
        } catch (t: Throwable) {
            Assert.assertEquals(ReddioErrorCode.NotSuchToken, (t as ReddioBusinessException).errorCode)
        }
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
        Assert.assertEquals(Instant.ofEpochMilli(1676126105000), data.timestamp)
        // uncomment this line after the new field is added to the response
        // Assert.assertEquals( Instant.ofEpochMilli(1676126105555), data.orderCreatedAt)
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
}