package com.reddio.api.v1;

import com.reddio.abi.Deposits;
import com.reddio.gas.GasOption;
import io.reactivex.disposables.Disposable;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface EthereumInteraction extends AutoCloseable {

    CompletableFuture<LogDeposit> depositETH(String starkKey, String amount, GasOption gasOption);

    CompletableFuture<LogDeposit> depositERC20(String tokenAddress, String starkKey, String amount, GasOption gasOption);

    CompletableFuture<LogDepositWithToken> depositERC721(String tokenAddress, String tokenId, String starkKey, GasOption gasOption);

    /**
     * @param ethAddress
     * @param assetType
     * @param gasOption
     * @return
     * @deprecated use {@link #withdrawalETH(String, GasOption)} and {@link #withdrawalERC20(String, String, GasOption)} instead
     */
    @Deprecated
    CompletableFuture<TransactionReceipt> withdrawETHOrERC20(String ethAddress, String assetType, GasOption gasOption);

    CompletableFuture<TransactionReceipt> withdrawalETH(String ethAddress, GasOption gasOption);

    CompletableFuture<TransactionReceipt> withdrawalERC20(String ethAddress, String erc20ContractAddress, GasOption gasOption);

    CompletableFuture<TransactionReceipt> withdrawalERC721(String ethAddress, String contractAddress, String tokenId, GasOption gasOption);

    CompletableFuture<TransactionReceipt> withdrawalERC721M(String ethAddress, String contractAddress, String tokenId, GasOption gasOption);


    Disposable watchDeposit(Consumer<Tuple2<Deposits.LogDepositEventResponse, EthBlock>> consumer);

    Disposable watchDeposit(Consumer<Tuple2<Deposits.LogDepositEventResponse, EthBlock>> consumer, BigInteger startBlockNumber);

    Disposable watchDeposit(Consumer<Tuple2<Deposits.LogDepositEventResponse, EthBlock>> consumer, BigInteger startBlockNumber, Long requiredBlockConfirmation);

    Disposable watchNftDeposit(Consumer<Tuple2<Deposits.LogNftDepositEventResponse, EthBlock>> consumer);

    Disposable watchNftDeposit(Consumer<Tuple2<Deposits.LogNftDepositEventResponse, EthBlock>> consumer, BigInteger startBlockNumber);

    Disposable watchNftDeposit(Consumer<Tuple2<Deposits.LogNftDepositEventResponse, EthBlock>> consumer, BigInteger startBlockNumber, Long requiredBlockConfirmation);

    BigInteger getStarkPrivateKey();

    CompletableFuture<TransactionReceipt> deployERC20AndRegister(String reddioDeployHelperAddress, String name, String symbol, BigInteger amount, GasOption gasOption);

    CompletableFuture<TransactionReceipt> deployERC721AndRegister(String reddioDeployHelperAddress, String name, String symbol, String baseURI, GasOption gasOption);

    CompletableFuture<TransactionReceipt> deployERC721MAndRegister(String reddioDeployHelperAddress, String name, String symbol, String baseURI, GasOption gasOption);

    CompletableFuture<TransactionReceipt> deployERC721MCAndRegister(String reddioDeployHelperAddress, String name, String symbol, GasOption gasOption);
}
