package com.nextstory.util;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 디바운스 클릭 리스너
 * 연속 클릭 방지를 위해 사용
 *
 * @author troy
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class DebounceOnClickListener implements View.OnClickListener {
    private static final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private static final Map<View, AtomicBoolean> bouncings = new WeakHashMap<>();

    @BindingAdapter("android:onClick")
    public static void setOnClickListener(@NonNull View v, @Nullable View.OnClickListener l) {
        if (l != null) {
            v.setOnClickListener(new DebounceOnClickListener() {
                @Override
                public void onDebounceClick(View v) {
                    l.onClick(v);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (!bouncings.containsKey(v)) {
            bouncings.put(v, new AtomicBoolean(false));
            v.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    // no-op
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    bouncings.remove(v);
                    v.removeOnAttachStateChangeListener(this);
                }
            });
        }
        AtomicBoolean debounce = Objects.requireNonNull(bouncings.get(v));
        if (!debounce.getAndSet(true)) {
            onDebounceClick(v);
        }
        mainThreadHandler.postDelayed(() -> {
            if (!bouncings.containsKey(v)) {
                bouncings.put(v, new AtomicBoolean(false));
            }
            AtomicBoolean debounce2 = Objects.requireNonNull(bouncings.get(v));
            debounce2.set(false);
        }, 1000L);
    }

    abstract public void onDebounceClick(View v);
}
