package com.reddio.api.misc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reddio.api.v1.rest.ResponseWrapper;
import com.reddio.exception.ReddioBusinessException;
import com.reddio.exception.ReddioErrorCode;
import com.reddio.exception.ReddioException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ToCompletableFutureCallback<T> implements Callback {

    private final CompletableFuture<T> future;
    private final TypeReference<T> typeReference;

    private final ObjectMapper objectMapper;

    public ToCompletableFutureCallback(CompletableFuture<T> future, TypeReference<T> typeReference, ObjectMapper objectMapper) {
        this.future = future;
        this.typeReference = typeReference;
        this.objectMapper = objectMapper;
    }

    public static <T> CompletableFuture<T> asFuture(Call call, TypeReference<T> typeReference, ObjectMapper objectMapper) {
        CompletableFuture<T> future = new CompletableFuture<>();
        // notice: the HTTP request would execute in the background after call.enqueue(), not after the future.get().
        call.enqueue(new ToCompletableFutureCallback<>(future, typeReference, objectMapper));
        return future;
    }

    public ResponseWrapper<Object> tryExtractParseResponse(String responseJsonString) {
        try {
            return objectMapper.readValue(responseJsonString, new TypeReference<ResponseWrapper<Object>>() {
            });
        } catch (Throwable e) {
            // TODO: debug log
            return null;
        }

    }

    @Override
    public void onFailure(Call call, IOException e) {
        this.future.completeExceptionally(e);
    }

    @Override
    public void onResponse(Call call, Response response) {
        try {
            String responseBodyAsString = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                final ResponseWrapper<?> model = tryExtractParseResponse(responseBodyAsString);
                if (model != null) {
                    this.future.completeExceptionally(new ReddioBusinessException(model.getStatus(), model.getError(), ReddioErrorCode.fromCode(model.getErrorCode()), model));
                } else {
                    this.future.completeExceptionally(new ReddioException(String.format("reddio service responded with status code %d, response body: %s", response.code(), responseBodyAsString)));
                }
                return;
            }
            this.future.complete(objectMapper.readValue(responseBodyAsString, typeReference));
        } catch (Throwable e) {
            this.future.completeExceptionally(e);
        }
    }
}
