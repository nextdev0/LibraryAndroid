package com.nextstory.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * {@link Application.ActivityLifecycleCallbacks} 간소화 인터페이스
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public interface SimpleActivityLifecycleCallbacks extends Application.ActivityLifecycleCallbacks {
  @Override
  default void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    // no-op
  }

  @Override
  default void onActivityStarted(@NonNull Activity activity) {
    // no-op
  }

  @Override
  default void onActivityResumed(@NonNull Activity activity) {
    // no-op
  }

  @Override
  default void onActivityPaused(@NonNull Activity activity) {
    // no-op
  }

  @Override
  default void onActivityStopped(@NonNull Activity activity) {
    // no-op
  }

  @Override
  default void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    // no-op
  }

  @Override
  default void onActivityDestroyed(@NonNull Activity activity) {
    // no-op
  }
}
