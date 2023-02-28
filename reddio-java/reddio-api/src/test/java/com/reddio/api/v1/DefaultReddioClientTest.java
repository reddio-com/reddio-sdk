package com.reddio.api.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reddio.IntegrationTest;
import com.reddio.api.v1.requests.ReddioOrderApi;
import com.reddio.api.v1.requests.ReddioTransferToApi;
import com.reddio.api.v1.requests.ReddioWithdrawalToApi;
import com.reddio.api.v1.rest.*;
import com.reddio.crypto.CryptoService;
import com.reddio.crypto.Signature;
import com.reddio.exception.ReddioBusinessException;
import com.reddio.exception.ReddioErrorCode;
import com.reddio.sign.PaymentSHA3;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.reddio.api.v1.DefaultEthereumInteractionTest.*;

public class DefaultReddioClientTest {
    @Test
    @Category(IntegrationTest.class)
    public void testTransfer() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        ReddioClient.WithStarkExSigner clientWithSigner = client.withStarkExSigner("0xa7b68cf2ee72b2a0789914daa8ae928aec21b6b0bf020e394833f4c732d99d");
        CompletableFuture<ResponseWrapper<TransferResponse>> future = clientWithSigner.transfer("0x6736f7449da3bf44bf0f7bdd6463818e1ef272641d43021e8bca17b32ec2df0", "1", REDDIO721_CONTRACT_ADDRESS, "497", "ERC721", "0x7865bc66b610d6196a7cbeb9bf066c64984f6f06b5ed3b6f5788bd9a6cb099c", 4194303L);
        ResponseWrapper<TransferResponse> result = future.get();
        Assert.assertEquals("OK", result.getStatus());
        System.out.println(new ObjectMapper().writeValueAsString(result));
    }

    @Test
    @Category(IntegrationTest.class)
    public void testWaitingRecordGetApproved() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        CompletableFuture<ResponseWrapper<GetRecordResponse>> future = client.waitingTransferGetApproved("0x6736f7449da3bf44bf0f7bdd6463818e1ef272641d43021e8bca17b32ec2df0", 300523);
        ResponseWrapper<GetRecordResponse> result = future.get();
        Assert.assertEquals("OK", result.getStatus());
        System.out.println(new ObjectMapper().writeValueAsString(result));
    }

    @Test
    @Category(IntegrationTest.class)
    public void testWithdrawalGoerliETH() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        ReddioClient.WithStarkExSigner clientWithSigner = client.withStarkExSigner("0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d");
        CompletableFuture<ResponseWrapper<WithdrawalToResponse>> future = clientWithSigner.withdrawal("0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4", "0.00013", "ETH", "", "ETH", "0x76f2Fc7ed90039d986e3eb4DB294f05E160c8F03", 4194303L);
        ResponseWrapper<WithdrawalToResponse> result = future.get();
        Assert.assertEquals("OK", result.getStatus());
        System.out.println(new ObjectMapper().writeValueAsString(result));
    }

    @Test
    @Category(IntegrationTest.class)
    public void testWithdrawalNTFERC721() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        ReddioClient.WithStarkExSigner clientWithSigner = client.withStarkExSigner("0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d");
        CompletableFuture<ResponseWrapper<WithdrawalToResponse>> future = clientWithSigner.withdrawal("0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4", "1", REDDIO721_CONTRACT_ADDRESS, "1022", "ERC721", "0x76f2Fc7ed90039d986e3eb4DB294f05E160c8F03", 4194303L);
        ResponseWrapper<WithdrawalToResponse> result = future.get();
        Assert.assertEquals("OK", result.getStatus());
        System.out.println(new ObjectMapper().writeValueAsString(result));
    }

    @Test
    @Category(IntegrationTest.class)
    public void testWithdrawalNTFERC721M() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        ReddioClient.WithStarkExSigner clientWithSigner = client.withStarkExSigner("0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d");
        CompletableFuture<ResponseWrapper<WithdrawalToResponse>> future = clientWithSigner.withdrawal("0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4", "1", REDDIO721M_CONTRACT_ADDRESS, "7", "ERC721M", "0x76f2Fc7ed90039d986e3eb4DB294f05E160c8F03", 4194303L);
        ResponseWrapper<WithdrawalToResponse> result = future.get();
        Assert.assertEquals("OK", result.getStatus());
        System.out.println(new ObjectMapper().writeValueAsString(result));
    }

    @Test
    @Category(IntegrationTest.class)
    public void testOrder() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        DefaultReddioRestClient restClient = DefaultReddioRestClient.testnet();
        CompletableFuture<ResponseWrapper<GetBalancesResponse>> balancesFuture = restClient.getBalances(GetBalancesMessage.of("0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4", REDDIO721_CONTRACT_ADDRESS, 10L, 1L));
        ResponseWrapper<GetBalancesResponse> balances = balancesFuture.get();
        Assert.assertEquals("OK", balances.getStatus());
        GetBalancesResponse.BalanceRecord toSell = balances.getData().getList().stream().filter((it) -> it.getBalanceAvailable() > 0).collect(Collectors.toList()).get(0);
        ReddioClient.WithStarkExSigner clientWithSigner = client.withStarkExSigner("0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d");
        CompletableFuture<ResponseWrapper<OrderResponse>> future = clientWithSigner.order("0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4", "0.013", "1", REDDIO721_CONTRACT_ADDRESS, toSell.getTokenId(), "11ed793a-cc11-4e44-9738-97165c4e14a7", "ERC721", OrderBehavior.SELL);
        ResponseWrapper<OrderResponse> result = future.get();
        System.out.println(new ObjectMapper().writeValueAsString(result));
        Assert.assertEquals("OK", result.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void testGetOrder() throws ExecutionException, InterruptedException {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        ResponseWrapper<Order> getOrderResponse = client.getOrder(304282).get();
        Assert.assertEquals("OK", getOrderResponse.getStatus());
        Order data = getOrderResponse.getData();
        Assert.assertEquals(304282, data.getOrderId().longValue());
        Assert.assertEquals("0x1ccc27877014bc1a81919fc855ebbd1b874603283c9ea93397d970b0704e581", data.getStarkKey());
        Assert.assertEquals("1000", data.getPrice());
        Assert.assertEquals(0, data.getDirection().longValue());
        Assert.assertEquals("1", data.getAmount());
        Assert.assertEquals("0", data.getUnFilled());

        Assert.assertEquals("0x352f9ffd821a525051de2d71126113505a7b0a73d98dbc0ac0ff343cfbdef5e", data.getSymbol().getBaseTokenAssetId());
        Assert.assertEquals("0x22d8810dfe28c2c083463d64b886b7e7fbe2b455c9a03ea2f0afd1457abd57d", data.getSymbol().getQuoteTokenAssetId());
        Assert.assertEquals("eth", data.getSymbol().getBaseTokenContractAddr());
        Assert.assertEquals("0x941661bd1134dc7cc3d107bf006b8631f6e65ad5", data.getSymbol().getQuoteTokenContractAddr());
        Assert.assertEquals("ETH", data.getSymbol().getBaseTokenName());
        Assert.assertEquals("REDDIO721", data.getSymbol().getQuoteTokenName());
        Assert.assertEquals("ERC721", data.getSymbol().getTokenType());
        Assert.assertEquals("1026", data.getSymbol().getTokenId());

        Assert.assertEquals("200", data.getFeeRate());
        Assert.assertEquals("ERC721", data.getTokenType());
        Assert.assertEquals("1026", data.getTokenId());
        Assert.assertEquals("0.001", data.getDisplayPrice());
        Assert.assertEquals(OrderState.Filled, data.getOrderState());

    }

    @Test
    @Category(IntegrationTest.class)
    public void testListOrders() throws ExecutionException, InterruptedException {
        ReddioClient reddioClient = DefaultReddioClient.testnet();
        ResponseWrapper<ListRecordsResponse> wrapper = reddioClient.listRecords("0x6736f7449da3bf44bf0f7bdd6463818e1ef272641d43021e8bca17b32ec2df0", 3L, 2L, null).get();
        Assert.assertEquals("OK", wrapper.getStatus());
        Assert.assertEquals(3, wrapper.getData().getPageSize().longValue());
        Assert.assertEquals(2, wrapper.getData().getCurrentPage().longValue());
        Assert.assertEquals(3, wrapper.getData().getList().size());
    }

    @Test
    @Category(IntegrationTest.class)
    public void testListOrdersWithSequenceIds() throws ExecutionException, InterruptedException {
        ReddioClient reddioClient = DefaultReddioClient.testnet();
        List<Long> sequenceIds = new ArrayList<>();
        sequenceIds.add(303531L);
        sequenceIds.add(303530L);
        ResponseWrapper<ListRecordsResponse> wrapper = reddioClient.listRecords(sequenceIds).get();
        Assert.assertEquals("OK", wrapper.getStatus());
        Assert.assertEquals(2, wrapper.getData().getList().size());
        Assert.assertEquals(2, wrapper.getData().getTotal().longValue());
        Assert.assertEquals(303531L, wrapper.getData().getList().get(0).getSequenceId().longValue());
        Assert.assertEquals(303530L, wrapper.getData().getList().get(1).getSequenceId().longValue());
    }

    @Test
    @Category(IntegrationTest.class)
    public void testOrderWithERC20() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        DefaultReddioRestClient restClient = DefaultReddioRestClient.testnet();
        CompletableFuture<ResponseWrapper<GetBalancesResponse>> balancesFuture = restClient.getBalances(GetBalancesMessage.of("0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4", REDDIO721_CONTRACT_ADDRESS, 100L, 1L));
        ResponseWrapper<GetBalancesResponse> balances = balancesFuture.get();
        Assert.assertEquals("OK", balances.getStatus());
        GetBalancesResponse.BalanceRecord toSell = balances.getData().getList().stream().filter((it) -> it.getBalanceAvailable() > 0).collect(Collectors.toList()).get(0);
        ReddioClient.WithStarkExSigner clientWithSigner = client.withStarkExSigner("0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d");
        CompletableFuture<ResponseWrapper<OrderResponse>> future = clientWithSigner.order("0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4", "ERC721", REDDIO721_CONTRACT_ADDRESS, toSell.getTokenId(), "0.013", "1", OrderBehavior.SELL, "ERC20", RDD20_CONTRACT_ADDRESS, "");
        ResponseWrapper<OrderResponse> result = future.get();
        System.out.println(new ObjectMapper().writeValueAsString(result));
        Assert.assertEquals("OK", result.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void testSellOrderWithRUSD() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        DefaultReddioRestClient restClient = DefaultReddioRestClient.testnet();
        ReddioClient.WithStarkExSigner clientWithSigner = client.withStarkExSigner("0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d");
        CompletableFuture<ResponseWrapper<OrderResponse>> future = clientWithSigner.sellNFTWithRUSD("0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4", "ERC721", REDDIO721_CONTRACT_ADDRESS, "1210", "0.013", "1", "");
        ResponseWrapper<OrderResponse> result = future.get();
        System.out.println(new ObjectMapper().writeValueAsString(result));
        Assert.assertEquals("OK", result.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void testBuyOrderWithPayInfoBaseTokenRUSD() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        DefaultReddioRestClient restClient = DefaultReddioRestClient.testnet();
        ReddioClient.WithStarkExSigner clientWithSigner = client.withStarkExSigner("5f6fbfbcd995e20f94a768193c42060f7e626e6ae8042cacc15e82031087a55");
        CompletableFuture<ResponseWrapper<OrderResponse>> future = clientWithSigner.buyNFTWithPayInfoBaseTokenRUSD("0x13a69a1b7a5f033ee2358ebb8c28fd5a6b86d42e30a61845d655d3c7be4ad0e", "ERC721", REDDIO721_CONTRACT_ADDRESS, "1209", "0.013", "1", "", Payment.PayInfo.of("123456789"), "0x1a35ffa8bafc5c6656271bcae1f847bb6201705d7e2895c413cfb7d757a3111");
        ResponseWrapper<OrderResponse> result = future.get();
        System.out.println(new ObjectMapper().writeValueAsString(result));
        Assert.assertEquals("OK", result.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void testGetSign() {
        BigInteger hash = PaymentSHA3.getPaymentHash(Payment.of(Payment.PayInfo.of("123456789"), ""), 2);
        System.out.println("hash: 0x" + hash.toString(16));
        Signature sign = CryptoService.sign(new BigInteger("0x1a35ffa8bafc5c6656271bcae1f847bb6201705d7e2895c413cfb7d757a3111".replace("0x", "").toLowerCase(), 16), hash, null);
        System.out.println("r: 0x" + sign.r);
        System.out.println("s: 0x" + sign.s);
    }

    @Test
    @Category(IntegrationTest.class)
    public void testCancelOrder() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        ReddioClient.WithStarkExSigner withStarkExSigner = client.withStarkExSigner("0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d");
        CompletableFuture<ResponseWrapper<CancelOrderResponse>> future = withStarkExSigner.cancelOrder("0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4", 303590);
        ResponseWrapper<CancelOrderResponse> result = future.get();
        System.out.println(new ObjectMapper().writeValueAsString(result));
        Assert.assertEquals("OK", result.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void testMints() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet("<truncated-api-key>");
        CompletableFuture<ResponseWrapper<MintResponse>> future = client.mints("0x113536494406bc039586c1ad9b8f51af664d6ef8", "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4", 1);
        ResponseWrapper<MintResponse> result = future.get();
        System.out.println(new ObjectMapper().writeValueAsString(result));
        Assert.assertEquals("OK", result.getStatus());
    }

    @Test
    @Category(IntegrationTest.class)
    public void testMintWithInvalidAmount() {
        DefaultReddioClient client = DefaultReddioClient.testnet("rk-1236d5fc-f4c1-4a19-a2ff-9c29e3a70e37");
        try {
            CompletableFuture<ResponseWrapper<MintResponse>> future = client.mints("0x113536494406bc039586c1ad9b8f51af664d6ef8", "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4", -1);
            future.join();
            Assert.fail();
        } catch (CompletionException e) {
            Assert.assertEquals(ReddioErrorCode.MintAmountInvalid, ((ReddioBusinessException) e.getCause()).getErrorCode());
        }
    }

    @Test
    @Category(IntegrationTest.class)
    public void testMintMintAgainWithSameTokenId() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet("rk-1236d5fc-f4c1-4a19-a2ff-9c29e3a70e37");
        List<Long> tokenIds = new ArrayList<>();
        tokenIds.add(300L);
        try {
            CompletableFuture<ResponseWrapper<MintResponse>> future = client.mints("0x113536494406bc039586c1ad9b8f51af664d6ef8", "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4", tokenIds);
            future.join();
            Assert.fail();
        } catch (CompletionException e) {
            Assert.assertEquals(ReddioErrorCode.TokenIDInvalid, ((ReddioBusinessException) e.getCause()).getErrorCode());
        }
    }

    @Test
    @Category(IntegrationTest.class)
    public void testTransferForNotSuchToken() {
        final BigInteger senderPrivateKey = CryptoService.getRandomPrivateKey();
        final BigInteger receiverPrivateKey = CryptoService.getRandomPrivateKey();
        try {
            ReddioTransferToApi.transferERC721(
                    DefaultReddioRestClient.testnet(),
                    "0x" + senderPrivateKey.toString(16),
                    "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5",
                    "9999999",
                    "ERC721",
                    "0x" + receiverPrivateKey.toString(16),
                    4194303L
            ).callAndPollRecord();
            Assert.fail();
        } catch (Throwable t) {
            Assert.assertEquals(ReddioErrorCode.NotSuchToken, ((ReddioBusinessException) t).getErrorCode());
        }
    }

    @Test
    @Category(IntegrationTest.class)
    public void testWithdrawalForNotSuchToken() {
        final BigInteger senderPrivateKey = CryptoService.getRandomPrivateKey();
        final BigInteger receiverPrivateKey = CryptoService.getRandomPrivateKey();
        try {
            ReddioWithdrawalToApi.withdrawalERC721(
                    DefaultReddioRestClient.testnet(),
                    "0x" + senderPrivateKey.toString(16),
                    "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5",
                    "9999999",
                    "ERC721",
                    "0x" + receiverPrivateKey.toString(16),
                    4194303L
            ).callAndPollRecord();
            Assert.fail();
        } catch (Throwable t) {
            Assert.assertEquals(ReddioErrorCode.NotSuchToken, ((ReddioBusinessException) t).getErrorCode());
        }
    }

    @Test
    @Category(IntegrationTest.class)
    public void testOrderForNoSuchToken() {
        final BigInteger buyerPrivateKey = CryptoService.getRandomPrivateKey();
        final BigInteger receiverPrivateKey = CryptoService.getRandomPrivateKey();
        try {
            ReddioOrderApi.orderWithETH(
                    DefaultReddioRestClient.testnet(),
                    "0x" + buyerPrivateKey.toString(16),
                    "ERC721",
                    "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5",
                    "99999",
                    "0.01",
                    "1",
                    OrderBehavior.BUY
            ).callAndPollOrder();
        } catch (Throwable t) {
            Assert.assertEquals(ReddioErrorCode.NotSuchToken, ((ReddioBusinessException) t).getErrorCode());
        }
    }
}
