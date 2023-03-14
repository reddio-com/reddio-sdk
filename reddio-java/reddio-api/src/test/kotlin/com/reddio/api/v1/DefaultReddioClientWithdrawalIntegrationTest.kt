package com.reddio.api.v1

import com.reddio.IntegrationTest
import com.reddio.api.v1.rest.WithdrawalStatusMessage
import com.reddio.crypto.CryptoService.Reddio
import com.reddio.fixtures.Fixtures
import com.reddio.fixtures.StarkKeysPool
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.categories.Category

@Category(IntegrationTest::class)
class DefaultReddioClientWithdrawalIntegrationTest {

    @Test
    fun testWithdrawalETH() {
        val withdrawalAmount = "0.001"
        val (account, _) = Fixtures.fetchStarkKeysWhichOwnETHOnLayer2(withdrawalAmount)
        val client = DefaultReddioClient.testnet()
        val response = client.withStarkExSigner(account.starkPrivateKey).withdrawalETH(
            withdrawalAmount,
            account.ethAddress,
            ReddioClient.MAX_EXPIRATION_TIMESTAMP
        ).join()
        Assert.assertEquals("OK", response.status)
    }

    @Test
    fun testWithdrawalERC20() {
        val withdrawalAmount = "0.001"
        val (account, _) = Fixtures.fetchStarkKeysWhichOwnERC20OnLayer2(amount = withdrawalAmount)
        val client = DefaultReddioClient.testnet()
        val response = client.withStarkExSigner(account.starkPrivateKey).withdrawalERC20(
            withdrawalAmount,
            Fixtures.ReddioTestERC20ContractAddress,
            account.ethAddress,
            ReddioClient.MAX_EXPIRATION_TIMESTAMP
        ).join()
        Assert.assertEquals("OK", response.status)
    }

    @Test
    fun testWithdrawalERC721() {
        val (account, erc721Ownership) = Fixtures.fetchStarkKeysWhichOwnERC721OnLayer2()

        val client = DefaultReddioClient.testnet()
        val response = client.withStarkExSigner(account.starkPrivateKey).withdrawalERC721(
            Fixtures.ReddioTestERC721ContractAddress,
            erc721Ownership.tokenId,
            ReddioClient.TOKEN_TYPE_ERC721,
            account.ethAddress,
            ReddioClient.MAX_EXPIRATION_TIMESTAMP
        ).join()
        Assert.assertEquals("OK", response.status)
    }

    @Test
    fun testWithdrawalERC721M() {
        val (account, erc721MOwnership) = Fixtures.fetchStarkKeysWhichOwnERC721MOnLayer2()

        val client = DefaultReddioClient.testnet()
        val response = client.withStarkExSigner(account.starkPrivateKey).withdrawalERC721(
            erc721MOwnership.contractAddress,
            erc721MOwnership.tokenId,
            ReddioClient.TOKEN_TYPE_ERC721M,
            account.ethAddress,
            ReddioClient.MAX_EXPIRATION_TIMESTAMP
        ).join()
        Assert.assertEquals("OK", response.status)
    }

    @Test
    fun testWithdrawalStatus() {
        val client = DefaultReddioClient.testnet()
        for (item in StarkKeysPool.pool()) {
            val response = client.withdrawalStatus(WithdrawalStatusMessage.STAGE_WITHDRAWAREA, item.starkKey).join()
            Assert.assertEquals("OK", response.status)
        }
    }
}