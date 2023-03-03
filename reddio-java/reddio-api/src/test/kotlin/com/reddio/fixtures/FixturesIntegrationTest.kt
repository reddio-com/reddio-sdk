package com.reddio.fixtures

import com.reddio.IntegrationTest
import org.junit.Ignore
import org.junit.Test
import org.junit.experimental.categories.Category

@Ignore("example usage for preparing fixtures/test assets")
@Category(IntegrationTest::class)
class FixturesIntegrationTest {

    @Test
    fun getStarkKeysWhichOwnAtLeastOneNFT() {
        val (_, ownership) = Fixtures.fetchStarkKeysWhichOwnedERC721()
        println("ownership: $ownership")
    }
}