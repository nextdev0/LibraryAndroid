package com.nextstory.http;

import androidx.annotation.NonNull;

/**
 * 응답 결과 변환 인터페이스
 *
 * @author troy
 * @since 1.1
 */
@SuppressWarnings("UnusedDeclaration")
public interface ResponseConverter {
  /**
   * 응답 데이터 변환
   *
   * @param response 응답 문자열 소스
   * @param clazz    모델 타입 클래스
   * @param <T>      모델 타입
   * @return 모델 데이터
   */
  <T> T convert(@NonNull String response, @NonNull Class<T> clazz);
}
