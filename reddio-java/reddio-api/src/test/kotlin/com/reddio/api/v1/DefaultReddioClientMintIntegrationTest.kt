package com.reddio.api.v1

import com.reddio.IntegrationTest
import com.reddio.fixtures.Fixtures
import mu.KotlinLogging
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.junit.experimental.categories.Category

private val logger = KotlinLogging.logger {}


@Ignore
@Category(IntegrationTest::class)
class DefaultReddioClientMintIntegrationTest {
    @Test
    fun testMint() {
        val restClient = DefaultReddioClient.testnet(Fixtures.fetchReddioAPIKey())
        val response = restClient.mints(
            "0x113536494406bc039586c1ad9b8f51af664d6ef8",
            "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
            1L
        ).join()

        Assert.assertEquals("OK", response.status)
        logger.info { "Mint result: ${response.data}" }

    }
}