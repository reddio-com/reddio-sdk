package com.reddio.api.v1

import com.reddio.ReddioException
import com.reddio.abi.Deposits
import com.reddio.api.v1.rest.*
import com.reddio.contract.EthNextEventSubscriber
import com.reddio.crypto.CryptoService
import com.reddio.gas.GasOption
import com.reddio.gas.StaticGasLimitSuggestionPriceGasProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.web3j.contracts.eip20.generated.ERC20
import org.web3j.contracts.eip721.generated.ERC721
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.utils.Convert
import java.math.BigInteger
import java.time.Duration
import java.time.Instant
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.pow
import kotlin.time.toKotlinDuration

class DefaultReddioClient(
    private val restClient: ReddioRestClient, private val chainId: Long, ethJSONRpcHTTPEndpoint: String
) : ReddioClient {

    private val web3j = Web3j.build(HttpService(ethJSONRpcHTTPEndpoint))

    override fun transfer(
        starkKey: String,
        privateKey: String,
        amount: String,
        contractAddress: String,
        tokenId: String,
        type: String,
        receiver: String,
        expirationTimeStamp: Long
    ): CompletableFuture<ResponseWrapper<TransferResponse>> {
        return CompletableFuture.supplyAsync {
            runBlocking {
                val assetId = getAssetId(contractAddress, tokenId, type)

                val quantizedAmount = quantizedAmount(amount, type, contractAddress).toString()
                val vaultsIds = getVaultsIds(assetId, starkKey, receiver)
                val senderVaultId = vaultsIds.senderVaultId
                val receiverVaultId = vaultsIds.receiverVaultId
                val nonce = restClient.getNonce(GetNonceMessage.of(starkKey)).await().getData().getNonce()
                val signature = signTransferMessage(
                    privateKey,
                    quantizedAmount,
                    nonce,
                    senderVaultId,
                    assetId,
                    receiverVaultId,
                    receiver,
                    expirationTimeStamp
                )
                restClient.transfer(
                    TransferMessage.of(
                        assetId,
                        starkKey,
                        quantizedAmount,
                        nonce,
                        senderVaultId,
                        receiver,
                        receiverVaultId,
                        expirationTimeStamp,
                        signature
                    )
                ).await()
            }
        }
    }

    override fun getRecord(starkKey: String?, sequenceId: Long): CompletableFuture<ResponseWrapper<GetRecordResponse>> {
        return restClient.getRecord(GetRecordMessage.of(starkKey, sequenceId))
    }

    override fun waitingTransferGetApproved(
        starkKey: String, sequenceId: Long
    ): CompletableFuture<ResponseWrapper<GetRecordResponse>> {
        val neverStop = AtomicBoolean(false);
        return waitingTransferGetApproved(
            starkKey, sequenceId,
            Duration.ofSeconds(5),
            Duration.ofSeconds(60),
            neverStop,
        )
    }

    override fun waitingTransferGetApproved(
        starkKey: String, sequenceId: Long, interval: Duration, deadline: Duration, shouldStop: AtomicBoolean
    ): CompletableFuture<ResponseWrapper<GetRecordResponse>> {
        val startTime = Instant.now()
        return CompletableFuture.supplyAsync {
            val result: ResponseWrapper<GetRecordResponse>
            runBlocking {
                while (true) {
                    if (shouldStop.get()) {
                        throw InterruptedException("cancelled")
                    }
                    if (Thread.interrupted()) {
                        throw InterruptedException("cancelled")
                    }
                    if (startTime.plus(deadline).isBefore(Instant.now())) {
                        throw InterruptedException("timed out")
                    }
                    val record = restClient.getRecord(GetRecordMessage.of(starkKey, sequenceId)).await()
                    if (GetRecordResponse.SequenceRecord.SEQUENCE_STATUS_ACCEPTED == record.getData()[0].getStatus()) {
                        result = record
                        break
                    }
                    if (GetRecordResponse.SequenceRecord.SEQUENCE_STATUS_FAILED == record.getData()[0].getStatus()) {
                        throw TransferFailedException("transfer failed", record.getData())
                    }
                    delay(interval.toKotlinDuration())
                }
                result
            }
        }
    }

    override fun withdrawal(
        starkKey: String,
        privateKey: String,
        amount: String,
        contractAddress: String,
        tokenId: String,
        type: String,
        receiver: String,
        expirationTimeStamp: Long
    ): CompletableFuture<ResponseWrapper<WithdrawalToResponse>> {
        return CompletableFuture.supplyAsync {
            runBlocking {
                val quantizedAmount = quantizedAmount(amount, type, contractAddress).toString()
                val assetId = getAssetId(contractAddress, tokenId, type)
                val vaultsIds = getVaultsIds(assetId, starkKey, receiver)
                val senderVaultId = vaultsIds.senderVaultId
                val receiverVaultId = vaultsIds.receiverVaultId
                val nonce = restClient.getNonce(GetNonceMessage.of(starkKey)).await().getData().getNonce()
                val signature = signTransferMessage(
                    privateKey,
                    quantizedAmount,
                    nonce,
                    senderVaultId,
                    assetId,
                    receiverVaultId,
                    receiver,
                    expirationTimeStamp
                )
                restClient.withdrawalTo(
                    WithdrawalToMessage.of(
                        contractAddress,
                        assetId,
                        starkKey,
                        quantizedAmount,
                        tokenId,
                        nonce,
                        senderVaultId,
                        receiver,
                        receiverVaultId,
                        expirationTimeStamp,
                        signature
                    )
                ).await()
            }
        }
    }

    override fun order(
        privateKey: String,
        starkKey: String,
        price: String,
        amount: String,
        tokenAddress: String,
        tokenId: String,
        marketplaceUuid: String,
        tokenType: String,
        orderType: OrderType
    ): CompletableFuture<ResponseWrapper<OrderResponse>> {
        return CompletableFuture.supplyAsync {
            runBlocking {
                val orderInfoResponse = restClient.orderInfo(
                    OrderInfoMessage.of(
                        starkKey, "ETH:ETH", String.format("%s:%s:%s", tokenType, tokenAddress, tokenId)
                    )
                ).await()
                if (orderInfoResponse.status != "OK") {
                    throw ReddioException("get order info, status is " + orderInfoResponse.status + ", error is " + orderInfoResponse.error)
                }

                val vaultIds = orderInfoResponse.data.getVaultIds()
                val quoteToken = orderInfoResponse.data.assetIds[1]
                // hard coded format ETH on layer2 (price * (10 **decimals) / quantum)
                val amountBuy =
                    Convert.toWei((price.toDouble() * amount.toDouble()).toString(), Convert.Unit.MWEI).toLong()
                        .toString()
                val formatPrice = Convert.toWei(price, Convert.Unit.MWEI).toString()

                val orderMessage = OrderMessage()
                orderMessage.amount = amount;
                orderMessage.baseToken = orderInfoResponse.data.getBaseToken()
                orderMessage.quoteToken = quoteToken
                orderMessage.price = formatPrice
                orderMessage.starkKey = starkKey
                orderMessage.expirationTimestamp = 4194303;
                orderMessage.nonce = orderInfoResponse.data.nonce;
                orderMessage.feeInfo = FeeInfo.of(
                    (orderInfoResponse.data.feeRate.toDouble() * amountBuy.toDouble()).toLong(),
                    orderInfoResponse.data.feeToken,
                    vaultIds[0].toLong()
                )
                if (orderType == OrderType.BUY) {
                    orderMessage.direction = OrderMessage.DIRECTION_BID
                    orderMessage.tokenSell = orderInfoResponse.data.baseToken
                    orderMessage.tokenBuy = quoteToken
                    orderMessage.amountSell = amountBuy
                    orderMessage.amountBuy = amount
                    orderMessage.vaultIdBuy = vaultIds[1]
                    orderMessage.vaultIdSell = vaultIds[0]
                } else {
                    orderMessage.direction = OrderMessage.DIRECTION_ASK
                    orderMessage.tokenSell = quoteToken
                    orderMessage.tokenBuy = orderInfoResponse.data.baseToken
                    orderMessage.amountSell = amount
                    orderMessage.amountBuy = amountBuy
                    orderMessage.vaultIdBuy = vaultIds[0]
                    orderMessage.vaultIdSell = vaultIds[1]
                }
                orderMessage.signature = signOrderMsgWithFee(
                    privateKey,
                    orderMessage.vaultIdSell,
                    orderMessage.vaultIdBuy,
                    orderMessage.amountSell,
                    orderMessage.amountBuy,
                    orderMessage.tokenSell,
                    orderMessage.tokenBuy,
                    orderMessage.nonce,
                    orderMessage.expirationTimestamp,
                    orderMessage.feeInfo.tokenId,
                    orderMessage.feeInfo.sourceVaultId,
                    orderMessage.feeInfo.feeLimit
                )
                restClient.order(orderMessage).await()
            }
        }
    }

    override fun order(
        privateKey: String,
        starkKey: String,
        contractType: String,
        contractAddress: String,
        tokenId: String,
        price: String,
        amount: String,
        orderType: OrderType,
        baseTokenType: String,
        baseTokenContract: String,
        marketplaceUuid: String
    ): CompletableFuture<ResponseWrapper<OrderResponse>> {
        return CompletableFuture.supplyAsync {
            runBlocking {
                val orderInfoResponse = restClient.orderInfo(
                    OrderInfoMessage.of(
                        starkKey,
                        String.format("%s:%s", baseTokenType, baseTokenContract),
                        String.format("%s:%s:%s", contractType, contractAddress, tokenId)
                    )
                ).await()

                if (orderInfoResponse.status != "OK") {
                    throw RuntimeException("get order info, status is " + orderInfoResponse.status + ", error is " + orderInfoResponse.error)
                }

                val vaultIds = orderInfoResponse.data.getVaultIds()
                val quoteToken = orderInfoResponse.data.assetIds[1]
                val quantizedPrice = quantizedAmount(price, baseTokenType, baseTokenContract)
                val formatPrice = quantizedPrice.toString()
                val amountBuy = (quantizedPrice.toDouble() * amount.toDouble()).toLong().toString()

                val orderMessage = OrderMessage()
                orderMessage.amount = amount;
                orderMessage.baseToken = orderInfoResponse.data.getBaseToken()
                orderMessage.quoteToken = quoteToken
                orderMessage.price = formatPrice
                orderMessage.starkKey = starkKey
                orderMessage.expirationTimestamp = 4194303;
                orderMessage.nonce = orderInfoResponse.data.nonce;
                orderMessage.feeInfo = FeeInfo.of(
                    (orderInfoResponse.data.feeRate.toDouble() * amountBuy.toDouble()).toLong(),
                    orderInfoResponse.data.feeToken,
                    vaultIds[0].toLong()
                )
                if (orderType == OrderType.BUY) {
                    orderMessage.direction = OrderMessage.DIRECTION_BID
                    orderMessage.tokenSell = orderInfoResponse.data.baseToken
                    orderMessage.tokenBuy = quoteToken
                    orderMessage.amountSell = amountBuy
                    orderMessage.amountBuy = amount
                    orderMessage.vaultIdBuy = vaultIds[1]
                    orderMessage.vaultIdSell = vaultIds[0]
                } else {
                    orderMessage.direction = OrderMessage.DIRECTION_ASK
                    orderMessage.tokenSell = quoteToken
                    orderMessage.tokenBuy = orderInfoResponse.data.baseToken
                    orderMessage.amountSell = amount
                    orderMessage.amountBuy = amountBuy
                    orderMessage.vaultIdBuy = vaultIds[0]
                    orderMessage.vaultIdSell = vaultIds[1]
                }
                orderMessage.signature = signOrderMsgWithFee(
                    privateKey,
                    orderMessage.vaultIdSell,
                    orderMessage.vaultIdBuy,
                    orderMessage.amountSell,
                    orderMessage.amountBuy,
                    orderMessage.tokenSell,
                    orderMessage.tokenBuy,
                    orderMessage.nonce,
                    orderMessage.expirationTimestamp,
                    orderMessage.feeInfo.tokenId,
                    orderMessage.feeInfo.sourceVaultId,
                    orderMessage.feeInfo.feeLimit
                )
                restClient.order(orderMessage).await()
            }
        }

    }

    override fun orderWithEth(
        privateKey: String,
        starkKey: String,
        contractType: String,
        contractAddress: String,
        tokenId: String,
        price: String,
        amount: String,
        orderType: OrderType
    ): CompletableFuture<ResponseWrapper<OrderResponse>> {
        return order(
            privateKey, starkKey, contractType, contractAddress, tokenId, price, amount, orderType, "ETH", "eth", ""
        )
    }

    override fun depositETH(
        privateKey: String,
        starkKey: String,
        quantizedAmount: String,
        gasOption: GasOption,
    ): CompletableFuture<LogDeposit> {
        val gasProvider = StaticGasLimitSuggestionPriceGasProvider(
            this.chainId, gasOption, StaticGasLimitSuggestionPriceGasProvider.DEFAULT_GAS_LIMIT
        )
        return CompletableFuture.supplyAsync {
            runBlocking {
                asyncDepositETH(privateKey, starkKey, quantizedAmount, web3j, gasProvider)
            }
        }
    }

    override fun depositERC20(
        privateKey: String, tokenAddress: String, starkKey: String, amount: String, gasOption: GasOption
    ): CompletableFuture<LogDeposit> {
        val gasProvider = StaticGasLimitSuggestionPriceGasProvider(
            this.chainId, gasOption, StaticGasLimitSuggestionPriceGasProvider.DEFAULT_GAS_LIMIT
        )
        return CompletableFuture.supplyAsync {
            runBlocking {
                asyncERC20Approve(privateKey, tokenAddress, amount, web3j, gasProvider)
                asyncDepositERC20(privateKey, tokenAddress, starkKey, amount, web3j, gasProvider)
            }
        }
    }

    private suspend fun asyncERC20Approve(
        privateKey: String,
        erc20ContractAddress: String,
        amount: String,
        web3j: Web3j,
        gasProvider: ContractGasProvider,
    ): ERC20.ApprovalEventResponse {

        val nonQuantizedAmount = this.nonQuantizedAmount(amount, "ERC20", erc20ContractAddress)
        val erc20Contract = ERC20.load(erc20ContractAddress, web3j, Credentials.create(privateKey), gasProvider)
        val call = erc20Contract.approve(
            this.reddioStarexContractAddress(),
            nonQuantizedAmount.toBigInteger(),
        )
        call.send()
        return EthNextEventSubscriber.create(erc20Contract::approvalEventFlowable, web3j).subscribeNextEvent()
    }

    private suspend fun asyncERC721Approve(
        erc721ContractAddress: String,
        privateKey: String,
        gasProvider: ContractGasProvider,
        tokenId: String,
    ): ERC721.ApprovalEventResponse {
        val erc721Contract = ERC721.load(erc721ContractAddress, web3j, Credentials.create(privateKey), gasProvider)
        val call = erc721Contract.approve(
            this.reddioStarexContractAddress(),
            BigInteger(tokenId, 10),
            BigInteger.ZERO,
        )
        call.send()
        return EthNextEventSubscriber.create(erc721Contract::approvalEventFlowable, web3j).subscribeNextEvent()
    }

    override fun depositERC721(
        privateKey: String, tokenAddress: String, tokenId: String, starkKey: String, gasOption: GasOption
    ): CompletableFuture<LogDepositWithToken> {
        val gasProvider = StaticGasLimitSuggestionPriceGasProvider(
            this.chainId, gasOption, StaticGasLimitSuggestionPriceGasProvider.DEFAULT_GAS_LIMIT
        )
        return CompletableFuture.supplyAsync {
            runBlocking {
                asyncERC721Approve(tokenAddress, privateKey, gasProvider, tokenId);
                asyncDepositERC721(privateKey, tokenAddress, tokenId, starkKey, web3j, gasProvider)
            }
        }
    }

    internal suspend fun asyncDepositETH(
        privateKey: String, starkKey: String, amount: String, web3j: Web3j, gasProvider: ContractGasProvider
    ): LogDeposit {
        val (assetId, assetType) = getAssetTypeAndId("ETH", "ETH", "")
        val vaultId =
            restClient.getVaultId(GetVaultIdMessage.of(assetId, listOf(starkKey))).await().getData().vaultIds[0]
        val deposits = Deposits.load(
            this.reddioStarexContractAddress(), web3j, Credentials.create(privateKey), gasProvider
        )

        val quantizedAmount = this.quantizedAmount(amount, "ETH", "ETH")
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
        privateKey: String,
        tokenAddress: String,
        starkKey: String,
        amount: String,
        web3j: Web3j,
        gasProvider: ContractGasProvider
    ): LogDeposit {
        val (assetId, assetType) = getAssetTypeAndId("ERC20", tokenAddress, "")

        val quantizedAmount = quantizedAmount(amount, "ERC20", tokenAddress)
        val vaultId =
            restClient.getVaultId(GetVaultIdMessage.of(assetId, listOf(starkKey))).await().getData().vaultIds[0]
        val deposits = Deposits.load(
            this.reddioStarexContractAddress(), web3j, Credentials.create(privateKey), gasProvider
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
        privateKey: String,
        tokenAddress: String,
        tokenId: String,
        starkKey: String,
        web3j: Web3j,
        gasProvider: ContractGasProvider,
    ): LogDepositWithToken {
        val (assetId, assetType) = getAssetTypeAndId("ERC721", tokenAddress, tokenId)

        val vaultId =
            restClient.getVaultId(GetVaultIdMessage.of(assetId, listOf(starkKey))).await().getData().vaultIds[0]

        val deposits = Deposits.load(
            this.reddioStarexContractAddress(), web3j, Credentials.create(privateKey), gasProvider
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
            event.nonQuantizedAmount.toString(16),
            event.quantizedAmount.toString(16)
        )
    }

    /**
     * quantizedAmount = (amount * 10^decimals) / quantum
     *
     * @param amount amount to be converted, for example, use 0.0013 here for converting 0.0013 eth
     * @param type token type, available values: ETH, ERC20
     * @param contractAddress, use literal ETH for ETH, and hash address for ERC20
     */
    private suspend fun quantizedAmount(
        amount: String,
        type: String,
        contractAddress: String
    ): Long {
        val contractInfo = restClient.getContractInfo(GetContractInfoMessage.of(type, contractAddress)).await()
        val quantizedAmount =
            (amount.toDouble() * 10.0.pow(contractInfo.data.decimals.toDouble()) / contractInfo.data.quantum).toLong()
        return quantizedAmount
    }

    /**
     * nonQuantizedAmount = amount * 10^decimals
     *
     * @param amount amount to be converted, for example, use 0.0013 here for converting 0.0013 eth
     * @param type token type, available values: ETH, ERC20
     * @param contractAddress, use literal ETH for ETH, and hash address for ERC20
     */
    private suspend fun nonQuantizedAmount(
        amount: String,
        type: String,
        contractAddress: String
    ): Long {
        val contractInfo = restClient.getContractInfo(GetContractInfoMessage.of(type, contractAddress)).await()
        val nonQuantizedAmount =
            (amount.toDouble() * 10.0.pow(contractInfo.data.decimals.toDouble())).toLong()
        return nonQuantizedAmount
    }

    private suspend fun reddioStarexContractAddress(): String {
        val starexContractsResponseResponseWrapper = this.restClient.starexContracts().await()
        if (this.chainId == MAINNET_ID) {
            return starexContractsResponseResponseWrapper.data.mainnet
        }
        return starexContractsResponseResponseWrapper.data.testnet
    }

    private suspend fun getAssetId(
        contractAddress: String,
        tokenId: String,
        type: String,
    ): String {
        val contractInfo =
            restClient.getContractInfo(GetContractInfoMessage.of(type, contractAddress)).await().getData()
        val result =
            restClient.getAssetId(GetAssetIdMessage.of(contractAddress, type, tokenId, contractInfo.quantum)).await()
        return result.getData().getAssetId()
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

    private suspend fun getVaultsIds(assetId: String, starkKey: String, receiver: String): VaultIds {
        val result = restClient.getVaultId(GetVaultIdMessage.of(assetId, listOf(starkKey, receiver))).await()
        return VaultIds(result.getData().vaultIds[0], result.getData().vaultIds[1])
    }

    internal fun signTransferMessage(
        privateKey: String,
        amount: String,
        nonce: Long,
        senderVaultId: String,
        token: String,
        receiverVaultId: String,
        receiverPublicKey: String,
        expirationTimestamp: Long = 4194303L,
    ): Signature {
        val result = CryptoService.sign(
            BigInteger(privateKey.lowercase().replace("0x", ""), 16), CryptoService.getTransferMsgHash(
                amount.toLong(),
                nonce,
                senderVaultId.toLong(),
                BigInteger(token.lowercase().replace("0x", ""), 16),
                receiverVaultId.toLong(),
                BigInteger(receiverPublicKey.lowercase().replace("0x", ""), 16),
                expirationTimestamp,
                null
            ), null
        )
        return Signature.of("0x${result.r}", "0x${result.s}")
    }

    internal fun signOrderMsgWithFee(
        privateKey: String,
        vaultIdSell: String,
        vaultIdBuy: String,
        amountSell: String,
        amountBuy: String,
        tokenSell: String,
        tokenBuy: String,
        nonce: Long,
        expirationTimestamp: Long = 4194303L,
        feeToken: String,
        feeSourceVaultId: Long,
        feeLimit: Long,
    ): Signature {
        val hash = CryptoService.getLimitOrderMsgHashWithFee(
            vaultIdSell.toLong(),
            vaultIdBuy.toLong(),
            amountSell.toLong(),
            amountBuy.toLong(),
            BigInteger(tokenSell.lowercase().replace("0x", ""), 16),
            BigInteger(tokenBuy.lowercase().replace("0x", ""), 16),
            nonce,
            expirationTimestamp,
            BigInteger(feeToken.lowercase().replace("0x", ""), 16),
            feeSourceVaultId,
            feeLimit
        )
        val result = CryptoService.sign(BigInteger(privateKey.lowercase().replace("0x", ""), 16), hash, null);
        return Signature.of("0x${result.r}", "0x${result.s}")
    }

    companion object {
        const val MAINNET_ID = 1L
        const val GOERLI_ID = 5L

        class Builder(private val chainId: Long) {
            private var ethJSONRpcHTTPEndpoint: String = "";

            fun setEthJSONRpcHTTPEndpoint(ethJSONRpcHTTPEndpoint: String): Builder {
                this.ethJSONRpcHTTPEndpoint = ethJSONRpcHTTPEndpoint
                return this
            }

            fun build(): DefaultReddioClient {
                val restClient = if (chainId == MAINNET_ID) {
                    DefaultReddioRestClient.mainnet()
                } else {
                    DefaultReddioRestClient.testnet()
                }
                return DefaultReddioClient(restClient, chainId, ethJSONRpcHTTPEndpoint)
            }
        }

        @JvmStatic
        fun builder(chainId: Long): Builder {
            return Builder(chainId)
        }

        @JvmStatic
        fun mainnet(): DefaultReddioClient = builder(MAINNET_ID).build()

        @JvmStatic
        fun testnet(): DefaultReddioClient = builder(GOERLI_ID).build()

        private data class VaultIds(val senderVaultId: String, val receiverVaultId: String)
    }
}

