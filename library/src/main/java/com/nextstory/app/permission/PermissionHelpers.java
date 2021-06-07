package com.nextstory.app.permission;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ComponentActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 권한 관련 도우미 클래스
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class PermissionHelpers {
    private static final int REQUEST_PERMISSIONS = 10011;
    private final ComponentActivity activity;
    private SharedPreferences permissionSharedPreferences = null;
    private PermissionListener permissionResult = null;

    public PermissionHelpers(Fragment fragment) {
        this(fragment.requireActivity());
    }

    @SuppressLint("RestrictedApi")
    public PermissionHelpers(ComponentActivity activity) {
        this.activity = activity;
        activity.getLifecycle().addObserver(new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            void onCreate() {
                permissionSharedPreferences = requireContext()
                        .getSharedPreferences("permissions", Context.MODE_PRIVATE);
                activity.getLifecycle().removeObserver(this);
            }
        });
    }

    private Context requireContext() {
        return Objects.requireNonNull(activity);
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            List<String> denied = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    denied.add(permissions[i]);
                }
            }
            if (permissionResult != null) {
                permissionResult.onPermissionResult(denied);
                permissionResult = null;
            }
        }
    }

    /**
     * 권한 요청 근거 표시 유무 반환
     *
     * @param permissions 필요 권한
     * @return 표시 유무
     */
    public boolean isNeedShowRequestPermissionRationale(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StringBuilder key = new StringBuilder();
            key.append("runtime_permissions::");
            key.append(getClass().getSimpleName());
            key.append(":");
            for (int i = 0; i < permissions.length; i++) {
                key.append(permissions[i]);
                if (i < permissions.length - 1) {
                    key.append("_");
                }
            }
            boolean result = permissionSharedPreferences.getBoolean(key.toString(), true);
            permissionSharedPreferences.edit().putBoolean(key.toString(), false).apply();
            return result;
        }
        return false;
    }

    /**
     * 권한 허용 유무 반환
     *
     * @param permissions 권한
     * @return 허용 유무
     */
    public boolean isPermissionGranted(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean result = true;
            for (String permission : permissions) {
                result &= ActivityCompat.checkSelfPermission(
                        requireContext(), permission) == PackageManager.PERMISSION_GRANTED;
            }
            return result;
        }
        return true;
    }

    /**
     * 권한 요청
     *
     * @param permissionResult 요청 결과 리스너, 거부된 권한 목록 반환
     * @param permissions      요청 권한
     */
    public void requestPermissions(@Nullable PermissionListener permissionResult,
                                   @NonNull String... permissions) {
        requestPermissions(permissions, permissionResult);
    }

    /**
     * 권한 요청
     *
     * @param permissions      요청 권한
     * @param permissionResult 요청 결과 리스너, 거부된 권한 목록 반환
     */
    public void requestPermissions(@NonNull String[] permissions,
                                   @Nullable PermissionListener permissionResult) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.permissionResult = permissionResult;
            activity.requestPermissions(permissions, REQUEST_PERMISSIONS);
            return;
        }
        if (permissionResult != null) {
            permissionResult.onPermissionResult(new ArrayList<>());
        }
    }
}
