package com.nextstory.http;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.lang.reflect.Type;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

/**
 * HTTP 요청 빌더
 *
 * @author troy
 * @since 1.1
 */
@SuppressWarnings({"UnusedDeclaration", "UnusedReturnValue"})
public interface HttpRequestBuilder {
  /**
   * 요청 URL 설정
   *
   * @param url URL
   * @return 빌더 인스턴스
   */
  HttpRequestBuilder setUrl(String url);

  /**
   * 요청 시 포함할 필드를 추가함
   *
   * @param key   필드 키
   * @param value 필드 값
   * @return 빌더 인스턴스
   */
  HttpRequestBuilder addField(String key, Object value);

  /**
   * 요청 시 포함할 필드를 추가함
   *
   * @param map 컬렉션 형태의 필드
   * @return 빌더 인스턴스
   * @since 1.2
   */
  HttpRequestBuilder addField(Map<String, Object> map);

  /**
   * 요청 시 포함할 직렬화 데이터 필드를 설정함
   * {@link ResponseConverter}를
   *
   * @param object 직렬화할 객체
   * @return 빌더 인스턴스
   * @since 1.4
   */
  HttpRequestBuilder putRequestBodyObject(Object object);

  /**
   * 요청 시 포함할 필드를 추가함
   *
   * @param body 요청 body
   * @param uri  Uri
   * @return 빌더 인스턴스
   */
  HttpRequestBuilder addMultipartByUri(@NonNull String body, @NonNull Uri uri);

  /**
   * 요청
   *
   * @param <T> 타입
   * @return 작업 인스턴스
   */
  <T> Single<T> request(Type type);

  /**
   * 스트리밍 요청
   *
   * @param bufferSize 스트리밍 버퍼 크기
   * @return 작업 인스턴스
   */
  Observable<StreamingState> requestStreaming(int bufferSize);
}
