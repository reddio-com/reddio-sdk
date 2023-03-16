package com.reddio.api.v1.requests

import com.reddio.IntegrationTest
import com.reddio.api.v1.DefaultEthereumInteractionTest
import com.reddio.api.v1.OrderBehavior
import com.reddio.api.v1.rest.DefaultReddioRestClient
import com.reddio.api.v1.rest.GetBalancesMessage
import com.reddio.api.v1.rest.GetBalancesResponse.BalanceRecord
import com.reddio.api.v1.rest.OrderListMessage
import com.reddio.api.v1.rest.OrderState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Ignore
import org.junit.Test
import org.junit.experimental.categories.Category
import java.util.stream.Collectors

@Ignore
class ReddioOrderApiTest {
    @Test
    @Category(IntegrationTest::class)
    fun testOrder() {
        val restClient = DefaultReddioRestClient.testnet()
        val request = ReddioOrderApi.order(
            restClient,
            "0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d",
            "ERC721",
            DefaultEthereumInteractionTest.REDDIO721_CONTRACT_ADDRESS,
            "2731",
            "0.003",
            "1",
            OrderBehavior.SELL,
            "ERC20",
            DefaultEthereumInteractionTest.RDD20_CONTRACT_ADDRESS,
            ""
        )
        val response = request.call()
        assertEquals("OK", response.getStatus())
    }

    @Test
    @Category(IntegrationTest::class)
    fun testOrderThenPolling_OrderPlaced() {
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
        assertEquals("OK", balances.status)
        val toSell = balances.getData().getList().stream().filter { it: BalanceRecord -> it.balanceAvailable > 0 }
            .collect(Collectors.toList())[0]
        val request = ReddioOrderApi.order(
            restClient,
            "0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d",
            "ERC721",
            DefaultEthereumInteractionTest.REDDIO721_CONTRACT_ADDRESS,
            toSell.tokenId,
            "0.003",
            "1",
            OrderBehavior.SELL,
            "ERC20",
            DefaultEthereumInteractionTest.RDD20_CONTRACT_ADDRESS,
            ""
        )
        val order = request.callAndPollOrder(*OrderState.values())
        assertNotNull(order)
        assertEquals(OrderState.Placed, order.getOrderState())
    }

    @Test
    @Category(IntegrationTest::class)
    fun testOrderThenPolling_InvalidOrder_NotOwnTheNFT() {
        val restClient = DefaultReddioRestClient.testnet()
        val request = ReddioOrderApi.order(
            restClient,
            "0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d",
            "ERC721",
            DefaultEthereumInteractionTest.REDDIO721_CONTRACT_ADDRESS,
            "2731",
            "0.013",
            "1",
            OrderBehavior.SELL,
            "ERC20",
            DefaultEthereumInteractionTest.RDD20_CONTRACT_ADDRESS,
            ""
        )
        val order = request.callAndPollOrder(*OrderState.values())
        assertNotNull(order)
        assertEquals(OrderState.Canceled, order.getOrderState())
    }

    @Test
    @Category(IntegrationTest::class)
    fun testOrderThenPooling_BuyOrder_Filled() {
        val restClient = DefaultReddioRestClient.testnet()
        val balancesFuture = restClient.getBalances(
            GetBalancesMessage.of(
                "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
                "",
                100L,
                1L
            )
        )
        val balances = balancesFuture.get()
        assertEquals("OK", balances.status)
        val ethBalance =
            balances.getData().list.stream().filter { "ETH" == it.symbol }.findFirst().get().displayValue.toDouble()

        val forSale = restClient.orderList(
            OrderListMessage.of(
                "",
                DefaultEthereumInteractionTest.REDDIO721_CONTRACT_ADDRESS,
                100L,
                1L,
                0,
                null,
                null
            )
        ).get()
        val toBuy = forSale.getData().list.stream().filter { "ETH" == it.symbol.baseTokenName }.filter {
            val price = it.displayPrice.toDouble()
            price < 0.01 && price < ethBalance
        }.findFirst().get()


        val request = ReddioOrderApi.orderWithETH(
            restClient,
            "0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d",
            "ERC721",
            DefaultEthereumInteractionTest.REDDIO721_CONTRACT_ADDRESS,
            toBuy.tokenId,
            toBuy.displayPrice,
            "1",
            OrderBehavior.BUY,
        )
        val order = request.callAndPollOrder(*OrderState.values())
        assertNotNull(order)
        assertEquals(OrderState.Filled, order.getOrderState())
    }
}