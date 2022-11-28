package com.reddio.api.v1

import com.reddio.abi.Deposits
import com.reddio.abi.Withdrawals
import com.reddio.api.v1.rest.GetAssetIdMessage
import com.reddio.api.v1.rest.GetContractInfoMessage
import com.reddio.api.v1.rest.GetVaultIdMessage
import com.reddio.api.v1.rest.ReddioRestClient
import com.reddio.contract.EthNextEventSubscriber
import com.reddio.gas.GasOption
import com.reddio.gas.StaticGasLimitSuggestionPriceGasProvider
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.web3j.contracts.eip20.generated.ERC20
import org.web3j.contracts.eip721.generated.ERC721
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.Web3jService
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.utils.Numeric
import java.math.BigInteger
import java.util.concurrent.CompletableFuture

class DefaultEthereumInteraction(
    private val restClient: ReddioRestClient,
    private val chainId: Long,
    web3jService: Web3jService,
    private val credentials: Credentials,
) : EthereumInteraction {

    private val web3j = Web3j.build(web3jService);
    private val quantizedHelper = QuantizedHelper(restClient)

    override fun depositETH(
        starkKey: String,
        quantizedAmount: String,
        gasOption: GasOption,
    ): CompletableFuture<LogDeposit> {
        val gasProvider = StaticGasLimitSuggestionPriceGasProvider(
            this.chainId, gasOption, StaticGasLimitSuggestionPriceGasProvider.DEFAULT_GAS_LIMIT
        )
        return CompletableFuture.supplyAsync {
            runBlocking {
                asyncDepositETH(starkKey, quantizedAmount, gasProvider)
            }
        }
    }

    override fun depositERC20(
        tokenAddress: String, starkKey: String, amount: String, gasOption: GasOption
    ): CompletableFuture<LogDeposit> {
        val gasProvider = StaticGasLimitSuggestionPriceGasProvider(
            this.chainId, gasOption, StaticGasLimitSuggestionPriceGasProvider.DEFAULT_GAS_LIMIT
        )
        return CompletableFuture.supplyAsync {
            runBlocking {
                asyncERC20Approve(tokenAddress, amount, web3j, gasProvider)
                asyncDepositERC20(tokenAddress, starkKey, amount, gasProvider)
            }
        }
    }

    private suspend fun asyncERC20Approve(
        erc20ContractAddress: String,
        amount: String,
        web3j: Web3j,
        gasProvider: ContractGasProvider,
    ): ERC20.ApprovalEventResponse {

        val nonQuantizedAmount = quantizedHelper.nonQuantizedAmount(amount, "ERC20", erc20ContractAddress)
        val erc20Contract = ERC20.load(erc20ContractAddress, web3j, credentials, gasProvider)
        val call = erc20Contract.approve(
            this.reddioStarexContractAddress(),
            nonQuantizedAmount.toBigInteger(),
        )
        call.send()
        return EthNextEventSubscriber.create(erc20Contract::approvalEventFlowable, web3j).subscribeNextEvent()
    }

    private suspend fun asyncERC721Approve(
        erc721ContractAddress: String,
        gasProvider: ContractGasProvider,
        tokenId: String,
    ): ERC721.ApprovalEventResponse {
        val erc721Contract = ERC721.load(erc721ContractAddress, web3j, credentials, gasProvider)
        val call = erc721Contract.approve(
            this.reddioStarexContractAddress(),
            BigInteger(tokenId, 10),
            BigInteger.ZERO,
        )
        call.send()
        return EthNextEventSubscriber.create(erc721Contract::approvalEventFlowable, web3j).subscribeNextEvent()
    }

    override fun depositERC721(
        tokenAddress: String, tokenId: String, starkKey: String, gasOption: GasOption
    ): CompletableFuture<LogDepositWithToken> {
        val gasProvider = StaticGasLimitSuggestionPriceGasProvider(
            this.chainId, gasOption, StaticGasLimitSuggestionPriceGasProvider.DEFAULT_GAS_LIMIT
        )
        return CompletableFuture.supplyAsync {
            runBlocking {
                asyncERC721Approve(tokenAddress, gasProvider, tokenId);
                asyncDepositERC721(tokenAddress, tokenId, starkKey, gasProvider)
            }
        }
    }

    internal suspend fun asyncDepositETH(
        starkKey: String, amount: String, gasProvider: ContractGasProvider
    ): LogDeposit {
        val (assetId, assetType) = getAssetTypeAndId("ETH", "ETH", "")
        val vaultId =
            restClient.getVaultId(GetVaultIdMessage.of(assetId, listOf(starkKey))).await().getData().vaultIds[0]
        val deposits = Deposits.load(
            this.reddioStarexContractAddress(), web3j, credentials, gasProvider
        )

        val quantizedAmount = quantizedHelper.quantizedAmount(amount, "ETH", "ETH")
        val call = deposits.depositEth(
            BigInteger(starkKey.lowercase().replace("0x", ""), 16),
            BigInteger(assetType.lowercase().replace("0x", ""), 16),
            BigInteger(vaultId, 10),
            quantizedAmount.toBigInteger(),
        );

        call.send();
        val event = EthNextEventSubscriber.create(deposits::logDepositEventFlowable, web3j).subscribeNextEvent()
        return LogDeposit.of(
            event.depositorEthKey,
            event.starkKey.toString(16),
            event.vaultId.toString(10),
            event.assetType.toString(16),
            event.nonQuantizedAmount.toString(10),
            event.quantizedAmount.toString(10)
        )
    }

    internal suspend fun asyncDepositERC20(
        tokenAddress: String,
        starkKey: String,
        amount: String,
        gasProvider: ContractGasProvider
    ): LogDeposit {
        val (assetId, assetType) = getAssetTypeAndId("ERC20", tokenAddress, "")

        val quantizedAmount = quantizedHelper.quantizedAmount(amount, "ERC20", tokenAddress)
        val vaultId =
            restClient.getVaultId(GetVaultIdMessage.of(assetId, listOf(starkKey))).await().getData().vaultIds[0]
        val deposits = Deposits.load(
            this.reddioStarexContractAddress(), web3j, credentials, gasProvider
        )

        val call = deposits.depositERC20(
            BigInteger(starkKey.lowercase().replace("0x", ""), 16),
            BigInteger(assetType.lowercase().replace("0x", ""), 16),
            BigInteger(vaultId, 10),
            quantizedAmount.toBigInteger(),
        )
        call.send()
        val event = EthNextEventSubscriber.create(deposits::logDepositEventFlowable, web3j).subscribeNextEvent()
        return LogDeposit.of(
            event.depositorEthKey,
            event.starkKey.toString(16),
            event.vaultId.toString(10),
            event.assetType.toString(16),
            event.nonQuantizedAmount.toString(10),
            event.quantizedAmount.toString(10)
        )
    }

    internal suspend fun asyncDepositERC721(
        tokenAddress: String,
        tokenId: String,
        starkKey: String,
        gasProvider: ContractGasProvider,
    ): LogDepositWithToken {
        val (assetId, assetType) = getAssetTypeAndId("ERC721", tokenAddress, tokenId)

        val vaultId =
            restClient.getVaultId(GetVaultIdMessage.of(assetId, listOf(starkKey))).await().getData().vaultIds[0]

        val deposits = Deposits.load(
            this.reddioStarexContractAddress(), web3j, credentials, gasProvider
        )
        val call = deposits.depositNft(
            BigInteger(starkKey.lowercase().replace("0x", ""), 16),
            BigInteger(assetType.lowercase().replace("0x", ""), 16),
            BigInteger(vaultId, 10),
            BigInteger(tokenId, 10),
        )
        call.send()
        val event =
            EthNextEventSubscriber.create(deposits::logDepositWithTokenIdEventFlowable, web3j).subscribeNextEvent()
        return LogDepositWithToken.of(
            event.depositorEthKey,
            event.starkKey.toString(16),
            event.vaultId.toString(10),
            event.assetType.toString(16),
            event.tokenId.toString(10),
            event.assetId.toString(16),
            event.nonQuantizedAmount.toString(10),
            event.quantizedAmount.toString(10)
        )
    }


    override fun withdrawETHOrERC20(
        ethAddress: String,
        assetType: String,
        gasOption: GasOption
    ): CompletableFuture<TransactionReceipt> {
        val gasProvider = StaticGasLimitSuggestionPriceGasProvider(
            this.chainId, gasOption, StaticGasLimitSuggestionPriceGasProvider.DEFAULT_GAS_LIMIT
        )
        return CompletableFuture.supplyAsync {
            runBlocking {
                asyncWithdrawal(ethAddress, assetType, gasProvider)
            }
        }
    }

    override fun withdrawalERC721(
        ethAddress: String,
        assetType: String,
        tokenId: String,
        gasOption: GasOption
    ): CompletableFuture<TransactionReceipt> {
        val gasProvider = StaticGasLimitSuggestionPriceGasProvider(
            this.chainId, gasOption, StaticGasLimitSuggestionPriceGasProvider.DEFAULT_GAS_LIMIT
        )
        return CompletableFuture.supplyAsync {
            runBlocking {
                asyncWithdrawalERC721(ethAddress, assetType, tokenId, gasProvider)
            }
        }
    }

    override fun withdrawalERC721M(
        ethAddress: String,
        assetType: String,
        tokenId: String,
        gasOption: GasOption
    ): CompletableFuture<TransactionReceipt> {
        val gasProvider = StaticGasLimitSuggestionPriceGasProvider(
            this.chainId, gasOption, StaticGasLimitSuggestionPriceGasProvider.DEFAULT_GAS_LIMIT
        )
        return CompletableFuture.supplyAsync {
            runBlocking {
                asyncWithdrawalERC721M(ethAddress, assetType, tokenId, gasProvider)
            }
        }
    }

    internal suspend fun asyncWithdrawal(
        ethAddress: String,
        assetType: String,
        gasProvider: ContractGasProvider,
    ): TransactionReceipt {
        val withdrawals = Withdrawals.load(this.reddioStarexContractAddress(), web3j, credentials, gasProvider)
        return withdrawals.withdraw(
            BigInteger(ethAddress.lowercase().replace("0x", ""), 16),
            BigInteger(assetType.lowercase().replace("0x", ""), 16),
        ).sendAsync().await()
    }

    internal suspend fun asyncWithdrawalERC721(
        ethAddress: String,
        assetType: String,
        tokenId: String,
        gasProvider: ContractGasProvider,
    ): TransactionReceipt {
        val withdrawals = Withdrawals.load(this.reddioStarexContractAddress(), web3j, credentials, gasProvider)
        return withdrawals.withdrawNft(
            BigInteger(ethAddress.lowercase().replace("0x", ""), 16),
            BigInteger(assetType.lowercase().replace("0x", ""), 16),
            BigInteger(tokenId, 10),
        ).sendAsync().await()
    }

    internal suspend fun asyncWithdrawalERC721M(
        ethAddress: String,
        assetType: String,
        tokenId: String,
        gasProvider: ContractGasProvider,
    ): TransactionReceipt {
        val withdrawals = Withdrawals.load(this.reddioStarexContractAddress(), web3j, credentials, gasProvider)

        return withdrawals.withdrawAndMint(
            BigInteger(ethAddress.lowercase().replace("0x", ""), 16),
            BigInteger(assetType.lowercase().replace("0x", ""), 16),
            Numeric.hexStringToByteArray(BigInteger(tokenId, 10).toString(16)),
        ).sendAsync().await()
    }

    private suspend fun getAssetTypeAndId(
        type: String,
        tokenAddress: String,
        tokenId: String,
    ): AssetIdAndAssetType {
        val contractInfo = restClient.getContractInfo(GetContractInfoMessage.of(type, tokenAddress)).await().getData()
        val result =
            restClient.getAssetId(GetAssetIdMessage.of(tokenAddress, type, tokenId, contractInfo.quantum)).await()
        return AssetIdAndAssetType(result.getData().getAssetId(), contractInfo.getAssetType())
    }

    private suspend fun reddioStarexContractAddress(): String {
        val starexContractsResponseResponseWrapper = this.restClient.starexContracts().await()
        if (this.chainId == MAINNET_ID) {
            return starexContractsResponseResponseWrapper.data.mainnet
        }
        return starexContractsResponseResponseWrapper.data.testnet
    }

    companion object {
        const val MAINNET_ID = 1L;
        const val GOERIL_ID = 5L;

        @JvmStatic
        fun build(
            restClient: ReddioRestClient,
            chainId: Long,
            web3jService: Web3jService,
            credentials: Credentials,
        ): DefaultEthereumInteraction {
            return DefaultEthereumInteraction(restClient, chainId, web3jService, credentials)
        }

        @JvmStatic
        fun build(
            restClient: ReddioRestClient,
            chainId: Long,
            ethJSONRpcHTTPEndpoint: String,
            credentials: Credentials,
        ): DefaultEthereumInteraction {
            return DefaultEthereumInteraction(
                restClient,
                chainId,
                HttpService(ethJSONRpcHTTPEndpoint),
                credentials
            )
        }

        @JvmStatic
        fun build(
            restClient: ReddioRestClient,
            chainId: Long,
            ethJSONRpcHTTPEndpoint: String,
            ethPrivateKey: String,
        ): DefaultEthereumInteraction {
            return DefaultEthereumInteraction(
                restClient,
                chainId,
                HttpService(ethJSONRpcHTTPEndpoint),
                Credentials.create(ethPrivateKey)
            )
        }

    }
}
