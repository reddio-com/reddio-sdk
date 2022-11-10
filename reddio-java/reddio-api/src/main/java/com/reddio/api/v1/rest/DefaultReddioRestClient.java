package com.reddio.api.v1.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DefaultReddioRestClient implements ReddioRestClient {
    public static final String MAINNET_API_ENDPOINT = "https://api.reddio.com";
    public static final String TESTNET_API_ENDPOINT = "https://api-dev.reddio.com";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final ObjectMapper objectMapper = new ObjectMapper();

    {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    private final String baseEndpoint;
    private final OkHttpClient httpClient;

    public DefaultReddioRestClient(String baseUrl) {
        this.baseEndpoint = baseUrl;
        this.httpClient = new OkHttpClient.Builder().addInterceptor(ReddioUAInterceptor.create()).build();
    }

    @Override
    public CompletableFuture<ResponseWrapper<TransferResponse>> transfer(TransferMessage transferMessage) {
        String endpoint = baseEndpoint + "/v1/transfers";

        final String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(transferMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Request request = new Request.Builder().url(endpoint).post(RequestBody.create(jsonString, JSON)).build();
        Call call = this.httpClient.newCall(request);

        return asFuture(call, new TypeReference<ResponseWrapper<TransferResponse>>() {
        });
    }

    @Override
    public CompletableFuture<ResponseWrapper<GetNonceResponse>> getNonce(GetNonceMessage getNonceMessage) {
        String endpoint = baseEndpoint + "/v1/nonce?stark_key=" + getNonceMessage.getStarkKey();
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return asFuture(call, new TypeReference<ResponseWrapper<GetNonceResponse>>() {
        });
    }

    @Override
    public CompletableFuture<ResponseWrapper<GetAssetIdResponse>> getAssetId(GetAssetIdMessage getAssetIdMessage) {
        String endpoint = baseEndpoint +
                "/v1/assetid?type=" + getAssetIdMessage.getType() +
                "&contract_address=" + getAssetIdMessage.getContractAddress() +
                "&token_id=" + getAssetIdMessage.getTokenId() +
                "&quantum=" + getAssetIdMessage.getQuantum();
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return asFuture(call, new TypeReference<ResponseWrapper<GetAssetIdResponse>>() {
        });
    }

    @Override
    public CompletableFuture<ResponseWrapper<GetVaultIdResponse>> getVaultId(GetVaultIdMessage getVaultIdMessage) {
        String endpoint = baseEndpoint + "/v1/vaults?asset_id=" + getVaultIdMessage.getAssetId() + "&stark_keys=" + String.join(",", getVaultIdMessage.getStarkKeys());
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return asFuture(call, new TypeReference<ResponseWrapper<GetVaultIdResponse>>() {
        });
    }

    @Override
    public CompletableFuture<ResponseWrapper<GetRecordResponse>> getRecord(GetRecordMessage getRecordMessage) {
        String endpoint = baseEndpoint + "/v1/record?stark_key=" + getRecordMessage.getStarkKey() + "&sequence_id=" + getRecordMessage.getSequenceId();
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return asFuture(call, new TypeReference<ResponseWrapper<GetRecordResponse>>() {
        });
    }

    @Override
    public CompletableFuture<ResponseWrapper<WithdrawalToResponse>> withdrawalTo(WithdrawalToMessage withdrawalToMessage) {
        String endpoint = baseEndpoint + "/v1/withdrawalto";

        final String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(withdrawalToMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Request request = new Request.Builder().url(endpoint).post(RequestBody.create(jsonString, JSON)).build();
        Call call = this.httpClient.newCall(request);

        return asFuture(call, new TypeReference<ResponseWrapper<WithdrawalToResponse>>() {
        });
    }

    @Override
    public CompletableFuture<ResponseWrapper<OrderResponse>> order(OrderMessage orderMessage) {
        String endpoint = baseEndpoint + "/v1/order";

        final String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(orderMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Request request = new Request.Builder().url(endpoint).post(RequestBody.create(jsonString, JSON)).build();
        Call call = this.httpClient.newCall(request);

        return asFuture(call, new TypeReference<ResponseWrapper<OrderResponse>>() {
        });
    }

    @Override
    public CompletableFuture<ResponseWrapper<OrderInfoResponse>> orderInfo(OrderInfoMessage orderInfoMessage) {
        String endpoint = baseEndpoint + "/v1/order/info?stark_key=" + orderInfoMessage.getStarkKey() + "&contract1=" + orderInfoMessage.getContract1() + "&contract2=" + orderInfoMessage.getContract2();

        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return asFuture(call, new TypeReference<ResponseWrapper<OrderInfoResponse>>() {
        });
    }


    @Override
    public CompletableFuture<ResponseWrapper<GetContractInfoResponse>> getContractInfo(GetContractInfoMessage getContractInfoMessage) {
        String endpoint = baseEndpoint + "/v1/contract_info?type=" + getContractInfoMessage.type + "&contract_address=" + getContractInfoMessage.contractAddress;

        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return asFuture(call, new TypeReference<ResponseWrapper<GetContractInfoResponse>>() {
        });
    }

    private static <T> CompletableFuture<T> asFuture(Call call, TypeReference<T> typeReference) {
        CompletableFuture<T> future = new CompletableFuture<>();
        // notice: the HTTP request would execute in the background after call.enqueue(), not after the future.get().
        call.enqueue(new ToCompletableFutureCallback<>(future, typeReference));
        return future;
    }

    private static class ToCompletableFutureCallback<T> implements Callback {
        private final CompletableFuture<T> future;
        private final TypeReference<T> typeReference;

        public ToCompletableFutureCallback(CompletableFuture<T> future, TypeReference<T> typeReference) {
            this.future = future;
            this.typeReference = typeReference;
        }

        @Override
        public void onFailure(@NotNull Call call, IOException e) {
            this.future.completeExceptionally(e);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            String jsonString = Objects.requireNonNull(response.body()).string();
            this.future.complete(objectMapper.readValue(jsonString, typeReference));
        }
    }

    public static final class ReddioUAInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest.newBuilder()
                    // TODO(@STRRL): use the release version
                    .header("User-Agent", "reddio-client-java/0.0.1").build();
            return chain.proceed(requestWithUserAgent);
        }

        public static ReddioUAInterceptor create() {
            return new ReddioUAInterceptor();
        }
    }

    public static DefaultReddioRestClient mainnet() {
        return new DefaultReddioRestClient(MAINNET_API_ENDPOINT);
    }

    public static DefaultReddioRestClient testnet() {
        return new DefaultReddioRestClient(TESTNET_API_ENDPOINT);
    }
}
