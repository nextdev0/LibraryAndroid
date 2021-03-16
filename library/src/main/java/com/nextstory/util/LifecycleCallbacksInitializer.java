package com.nextstory.util;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.startup.Initializer;

import java.util.Collections;
import java.util.List;

/**
 * {@link LifecycleCallbacks} 초기화 클래스
 *
 * @author troy
 * @version 1.0
 * @since 1.1
 */
public final class LifecycleCallbacksInitializer implements Initializer<Object> {
    private final LifecycleCallbacks lifecycleCallbacks = new LifecycleCallbacks();

    @NonNull
    @Override
    public Object create(@NonNull Context context) {
        LifecycleCallbacks.sApplication = (Application) context.getApplicationContext();
        LifecycleCallbacks.sApplication.registerActivityLifecycleCallbacks(lifecycleCallbacks);
        return new Object();
    }

    @NonNull
    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        return Collections.emptyList();
    }
}
