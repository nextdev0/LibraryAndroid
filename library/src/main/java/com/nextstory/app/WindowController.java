package com.nextstory.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.ComponentActivity;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.nextstory.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * 윈도우 컨트롤러
 * <p>
 * 시스템 UI와 앱 컨텐츠 표시 설정을 바꿀 수 있음
 *
 * @author troy
 * @since 2.0
 */
public final class WindowController implements ViewTreeObserver.OnGlobalLayoutListener {
    /**
     * 일반 윈도우
     */
    public static final int TYPE_NORMAL = 0;
    /**
     * 상태바 영역에 컨텐츠가 겹치도록 설정
     * 그리고 제스처바가 있을경우 겹치도록 설정
     */
    public static final int TYPE_OVERLAY_SYSTEM_BARS = 1;
    /**
     * 전체화면 설정
     */
    public static final int TYPE_FULLSCREEN = 2;

    /**
     * 상태바 반투명 색상
     */
    private static final int TRANSLUCENT_STATUS_BAR_COLOR = 0x3f000000;

    /**
     * 윈도우별 이전 설정
     */
    private static final Map<Window, PriorSettings> SETTINGS = new WeakHashMap<>();

    private final Set<Runnable> postRunnables = new HashSet<>();
    private Window window = null;
    private ViewGroup contentView = null;
    private InputMethodManager inputMethodManager = null;
    private SharedPreferences recentNavBarSharedPreferences = null;
    private int actualDeviceHeight = 0;

    public WindowController(Fragment fragment) {
        initialize(fragment, () -> {
            window = fragment.requireActivity().getWindow();
            contentView = fragment.requireActivity().findViewById(android.R.id.content);
        });
    }

    public WindowController(ComponentActivity activity) {
        initialize(activity, () -> {
            window = activity.getWindow();
            contentView = activity.findViewById(android.R.id.content);

            recentNavBarSharedPreferences = activity.getSharedPreferences(
                    "recent_nav_bar", Context.MODE_PRIVATE);

            window.getDecorView()
                    .getViewTreeObserver()
                    .addOnGlobalLayoutListener(this);
        });
    }

    private int getNavBarInteractionModeConfig() {
        if (contentView == null) {
            return 0;
        }
        int resourceId = contentView.getResources().getIdentifier(
                "config_navBarInteractionMode", "integer", "android");
        if (resourceId > 0) {
            return contentView.getResources().getInteger(resourceId);
        }
        return 0;
    }

    private void initialize(LifecycleOwner lifecycleOwner, Runnable createdRunnable) {
        lifecycleOwner.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source,
                                       @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_RESUME) {
                    actualDeviceHeight = 0;

                    if (recentNavBarSharedPreferences != null) {
                        String key = "mode_" + lifecycleOwner.hashCode();
                        if (!recentNavBarSharedPreferences.contains(key)) {
                            int currentNavBarMode = getNavBarInteractionModeConfig();
                            recentNavBarSharedPreferences.edit()
                                    .putInt(key, currentNavBarMode)
                                    .apply();
                        } else {
                            int lastNavBarMode = recentNavBarSharedPreferences.getInt(key, 0);
                            int currentNavBarMode = getNavBarInteractionModeConfig();
                            if (lastNavBarMode != currentNavBarMode) {
                                recentNavBarSharedPreferences.edit()
                                        .remove(key)
                                        .apply();
                                if (lifecycleOwner instanceof Activity) {
                                    ((Activity) lifecycleOwner).recreate();
                                    return;
                                }
                            }
                        }
                    }
                }
                if (event == Lifecycle.Event.ON_CREATE || event == Lifecycle.Event.ON_START) {
                    createdRunnable.run();
                    for (Runnable runnable : postRunnables) {
                        runnable.run();
                    }
                    postRunnables.clear();
                    if (inputMethodManager == null) {
                        inputMethodManager = ContextCompat.getSystemService(
                                contentView.getContext(), InputMethodManager.class);
                    }
                    return;
                }
                if (event == Lifecycle.Event.ON_DESTROY) {
                    if (inputMethodManager != null && window != null) {
                        View view = window.getCurrentFocus();
                        if (view != null) {
                            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            view.clearFocus();
                        }
                    }
                    if (window != null) {
                        SETTINGS.remove(window);
                    }
                    window = null;
                    contentView = null;
                    inputMethodManager = null;
                    recentNavBarSharedPreferences = null;
                    lifecycleOwner.getLifecycle().removeObserver(this);
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onGlobalLayout() {
        if (window != null && contentView != null) {
            if (!window.getDecorView().getViewTreeObserver().isAlive()) {
                window.getDecorView()
                        .getViewTreeObserver()
                        .removeOnGlobalLayoutListener(this);
                return;
            }

            // 전체화면일 경우 터치시마다 테마를 재적용함.
            int windowType = Objects.requireNonNull(getSettings()).getWindowType();
            if (windowType == WindowController.TYPE_FULLSCREEN) {
                applyWindowType(WindowController.TYPE_FULLSCREEN);
                return;
            }

            // 키보드 여백 맞추기
            int mode = window.getAttributes().softInputMode;
            if ((mode & WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) != 0) {
                Rect r = new Rect();
                window.getDecorView().getWindowVisibleDisplayFrame(r);
                if (r.height() > actualDeviceHeight) {
                    actualDeviceHeight = r.height();
                }
                int diff = actualDeviceHeight - r.bottom;
                if (diff > 0) {
                    contentView.setPadding(0, 0, 0, diff + r.top);
                } else {
                    if (contentView.getPaddingBottom() != 0) {
                        contentView.setPadding(0, 0, 0, 0);
                    }
                }
            }
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    PriorSettings getSettings() {
        if (window == null) {
            return null;
        }
        if (!SETTINGS.containsKey(window)) {
            SETTINGS.put(window, new PriorSettings());
        }
        return Objects.requireNonNull(SETTINGS.get(window));
    }

    /**
     * 윈도우 테마 설정
     *
     * @param type 타입
     * @return 체이닝을 위한 인스턴스 반환
     */
    @SuppressWarnings("deprecation")
    public WindowController applyWindowType(@WindowType int type) {
        actualDeviceHeight = 0;
        post(() -> {
            if (window != null && contentView != null) {
                View decorView = window.getDecorView();
                int flags = decorView.getSystemUiVisibility();

                // 이전 설정 지우기
                switch (Objects.requireNonNull(getSettings()).getWindowType()) {
                    default:
                    case TYPE_NORMAL:
                        break;
                    case TYPE_OVERLAY_SYSTEM_BARS: {
                        contentView.removeView(
                                contentView.findViewById(R.id.translucent_status_bar));
                        window.setStatusBarColor(getSettings().getStatusBarColor());
                        flags &= ~View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                        flags &= ~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                        flags &= ~WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;

                        // 제스처바 처리
                        if (getSettings().hasGestureBar()) {
                            window.setNavigationBarColor(
                                    getSettings().getNavigationBarColor());
                            flags &= ~WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
                        }
                        break;
                    }
                    case TYPE_FULLSCREEN: {
                        flags &= ~View.SYSTEM_UI_FLAG_FULLSCREEN;
                        flags &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                        flags &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                        flags &= ~View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                        break;
                    }
                }

                // 설정 적용
                switch (type) {
                    default:
                    case TYPE_NORMAL:
                        break;
                    case TYPE_OVERLAY_SYSTEM_BARS: {
                        getSettings().setStatusBarColor(window.getStatusBarColor());
                        window.setStatusBarColor(Color.TRANSPARENT);
                        flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                        flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                        flags |= WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;

                        // 제스처바 처리
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            int navBarMode = getNavBarInteractionModeConfig();
                            if (navBarMode == 2) {
                                getSettings().setNavigationBarColor(
                                        window.getNavigationBarColor());
                                window.setNavigationBarColor(Color.TRANSPARENT);
                                flags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
                                getSettings().setHasGestureBar(true);
                            } else {
                                getSettings().setHasGestureBar(false);
                            }
                        }

                        break;
                    }
                    case TYPE_FULLSCREEN: {
                        flags |= View.SYSTEM_UI_FLAG_FULLSCREEN;
                        flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                        flags |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                        flags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                        break;
                    }
                }

                decorView.setSystemUiVisibility(flags);

                getSettings().setWindowType(type);

                // L 버전 반투명 상태바 표시
                if (type == TYPE_OVERLAY_SYSTEM_BARS) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        return;
                    }
                    applyTranslucentStatusBar(true);
                }
            }
        });
        return this;
    }

    /**
     * 반투명 상태바 적용
     *
     * @param isEnabled 활성화 유무
     * @return 체이닝을 위한 인스턴스 반환
     */
    @SuppressWarnings("UnusedReturnValue")
    public WindowController applyTranslucentStatusBar(boolean isEnabled) {
        post(() -> {
            if (window != null && contentView != null) {
                contentView.postDelayed(() -> {
                    if (window != null && contentView != null) {
                        if (!isEnabled) {
                            contentView.removeView(
                                    contentView.findViewById(R.id.translucent_status_bar));
                            return;
                        }
                        WindowInsetsCompat windowInsets =
                                ViewCompat.getRootWindowInsets(contentView);
                        int statusBarHeight = Objects.requireNonNull(windowInsets)
                                .getInsets(WindowInsetsCompat.Type.statusBars()).top;
                        if (contentView.findViewById(R.id.translucent_status_bar) == null) {
                            View statusBarView = new View(contentView.getContext());
                            statusBarView.setId(R.id.translucent_status_bar);
                            statusBarView.setBackgroundColor(TRANSLUCENT_STATUS_BAR_COLOR);
                            contentView.addView(statusBarView,
                                    new ViewGroup.LayoutParams(-1, statusBarHeight));
                        }
                    }
                }, 100);
            }
        });
        return this;
    }

    /**
     * 상태바 아이콘 어두운 색상 설정
     *
     * @param isEnabled 활성화 유무
     * @return 체이닝을 위한 인스턴스 반환
     */
    @SuppressWarnings("UnusedReturnValue")
    public WindowController applyStatusBarDarkIcon(boolean isEnabled) {
        post(() -> {
            if (window != null) {
                View decorView = window.getDecorView();
                WindowInsetsControllerCompat windowInsetsController
                        = new WindowInsetsControllerCompat(window, decorView);
                windowInsetsController.setAppearanceLightStatusBars(isEnabled);
            }
        });
        return this;
    }

    /**
     * 윈도우가 생성되기까지 작업 예약
     *
     * @param runnable 실행할 작업
     */
    private void post(Runnable runnable) {
        if (window == null) {
            postRunnables.add(runnable);
            return;
        }
        runnable.run();
    }

    /**
     * 테마 타입 어노테이션
     */
    @IntDef({
            TYPE_NORMAL,
            TYPE_OVERLAY_SYSTEM_BARS,
            TYPE_FULLSCREEN
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface WindowType {
    }

    /**
     * 이전 설정 저장용 클래스
     */
    static class PriorSettings {
        private int windowType = TYPE_NORMAL;
        private int statusBarColor = 0;
        private int navigationBarColor = 0;
        private boolean hasGestureBar = false;

        public int getWindowType() {
            return windowType;
        }

        public void setWindowType(int windowType) {
            this.windowType = windowType;
        }

        public int getStatusBarColor() {
            return statusBarColor;
        }

        public void setStatusBarColor(int statusBarColor) {
            this.statusBarColor = statusBarColor;
        }

        public int getNavigationBarColor() {
            return navigationBarColor;
        }

        public void setNavigationBarColor(int navigationBarColor) {
            this.navigationBarColor = navigationBarColor;
        }

        public boolean hasGestureBar() {
            return hasGestureBar;
        }

        public void setHasGestureBar(boolean hasGestureBar) {
            this.hasGestureBar = hasGestureBar;
        }
    }
}
