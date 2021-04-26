package com.nextstory.app;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.nextstory.R;
import com.nextstory.app.locale.LocaleManager;
import com.nextstory.app.locale.LocaleManagerImpl;
import com.nextstory.app.permission.PermissionHelpers;
import com.nextstory.app.permission.PermissionListener;
import com.nextstory.app.theme.ThemeHelpers;
import com.nextstory.app.theme.ThemeType;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 기본 프래그먼트
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractBaseFragment extends Fragment {
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private final OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            onBackPressed();
            setEnabled(true);
        }
    };
    private final ThemeHelpers themeHelpers = new ThemeHelpers();
    private final LocaleManager localeManager =
            new LocaleManagerImpl(() -> requireContext().getApplicationContext());
    private final PermissionHelpers permissionHelpers = new PermissionHelpers(this);

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelpers.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), backPressedCallback);
    }

    /**
     * 현재 적용된 테마 반환
     *
     * @return 테마
     * @see ThemeType
     */
    @ThemeType
    public int getApplicationTheme() {
        return themeHelpers.getCurrentTheme();
    }

    /**
     * 앱 테마 적용
     *
     * @param type 테마
     * @see ThemeType
     */
    public void applyApplicationTheme(@ThemeType int type) {
        themeHelpers.applyTheme(type);
    }

    /**
     * 지원되는 로케일 목록 지정
     *
     * @param locales 로케일 목록
     * @since 1.3
     */
    public void registerSupportedLocales(List<Locale> locales) {
        localeManager.registerSupportedLocales(locales);
    }

    /**
     * @return 현재 로케일
     */
    @NonNull
    public Locale getLocale() {
        return localeManager.getLocale();
    }

    /**
     * 로케일 적용
     *
     * @param locale 로케일
     */
    public void applyLocale(@NonNull Locale locale) {
        localeManager.applyLocale(Objects.requireNonNull(locale));
    }

    /**
     * 반투명 테마 적용
     */
    public void applyTranslucentTheme() {
        Window window = requireActivity().getWindow();
        ViewGroup contentView = requireActivity().findViewById(android.R.id.content);
        if (window != null && contentView != null) {
            window.setStatusBarColor(Color.TRANSPARENT);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            int flags = window.getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            window.getDecorView().setSystemUiVisibility(flags);
            contentView.post(() -> {
                if (contentView.findViewById(R.id.translucent_status_bar) == null) {
                    View statusBarView = new View(contentView.getContext());
                    statusBarView.setId(R.id.translucent_status_bar);
                    statusBarView.setBackgroundColor(0x3f000000);
                    contentView.addView(statusBarView,
                            new ViewGroup.LayoutParams(-1, getStatusBarHeight(window)));
                }
            });
        }
        applyLightStatusBar(false);
    }

    /**
     * 투명 테마 적용
     */
    public void applyTransparentTheme() {
        Window window = requireActivity().getWindow();
        ViewGroup contentView = requireActivity().findViewById(android.R.id.content);
        if (window != null && contentView != null) {
            View statusBar = contentView.findViewById(R.id.translucent_status_bar);
            if (statusBar != null) {
                contentView.removeView(statusBar);
            }
            window.setStatusBarColor(Color.TRANSPARENT);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            int flags = window.getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            window.getDecorView().setSystemUiVisibility(flags);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                contentView.post(() -> {
                    if (contentView.findViewById(R.id.translucent_status_bar) == null) {
                        View statusBarView = new View(contentView.getContext());
                        statusBarView.setId(R.id.translucent_status_bar);
                        statusBarView.setBackgroundColor(0x3f000000);
                        contentView.addView(statusBarView,
                                new ViewGroup.LayoutParams(-1, getStatusBarHeight(window)));
                    }
                });
            }
        }
    }

    /**
     * 상태바 밝음 유무 상태를 설정함
     *
     * @param enabled 활성화 유무
     */
    @SuppressWarnings("SameParameterValue")
    public void applyLightStatusBar(boolean enabled) {
        Window window = requireActivity().getWindow();
        ViewGroup contentView = requireActivity().findViewById(android.R.id.content);
        if (window != null && contentView != null) {
            View decorView = window.getDecorView();
            decorView.postDelayed(() -> {
                int flags = decorView.getSystemUiVisibility();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (enabled) {
                        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    } else {
                        flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                    }
                }
                decorView.setSystemUiVisibility(flags);
            }, 250L);
        }
    }

    /**
     * 전체화면 테마 적용
     */
    public void applyFullscreenTheme() {
        Window window = requireActivity().getWindow();
        if (window != null) {
            View decorView = window.getDecorView();
            decorView.postDelayed(() -> decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION), 250L);
        }
    }

    /**
     * 상태바 높이를 반환
     *
     * @param window 윈도우 인스턴스
     * @return 높이값, 윈도우가 {@code null}일 경우 {@code 0}을 반환
     */
    private int getStatusBarHeight(Window window) {
        if (window == null) {
            return 0;
        }
        Resources resources = window.getDecorView().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return (resourceId == 0) ? 0 : resources.getDimensionPixelSize(resourceId);
    }


    /**
     * 권한 요청 근거 표시 유무 반환
     *
     * @param permissions 필요 권한
     * @return 표시 유무
     */
    public boolean isNeedShowRequestPermissionRationale(String... permissions) {
        return permissionHelpers.isNeedShowRequestPermissionRationale(permissions);
    }

    /**
     * 권한 허용 유무 반환
     *
     * @param permissions 권한
     * @return 허용 유무
     */
    public boolean isPermissionGranted(String... permissions) {
        return permissionHelpers.isPermissionGranted(permissions);
    }

    /**
     * 권한 요청
     *
     * @param permissionResult 요청 결과 리스너, 거부된 권한 목록 반환
     * @param permissions      요청 권한
     */
    public void requestPermissions(@Nullable PermissionListener permissionResult,
                                   @NonNull String... permissions) {
        permissionHelpers.requestPermissions(permissionResult, permissions);
    }

    /**
     * 권한 요청
     *
     * @param permissions      요청 권한
     * @param permissionResult 요청 결과 리스너, 거부된 권한 목록 반환
     */
    public void requestPermissions(@NonNull String[] permissions,
                                   @Nullable PermissionListener permissionResult) {
        permissionHelpers.requestPermissions(permissions, permissionResult);
    }

    /**
     * 뒤로가기 동작
     */
    public void onBackPressed() {
        try {
            if (isVisible()) {
                backPressedCallback.setEnabled(false);
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        } catch (Exception e) {
            backPressedCallback.setEnabled(false);
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        }
    }

    /**
     * 토스트 표시
     *
     * @param res 문자열 리소스
     */
    public void showToast(@StringRes int res) {
        Toast.makeText(requireContext(), res, Toast.LENGTH_SHORT).show();
    }

    /**
     * 토스트 표시
     *
     * @param message 문자열
     */
    public void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 작업이 UI 스레드에 실행되도록 추가
     *
     * @param runnable 실행할 작업
     */
    public final void runOnUiThread(@NonNull Runnable runnable) {
        mainThreadHandler.post(runnable);
    }

    /**
     * @return 메인 스레드 핸들러
     */
    public final Handler getMainThreadHandler() {
        return mainThreadHandler;
    }
}