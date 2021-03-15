package com.nextstory.http;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

/**
 * HTTP 클라이언트
 *
 * @author troy
 * @version 1.0
 * @since 1.1
 */
@SuppressWarnings("UnusedDeclaration")
public class HttpClient {
    private static final String TAG = "HttpClient";
    private final Context context;
    private ResponseConverter responseConverter = null;
    private String baseUrl = "";
    private int connectionTimeout = 20000;
    private int readTimeout = 20000;

    private HttpClient(@NonNull Context context) {
        this.context = context;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    Context getContext() {
        return context;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void registerResponseConverter(@NonNull ResponseConverter responseConverter) {
        this.responseConverter = responseConverter;
    }

    public HttpRequestBuilder newPostRequest() {
        return new PostHttpRequestBuilder(this);
    }

    public HttpRequestBuilder newGetRequest() {
        return new GetHttpRequestBuilder(this);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    <T> T onResponse(@NonNull String responseSource, Class<T> clazz) {
        if (responseConverter == null) {
            return null;
        }
        return responseConverter.convert(responseSource, clazz);
    }

    public static class Builder {
        private final Context context;
        private ResponseConverter responseConverter = null;
        private String baseUrl = "";
        private int connectionTimeout = 20000;
        private int readTimeout = 20000;

        public Builder(@NonNull Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder registerResponseConverter(@NonNull ResponseConverter responseConverter) {
            this.responseConverter = responseConverter;
            return this;
        }

        public HttpClient build() {
            HttpClient httpClient = new HttpClient(context);
            httpClient.setBaseUrl(baseUrl);
            httpClient.setConnectionTimeout(connectionTimeout);
            httpClient.setReadTimeout(readTimeout);
            httpClient.registerResponseConverter(responseConverter);
            return httpClient;
        }
    }
}
