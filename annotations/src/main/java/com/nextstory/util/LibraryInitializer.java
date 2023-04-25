package com.nextstory.util;

import android.content.Context;

/**
 * 라이브러리 초기화 인터페이스
 *
 * @author troy
 * @since 1.0
 */
public interface LibraryInitializer {
  /**
   * 초기화 시 호출
   *
   * @param context  앱 컨텍스트
   * @param argument 초기화 인수
   */
  void onInitialized(Context context, String argument);
}
