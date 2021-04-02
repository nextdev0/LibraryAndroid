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
 * (연속 클릭 방지를 위해 사용)
 *
 * @author troy
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class DebounceOnClickListener implements View.OnClickListener {
    private static final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private static final Map<View, AtomicBoolean> bouncings = new WeakHashMap<>();

    /**
     * 클릭 리스너 바인딩 어댑터
     *
     * @param v 뷰
     * @param l 리스너
     */
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

            // 바운딩 체크를 위한 뷰 참조를 누수 방지를 위해 제거함
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

    /**
     * 실제 클릭 처리 메소드
     *
     * @param v 뷰
     */
    abstract public void onDebounceClick(View v);
}
