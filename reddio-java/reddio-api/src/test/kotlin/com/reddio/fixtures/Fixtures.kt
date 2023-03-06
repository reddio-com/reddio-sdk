package com.reddio.fixtures

import com.reddio.api.v1.QuantizedHelper
import com.reddio.api.v1.ReddioClient
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
        data class ETHOwnership(val starkKey: String, val balance: Long, val displayBalance: String)

        /**
         * Class ERC20Ownership represents the ownership of ERC20 token on layer 2 belongs to the StarkKey.
         */
        data class ERC20Ownership(
            val starkKey: String,
            val contractAddress: String,
            val balance: Long,
            val displayBalance: String
        )


        /**
         * Class ERC721Ownership represents the ownership of ERC721 token on layer 2 belongs to the StarkKey.
         */
        data class ERC721Ownership(val starkKey: String, val contractAddress: String, val tokenId: String)

        val ReddioTestERC20ContractAddress = "0x57f3560b6793dcc2cb274c39e8b8eba1dd18a086"
        val ReddioTestERC721ContractAddress = "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5"

        /**
         * Fetches the combination of StarkKeys private and public keys, which owns at least one NFT.
         *
         * And it would return the richer one to make the balance of the account tend to balance.
         */
        fun fetchStarkKeysWhichOwnedETH(amount: String): Pair<EthAndStarkKeys, ETHOwnership> {
            val quantizedAmount = runBlocking {
                QuantizedHelper(DefaultReddioRestClient.testnet()).quantizedAmount(
                    amount,
                    ReddioClient.TOKEN_TYPE_ETH,
                    ReddioClient.TOKEN_TYPE_ETH
                )
            }
            val restClient = DefaultReddioRestClient.testnet()
            return StarkKeysPool.pool().parallelStream().map {
                val balances = runBlocking {
                    restClient.getBalances(
                        GetBalancesMessage.of(
                            it.starkKey, ReddioClient.TOKEN_TYPE_ETH, null, null
                        )
                    ).await()
                }
                Pair(it, balances)
            }.map {
                val owner = it.first
                val balances = it.second
                val ownedETH =
                    balances.data.list.parallelStream().filter { entry -> entry.balanceAvailable > quantizedAmount }
                        .findAny()
                Pair(owner, ownedETH)
            }.filter {
                it.second.isPresent
            }.sorted { first, second ->
                second.second.get().balanceAvailable.compareTo(first.second.get().balanceAvailable)
            }.map {
                val owner = it.first
                val ownedETH = it.second.get()
                val ownership = ETHOwnership(owner.starkKey, ownedETH.balanceAvailable, ownedETH.displayValue)
                Pair(owner, ownership)
            }.findFirst()
                .orElseThrow { FixtureException("Insufficient test assets ETH for amount $amount from given stark keys") }
        }

        /**
         * Fetches the combination of StarkKeys private and public keys, which owns at least one NFT.
         *
         * And it would return the richer one to make the balance of the account tend to balance.
         */
        fun fetchStarkKeysWhichOwnedERC20(
            contractAddress: String = ReddioTestERC20ContractAddress,
            amount: String
        ): Pair<EthAndStarkKeys, ERC20Ownership> {
            val quantizedAmount = runBlocking {
                QuantizedHelper(DefaultReddioRestClient.testnet()).quantizedAmount(
                    amount,
                    ReddioClient.TOKEN_TYPE_ERC20,
                    contractAddress
                )
            }

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
                val ownedERC20 =
                    balances.data.list.parallelStream().filter { entry -> entry.balanceAvailable > quantizedAmount }
                        .findAny()
                Pair(owner, ownedERC20)
            }.filter {
                it.second.isPresent
            }.sorted { first, second ->
                second.second.get().balanceAvailable.compareTo(first.second.get().balanceAvailable)
            }.map {
                val owner = it.first
                val ownedERC20 = it.second.get()
                val ownership = ERC20Ownership(
                    owner.starkKey,
                    ownedERC20.contractAddress,
                    ownedERC20.balanceAvailable,
                    ownedERC20.displayValue
                )
                Pair(owner, ownership)
            }.findFirst()
                .orElseThrow { FixtureException("Insufficient test assets ERC20 $contractAddress for amount $amount from given stark keys") }
        }

        /**
         * Fetches the combination of StarkKeys private and public keys, which owns at least one NFT.
         *
         * And it would return the richer one to make the balance of the account tend to balance.
         */
        fun fetchStarkKeysWhichOwnedERC721(
            contractAddress: String = ReddioTestERC721ContractAddress
        ): Pair<EthAndStarkKeys, ERC721Ownership> {
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
            }.sorted { first, second ->
                second.second.data.list.size.compareTo(first.second.data.list.size)
            }.map {
                val owner = it.first
                val balances = it.second
                val ownedERC721 =
                    balances.data.list.parallelStream().filter { entry -> entry.balanceAvailable > 0 }.findAny()
                Pair(owner, ownedERC721)
            }.filter {
                it.second.isPresent
            }.map {
                val owner = it.first
                val ownedERC721 = it.second.get()
                val ownership = ERC721Ownership(owner.starkKey, ownedERC721.contractAddress, ownedERC721.tokenId)
                Pair(owner, ownership)
            }.findFirst()
                .orElseThrow { FixtureException("Insufficient test assets ERC721 $contractAddress from given stark keys") }
        }

    }
}