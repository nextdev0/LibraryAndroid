package com.nextstory.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * 내부 처리용 {@link Application.ActivityLifecycleCallbacks} 간소화 인터페이스
 *
 * @author troy
 * @since 1.0
 */
public interface SimpleActivityCallback extends Application.ActivityLifecycleCallbacks {
    @Override
    default void onActivityCreated(Activity activity,
                                   Bundle savedInstanceState) {
        // no-op
    }

    @Override
    default void onActivityStarted(Activity activity) {
        // no-op
    }

    @Override
    default void onActivityResumed(Activity activity) {
        // no-op
    }

    @Override
    default void onActivityPaused(Activity activity) {
        // no-op
    }

    @Override
    default void onActivityStopped(Activity activity) {
        // no-op
    }

    @Override
    default void onActivitySaveInstanceState(Activity activity,
                                             Bundle outState) {
        // no-op
    }

    @Override
    default void onActivityDestroyed(Activity activity) {
        // no-op
    }
}
