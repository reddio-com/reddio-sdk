package com.reddio.api.v1.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.reddio.api.v1.DefaultEthereumInteractionTest.REDDIO721_CONTRACT_ADDRESS;

public class DefaultReddioRestClientTest extends TestCase {

    public void testGetRecord() throws ExecutionException, InterruptedException {
        DefaultReddioRestClient client = DefaultReddioRestClient.testnet();
        ResponseWrapper<GetRecordResponse> response = client.getRecord(
                GetRecordMessage.of("0x6736f7449da3bf44bf0f7bdd6463818e1ef272641d43021e8bca17b32ec2df0", 300523L)).get();
        Assert.assertEquals(SequenceRecord.SEQUENCE_STATUS_ACCEPTED, response.data.get(0).status);
    }

    public void testGetTxn() throws ExecutionException, InterruptedException {
        DefaultReddioRestClient client = DefaultReddioRestClient.testnet();
        ResponseWrapper<GetTxnResponse> response = client.getTxn(
                GetTxnMessage.of(300523L)).get();
        Assert.assertEquals(SequenceRecord.SEQUENCE_STATUS_ACCEPTED, response.data.get(0).status);
    }

    public void testOrderList() throws ExecutionException, InterruptedException {
        DefaultReddioRestClient client = DefaultReddioRestClient.testnet();
        CompletableFuture<ResponseWrapper<OrderListResponse>> future = client.orderList(OrderListMessage.of(
                "0x1c2847406b96310a32c379536374ec034b732633e8675860f20f4141e701ff4",
                REDDIO721_CONTRACT_ADDRESS,
                null,
                null,
                null,
                null,
                null));
        ResponseWrapper<OrderListResponse> result = future.get();
        Assert.assertEquals("OK", result.status);
    }

    public void testStarexContract() throws JsonProcessingException, ExecutionException, InterruptedException {
        DefaultReddioRestClient client = DefaultReddioRestClient.testnet();
        CompletableFuture<ResponseWrapper<StarexContractsResponse>> future = client.starexContracts();
        ResponseWrapper<StarexContractsResponse> result = future.get();
        Assert.assertEquals("OK", result.status);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(result);
        System.out.println(jsonContent);
    }

    public void testGetRecordBySignature() throws ExecutionException, InterruptedException {
        DefaultReddioRestClient restClient = DefaultReddioRestClient.testnet();
        ResponseWrapper<GetRecordResponse> wrapper = restClient.getRecordBySignature(Signature.of("0x603893549d9fbb1d710ca68f2e44aa8ae2f7c4353219cb662d94e48971dd15f", "0x7556a28be6f49fa896077b3bd96f909e07a226392e2af93852e4ecd8d56814d")).get();
        Assert.assertEquals("OK", wrapper.getStatus());
        Assert.assertEquals(1, wrapper.getData().size());

        SequenceRecord record = wrapper.getData().get(0);
        Assert.assertEquals("1", record.getAmount());
        Assert.assertEquals(RecordType.ASKOrderRecordType, record.getRecordType());
        Assert.assertEquals(304287L, record.getSequenceId());
        Assert.assertEquals("0x1ccc27877014bc1a81919fc855ebbd1b874603283c9ea93397d970b0704e581", record.getStarkKey());
        Assert.assertEquals(RecordStatus.AcceptedByReddio, record.getStatus());
        Assert.assertEquals(1676261428L, record.getTime());


        SequenceRecord.Order order = record.getOrder();
        Assert.assertEquals("0x352f9ffd821a525051de2d71126113505a7b0a73d98dbc0ac0ff343cfbdef5e", order.getBaseAssetId());
        Assert.assertEquals("ETH", order.getBaseAssetName());
        Assert.assertEquals("eth", order.getBaseContractAddress());
        Assert.assertEquals(0, order.getDirection());
        Assert.assertEquals("0.001", order.getDisplayPrice());
        Assert.assertEquals("ETH", order.getFeeAssetName());
        Assert.assertEquals("20", order.getFeeTaken());
        Assert.assertEquals("0x352f9ffd821a525051de2d71126113505a7b0a73d98dbc0ac0ff343cfbdef5e", order.getFeeTokenAsset());
        Assert.assertEquals("1", order.getFilled());
        Assert.assertEquals("1000", order.getPrice());
        Assert.assertEquals("0x186c5c7aea54893970e449b6e73604c5f21e60f7004ce948829366a5384de7a", order.getQuoteAssetId());
        Assert.assertEquals("REDDIO721", order.getQuoteAssetName());
        Assert.assertEquals("ERC721", order.getQuoteAssetType());
        Assert.assertEquals("0x941661bd1134dc7cc3d107bf006b8631f6e65ad5", order.getQuoteContractAddress());
        Assert.assertEquals("1041", order.getTokenId());
        Assert.assertEquals("1000", order.getVolume());

    }

    public void testGetRecordBySignature2() throws ExecutionException, InterruptedException {
        DefaultReddioRestClient restClient = DefaultReddioRestClient.testnet();
        ResponseWrapper<GetRecordResponse> wrapper = restClient.getRecordBySignature(Signature.of("0x6314640fa3955fec989b9c8e85446d3f10fffad4f2df8b3a1f90d75e9649804", "0x70514b88e78028eacdaad5583bda1ecfd93d4fbe5f65eadaeb42e94201662e9")).get();
        Assert.assertEquals("OK", wrapper.getStatus());
        Assert.assertEquals(1, wrapper.getData().size());

        SequenceRecord record = wrapper.getData().get(0);
        Assert.assertEquals("1", record.getAmount());
        Assert.assertEquals("0x18a953f110c1fb954319716c388bd1f1064aebddb1ce535287256132727b8e2", record.getAssetId());
        Assert.assertEquals("REDDIO721", record.getAssetName());
        Assert.assertEquals("ERC721", record.getAssetType());
        Assert.assertEquals("0x941661bd1134dc7cc3d107bf006b8631f6e65ad5", record.getContractAddress());
        Assert.assertEquals("1", record.getDisplayValue());
        Assert.assertEquals(RecordType.WithdrawRecordType, record.getRecordType());
        Assert.assertEquals(304312L, record.getSequenceId());
        Assert.assertEquals("0x7865bc66b610d6196a7cbeb9bf066c64984f6f06b5ed3b6f5788bd9a6cb099c", record.getStarkKey());
        Assert.assertEquals(RecordStatus.AcceptedOnL1, record.getStatus());
        Assert.assertEquals(1676351391L, record.getTime());


        Assert.assertNull(record.getOrder());
    }
}