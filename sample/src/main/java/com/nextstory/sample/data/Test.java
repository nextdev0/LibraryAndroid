package com.nextstory.sample.data;

import com.nextstory.annotations.AutoSharedPreferences;
import com.nextstory.annotations.AutoSharedPreferencesItem;

/**
 * @author troy
 * @since 1.0
 */
@AutoSharedPreferences("test_settings")
interface Test {
  @AutoSharedPreferencesItem("value_1")
  int value1 = 3;

  @AutoSharedPreferencesItem("value_2")
  int value2 = 0;

  // @AutoSharedPreferencesItem(저장될 키 이름)
  // int 변수명 = 기본값;
}
