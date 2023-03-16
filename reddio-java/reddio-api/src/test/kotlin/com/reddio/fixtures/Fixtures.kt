package com.reddio.fixtures

import com.reddio.api.v1.DefaultEthereumInteraction
import com.reddio.api.v1.QuantizedHelper
import com.reddio.api.v1.ReddioClient
import com.reddio.api.v1.StarkKeys
import com.reddio.api.v1.rest.DefaultReddioRestClient
import com.reddio.api.v1.rest.GetBalancesMessage
import com.reddio.api.v1.rest.WithdrawalStatusMessage
import com.reddio.api.v1.rest.WithdrawalStatusResponse.WithdrawalStatusRecord
import com.reddio.gas.GasOption
import com.reddio.gas.StaticGasLimitSuggestionPriceGasProvider
import jdk.internal.joptsimple.internal.Strings
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.web3j.contracts.eip20.generated.ERC20
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Class Fixtures prepares test data for integration tests.
 */
class Fixtures {

    companion object {


        data class ETHOwnership(val ethAddress: String, val balance: BigInteger, val displayBalance: String)
        data class ERC20Ownership(
            val ethAddress: String, val contractAddress: String, val balance: BigInteger, val displayBalance: String
        )

        data class ERC721Ownership(val ethAddress: String, val contractAddress: String, val tokenId: String)

        /**
         * Class ETHOwnership represents the ownership of ETH on layer 2 belongs to the StarkKey.
         */
        data class Layer2ETHOwnership(val starkKey: String, val balance: Long, val displayBalance: String)

        /**
         * Class ERC20Ownership represents the ownership of ERC20 token on layer 2 belongs to the StarkKey.
         */
        data class Layer2ERC20Ownership(
            val starkKey: String, val contractAddress: String, val balance: Long, val displayBalance: String
        )


        /**
         * Class ERC721Ownership represents the ownership of ERC721 token on layer 2 belongs to the StarkKey.
         */
        data class Layer2ERC721Ownership(
            val starkKey: String,
            val contractAddress: String,
            val tokenId: String,
            val type: String
        )

        val ReddioTestERC20ContractAddress = "0x57f3560b6793dcc2cb274c39e8b8eba1dd18a086"
        val ReddioTestERC721ContractAddress = "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5"
        val ReddioTestERC721MContractAddress = "0x113536494406bc039586c1ad9b8f51af664d6ef8"

        /**
         * Fetches the combination of StarkKeys private and public keys, which owns at least one NFT.
         *
         * And it would return the richer one to make the balance of the account tend to balance.
         */
        fun fetchStarkKeysWhichOwnETHOnLayer2(amount: String): Pair<EthAndStarkKeys, Layer2ETHOwnership> {
            val quantizedAmount = runBlocking {
                QuantizedHelper(DefaultReddioRestClient.testnet()).quantizedAmount(
                    amount, ReddioClient.TOKEN_TYPE_ETH, ReddioClient.TOKEN_TYPE_ETH
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
                val ownership = Layer2ETHOwnership(owner.starkKey, ownedETH.balanceAvailable, ownedETH.displayValue)
                Pair(owner, ownership)
            }.findFirst()
                .orElseThrow { FixtureException("Insufficient test assets ETH for amount $amount from given stark keys") }
        }

        /**
         * Fetches the combination of StarkKeys private and public keys, which owns at least one NFT.
         *
         * And it would return the richer one to make the balance of the account tend to balance.
         */
        fun fetchStarkKeysWhichOwnERC20OnLayer2(
            contractAddress: String = ReddioTestERC20ContractAddress, amount: String
        ): Pair<EthAndStarkKeys, Layer2ERC20Ownership> {
            val quantizedAmount = runBlocking {
                QuantizedHelper(DefaultReddioRestClient.testnet()).quantizedAmount(
                    amount, ReddioClient.TOKEN_TYPE_ERC20, contractAddress
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
                val ownership = Layer2ERC20Ownership(
                    owner.starkKey, ownedERC20.contractAddress, ownedERC20.balanceAvailable, ownedERC20.displayValue
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
        fun fetchStarkKeysWhichOwnERC721OnLayer2(
            contractAddress: String = ReddioTestERC721ContractAddress
        ): Pair<EthAndStarkKeys, Layer2ERC721Ownership> {
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
            }.filter {
                it.second.get().type == ReddioClient.TOKEN_TYPE_ERC721
            }.map {
                val owner = it.first
                val ownedERC721 = it.second.get()
                val ownership = Layer2ERC721Ownership(
                    owner.starkKey,
                    ownedERC721.contractAddress,
                    ownedERC721.tokenId,
                    ownedERC721.type
                )
                Pair(owner, ownership)
            }.findFirst()
                .orElseThrow { FixtureException("Insufficient test assets ERC721 $contractAddress from given stark keys") }
        }

        fun fetchStarkKeysWhichOwnERC721MOnLayer2(
            contractAddress: String = ReddioTestERC721MContractAddress
        ): Pair<EthAndStarkKeys, Layer2ERC721Ownership> {
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
            }.filter {
                it.second.get().type == ReddioClient.TOKEN_TYPE_ERC721M
            }.map {
                val owner = it.first
                val ownedERC721 = it.second.get()
                val ownership = Layer2ERC721Ownership(
                    owner.starkKey,
                    ownedERC721.contractAddress,
                    ownedERC721.tokenId,
                    ownedERC721.type
                )
                Pair(owner, ownership)
            }.findFirst()
                .orElseThrow { FixtureException("Insufficient test assets ERC721M $contractAddress from given stark keys") }
        }


        /**
         * return the ETH json rpc node url restored from the environment variable INTEGRATION_TEST_ETH_JSON_RPC_NODE
         */
        fun fetchETHJsonRPCNode(): String {
            val env = System.getenv("INTEGRATION_TEST_ETH_JSON_RPC_NODE")
            if (Strings.isNullOrEmpty(env)) {
                throw FixtureException("environment variable INTEGRATION_TEST_ETH_JSON_RPC_NODE is not set")
            }
            return env
        }

        fun displayValueOfETHOrERC20(amount: BigInteger, decimals: Int): String {
            return amount.divide(BigInteger.TEN.pow(decimals)).toString()
        }

        fun fetchStarkKeysWhichOwnETHOnLayer1(balance: String): Pair<EthAndStarkKeys, ETHOwnership> {
            val web3j = Web3j.build(HttpService(fetchETHJsonRPCNode()))

            val balanceInWei = Convert.toWei(balance, Convert.Unit.ETHER).toBigInteger();
            val result = StarkKeysPool.pool().parallelStream().map {
                val ethGetBalance = web3j.ethGetBalance(it.ethAddress, DefaultBlockParameterName.LATEST).send()
                Pair(it, ethGetBalance.balance)
            }.filter {
                it.second >= balanceInWei
            }.sorted { a, b ->
                b.second.compareTo(a.second)
            }.findFirst().orElseThrow {
                FixtureException("Insufficient test assets ETH for amount $balance from given eth accounts")
            }
            return Pair(
                result.first,
                ETHOwnership(result.first.ethAddress, result.second, displayValueOfETHOrERC20(result.second, 18))
            )
        }

        fun fetchStarkKeysWhichOwnERC20OnLayer1(
            contractAddress: String = ReddioTestERC20ContractAddress, balance: String
        ): Pair<EthAndStarkKeys, ERC20Ownership> {
            val web3j = Web3j.build(HttpService(fetchETHJsonRPCNode()))
            return StarkKeysPool.pool().parallelStream().map {
                val contract = ERC20.load(
                    contractAddress,
                    web3j,
                    Credentials.create(it.ethPrivateKey),
                    StaticGasLimitSuggestionPriceGasProvider(
                        DefaultEthereumInteraction.GOERIL_ID, GasOption.Market, BigInteger.valueOf(100000L)
                    )
                )
                val erc20Balance = contract.balanceOf(it.ethAddress).send()
                val decimals = contract.decimals().send()
                Triple(it, erc20Balance, decimals.toLong())
            }.filter { it ->
                val decimals = it.third
                val requiredBalance =
                    balance.toBigDecimal().multiply(BigDecimal.TEN.pow(decimals.toInt())).toBigInteger()
                it.second >= requiredBalance
            }.sorted { a, b ->
                b.second.compareTo(a.second)
            }.findFirst().orElseThrow {
                FixtureException("Insufficient test assets ERC20 $contractAddress for amount $balance from given eth accounts")
            }.let { result ->
                val decimals = result.third
                val balance = result.second
                Pair(
                    result.first, ERC20Ownership(
                        result.first.ethAddress,
                        result.first.ethPrivateKey,
                        balance,
                        displayValueOfETHOrERC20(balance, decimals.toInt())
                    )
                )
            }
        }

        fun fetchStarkKeysWhichOwnERC721OnLayer1(
            contractAddress: String = ReddioTestERC721ContractAddress,
        ): Pair<EthAndStarkKeys, ERC721Ownership> {
            return StarkKeysPool.pool().stream().map {
                val erc721s = EtherscanNFTFetcher.listERC721OwnedByEthAddress(it.ethAddress, contractAddress)
                Pair(it, erc721s)
            }.filter {
                it.second.isNotEmpty()
            }.sorted { a, b ->
                b.second.size.compareTo(a.second.size)
            }.findFirst().orElseThrow {
                FixtureException("Insufficient test assets ERC721 $contractAddress from given eth accounts")
            }.let { result ->
                val erc721 = result.second[0]
                Pair(result.first, erc721)
            }
        }

        fun fetchStarkKeysWhichCouldWithdrawalETHOnLayer1(): Pair<EthAndStarkKeys, WithdrawalStatusRecord> {
            return StarkKeysPool.pool().stream().map {
                val restClient = DefaultReddioRestClient.testnet()
                val response = restClient.withdrawalStatus(
                    WithdrawalStatusMessage.of(
                        WithdrawalStatusMessage.STAGE_WITHDRAWAREA, it.ethAddress
                    )
                ).join()
                Pair(it, response)
            }.flatMap {
                val keys = it.first
                val response = it.second
                response.data.stream().map { item ->
                    Pair(keys, item)
                }
            }.filter {
                val withdrawalAsset = it.second
                withdrawalAsset.type.toLowerCase() == "eth"
            }.findAny().orElseThrow {
                FixtureException("Insufficient test assets ETH for withdrawal from given stark keys")
            }.let { result ->
                val keys = result.first
                val withdrawalAsset = result.second
                Pair(keys, withdrawalAsset)
            }
        }

        fun fetchStarkKeysWhichCouldWithdrawalERC20OnLayer1(contractAddress: String = ReddioTestERC20ContractAddress): Pair<EthAndStarkKeys, WithdrawalStatusRecord> {
            return StarkKeysPool.pool().stream().map {
                val restClient = DefaultReddioRestClient.testnet()
                val response = restClient.withdrawalStatus(
                    WithdrawalStatusMessage.of(
                        WithdrawalStatusMessage.STAGE_WITHDRAWAREA, it.ethAddress
                    )
                ).join()
                Pair(it, response)
            }.flatMap {
                val keys = it.first
                val response = it.second
                response.data.stream().map { item ->
                    Pair(keys, item)
                }
            }.filter {
                val withdrawalAsset = it.second
                withdrawalAsset.getType().toLowerCase() == "erc20" &&
                        withdrawalAsset.getContractAddress().equals(contractAddress, ignoreCase = true)
            }.findAny().orElseThrow {
                FixtureException("Insufficient test assets ETH for withdrawal from given stark keys")
            }.let { result ->
                val keys = result.first
                val withdrawalAsset = result.second
                Pair(keys, withdrawalAsset)
            }
        }

        fun fetchStarkKeysWhichCouldWithdrawalERC721OnLayer1(contractAddress: String = ReddioTestERC721ContractAddress): Pair<EthAndStarkKeys, WithdrawalStatusRecord> {
            return StarkKeysPool.pool().stream().map {
                val restClient = DefaultReddioRestClient.testnet()
                val response = restClient.withdrawalStatus(
                    WithdrawalStatusMessage.of(
                        WithdrawalStatusMessage.STAGE_WITHDRAWAREA, it.ethAddress
                    )
                ).join()
                Pair(it, response)
            }.flatMap {
                val keys = it.first
                val response = it.second
                response.data.stream().map { item ->
                    Pair(keys, item)
                }
            }.filter {
                val withdrawalAsset = it.second
                withdrawalAsset.getType().toLowerCase() == "erc721" &&
                        withdrawalAsset.getContractAddress().equals(contractAddress, ignoreCase = true)
            }.findAny().orElseThrow {
                FixtureException("Insufficient test assets ETH for withdrawal from given stark keys")
            }.let { result ->
                val keys = result.first
                val withdrawalAsset = result.second
                Pair(keys, withdrawalAsset)
            }
        }

        fun fetchStarkKeysWhichCouldWithdrawalERC721MOnLayer1(contractAddress: String = ReddioTestERC721MContractAddress): Pair<EthAndStarkKeys, WithdrawalStatusRecord> {
            return StarkKeysPool.pool().stream().map {
                val restClient = DefaultReddioRestClient.testnet()
                val response = restClient.withdrawalStatus(
                    WithdrawalStatusMessage.of(
                        WithdrawalStatusMessage.STAGE_WITHDRAWAREA, it.ethAddress
                    )
                ).join()
                Pair(it, response)
            }.flatMap {
                val keys = it.first
                val response = it.second
                response.data.stream().map { item ->
                    Pair(keys, item)
                }
            }.filter {
                val withdrawalAsset = it.second
                withdrawalAsset.getType().toLowerCase() == "erc721m" &&
                        withdrawalAsset.getContractAddress().equals(contractAddress, ignoreCase = true)
            }.findAny().orElseThrow {
                FixtureException("Insufficient test assets ETH for withdrawal from given stark keys")
            }.let { result ->
                val keys = result.first
                val withdrawalAsset = result.second
                Pair(keys, withdrawalAsset)
            }
        }


        fun fetchReddioAPIKey(): String {
            val env = System.getenv("INTEGRATION_TEST_REDDIO_API_KEY")
            if (Strings.isNullOrEmpty(env)) {
                throw FixtureException("environment variable INTEGRATION_TEST_REDDIO_API_KEY is not set")
            }
            return env
        }
    }
}