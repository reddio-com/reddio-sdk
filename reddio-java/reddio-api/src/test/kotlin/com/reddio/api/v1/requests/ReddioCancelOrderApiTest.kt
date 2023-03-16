package com.reddio.api.v1.requests

import com.reddio.IntegrationTest
import com.reddio.api.v1.StarkExSigner
import com.reddio.api.v1.rest.DefaultReddioRestClient
import com.reddio.api.v1.rest.OrderListMessage
import com.reddio.api.v1.rest.OrderState
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test
import org.junit.experimental.categories.Category

@Ignore
class ReddioCancelOrderApiTest {

    @Test
    @Category(IntegrationTest::class)
    fun testCancelOrder_ThenPollTheOriginalOrder() {
        val restClient = DefaultReddioRestClient.testnet()
        val starkExSigner = StarkExSigner("0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d")
        val orders = restClient.orderList(
            OrderListMessage.of(
                starkExSigner.getStarkKey(), "", 100L, 1L, 0, null, null
            )
        ).get()
        assertEquals("OK", orders.status)
        val orderToCancel =
            orders.getData().list.stream().filter { OrderState.Placed == it.orderState }.findFirst().get()
        val orderAfterCancel = ReddioCancelOrderApi.cancelOrder(
            restClient,
            "0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d",
            orderToCancel.orderId
        ).callAndPollOrder()
        assertEquals(orderToCancel.orderId, orderAfterCancel.orderId)
        assertEquals(OrderState.Canceled, orderAfterCancel.orderState)
    }
}