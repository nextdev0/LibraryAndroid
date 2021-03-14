package com.nextstory.util;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import io.reactivex.rxjava3.core.Single;

/**
 * 액티비티 결과 처리 유틸 class
 *
 * @author troy
 * @version 1.0.1
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public final class RxActivityResult {
    private static final Handler MAIN_THREAD_HANDLER = new Handler(Looper.getMainLooper());
    private final WeakReference<FragmentActivity> activity;
    private Intent intent;
    private Listener listener;
    private int resultCode;
    private Intent data;

    public RxActivityResult(@NonNull Fragment fragment) {
        this(Objects.requireNonNull(fragment).requireActivity());
    }

    public RxActivityResult(@NonNull FragmentActivity activity) {
        Objects.requireNonNull(activity);
        this.activity = new WeakReference<>(activity);
    }

    public RxActivityResult setIntent(@NonNull Intent intent) {
        this.intent = Objects.requireNonNull(intent);
        return this;
    }

    public RxActivityResult setListener(@Nullable Listener listener) {
        this.listener = listener;
        return this;
    }

    public Single<RxActivityResult> asSingle() {
        return Single.create(e -> {
            CountDownLatch lock = new CountDownLatch(1);
            listener = (resultCode, data) -> {
                this.resultCode = resultCode;
                this.data = data;
                lock.countDown();
            };
            MAIN_THREAD_HANDLER.post(() -> {
                if (activity.get() != null) {
                    ActivityResultFragment fragment = new ActivityResultFragment();
                    fragment.setIntent(intent);
                    fragment.setListener(listener);
                    activity.get()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .add(fragment, "activity_result_fragment")
                            .commitNowAllowingStateLoss();
                }
            });
            lock.await();
            e.onSuccess(this);
        });
    }

    public int getResultCode() {
        return resultCode;
    }

    public Intent getData() {
        return data;
    }

    public interface Listener {
        void onActivityResult(int resultCode, Intent data);
    }

    /**
     * 내부 프래그먼트 class
     */
    public static class ActivityResultFragment extends Fragment {
        private static final int REQUEST_CODE = 1000;

        @Nullable
        private Listener listener;
        private Intent intent;

        public ActivityResultFragment() {
            setRetainInstance(true);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            startActivityForResult(Objects.requireNonNull(intent), REQUEST_CODE);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CODE) {
                if (listener != null) {
                    listener.onActivityResult(resultCode, data);
                }
                requireFragmentManager()
                        .beginTransaction()
                        .remove(this)
                        .commitAllowingStateLoss();
            }
        }

        @Override
        public void onDestroyView() {
            listener = null;
            intent = null;
            super.onDestroyView();
        }

        public void setListener(@Nullable Listener listener) {
            if (listener != null) {
                this.listener = listener;
            }
        }

        public void setIntent(Intent intent) {
            this.intent = intent;
        }
    }
}
