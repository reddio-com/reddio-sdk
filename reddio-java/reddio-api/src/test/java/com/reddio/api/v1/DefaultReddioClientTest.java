package com.reddio.api.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reddio.api.v1.rest.*;
import com.reddio.crypto.CryptoService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class DefaultReddioClientTest {
    @Test
    public void testSign() {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        Signature signature = client.signTransferMessage$reddio_api(
                "0xa7b68cf2ee72b2a0789914daa8ae928aec21b6b0bf020e394833f4c732d99d",
                "1",
                59,
                "23400424",
                "0x1c9c7dee2be5015eddff167dda36f319f6bfcd4bd41f9d5cb49fe164bc58cb1",
                "23400425",
                "0x7865bc66b610d6196a7cbeb9bf066c64984f6f06b5ed3b6f5788bd9a6cb099c", 4194303L
        );
        Assert.assertEquals("0x7b191774b10a208331d716ab4fe0ecd24b430d0142bdd123a14d243abf626b1", signature.r);
        Assert.assertEquals("0x21f3a32d5779668d66af7f9f161d90afb3765c3a6326b6397f73ea346f94e5d", signature.s);
    }

    @Test
    public void testTransfer() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        CompletableFuture<ResponseWrapper<TransferResponse>> future = client.transfer(
                "0x6736f7449da3bf44bf0f7bdd6463818e1ef272641d43021e8bca17b32ec2df0",
                "0xa7b68cf2ee72b2a0789914daa8ae928aec21b6b0bf020e394833f4c732d99d",
                "1",
                "0x941661bd1134dc7cc3d107bf006b8631f6e65ad5",
                "497",
                "ERC721",
                "0x7865bc66b610d6196a7cbeb9bf066c64984f6f06b5ed3b6f5788bd9a6cb099c",
                4194303L
        );
        ResponseWrapper<TransferResponse> result = future.get();
        Assert.assertEquals("OK", result.status);
        System.out.println(new ObjectMapper().writeValueAsString(result));
    }

    @Test
    public void testWaitingRecordGetApproved() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        CompletableFuture<ResponseWrapper<GetRecordResponse>> future = client.waitingTransferGetApproved("0x6736f7449da3bf44bf0f7bdd6463818e1ef272641d43021e8bca17b32ec2df0",
                300523);
        ResponseWrapper<GetRecordResponse> result = future.get();
        Assert.assertEquals("OK", result.status);
        System.out.println(new ObjectMapper().writeValueAsString(result));
    }

    @Test
    public void testWithdrawalGoerliETH() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        CompletableFuture<ResponseWrapper<WithdrawalToResponse>> future = client.withdrawal(
                "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
                "0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d",
                // 13x10^-6, 0.000013 ETH
                "13",
                "ETH",
                "",
                "ETH",
                "0x76f2Fc7ed90039d986e3eb4DB294f05E160c8F03",
                4194303L
        );
        ResponseWrapper<WithdrawalToResponse> result = future.get();
        Assert.assertEquals("OK", result.status);
        System.out.println(new ObjectMapper().writeValueAsString(result));
    }

    @Test
    @Ignore("this test is not reproducible because it depends on the real stock of the NFT on layer2")
    public void testWithdrawalNTFERC721() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        CompletableFuture<ResponseWrapper<WithdrawalToResponse>> future = client.withdrawal(
                "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
                "0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d",
                "1",
                "0x941661bd1134dc7cc3d107bf006b8631f6e65ad5",
                "1022",
                "ERC721",
                "0x76f2Fc7ed90039d986e3eb4DB294f05E160c8F03",
                4194303L
        );
        ResponseWrapper<WithdrawalToResponse> result = future.get();
        Assert.assertEquals("OK", result.status);
        System.out.println(new ObjectMapper().writeValueAsString(result));
    }

    @Test
    @Ignore("this test is not reproducible because it depends on the real stock of the NFT on layer2")
    public void testWithdrawalNTFERC721M() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        CompletableFuture<ResponseWrapper<WithdrawalToResponse>> future = client.withdrawal(
                "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
                "0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d",
                "1",
                "0xe3d2a2ca17a8dedb740b6c259b4eeeaaf81c9fb6",
                "3",
                "ERC721M",
                "0x76f2Fc7ed90039d986e3eb4DB294f05E160c8F03",
                4194303L
        );
        ResponseWrapper<WithdrawalToResponse> result = future.get();
        Assert.assertEquals("OK", result.status);
        System.out.println(new ObjectMapper().writeValueAsString(result));
    }

    @Test
    @Ignore("not reproducible test")
    public void testOrder() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        DefaultReddioRestClient restClient = DefaultReddioRestClient.testnet();
        CompletableFuture<ResponseWrapper<GetBalancesResponse>> balancesFuture = restClient.getBalances(GetBalancesMessage.of(
                "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
                "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5",
                10L)
        );
        ResponseWrapper<GetBalancesResponse> balances = balancesFuture.get();
        Assert.assertEquals("OK", balances.status);
        GetBalancesResponse.BalanceRecord toSell = balances.getData().getList().stream().filter((it) ->
                it.balanceAvailable > 0
        ).collect(Collectors.toList()).get(0);
        CompletableFuture<ResponseWrapper<OrderResponse>> future = client.order(
                "0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d",
                "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
                "0.013",
                "1",
                "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5",
                toSell.getTokenId(),
                "11ed793a-cc11-4e44-9738-97165c4e14a7",
                "ERC721",
                OrderType.SELL
        );
        ResponseWrapper<OrderResponse> result = future.get();
        System.out.println(new ObjectMapper().writeValueAsString(result));
        Assert.assertEquals("OK", result.status);
    }

    @Test
//    @Ignore("not reproducible test")
    public void testOrderWithERC20() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        DefaultReddioRestClient restClient = DefaultReddioRestClient.testnet();
        CompletableFuture<ResponseWrapper<GetBalancesResponse>> balancesFuture = restClient.getBalances(GetBalancesMessage.of(
                "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
                "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5",
                10L)
        );
        ResponseWrapper<GetBalancesResponse> balances = balancesFuture.get();
        Assert.assertEquals("OK", balances.status);
        GetBalancesResponse.BalanceRecord toSell = balances.getData().getList().stream().filter((it) ->
                it.balanceAvailable > 0
        ).collect(Collectors.toList()).get(0);
        CompletableFuture<ResponseWrapper<OrderResponse>> future = client.order(
                "0x4d55b547af138c5b6200495d86ab6aed3e06c25fdd75b4b6a00e48515df2b3d",
                "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
                "ERC721",
                "0x941661Bd1134DC7cc3D107BF006B8631F6E65Ad5",
                toSell.getTokenId(),
                "0.013",
                "1",
                OrderType.SELL,
                "ERC20",
                "0x57f3560b6793dcc2cb274c39e8b8eba1dd18a086",
                ""
        );
        ResponseWrapper<OrderResponse> result = future.get();
        System.out.println(new ObjectMapper().writeValueAsString(result));
        Assert.assertEquals("OK", result.status);
    }
}
