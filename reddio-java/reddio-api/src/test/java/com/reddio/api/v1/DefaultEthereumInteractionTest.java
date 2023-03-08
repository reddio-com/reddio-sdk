package com.reddio.api.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reddio.IntegrationTest;
import com.reddio.abi.Erc721m;
import com.reddio.api.v1.rest.DefaultReddioRestClient;
import com.reddio.api.v1.rest.GetContractInfoMessage;
import com.reddio.api.v1.rest.GetContractInfoResponse;
import com.reddio.api.v1.rest.ResponseWrapper;
import com.reddio.crypto.CryptoService;
import com.reddio.gas.GasOption;
import com.reddio.gas.StaticGasLimitSuggestionPriceGasProvider;
import io.reactivex.disposables.Disposable;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.lang.ref.Reference;
import java.math.BigInteger;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultEthereumInteractionTest {

    public static final String RDD20_CONTRACT_ADDRESS = "0x57f3560b6793dcc2cb274c39e8b8eba1dd18a086";
    public static final String REDDIO721_CONTRACT_ADDRESS = "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5";
    public static final String REDDIO721M_CONTRACT_ADDRESS = "0xa5864abaab46d01412d154cd8836e62d201be436";

    @Test
    @Category(IntegrationTest.class)
    public void testDepositETH() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultEthereumInteraction ethereumInteraction = DefaultEthereumInteraction.build(DefaultReddioRestClient.testnet(), DefaultEthereumInteraction.GOERIL_ID, "https://eth-goerli.g.alchemy.com/v2/yyabgQ1GlM0xxqDC4ZBbR1lBcBKQmnxT", "0x27832a8be401e504eaa3e66904f929f02f72cd7f697e3f8f0a1c3d4b8654ba9d");
        CompletableFuture<LogDeposit> future = ethereumInteraction.depositETH("0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4", "0.00019", GasOption.Market);
        LogDeposit result = future.get();
        System.out.println(new ObjectMapper().writeValueAsString(result));
    }

    @Test
    @Category(IntegrationTest.class)
    public void testDepositERC20() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultEthereumInteraction ethereumInteraction = DefaultEthereumInteraction.build(DefaultReddioRestClient.testnet(), DefaultEthereumInteraction.GOERIL_ID, "https://eth-goerli.g.alchemy.com/v2/yyabgQ1GlM0xxqDC4ZBbR1lBcBKQmnxT", "0x27832a8be401e504eaa3e66904f929f02f72cd7f697e3f8f0a1c3d4b8654ba9d");
        CompletableFuture<LogDeposit> future = ethereumInteraction.depositERC20(RDD20_CONTRACT_ADDRESS, "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4", "0.013", GasOption.Market);
        LogDeposit result = future.get();
        System.out.println(new ObjectMapper().writeValueAsString(result));
    }

    @Test
    @Category(IntegrationTest.class)
    public void testDepositERC721() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultEthereumInteraction ethereumInteraction = DefaultEthereumInteraction.build(DefaultReddioRestClient.testnet(), DefaultEthereumInteraction.GOERIL_ID, "https://eth-goerli.g.alchemy.com/v2/yyabgQ1GlM0xxqDC4ZBbR1lBcBKQmnxT", "0x27832a8be401e504eaa3e66904f929f02f72cd7f697e3f8f0a1c3d4b8654ba9d");
        CompletableFuture<LogDepositWithToken> future = ethereumInteraction.depositERC721(REDDIO721_CONTRACT_ADDRESS, "1205", "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4", GasOption.Market);
        LogDepositWithToken result = future.get();
        System.out.println(new ObjectMapper().writeValueAsString(result));
    }

    @Test
    @Category(IntegrationTest.class)
    public void testWithdrawalETH() throws ExecutionException, InterruptedException {
        DefaultReddioRestClient restClient = DefaultReddioRestClient.testnet();
        DefaultEthereumInteraction ethereumInteraction = DefaultEthereumInteraction.build(restClient, DefaultEthereumInteraction.GOERIL_ID, "https://eth-goerli.g.alchemy.com/v2/yyabgQ1GlM0xxqDC4ZBbR1lBcBKQmnxT", "0x27832a8be401e504eaa3e66904f929f02f72cd7f697e3f8f0a1c3d4b8654ba9d");
        ResponseWrapper<GetContractInfoResponse> contractInfo = restClient.getContractInfo(GetContractInfoMessage.of("eth", "eth")).get();
        String assetType = contractInfo.getData().getAssetType();
        TransactionReceipt txn = ethereumInteraction.withdrawETHOrERC20("0x76f2Fc7ed90039d986e3eb4DB294f05E160c8F03", assetType, GasOption.Market).get();
        System.out.println(txn.getTransactionHash());
    }

    @Test
    @Category(IntegrationTest.class)
    public void testWithdrawalERC20() throws ExecutionException, InterruptedException {
        DefaultReddioRestClient restClient = DefaultReddioRestClient.testnet();
        DefaultEthereumInteraction ethereumInteraction = DefaultEthereumInteraction.build(restClient, DefaultEthereumInteraction.GOERIL_ID, "https://eth-goerli.g.alchemy.com/v2/yyabgQ1GlM0xxqDC4ZBbR1lBcBKQmnxT", "0x27832a8be401e504eaa3e66904f929f02f72cd7f697e3f8f0a1c3d4b8654ba9d");
        ResponseWrapper<GetContractInfoResponse> contractInfo = restClient.getContractInfo(GetContractInfoMessage.of("ERC20", RDD20_CONTRACT_ADDRESS)).get();
        String assetType = contractInfo.getData().getAssetType();
        TransactionReceipt txn = ethereumInteraction.withdrawETHOrERC20("0x76f2Fc7ed90039d986e3eb4DB294f05E160c8F03", assetType, GasOption.Market).get();
        System.out.println(txn.getTransactionHash());
    }

    @Test
    @Category(IntegrationTest.class)
    public void testWithdrawalERC721() throws ExecutionException, InterruptedException {
        DefaultReddioRestClient restClient = DefaultReddioRestClient.testnet();
        DefaultEthereumInteraction ethereumInteraction = DefaultEthereumInteraction.build(restClient, DefaultEthereumInteraction.GOERIL_ID, "https://eth-goerli.g.alchemy.com/v2/yyabgQ1GlM0xxqDC4ZBbR1lBcBKQmnxT", "0x27832a8be401e504eaa3e66904f929f02f72cd7f697e3f8f0a1c3d4b8654ba9d");
        ResponseWrapper<GetContractInfoResponse> contractInfo = restClient.getContractInfo(GetContractInfoMessage.of("ERC721", REDDIO721_CONTRACT_ADDRESS)).get();
        String assetType = contractInfo.getData().getAssetType();
        TransactionReceipt txn = ethereumInteraction.withdrawalERC721("0x76f2Fc7ed90039d986e3eb4DB294f05E160c8F03", assetType, "1022", GasOption.Market).get();
        System.out.println(txn.getTransactionHash());
    }

    @Test
    @Category(IntegrationTest.class)
    public void testWithdrawalERC721M() throws ExecutionException, InterruptedException {
        DefaultReddioRestClient restClient = DefaultReddioRestClient.testnet();
        DefaultEthereumInteraction ethereumInteraction = DefaultEthereumInteraction.build(restClient, DefaultEthereumInteraction.GOERIL_ID, "https://eth-goerli.g.alchemy.com/v2/yyabgQ1GlM0xxqDC4ZBbR1lBcBKQmnxT", "0x27832a8be401e504eaa3e66904f929f02f72cd7f697e3f8f0a1c3d4b8654ba9d");
        ResponseWrapper<GetContractInfoResponse> contractInfo = restClient.getContractInfo(GetContractInfoMessage.of("ERC721M", "0xe3d2a2ca17a8dedb740b6c259b4eeeaaf81c9fb6")).get();
        String assetType = contractInfo.getData().getAssetType();
        TransactionReceipt txn = ethereumInteraction.withdrawalERC721M("0x76f2Fc7ed90039d986e3eb4DB294f05E160c8F03", assetType, "3", GasOption.Market).get();
        System.out.println(txn.getTransactionHash());
    }

    @Test
    @Category(IntegrationTest.class)
    @Ignore("never end")
    public void testWatchDeposit() throws InterruptedException, IOException {
        ObjectMapper om = new ObjectMapper();
        DefaultReddioRestClient restClient = DefaultReddioRestClient.testnet();
        DefaultEthereumInteraction ethereumInteraction = DefaultEthereumInteraction.build(restClient, DefaultEthereumInteraction.GOERIL_ID, "https://eth-goerli.g.alchemy.com/v2/yyabgQ1GlM0xxqDC4ZBbR1lBcBKQmnxT", "0x0");
        Long requiredBlockConfirmations = 2L;
        Web3j web3j = Web3j.build(new HttpService("https://eth-goerli.g.alchemy.com/v2/yyabgQ1GlM0xxqDC4ZBbR1lBcBKQmnxT"));
        BigInteger startBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();

        AtomicReference<Disposable> disposableReference = new AtomicReference<>();

        Disposable disposable = ethereumInteraction.watchDeposit((it) -> {
            try {
                String asJson = om.writeValueAsString(it.component1());
                System.out.println(asJson);
                System.out.println(it.component2().toString());
                BigInteger currentBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
                System.out.println("currentBlockNumber: " + currentBlockNumber.longValue());
                Assert.assertTrue(currentBlockNumber.subtract(it.component1().log.getBlockNumber()).longValue() >= requiredBlockConfirmations);
                disposableReference.get().dispose();
                synchronized (disposableReference) {
                    disposableReference.notify();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, startBlockNumber, requiredBlockConfirmations);
        disposableReference.set(disposable);
        synchronized (disposableReference) {
            disposableReference.wait();
        }
    }

    @Test
    @Category(IntegrationTest.class)
    @Ignore("never end")
    public void testWatchNftDeposit() throws InterruptedException {
        ObjectMapper om = new ObjectMapper();
        DefaultReddioRestClient restClient = DefaultReddioRestClient.testnet();
        DefaultEthereumInteraction ethereumInteraction = DefaultEthereumInteraction.build(restClient, DefaultEthereumInteraction.GOERIL_ID, "https://eth-goerli.g.alchemy.com/v2/yyabgQ1GlM0xxqDC4ZBbR1lBcBKQmnxT", "0x0");
        Disposable disposable = ethereumInteraction.watchNftDeposit((it) -> {
            try {
                String asJson = om.writeValueAsString(it);
                System.out.println(asJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        Thread.sleep(Duration.ofSeconds(600).toMillis());
        disposable.dispose();
    }

    @Test
    public void testEthSignAndGetStarkKey() {
        DefaultReddioRestClient restClient = DefaultReddioRestClient.testnet();
        DefaultEthereumInteraction ethereumInteraction = DefaultEthereumInteraction.build(restClient, DefaultEthereumInteraction.GOERIL_ID, "https://eth-goerli.g.alchemy.com/v2/yyabgQ1GlM0xxqDC4ZBbR1lBcBKQmnxT",
                "552ad9b756acfeb2e32cfd3354b653b1f95177b851a44155d6178d244b80e08b");
        BigInteger result = ethereumInteraction.getStarkPrivateKey();
        Assert.assertEquals("5f6fbfbcd995e20f94a768193c42060f7e626e6ae8042cacc15e82031087a55", result.toString(16));
    }

    @Test
    public void testEthSignAndGetStarkKeyStaticMethod() {
        BigInteger result = DefaultEthereumInteraction.getStarkPrivateKey("552ad9b756acfeb2e32cfd3354b653b1f95177b851a44155d6178d244b80e08b", DefaultEthereumInteraction.GOERIL_ID);
        Assert.assertEquals("5f6fbfbcd995e20f94a768193c42060f7e626e6ae8042cacc15e82031087a55", result.toString(16));
    }

    @Test
    public void testGetStarkKeys() {
        StarkKeys starkKeys = DefaultEthereumInteraction.getStarkKeys("552ad9b756acfeb2e32cfd3354b653b1f95177b851a44155d6178d244b80e08b", DefaultEthereumInteraction.GOERIL_ID);
        Assert.assertEquals("0x13a69a1b7a5f033ee2358ebb8c28fd5a6b86d42e30a61845d655d3c7be4ad0e", starkKeys.getStarkKey());
        Assert.assertEquals("0x5f6fbfbcd995e20f94a768193c42060f7e626e6ae8042cacc15e82031087a55", starkKeys.getStarkPrivateKey());
    }

    @Test
    @Category(IntegrationTest.class)
    public void testDeployERC721M() throws Exception {
        Web3j web3j = Web3j.build(new HttpService("https://eth-goerli.g.alchemy.com/v2/yyabgQ1GlM0xxqDC4ZBbR1lBcBKQmnxT"));
        Credentials credentials = Credentials.create("552ad9b756acfeb2e32cfd3354b653b1f95177b851a44155d6178d244b80e08b");
        StaticGasLimitSuggestionPriceGasProvider gasProvider = new StaticGasLimitSuggestionPriceGasProvider(5, GasOption.Market, new BigInteger("10000000"));
        RemoteCall<Erc721m> deployRemoteCall = Erc721m.deploy(web3j, credentials, gasProvider, "NON3", "NON3", "");
        Erc721m send = deployRemoteCall.send();
    }
}
