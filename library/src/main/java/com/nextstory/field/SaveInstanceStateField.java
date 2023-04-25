package com.nextstory.field;

import android.os.Bundle;

import androidx.annotation.NonNull;

/**
 * 상태 저장 필드 인터페이스
 * 액티비티 회전 등 설정 변경시 필드가 초기화되는걸 방지하기 위함
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public interface SaveInstanceStateField {
  /**
   * @return 상태값 저장을 위한 키값
   */
  String getKey();

  /**
   * 키값 지정
   *
   * @param key 키
   */
  void setKey(String key);

  /**
   * 상태 복구시 호출
   *
   * @param savedInstanceState 번들
   */
  void onRestoreInstanceState(@NonNull Bundle savedInstanceState);

  /**
   * 상태 저장시 호출
   *
   * @param outState 번들
   */
  void onSaveInstanceState(@NonNull Bundle outState);
}
