package com.reddio.api.v1.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reddio.api.misc.EnsureSuccess;
import com.reddio.api.misc.ReddioApiKeyInterceptor;
import com.reddio.api.misc.ReddioUAInterceptor;
import com.reddio.api.misc.ToCompletableFutureCallback;
import com.reddio.exception.ReddioException;
import okhttp3.*;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DefaultReddioRestClient implements ReddioRestClient {
    public static final String MAINNET_API_ENDPOINT = "https://api.reddio.com";
    public static final String TESTNET_API_ENDPOINT = "https://api-dev.reddio.com";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.findAndRegisterModules();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private final String baseEndpoint;
    private final String apiKey;
    private final OkHttpClient httpClient;


    public DefaultReddioRestClient(String baseUrl, String apiKey) {
        this.apiKey = apiKey;
        this.baseEndpoint = baseUrl;
        this.httpClient = new OkHttpClient.Builder().addInterceptor(ReddioUAInterceptor.create()).addInterceptor(ReddioApiKeyInterceptor.create(this.apiKey)).build();
    }

    public DefaultReddioRestClient(String baseUrl) {
        this(baseUrl, "");
    }

    public static DefaultReddioRestClient mainnet() {
        return new DefaultReddioRestClient(MAINNET_API_ENDPOINT);
    }

    public static DefaultReddioRestClient mainnet(String apiKey) {
        return new DefaultReddioRestClient(MAINNET_API_ENDPOINT, apiKey);
    }

    public static DefaultReddioRestClient testnet() {
        return new DefaultReddioRestClient(TESTNET_API_ENDPOINT);
    }

    public static DefaultReddioRestClient testnet(String apiKey) {
        return new DefaultReddioRestClient(TESTNET_API_ENDPOINT, apiKey);
    }

    @Override
    public void close() throws Exception {
        // noop
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

        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<TransferResponse>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<BatchTransferResponse>> batchTransfer(BatchTransferMessage batchTransferMessage) {
        String endpoint = baseEndpoint + "/v1/batchtransfer";

        final String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(batchTransferMessage);
        } catch (JsonProcessingException e) {
            throw new ReddioException(e);
        }

        Request request = new Request.Builder().url(endpoint).post(RequestBody.create(jsonString, JSON)).build();
        Call call = this.httpClient.newCall(request);

        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<BatchTransferResponse>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<MultiTransferResponse>> multiTransfer(MultiTransferMessage multiTransferMessage) {
        String endpoint = baseEndpoint + "/v1/multitransfer";

        final String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(multiTransferMessage);
        } catch (JsonProcessingException e) {
            throw new ReddioException(e);
        }

        Request request = new Request.Builder().url(endpoint).post(RequestBody.create(jsonString, JSON)).build();
        Call call = this.httpClient.newCall(request);

        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<MultiTransferResponse>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<GetNonceResponse>> getNonce(GetNonceMessage getNonceMessage) {
        String endpoint = baseEndpoint + "/v1/nonce?stark_key=" + getNonceMessage.getStarkKey();
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<GetNonceResponse>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<GetAssetIdResponse>> getAssetId(GetAssetIdMessage getAssetIdMessage) {
        String endpoint = baseEndpoint + "/v1/assetid?type=" + getAssetIdMessage.getType() + "&contract_address=" + getAssetIdMessage.getContractAddress() + "&token_id=" + getAssetIdMessage.getTokenId() + "&quantum=" + getAssetIdMessage.getQuantum();
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<GetAssetIdResponse>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<GetVaultIdResponse>> getVaultId(GetVaultIdMessage getVaultIdMessage) {
        String endpoint = baseEndpoint + "/v1/vaults?asset_id=" + getVaultIdMessage.getAssetId() + "&stark_keys=" + String.join(",", getVaultIdMessage.getStarkKeys());
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<GetVaultIdResponse>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<GetRecordResponse>> getRecord(GetRecordMessage getRecordMessage) {

        final HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(baseEndpoint + "/v1/record")).newBuilder();
        if (getRecordMessage.getStarkKey() != null) {
            builder.addQueryParameter("stark_key", getRecordMessage.getStarkKey());
        }
        if (getRecordMessage.getSequenceId() != null) {
            builder.addQueryParameter("sequence_id", getRecordMessage.getSequenceId().toString());
        }

        final HttpUrl endpoint = builder.build();
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<GetRecordResponse>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint.toString()));
    }

    @Override
    public CompletableFuture<ResponseWrapper<GetRecordResponse>> getRecordBySignature(Signature signature) {
        String endpoint = baseEndpoint + "/v1/record/by/signature?r=" + Objects.requireNonNull(signature.getR()) + "&s=" + Objects.requireNonNull(signature.getS());
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<GetRecordResponse>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<ListRecordsResponse>> listRecords(ListRecordsMessage listRecordsMessage) {
        final HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(baseEndpoint + "/v1/records")).newBuilder();
        if (listRecordsMessage.getStarkKey() != null) {
            builder.addQueryParameter("stark_key", listRecordsMessage.getStarkKey());
        }
        if (listRecordsMessage.getContractAddress() != null) {
            builder.addQueryParameter("contract_address", listRecordsMessage.getContractAddress());
        }
        if (listRecordsMessage.getLimit() != null) {
            builder.addQueryParameter("limit", listRecordsMessage.getLimit().toString());
        }
        if (listRecordsMessage.getPage() != null) {
            builder.addQueryParameter("page", listRecordsMessage.getPage().toString());
        }
        if (listRecordsMessage.getSequenceIds() != null) {
            builder.addQueryParameter("sequence_ids", listRecordsMessage.getSequenceIds().stream().map(Objects::toString).collect(Collectors.joining(",")));
        }

        final HttpUrl endpoint = builder.build();
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<ListRecordsResponse>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint.toString()));
    }

    @Override
    public CompletableFuture<ResponseWrapper<GetTxnResponse>> getTxn(GetTxnMessage getTxnMessage) {
        String endpoint = baseEndpoint + "/v1/txn?" + "sequence_id=" + getTxnMessage.getSequenceId();

        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<GetTxnResponse>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint));
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

        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<WithdrawalToResponse>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<WithdrawalStatusResponse>> withdrawalStatus(WithdrawalStatusMessage withdrawalStatusMessage) {
        String endpoint = baseEndpoint + "/v1/withdrawal/status?" + "stage=" + withdrawalStatusMessage.getStage() + "&ethaddress=" + withdrawalStatusMessage.getEthAddress();
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<WithdrawalStatusResponse>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint));
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

        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<OrderResponse>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<OrderInfoResponse>> orderInfo(OrderInfoMessage orderInfoMessage) {
        String endpoint = baseEndpoint + "/v1/order/info?stark_key=" + orderInfoMessage.getStarkKey() + "&contract1=" + orderInfoMessage.getContract1() + "&contract2=" + orderInfoMessage.getContract2();

        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<OrderInfoResponse>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<Order>> getOrder(Long orderId) {
        String endpoint = baseEndpoint + "/v1/order?order_id=" + orderId;

        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<Order>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<OrderListResponse>> orderList(OrderListMessage orderListMessage) {
        final HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(baseEndpoint + "/v1/orders")).newBuilder();
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
        if (orderListMessage.getOrderState() != null) {
            builder.addQueryParameter("order_state", Integer.toString(orderListMessage.getOrderState().getValue()));
        }

        final HttpUrl endpoint = builder.build();

        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return

                ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<OrderListResponse>>() {
                }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint.toString()));
    }

    @Override
    public CompletableFuture<ResponseWrapper<CancelOrderResponse>> cancelOrder(Long orderId, CancelOrderMessage cancelOrderMessage) {
        final HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(String.format("%s/v1/orders/%d/cancel", baseEndpoint, orderId))).newBuilder();
        final String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(cancelOrderMessage);
        } catch (JsonProcessingException e) {
            throw new ReddioException(e);
        }
        final HttpUrl endpoint = builder.build();

        Request request = new Request.Builder().url(endpoint).post(RequestBody.create(jsonString, JSON)).build();
        Call call = this.httpClient.newCall(request);
        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<CancelOrderResponse>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint.toString()));
    }

    @Override
    public CompletableFuture<ResponseWrapper<GetContractInfoResponse>> getContractInfo(GetContractInfoMessage getContractInfoMessage) {
        String endpoint = baseEndpoint + "/v1/contract_info?type=" + getContractInfoMessage.getType() + "&contract_address=" + getContractInfoMessage.getContractAddress();

        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<GetContractInfoResponse>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint));
    }

    /**
     * @deprecated use {@link com.reddio.api.v3.rest.ReddioRestClient#getBalances(com.reddio.api.v3.rest.GetBalancesMessage)} as instead.
     */
    @Deprecated
    @Override
    public CompletableFuture<ResponseWrapper<GetBalancesResponse>> getBalances(GetBalancesMessage getBalancesMessage) {
        final HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(baseEndpoint + "/v1/balances")).newBuilder();
        builder.addQueryParameter("stark_key", Objects.requireNonNull(getBalancesMessage.getStarkKey()));
        if (getBalancesMessage.getContractAddress() != null) {
            builder.addQueryParameter("contract_address", getBalancesMessage.getContractAddress());
        }
        if (getBalancesMessage.getLimit() != null) {
            builder.addQueryParameter("limit", getBalancesMessage.getLimit().toString());
        }
        if (getBalancesMessage.getPage() != null) {
            builder.addQueryParameter("page", getBalancesMessage.getPage().toString());
        }
        final HttpUrl endpoint = builder.build();
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<GetBalancesResponse>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint.toString()));
    }

    @Override
    public CompletableFuture<ResponseWrapper<StarexContractsResponse>> starexContracts() {
        String endpoint = baseEndpoint + "/v1/starkex/contracts";
        Request request = new Request.Builder().url(endpoint).get().build();
        Call call = this.httpClient.newCall(request);
        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<StarexContractsResponse>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint));
    }

    @Override
    public CompletableFuture<ResponseWrapper<MintResponse>> mints(MintsMessage mintsMessage) {
        this.requireApiKey();
        final HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(baseEndpoint + "/v1/mints")).newBuilder();

        final String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(mintsMessage);
        } catch (JsonProcessingException e) {
            throw new ReddioException(e);
        }
        final HttpUrl endpoint = builder.build();
        Request request = new Request.Builder().url(endpoint).post(RequestBody.create(jsonString, JSON)).build();
        Call call = this.httpClient.newCall(request);
        return ToCompletableFutureCallback.asFuture(call, new TypeReference<ResponseWrapper<MintResponse>>() {
        }, objectMapper).thenApply(it -> EnsureSuccess.ensureSuccess(it, "endpoint", endpoint.toString()));
    }

    public void requireApiKey() {
        if (null == this.apiKey || this.apiKey.isEmpty()) {
            throw new ReddioException("API key is required");
        }
    }

}
