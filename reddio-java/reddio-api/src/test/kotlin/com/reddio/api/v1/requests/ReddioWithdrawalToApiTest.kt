package com.reddio.api.v1.requests

import com.reddio.IntegrationTest
import com.reddio.api.v1.rest.DefaultReddioRestClient
import com.reddio.api.v1.rest.RecordStatus
import org.junit.Assert.*
import org.junit.Test
import org.junit.experimental.categories.Category

class ReddioWithdrawalToApiTest {
    @Test
    @Category(IntegrationTest::class)
    fun testWithdrawalETH() {
        val restClient = DefaultReddioRestClient.testnet()
        val withdrawalETHRequest = ReddioWithdrawalToApi.withdrawalETH(
            restClient,
            "0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d",
            "0.00013",
            "0x76f2Fc7ed90039d986e3eb4DB294f05E160c8F03",
            4194303L
        )
        val signature = withdrawalETHRequest.signature
        assertNotNull(signature)
        assertNotNull(signature.r)
        assertNotNull(signature.s)
        val response = withdrawalETHRequest.call()
        assertEquals("OK", response.status)
    }

    @Test
    @Category(IntegrationTest::class)
    fun testWithdrawalETHThenGetRecordBySignature() {
        val restClient = DefaultReddioRestClient.testnet()
        val withdrawalETHRequest = ReddioWithdrawalToApi.withdrawalETH(
            restClient,
            "0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d",
            "0.00013",
            "0x76f2Fc7ed90039d986e3eb4DB294f05E160c8F03",
            4194303L
        )
        val signature = withdrawalETHRequest.signature
        assertNotNull(signature)
        assertNotNull(signature.r)
        assertNotNull(signature.s)
        val response = withdrawalETHRequest.call()
        assertEquals("OK", response.status)

        val record = restClient.getRecordBySignature(signature).get()
        assertEquals("OK", record.getStatus())
        assertNotNull(record.getData())
    }

    @Test
    @Category(IntegrationTest::class)
    fun testWithdrawalETHThenPolling() {
        val restClient = DefaultReddioRestClient.testnet()
        val withdrawalETHRequest = ReddioWithdrawalToApi.withdrawalETH(
            restClient,
            "0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d",
            "0.00013",
            "0x76f2Fc7ed90039d986e3eb4DB294f05E160c8F03",
            4194303L
        )

        val record = withdrawalETHRequest.callAndPollRecord(RecordStatus.AcceptedByReddio)
        assertEquals(RecordStatus.AcceptedByReddio, record.getStatus())
        println(record.toString())
    }
}