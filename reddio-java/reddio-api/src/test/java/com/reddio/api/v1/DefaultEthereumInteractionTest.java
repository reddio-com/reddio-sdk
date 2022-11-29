package com.reddio.api.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reddio.api.v1.rest.DefaultReddioRestClient;
import com.reddio.api.v1.rest.GetContractInfoMessage;
import com.reddio.api.v1.rest.GetContractInfoResponse;
import com.reddio.api.v1.rest.ResponseWrapper;
import com.reddio.gas.GasOption;
import org.junit.Ignore;
import org.junit.Test;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class DefaultEthereumInteractionTest {

    public static final String RDD20_CONTRACT_ADDRESS = "0x57f3560b6793dcc2cb274c39e8b8eba1dd18a086";
    public static final String REDDIO721_CONTRACT_ADDRESS = "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5";

    @Test
    @Ignore("not reproducible test")
    public void testDepositETH() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultEthereumInteraction ethereumInteraction = DefaultEthereumInteraction.build(
                DefaultReddioRestClient.testnet(),
                DefaultEthereumInteraction.GOERIL_ID,
                "https://eth-goerli.g.alchemy.com/v2/yyabgQ1GlM0xxqDC4ZBbR1lBcBKQmnxT",
                "0x27832a8be401e504eaa3e66904f929f02f72cd7f697e3f8f0a1c3d4b8654ba9d"
        );
        CompletableFuture<LogDeposit> future = ethereumInteraction.depositETH(
                "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
                "0.00019",
                GasOption.Market);
        LogDeposit result = future.get();
        System.out.println(new ObjectMapper().writeValueAsString(result));
    }

    @Test
    @Ignore("not reproducible test")
    public void testDepositERC20() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultEthereumInteraction ethereumInteraction = DefaultEthereumInteraction.build(
                DefaultReddioRestClient.testnet(),
                DefaultEthereumInteraction.GOERIL_ID,
                "https://eth-goerli.g.alchemy.com/v2/yyabgQ1GlM0xxqDC4ZBbR1lBcBKQmnxT",
                "0x27832a8be401e504eaa3e66904f929f02f72cd7f697e3f8f0a1c3d4b8654ba9d"
        );
        CompletableFuture<LogDeposit> future = ethereumInteraction.depositERC20(
                RDD20_CONTRACT_ADDRESS,
                "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
                "0.013",
                GasOption.Market
        );
        LogDeposit result = future.get();
        System.out.println(new ObjectMapper().writeValueAsString(result));
    }

    @Test
    @Ignore("not reproducible test")
    public void testDepositERC721() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultEthereumInteraction ethereumInteraction = DefaultEthereumInteraction.build(
                DefaultReddioRestClient.testnet(),
                DefaultEthereumInteraction.GOERIL_ID,
                "https://eth-goerli.g.alchemy.com/v2/yyabgQ1GlM0xxqDC4ZBbR1lBcBKQmnxT",
                "0x27832a8be401e504eaa3e66904f929f02f72cd7f697e3f8f0a1c3d4b8654ba9d"
        );
        CompletableFuture<LogDepositWithToken> future = ethereumInteraction.depositERC721(
                REDDIO721_CONTRACT_ADDRESS,
                "1205",
                "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
                GasOption.Market
        );
        LogDepositWithToken result = future.get();
        System.out.println(new ObjectMapper().writeValueAsString(result));
    }

    @Test
    @Ignore("not reproducible test")
    public void testWithdrawalETH() throws ExecutionException, InterruptedException {
        DefaultReddioRestClient restClient = DefaultReddioRestClient.testnet();
        DefaultEthereumInteraction ethereumInteraction = DefaultEthereumInteraction.build(
                restClient,
                DefaultEthereumInteraction.GOERIL_ID,
                "https://eth-goerli.g.alchemy.com/v2/yyabgQ1GlM0xxqDC4ZBbR1lBcBKQmnxT",
                "0x27832a8be401e504eaa3e66904f929f02f72cd7f697e3f8f0a1c3d4b8654ba9d"
        );
        ResponseWrapper<GetContractInfoResponse> contractInfo = restClient.getContractInfo(GetContractInfoMessage.of("eth", "eth")).get();
        String assetType = contractInfo.getData().getAssetType();
        TransactionReceipt txn = ethereumInteraction.withdrawETHOrERC20(
                "0x76f2Fc7ed90039d986e3eb4DB294f05E160c8F03",
                assetType,
                GasOption.Market).get();
        System.out.println(txn.getTransactionHash());
    }

    @Test
    @Ignore("not reproducible test")
    public void testWithdrawalERC20() throws ExecutionException, InterruptedException {
        DefaultReddioRestClient restClient = DefaultReddioRestClient.testnet();
        DefaultEthereumInteraction ethereumInteraction = DefaultEthereumInteraction.build(
                restClient,
                DefaultEthereumInteraction.GOERIL_ID,
                "https://eth-goerli.g.alchemy.com/v2/yyabgQ1GlM0xxqDC4ZBbR1lBcBKQmnxT",
                "0x27832a8be401e504eaa3e66904f929f02f72cd7f697e3f8f0a1c3d4b8654ba9d"
        );
        ResponseWrapper<GetContractInfoResponse> contractInfo = restClient.getContractInfo(GetContractInfoMessage.of("ERC20", RDD20_CONTRACT_ADDRESS)).get();
        String assetType = contractInfo.getData().getAssetType();
        TransactionReceipt txn = ethereumInteraction.withdrawETHOrERC20(
                "0x76f2Fc7ed90039d986e3eb4DB294f05E160c8F03",
                assetType,
                GasOption.Market).get();
        System.out.println(txn.getTransactionHash());
    }

    @Test
    @Ignore("not reproducible test")
    public void testWithdrawalERC721() throws ExecutionException, InterruptedException {
        DefaultReddioRestClient restClient = DefaultReddioRestClient.testnet();
        DefaultEthereumInteraction ethereumInteraction = DefaultEthereumInteraction.build(
                restClient,
                DefaultEthereumInteraction.GOERIL_ID,
                "https://eth-goerli.g.alchemy.com/v2/yyabgQ1GlM0xxqDC4ZBbR1lBcBKQmnxT",
                "0x27832a8be401e504eaa3e66904f929f02f72cd7f697e3f8f0a1c3d4b8654ba9d"
        );
        ResponseWrapper<GetContractInfoResponse> contractInfo = restClient.getContractInfo(GetContractInfoMessage.of(
                "ERC721",
                REDDIO721_CONTRACT_ADDRESS
        )).get();
        String assetType = contractInfo.getData().getAssetType();
        TransactionReceipt txn = ethereumInteraction.withdrawalERC721(
                "0x76f2Fc7ed90039d986e3eb4DB294f05E160c8F03",
                assetType,
                "1022",
                GasOption.Market).get();
        System.out.println(txn.getTransactionHash());
    }

    @Test
    @Ignore("not reproducible test")
    public void testWithdrawalERC721M() throws ExecutionException, InterruptedException {
        DefaultReddioRestClient restClient = DefaultReddioRestClient.testnet();
        DefaultEthereumInteraction ethereumInteraction = DefaultEthereumInteraction.build(
                restClient,
                DefaultEthereumInteraction.GOERIL_ID,
                "https://eth-goerli.g.alchemy.com/v2/yyabgQ1GlM0xxqDC4ZBbR1lBcBKQmnxT",
                "0x27832a8be401e504eaa3e66904f929f02f72cd7f697e3f8f0a1c3d4b8654ba9d"
        );
        ResponseWrapper<GetContractInfoResponse> contractInfo = restClient.getContractInfo(GetContractInfoMessage.of(
                "ERC721",
                REDDIO721_CONTRACT_ADDRESS
        )).get();
        String assetType = contractInfo.getData().getAssetType();
        TransactionReceipt txn = ethereumInteraction.withdrawalERC721M(
                "0x76f2Fc7ed90039d986e3eb4DB294f05E160c8F03",
                assetType,
                "1022",
                GasOption.Market).get();
        System.out.println(txn.getTransactionHash());
    }
}
