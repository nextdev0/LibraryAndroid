package com.nextstory.libgdx.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.appcompat.app.AppCompatActivity;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.nextstory.libgdx.Libgdx;
import com.nextstory.libgdx.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * libgdx 컨테이너 액티비티
 *
 * @author troy
 * @since 2.1
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class LibgdxActivity
        extends AppCompatActivity implements AndroidFragmentApplication.Callbacks {
    private static final String EXTRA_CLASS = "extra_class";
    private static final int PERMISSION_REQUEST_CODE = 1000;

    private Libgdx libgdx = null;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public static Intent createIntent(@NonNull Context context,
                                      Class<? extends Libgdx> libGdxClass) {
        return new Intent(context, LibgdxActivity.class)
                .putExtra(EXTRA_CLASS, libGdxClass);
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode,
                                                 @NonNull String[] permissions,
                                                 @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            // 요청 결과 체크 및 거부 항목 수집
            List<String> deniedPermissions = new ArrayList<>();
            AtomicBoolean isGranted = new AtomicBoolean(true);
            for (int i = 0; i < permissions.length; i++) {
                boolean isDenied = grantResults[i] == PackageManager.PERMISSION_DENIED;
                if (isGranted.compareAndSet(isDenied, false)) {
                    String permission = permissions[i];
                    deniedPermissions.add(permission);
                }
            }

            // 모든 요청을 승인하면 기능 동작을 시킴
            // 하나라도 거부한 것이 존재하면 실패 콜백 처리함.
            if (isGranted.get()) {
                onPermissionGranted();
            } else {
                if (libgdx != null) {
                    libgdx.onRequirePermissionDenied(deniedPermissions);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_libgdx);

        try {
            Class<? extends Libgdx> arGameClass =
                    (Class<? extends Libgdx>) getIntent().getSerializableExtra(EXTRA_CLASS);

            libgdx = Objects.requireNonNull(arGameClass.newInstance());
            libgdx.attachActivity(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                List<String> requirePermissions = libgdx.getRequirePermissions();
                String[] realRequirePermissions = new String[requirePermissions.size()];
                if (requirePermissions.size() > 0) {
                    for (int i = 1; i < realRequirePermissions.length; i++) {
                        realRequirePermissions[i] = requirePermissions.get(i);
                    }
                    requestPermissions(realRequirePermissions, PERMISSION_REQUEST_CODE);
                } else {
                    onPermissionGranted();
                }
            } else {
                onPermissionGranted();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        libgdx = null;
        super.onDestroy();
    }

    @Override
    public void exit() {
        // no-op
    }

    public void onPermissionGranted() {
        LibgdxFragment libgdxFragment = new LibgdxFragment();
        libgdxFragment.setLibgdx(libgdx);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.libgdx_fragment_container, libgdxFragment)
                .commit();
    }
}
