package com.reddio.api.v1.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reddio.ReddioException;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
            throw new ReddioException(e);
        }

        Request request = new Request.Builder().url(endpoint).post(RequestBody.create(jsonString, JSON)).build();
        Call call = this.httpClient.newCall(request);

        return asFuture(call, new TypeReference<ResponseWrapper<TransferResponse>>() {
        }).thenApply(it -> ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<GetNonceResponse>> getNonce(GetNonceMessage getNonceMessage) {
        String endpoint = baseEndpoint + "/v1/nonce?stark_key=" + getNonceMessage.getStarkKey();
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return asFuture(call, new TypeReference<ResponseWrapper<GetNonceResponse>>() {
        }).thenApply(it -> ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<GetAssetIdResponse>> getAssetId(GetAssetIdMessage getAssetIdMessage) {
        String endpoint = baseEndpoint + "/v1/assetid?type=" + getAssetIdMessage.getType() + "&contract_address=" + getAssetIdMessage.getContractAddress() + "&token_id=" + getAssetIdMessage.getTokenId() + "&quantum=" + getAssetIdMessage.getQuantum();
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return asFuture(call, new TypeReference<ResponseWrapper<GetAssetIdResponse>>() {
        }).thenApply(it -> ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<GetVaultIdResponse>> getVaultId(GetVaultIdMessage getVaultIdMessage) {
        String endpoint = baseEndpoint + "/v1/vaults?asset_id=" + getVaultIdMessage.getAssetId() + "&stark_keys=" + String.join(",", getVaultIdMessage.getStarkKeys());
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return asFuture(call, new TypeReference<ResponseWrapper<GetVaultIdResponse>>() {
        }).thenApply(it -> ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<GetRecordResponse>> getRecord(GetRecordMessage getRecordMessage) {
        String endpoint = baseEndpoint + "/v1/record?stark_key=" + getRecordMessage.getStarkKey() + "&sequence_id=" + getRecordMessage.getSequenceId();
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return asFuture(call, new TypeReference<ResponseWrapper<GetRecordResponse>>() {
        }).thenApply(it -> ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<WithdrawalToResponse>> withdrawalTo(WithdrawalToMessage withdrawalToMessage) {
        String endpoint = baseEndpoint + "/v1/withdrawalto";

        final String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(withdrawalToMessage);
        } catch (JsonProcessingException e) {
            throw new ReddioException(e);
        }

        Request request = new Request.Builder().url(endpoint).post(RequestBody.create(jsonString, JSON)).build();
        Call call = this.httpClient.newCall(request);

        return asFuture(call, new TypeReference<ResponseWrapper<WithdrawalToResponse>>() {
        }).thenApply(it -> ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<OrderResponse>> order(OrderMessage orderMessage) {
        String endpoint = baseEndpoint + "/v1/order";

        final String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(orderMessage);
        } catch (JsonProcessingException e) {
            throw new ReddioException(e);
        }

        Request request = new Request.Builder().url(endpoint).post(RequestBody.create(jsonString, JSON)).build();
        Call call = this.httpClient.newCall(request);

        return asFuture(call, new TypeReference<ResponseWrapper<OrderResponse>>() {
        }).thenApply(it -> ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<OrderInfoResponse>> orderInfo(OrderInfoMessage orderInfoMessage) {
        String endpoint = baseEndpoint + "/v1/order/info?stark_key=" + orderInfoMessage.getStarkKey() + "&contract1=" + orderInfoMessage.getContract1() + "&contract2=" + orderInfoMessage.getContract2();

        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return asFuture(call, new TypeReference<ResponseWrapper<OrderInfoResponse>>() {
        }).thenApply(it -> ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<OrderListResponse>> orderList(OrderListMessage orderListMessage) {
        final HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(baseEndpoint + "/v1/balances")).newBuilder();
        if (orderListMessage.getStarkKey() != null) {
            builder.addQueryParameter("stark_key", orderListMessage.getStarkKey());
        }
        if (orderListMessage.getContractAddress() != null) {
            builder.addQueryParameter("contract_address", orderListMessage.getContractAddress());
        }
        if (orderListMessage.getDirection() != null) {
            builder.addQueryParameter("direction", orderListMessage.getDirection().toString());
        }
        if (orderListMessage.getTokenIds() != null && !orderListMessage.getTokenIds().isEmpty()) {
            builder.addQueryParameter("token_ids", orderListMessage.getTokenIds().stream().map(Object::toString).collect(Collectors.joining(",")));
        }
        if (orderListMessage.getLimit() != null) {
            builder.addQueryParameter("limit", orderListMessage.getLimit().toString());
        }
        if (orderListMessage.getPage() != null) {
            builder.addQueryParameter("page", orderListMessage.getPage().toString());
        }

        final HttpUrl endpoint = builder.build();

        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return

                asFuture(call, new TypeReference<ResponseWrapper<OrderListResponse>>() {
                }).thenApply(it -> ensureSuccess(it, "endpoint", endpoint.toString()));
    }

    @Override
    public CompletableFuture<ResponseWrapper<GetContractInfoResponse>> getContractInfo(GetContractInfoMessage getContractInfoMessage) {
        String endpoint = baseEndpoint + "/v1/contract_info?type=" + getContractInfoMessage.type + "&contract_address=" + getContractInfoMessage.contractAddress;

        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return asFuture(call, new TypeReference<ResponseWrapper<GetContractInfoResponse>>() {
        }).thenApply(it -> ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<GetBalancesResponse>> getBalances(GetBalancesMessage getBalancesMessage) {
        final HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(baseEndpoint + "/v1/balances")).newBuilder();
        builder.addQueryParameter("stark_key", Objects.requireNonNull(getBalancesMessage.starkKey));
        if (getBalancesMessage.getContractAddress() != null) {
            builder.addQueryParameter("contract_address", getBalancesMessage.contractAddress);
        }
        if (getBalancesMessage.getLimit() != null) {
            builder.addQueryParameter("limit", getBalancesMessage.limit.toString());
        }
        if (getBalancesMessage.getPage() != null) {
            builder.addQueryParameter("page", getBalancesMessage.page.toString());
        }
        final HttpUrl endpoint = builder.build();
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return asFuture(call, new TypeReference<ResponseWrapper<GetBalancesResponse>>() {
        }).thenApply(it -> ensureSuccess(it, "endpoint", endpoint.toString()));
    }

    @Override
    public CompletableFuture<ResponseWrapper<StarexContractsResponse>> starexContracts() {
        String endpoint = baseEndpoint + "/v1/starkex/contracts";
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return asFuture(call, new TypeReference<ResponseWrapper<StarexContractsResponse>>() {
        }).thenApply(it -> ensureSuccess(it, "endpoint", endpoint));
    }

    private static <T> CompletableFuture<T> asFuture(Call call, TypeReference<T> typeReference) {
        CompletableFuture<T> future = new CompletableFuture<>();
        // notice: the HTTP request would execute in the background after call.enqueue(), not after the future.get().
        call.enqueue(new ToCompletableFutureCallback<>(future, typeReference));
        return future;
    }

    public static <T> ResponseWrapper<T> ensureSuccess(ResponseWrapper<T> responseWrapper, String... messages) {
        if ("OK".equals(responseWrapper.getStatus())) {
            return responseWrapper;
        }
        throw new ReddioException("response status is not OK, status: " + responseWrapper.getStatus() + ", error: " + responseWrapper.error + ", messages: " + String.join(",", messages));
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
            if (!response.isSuccessful()) {
                this.future.completeExceptionally(new IOException("response is not successful, code: " + response.code()));
                return;
            }
            String jsonString = Objects.requireNonNull(response.body()).string();
            this.future.complete(objectMapper.readValue(jsonString, typeReference));
        }
    }

    public static final class ReddioUAInterceptor implements Interceptor {

        public ReddioUAInterceptor(String version) {
            this.version = version;
        }

        private String version;

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest.newBuilder().header("User-Agent", String.format("reddio-client-java/%s", this.version)).build();
            return chain.proceed(requestWithUserAgent);
        }

        private static String geMavenProjecttVersion() {
            Properties properties = new Properties();
            try {
                properties.load(Objects.requireNonNull(ReddioUAInterceptor.class.getResourceAsStream("/version.properties")));
            } catch (IOException e) {
                throw new RuntimeException("get maven project version from resource /version.properties", e);
            }
            return properties.getProperty("version");
        }

        public static ReddioUAInterceptor create() {
            return new ReddioUAInterceptor(geMavenProjecttVersion());
        }

    }

    public static DefaultReddioRestClient mainnet() {
        return new DefaultReddioRestClient(MAINNET_API_ENDPOINT);
    }

    public static DefaultReddioRestClient testnet() {
        return new DefaultReddioRestClient(TESTNET_API_ENDPOINT);
    }
}
