package com.nextstory.http;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.lang.reflect.Type;

import io.reactivex.rxjava3.core.Single;

/**
 * HTTP 요청 빌더
 *
 * @author troy
 * @version 1.0
 * @since 1.1
 */
@SuppressWarnings("UnusedDeclaration")
public interface HttpRequestBuilder {
    /**
     * 요청 URL 설정
     *
     * @param url URL
     */
    HttpRequestBuilder setUrl(String url);

    /**
     * 요청 시 포함할 필드를 추가함
     *
     * @param key   필드 키
     * @param value 필드 값
     */
    HttpRequestBuilder addField(String key, Object value);

    /**
     * 요청 시 포함할 필드를 추가함
     *
     * @param body 요청 body
     * @param uri  Uri
     */
    HttpRequestBuilder addMultipartByUri(@NonNull String body, @NonNull Uri uri);

    /**
     * 요청
     *
     * @param <T> 타입
     * @return 작업 인스턴스
     */
    <T> Single<T> request(Type type);
}
