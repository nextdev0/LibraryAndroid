package com.nextstory.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.rxjava3.core.Single;

/**
 * Rx 기반 권한 요청 유틸
 *
 * @author troy
 * @since 1.3
 */
@SuppressWarnings("UnusedDeclaration")
public final class RxPermission {
    private static final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private final ComponentActivity activity;
    private final LifecycleOwner lifecycleOwner;
    private final List<String> requestPermissions = new ArrayList<>();
    private final SharedPreferences permissionSharedPreferences;

    public RxPermission(@NonNull ComponentActivity activity) {
        this.activity = activity;
        this.lifecycleOwner = activity;
        this.permissionSharedPreferences = activity
                .getSharedPreferences("permissions", Context.MODE_PRIVATE);
    }

    public RxPermission(@NonNull Fragment fragment) {
        this.activity = fragment.requireActivity();
        this.lifecycleOwner = fragment;
        this.permissionSharedPreferences = fragment.requireContext()
                .getSharedPreferences("permissions", Context.MODE_PRIVATE);
    }

    /**
     * 요청할 권한 추가
     *
     * @param permissions 권한
     * @return 빌더 인스턴스
     */
    public RxPermission add(@NonNull String... permissions) {
        requestPermissions.addAll(Arrays.asList(permissions));
        return this;
    }

    /**
     * 요청
     *
     * @return Rx 인스턴스
     */
    public Single<PermissionResult> request() {
        PermissionResult result = new PermissionResult();
        return Single.create(e -> {
            if (requestPermissions.size() == 0) {
                e.onError(new IllegalStateException("request permission must be > 0."));
                return;
            }
            boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();
            CountDownLatch lock = new CountDownLatch(1);
            mainThreadHandler.post(() -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String[] permissions = new String[requestPermissions.size()];
                    for (int i = 0; i < requestPermissions.size(); i++) {
                        permissions[i] = requestPermissions.get(i);
                    }
                    AtomicBoolean firstResumed = new AtomicBoolean(false);
                    lifecycleOwner.getLifecycle().addObserver(new LifecycleObserver() {
                        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
                        void onLifecycleResume() {
                            if (firstResumed.getAndSet(true)) {
                                checkPermissions(result);
                                if (isMainThread) {
                                    e.onSuccess(result);
                                } else {
                                    lock.countDown();
                                }
                                lifecycleOwner.getLifecycle().removeObserver(this);
                            }
                        }
                    });
                    activity.requestPermissions(permissions, 0x1000);
                } else {
                    result.getGrantedPermissions().addAll(requestPermissions);
                    lock.countDown();
                }
            });
            if (!isMainThread) {
                lock.await();
                e.onSuccess(result);
            }
        });
    }

    /**
     * 권한 요청 결과 검사
     *
     * @param result 결과를 지정할 모델
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private void checkPermissions(PermissionResult result) {
        for (String permission : requestPermissions) {
            int permissionResult = ((Context) activity).checkSelfPermission(permission);
            if (permissionResult == PackageManager.PERMISSION_GRANTED) {
                result.getGrantedPermissions().add(permission);
            } else {
                result.getDeniedPermissions().add(permission);
            }
        }
    }

    /**
     * 권한 요청 결과
     */
    public static final class PermissionResult {
        private final List<String> grantedPermissions = new ArrayList<>();
        private final List<String> deniedPermissions = new ArrayList<>();

        public List<String> getGrantedPermissions() {
            return grantedPermissions;
        }

        public List<String> getDeniedPermissions() {
            return deniedPermissions;
        }

        public boolean isPermissionAllGranted() {
            return deniedPermissions.size() == 0;
        }
    }
}
