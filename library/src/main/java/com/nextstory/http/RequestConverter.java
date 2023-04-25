package com.nextstory.http;

/**
 * 요청 데이터 변환 인터페이스
 *
 * @author troy
 * @since 1.4
 */
public interface RequestConverter {
  /**
   * 요청 시 필요한 데이터로 변환
   *
   * @param object 변환할 객체
   * @return 직렬화된 문자열 데이터
   */
  String convert(Object object);
}
