package com.reddio.fixtures

import com.reddio.api.v1.DefaultReddioClient
import com.reddio.api.v1.StarkKeys
import com.reddio.api.v1.rest.DefaultReddioRestClient
import com.reddio.api.v1.rest.GetBalancesMessage
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking

/**
 * Class Fixtures prepares test data for integration tests.
 */
class Fixtures {

    companion object {

        /**
         * Class ETHOwnership represents the ownership of ETH on layer 2 belongs to the StarkKey.
         */
        data class ETHOwnership(val starkKey: String, val amount: String)

        /**
         * Class ERC20Ownership represents the ownership of ERC20 token on layer 2 belongs to the StarkKey.
         */
        data class ERC20Ownership(val starkKey: String, val contractAddress: String, val amount: String)


        /**
         * Class ERC721Ownership represents the ownership of ERC721 token on layer 2 belongs to the StarkKey.
         */
        data class ERC721Ownership(val starkKey: String, val contractAddress: String, val tokenId: String)

        val ReddioTestERC721ContractAddress = "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5"

        /**
         * Fetches the combination of StarkKeys private and public keys, which owns at least one NFT.
         *
         */
        fun fetchStarkKeysWhichOwnedERC721(
            contractAddress: String = ReddioTestERC721ContractAddress
        ): Pair<StarkKeys, ERC721Ownership> {
            val restClient = DefaultReddioRestClient.testnet()
            return StarkKeysPool.pool().parallelStream().map {
                val balances = runBlocking {
                    restClient.getBalances(
                        GetBalancesMessage.of(
                            it.starkKey, contractAddress, null, null
                        )
                    ).await()
                }
                Pair(it, balances)
            }.map {
                val owner = it.first
                val balances = it.second
                val ownedERC721 =
                    balances.data.list.parallelStream().filter { entry -> entry.balanceAvailable > 0 }
                        .findAny()
                Pair(owner, ownedERC721)
            }.filter {
                it.second.isPresent
            }.map {
                val owner = it.first
                val ownedERC721 = it.second.get()
                val ownership = ERC721Ownership(owner.starkKey, ownedERC721.contractAddress, ownedERC721.tokenId)
                Pair(owner, ownership)
            }.findAny().orElseThrow { FixtureException("Insufficient test assets ERC721 from given stark keys") }
        }

    }
}