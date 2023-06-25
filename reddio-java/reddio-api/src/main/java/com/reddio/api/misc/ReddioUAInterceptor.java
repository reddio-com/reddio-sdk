package com.reddio.api.misc;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public final class ReddioUAInterceptor implements Interceptor {

    private final String version;

    public ReddioUAInterceptor(String version) {
        this.version = version;
    }

    private static String geMavenProjectVersion() {
        Properties properties = new Properties();
        try {
            properties.load(Objects.requireNonNull(ReddioUAInterceptor.class.getResourceAsStream("/version.properties")));
        } catch (IOException e) {
            throw new RuntimeException("get maven project version from resource /version.properties", e);
        }
        return properties.getProperty("version");
    }

    public static ReddioUAInterceptor create() {
        return new ReddioUAInterceptor(geMavenProjectVersion());
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request requestWithUserAgent = originalRequest.newBuilder().header("User-Agent", String.format("reddio-client-java/%s", this.version)).build();
        return chain.proceed(requestWithUserAgent);
    }

}
