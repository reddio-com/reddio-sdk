package com.reddio.api.v1;

import com.reddio.abi.Deposits;
import com.reddio.abi.Withdrawals;
import com.reddio.gas.GasOption;
import io.reactivex.disposables.Disposable;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;

import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface EthereumInteraction {

    CompletableFuture<LogDeposit> depositETH(
            String starkKey,
            String amount,
            GasOption gasOption
    );

    CompletableFuture<LogDeposit> depositERC20(
            String tokenAddress,
            String starkKey,
            String amount,
            GasOption gasOption
    );

    CompletableFuture<LogDepositWithToken> depositERC721(
            String tokenAddress,
            String tokenId,
            String starkKey,
            GasOption gasOption
    );

    CompletableFuture<TransactionReceipt> withdrawETHOrERC20(
            String ethAddress,
            String assetType,
            GasOption gasOption
    );

    CompletableFuture<TransactionReceipt> withdrawalERC721(
            String ethAddress,
            String assetType,
            String tokenId,
            GasOption gasOption
    );

    CompletableFuture<TransactionReceipt> withdrawalERC721M(
            String ethAddress,
            String assetType,
            String tokenId,
            GasOption gasOption
    );

    Disposable watchDeposit(Consumer<Tuple2<Deposits.LogDepositEventResponse, EthBlock>> consumer);

    Disposable watchDeposit(Consumer<Tuple2<Deposits.LogDepositEventResponse, EthBlock>> consumer, BigInteger startBlockNumber);

    Disposable watchDeposit(Consumer<Tuple2<Deposits.LogDepositEventResponse, EthBlock>> consumer, BigInteger startBlockNumber, Long requiredBlockConfirmation);

    Disposable watchNftDeposit(Consumer<Tuple2<Deposits.LogNftDepositEventResponse, EthBlock>> consumer);

    Disposable watchNftDeposit(Consumer<Tuple2<Deposits.LogNftDepositEventResponse, EthBlock>> consumer, BigInteger startBlockNumber);

    Disposable watchNftDeposit(Consumer<Tuple2<Deposits.LogNftDepositEventResponse, EthBlock>> consumer, BigInteger startBlockNumber, Long requiredBlockConfirmation);

    BigInteger getStarkPrivateKey();

}
