package com.reddio.api.v1

import com.reddio.ReddioException
import com.reddio.abi.Deposits
import com.reddio.api.v1.rest.*
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
import org.web3j.protocol.core.DefaultBlockParameterNumber
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
    private val restClient: ReddioRestClient, private val chainId: Long, private val ethJSONRpcHTTPEndpoint: String
) : ReddioClient {

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
                val vaultsIds = getVaultsIds(assetId, starkKey, receiver)
                val senderVaultId = vaultsIds.senderVaultId
                val receiverVaultId = vaultsIds.receiverVaultId
                val nonce = restClient.getNonce(GetNonceMessage.of(starkKey)).await().getData().getNonce()
                val signature = signTransferMessage(
                    privateKey, amount, nonce, senderVaultId, assetId, receiverVaultId, receiver, expirationTimeStamp
                )
                restClient.transfer(
                    TransferMessage.of(
                        assetId,
                        starkKey,
                        amount,
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
                val assetId = getAssetId(contractAddress, tokenId, type)
                val vaultsIds = getVaultsIds(assetId, starkKey, receiver)
                val senderVaultId = vaultsIds.senderVaultId
                val receiverVaultId = vaultsIds.receiverVaultId
                val nonce = restClient.getNonce(GetNonceMessage.of(starkKey)).await().getData().getNonce()
                val signature = signTransferMessage(
                    privateKey, amount, nonce, senderVaultId, assetId, receiverVaultId, receiver, expirationTimeStamp
                )
                restClient.withdrawalTo(
                    WithdrawalToMessage.of(
                        contractAddress,
                        assetId,
                        starkKey,
                        amount,
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

    override fun depositETH(
        privateKey: String,
        starkKey: String,
        quantizedAmount: String,
        gasOption: GasOption,
    ): CompletableFuture<LogDeposit> {
        return CompletableFuture.supplyAsync {
            runBlocking {
                asyncDepositETH(privateKey, starkKey, quantizedAmount, gasOption)
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
                asyncERC20Approve(tokenAddress, privateKey, gasProvider, amount)
                asyncDepositERC20(
                    privateKey, tokenAddress, starkKey, amount, gasOption
                )
            }
        }
    }

    private suspend fun asyncERC20Approve(
        erc20ContractAddress: String,
        privateKey: String,
        gasProvider: ContractGasProvider,
        amount: String,
    ): ERC20.ApprovalEventResponse {
        val web3j = Web3j.build(HttpService(this.ethJSONRpcHTTPEndpoint))

        val contractInfo = restClient.getContractInfo(GetContractInfoMessage.of("ERC20", erc20ContractAddress)).await()
        val amountAfterDecimal =
            (amount.toDouble() * 10.0.pow(contractInfo.data.decimals.toDouble())).toLong().toString()
        val erc20Contract = ERC20.load(erc20ContractAddress, web3j, Credentials.create(privateKey), gasProvider)
        val call = erc20Contract.approve(
            // FIXME: load contract from API /v1/starkex/contracts
            "0x8eb82154f314ec687957ce1e9c1a5dc3a3234df9",
            BigInteger(amountAfterDecimal, 10),
        )

        val transactionReceipt = call.send()
        val future = CompletableFuture<ERC20.ApprovalEventResponse>()
        val currentBlock = web3j.ethBlockNumber().sendAsync().await()
        val from = DefaultBlockParameterNumber(currentBlock.blockNumber.subtract(BigInteger("10")))
        val to = DefaultBlockParameterNumber(currentBlock.blockNumber.add(BigInteger("5")))
        val subscription = erc20Contract.approvalEventFlowable(
            from, to
        ).subscribe({ future.complete(it) }, { future.completeExceptionally(it) })

        return try {
            future.await()
        } finally {
            subscription.dispose()
        }
    }

    private suspend fun asyncERC721Approve(
        erc721ContractAddress: String,
        privateKey: String,
        gasProvider: ContractGasProvider,
        tokenId: String,
    ): ERC721.ApprovalEventResponse {
        val web3j = Web3j.build(HttpService(this.ethJSONRpcHTTPEndpoint))
        val erc721Contract = ERC721.load(erc721ContractAddress, web3j, Credentials.create(privateKey), gasProvider)
        val call = erc721Contract.approve(
            // FIXME: load contract from API /v1/starkex/contracts
            "0x8eb82154f314ec687957ce1e9c1a5dc3a3234df9",
            BigInteger(tokenId, 10),
            BigInteger.ZERO,
        )

        val transactionReceipt = call.send()
        val future = CompletableFuture<ERC721.ApprovalEventResponse>()
        val currentBlock = web3j.ethBlockNumber().sendAsync().await()
        val from = DefaultBlockParameterNumber(currentBlock.blockNumber.subtract(BigInteger("10")))
        val to = DefaultBlockParameterNumber(currentBlock.blockNumber.add(BigInteger("5")))
        val subscription = erc721Contract.approvalEventFlowable(
            from, to
        ).subscribe({ future.complete(it) }, { future.completeExceptionally(it) })

        return try {
            future.await()
        } finally {
            subscription.dispose()
        }
    }

    override fun depositERC721(
        privateKey: String, tokenAddress: String, tokenId: String, starkKey: String, gasOption: GasOption
    ): CompletableFuture<LogDepositWithToken> {
        val gasProvider = StaticGasLimitSuggestionPriceGasProvider(
            this.chainId, gasOption, StaticGasLimitSuggestionPriceGasProvider.DEFAULT_GAS_LIMIT
        )

        return CompletableFuture.supplyAsync {
            runBlocking {
                asyncERC721Approve(
                    tokenAddress, privateKey, gasProvider, tokenId
                );
                asyncDepositERC721(privateKey, tokenAddress, tokenId, starkKey, gasOption)
            }
        }
    }

    internal suspend fun asyncDepositETH(
        privateKey: String, starkKey: String, quantizedAmount: String, gasOption: GasOption
    ): LogDeposit {
        ensureEthJSONRpcEndpoint();
        val (assetId, assetType) = getAssetTypeAndId("ETH", "ETH", "")

        val vaultId =
            restClient.getVaultId(GetVaultIdMessage.of(assetId, listOf(starkKey))).await().getData().vaultIds[0]

        val web3j = Web3j.build(HttpService(this.ethJSONRpcHTTPEndpoint))

        val gasProvider = StaticGasLimitSuggestionPriceGasProvider(
            this.chainId, gasOption, StaticGasLimitSuggestionPriceGasProvider.DEFAULT_GAS_LIMIT
        )

        // FIXME: load contract from API /v1/starkex/contracts
        val deposits = Deposits.load(
            "0x8Eb82154f314EC687957CE1e9c1A5Dc3A3234DF9", web3j, Credentials.create(privateKey), gasProvider
        )
        val call = deposits.depositEth(
            BigInteger(starkKey.lowercase().replace("0x", ""), 16),
            BigInteger(assetType.lowercase().replace("0x", ""), 16),
            BigInteger(vaultId, 10),
            Convert.toWei(quantizedAmount, Convert.Unit.ETHER).toBigInteger()
        )
        val transactionReceipt = call.send();
        val future = CompletableFuture<LogDeposit>()
        val currentBlock = web3j.ethBlockNumber().sendAsync().await()
        val from = DefaultBlockParameterNumber(currentBlock.blockNumber.subtract(BigInteger("10")))
        val to = DefaultBlockParameterNumber(currentBlock.blockNumber.add(BigInteger("5")))
        val subscription = deposits.logDepositEventFlowable(
            from, to
        ).subscribe({
            future.complete(
                LogDeposit.of(
                    it.depositorEthKey,
                    it.starkKey.toString(16),
                    it.vaultId.toString(10),
                    it.assetType.toString(16),
                    it.nonQuantizedAmount.toString(16),
                    it.quantizedAmount.toString(16)
                )
            )
        }, {
            future.completeExceptionally(it)
        })

        return try {
            future.await()
        } finally {
            subscription.dispose()
        }
    }

    internal suspend fun asyncDepositERC20(
        privateKey: String, tokenAddress: String, starkKey: String, amount: String, gasOption: GasOption
    ): LogDeposit {
        ensureEthJSONRpcEndpoint();
        val (assetId, assetType) = getAssetTypeAndId("ERC20", tokenAddress, "")

        val vaultId =
            restClient.getVaultId(GetVaultIdMessage.of(assetId, listOf(starkKey))).await().getData().vaultIds[0]

        val web3j = Web3j.build(HttpService(this.ethJSONRpcHTTPEndpoint))

        val gasProvider = StaticGasLimitSuggestionPriceGasProvider(
            this.chainId, gasOption, StaticGasLimitSuggestionPriceGasProvider.DEFAULT_GAS_LIMIT
        )

        val contractInfo = restClient.getContractInfo(GetContractInfoMessage.of("ERC20", tokenAddress)).await()
        val quantizedAmount =
            (amount.toDouble() * 10.0.pow(contractInfo.data.decimals.toDouble()) / contractInfo.data.quantum).toLong()
                .toString()

        // FIXME: load contract from API /v1/starkex/contracts
        val deposits = Deposits.load(
            "0x8Eb82154f314EC687957CE1e9c1A5Dc3A3234DF9", web3j, Credentials.create(privateKey), gasProvider
        )

        val call = deposits.depositERC20(
            BigInteger(starkKey.lowercase().replace("0x", ""), 16),
            BigInteger(assetType.lowercase().replace("0x", ""), 16),
            BigInteger(vaultId, 10),
            BigInteger(quantizedAmount, 10),
        )
        val transactionReceipt = call.send()
        val future = CompletableFuture<LogDeposit>()
        val currentBlock = web3j.ethBlockNumber().sendAsync().await()
        val from = DefaultBlockParameterNumber(currentBlock.blockNumber.subtract(BigInteger("10")))
        val to = DefaultBlockParameterNumber(currentBlock.blockNumber.add(BigInteger("5")))
        val subscription = deposits.logDepositEventFlowable(
            from, to
        ).subscribe({
            future.complete(
                LogDeposit.of(
                    it.depositorEthKey,
                    it.starkKey.toString(16),
                    it.vaultId.toString(10),
                    it.assetType.toString(16),
                    it.nonQuantizedAmount.toString(16),
                    it.quantizedAmount.toString(16)
                )
            )
        }, {
            future.completeExceptionally(it)
        })

        return try {
            val result = future.await()
            result
        } finally {
            subscription.dispose()
        }
    }

    internal suspend fun asyncDepositERC721(
        privateKey: String, tokenAddress: String, tokenId: String, starkKey: String, gasOption: GasOption
    ): LogDepositWithToken {
        ensureEthJSONRpcEndpoint();
        val (assetId, assetType) = getAssetTypeAndId("ERC721", tokenAddress, tokenId)

        val vaultId =
            restClient.getVaultId(GetVaultIdMessage.of(assetId, listOf(starkKey))).await().getData().vaultIds[0]

        val web3j = Web3j.build(HttpService(this.ethJSONRpcHTTPEndpoint))

        val gasProvider = StaticGasLimitSuggestionPriceGasProvider(
            this.chainId, gasOption, StaticGasLimitSuggestionPriceGasProvider.DEFAULT_GAS_LIMIT
        )

        // FIXME: load contract from API /v1/starkex/contracts
        val deposits = Deposits.load(
            "0x8Eb82154f314EC687957CE1e9c1A5Dc3A3234DF9", web3j, Credentials.create(privateKey), gasProvider
        )
        val call = deposits.depositNft(
            BigInteger(starkKey.lowercase().replace("0x", ""), 16),
            BigInteger(assetType.lowercase().replace("0x", ""), 16),
            BigInteger(vaultId, 10),
            BigInteger(tokenId, 10),
        )
        val transactionReceipt = call.sendAsync().await()
        val future = CompletableFuture<LogDepositWithToken>()
        val currentBlock = web3j.ethBlockNumber().sendAsync().await()
        val from = DefaultBlockParameterNumber(currentBlock.blockNumber.subtract(BigInteger("10")))
        val to = DefaultBlockParameterNumber(currentBlock.blockNumber.add(BigInteger("5")))
        val subscription = deposits.logDepositWithTokenIdEventFlowable(
            from, to
        ).subscribe({
            future.complete(
                LogDepositWithToken.of(
                    it.depositorEthKey,
                    it.starkKey.toString(16),
                    it.vaultId.toString(10),
                    it.assetType.toString(16),
                    it.tokenId.toString(10),
                    it.assetId.toString(16),
                    it.nonQuantizedAmount.toString(16),
                    it.quantizedAmount.toString(16)
                )
            )
        }, {
            future.completeExceptionally(it)
        })

        return try {
            val result = future.await()
            result
        } finally {
            subscription.dispose()
        }
    }

    private fun ensureEthJSONRpcEndpoint() {
        if (ethJSONRpcHTTPEndpoint.isEmpty()) {
            throw ReddioException("eth json rpc endpoint is required for this operation")
        }
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

