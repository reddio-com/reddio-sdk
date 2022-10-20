package com.reddio.api.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reddio.api.v1.rest.GetRecordResponse;
import com.reddio.api.v1.rest.ResponseWrapper;
import com.reddio.api.v1.rest.Signature;
import com.reddio.api.v1.rest.TransferResponse;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class DefaultReddioClientTest extends TestCase {
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

    public void testWaitingRecordGetApproved() throws ExecutionException, InterruptedException, JsonProcessingException {
        DefaultReddioClient client = DefaultReddioClient.testnet();
        CompletableFuture<ResponseWrapper<GetRecordResponse>> future = client.waitingTransferGetApproved("0x6736f7449da3bf44bf0f7bdd6463818e1ef272641d43021e8bca17b32ec2df0",
                300523);
        ResponseWrapper<GetRecordResponse> result = future.get();
        Assert.assertEquals("OK", result.status);
        System.out.println(new ObjectMapper().writeValueAsString(result));
    }
}
