package com.reddio.api.v1;

import com.reddio.gas.GasOption;

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
}
