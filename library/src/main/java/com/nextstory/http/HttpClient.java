package com.nextstory.http;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

/**
 * HTTP 클라이언트
 *
 * @author troy
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

    /**
     * 내부 스토리지 경로 참조를 위한 컨텍스트
     *
     * @return 컨텍스트
     */
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

    /**
     * 응답 데이터 컨버터 등록
     *
     * @param responseConverter 컨버터
     */
    public void registerResponseConverter(@NonNull ResponseConverter responseConverter) {
        this.responseConverter = responseConverter;
    }

    /**
     * @return POST 요청 빌더
     */
    public HttpRequestBuilder newPostRequest() {
        return new PostHttpRequestBuilder(this);
    }

    /**
     * @return GET 요청 빌더
     */
    public HttpRequestBuilder newGetRequest() {
        return new GetHttpRequestBuilder(this);
    }

    /**
     * 응답 처리
     *
     * @param responseSource 응답 결과 문자열 소스
     * @param clazz          반환 타입 클래스
     * @param <T>            변환 타입
     * @return 변환된 모델 인스턴스
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    <T> T onResponse(@NonNull String responseSource, Class<T> clazz) {
        if (responseConverter == null) {
            return null;
        }
        return responseConverter.convert(responseSource, clazz);
    }

    /**
     * 빌더 class
     */
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
