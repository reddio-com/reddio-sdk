package com.reddio.api.misc;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class ReddioApiKeyInterceptor implements Interceptor {

    private final String apiKey;

    public ReddioApiKeyInterceptor(String apiKey) {
        this.apiKey = apiKey;
    }

    public static ReddioApiKeyInterceptor create(String apiKey) {
        return new ReddioApiKeyInterceptor(apiKey);
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request requestWithUserAgent = originalRequest.newBuilder().header("x-api-key", this.apiKey).build();
        return chain.proceed(requestWithUserAgent);
    }
}
