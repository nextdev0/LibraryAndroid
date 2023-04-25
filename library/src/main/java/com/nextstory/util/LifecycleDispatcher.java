package com.nextstory.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

@SuppressWarnings("UnusedDeclaration")
public final class LifecycleDispatcher {
    private static void invokeInternal(@NonNull LifecycleOwner lifecycleOwner,
                                       @NonNull Runnable runnable,
                                       @NonNull Lifecycle.Event event) {
        lifecycleOwner.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source,
                                       @NonNull Lifecycle.Event event2) {
                if (source == lifecycleOwner && event2 == event) {
                    lifecycleOwner.getLifecycle().removeObserver(this);
                    runnable.run();
                }
            }
        });
    }

    public static void invokeOnAny(@NonNull LifecycleOwner lifecycleOwner,
                                   @NonNull Runnable runnable) {
        invokeInternal(lifecycleOwner, runnable, Lifecycle.Event.ON_ANY);
    }

    public static void invokeOnCreate(@NonNull LifecycleOwner lifecycleOwner,
                                      @NonNull Runnable runnable) {
        invokeInternal(lifecycleOwner, runnable, Lifecycle.Event.ON_CREATE);
    }

    public static void invokeOnStart(@NonNull LifecycleOwner lifecycleOwner,
                                     @NonNull Runnable runnable) {
        invokeInternal(lifecycleOwner, runnable, Lifecycle.Event.ON_START);
    }

    public static void invokeOnResume(@NonNull LifecycleOwner lifecycleOwner,
                                      @NonNull Runnable runnable) {
        invokeInternal(lifecycleOwner, runnable, Lifecycle.Event.ON_RESUME);
    }

    public static void invokeOnPause(@NonNull LifecycleOwner lifecycleOwner,
                                     @NonNull Runnable runnable) {
        invokeInternal(lifecycleOwner, runnable, Lifecycle.Event.ON_PAUSE);
    }

    public static void invokeOnStop(@NonNull LifecycleOwner lifecycleOwner,
                                    @NonNull Runnable runnable) {
        invokeInternal(lifecycleOwner, runnable, Lifecycle.Event.ON_STOP);
    }

    public static void invokeOnDestroy(@NonNull LifecycleOwner lifecycleOwner,
                                       @NonNull Runnable runnable) {
        invokeInternal(lifecycleOwner, runnable, Lifecycle.Event.ON_DESTROY);
    }
}
