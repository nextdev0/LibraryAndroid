package com.nextstory.util;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * {@link TextWatcher} 간소화 인터페이스
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public interface SimpleTextWatcher extends TextWatcher {
  @Override
  default void beforeTextChanged(CharSequence s, int start, int count, int after) {
    // no-op
  }

  @Override
  default void onTextChanged(CharSequence s, int start, int before, int count) {
    // no-op
  }

  @Override
  default void afterTextChanged(Editable s) {
    // no-op
  }
}
