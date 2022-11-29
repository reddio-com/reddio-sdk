package com.reddio.api.v1;

import com.reddio.gas.GasOption;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.concurrent.CompletableFuture;

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

}
