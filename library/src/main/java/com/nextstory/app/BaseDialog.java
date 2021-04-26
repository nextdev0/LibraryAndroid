package com.nextstory.app;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.core.view.ViewCompat;
import androidx.databinding.ViewDataBinding;

import com.nextstory.R;
import com.nextstory.app.locale.LocaleManager;
import com.nextstory.app.locale.LocaleManagerImpl;
import com.nextstory.app.theme.ThemeHelpers;
import com.nextstory.app.theme.ThemeType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 기본 다이얼로그
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings({"UnusedDeclaration", "deprecation"})
public abstract class BaseDialog<B extends ViewDataBinding> extends Dialog {
    private final ThemeHelpers themeHelpers = new ThemeHelpers();
    private final LocaleManager localeManager =
            new LocaleManagerImpl(() -> getContext().getApplicationContext());

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    B binding = null;

    public BaseDialog(@NonNull Context context) {
        this(context, R.style.Theme_Dialog_Base);
    }

    public BaseDialog(@NonNull Context context, @StyleRes int themeRes) {
        super(context, themeRes);
    }

    @SuppressWarnings("unchecked")
    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (binding == null && savedInstanceState == null) {
            ParameterizedType parameterizedType =
                    (ParameterizedType) getClass().getGenericSuperclass();
            if (parameterizedType != null) {
                try {
                    Method method = ((Class<?>) parameterizedType.getActualTypeArguments()[0])
                            .getMethod("inflate", LayoutInflater.class);
                    binding = (B) Objects.requireNonNull(method.invoke(null, getLayoutInflater()));
                } catch (NoSuchMethodException
                        | InvocationTargetException
                        | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        applyTransparentTheme();
        applyLightStatusBar(false);
        if (binding != null) {
            super.setContentView(binding.getRoot());
        }
        Window window = getWindow();
        if (window != null) {
            window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
        }
        ViewGroup contentView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(contentView, (v, insets) -> {
            if (binding != null) {
                binding.getRoot().setPadding(
                        insets.getSystemWindowInsetLeft(),
                        0,
                        insets.getSystemWindowInsetRight(),
                        insets.getSystemWindowInsetBottom());
            }
            return insets.consumeSystemWindowInsets();
        });
    }

    @CallSuper
    @Override
    protected void onStart() {
        Window window = getWindow();
        if (window != null) {
            window.setDimAmount(getDimAmount());
        }
        super.onStart();
    }

    @Deprecated
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @Override
    public final void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Deprecated
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @Override
    public final void setContentView(@NonNull View view) {
        super.setContentView(view);
    }

    @Deprecated
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @Override
    public final void setContentView(@NonNull View view, @Nullable ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
    }

    @Deprecated
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @Override
    public final void addContentView(@NonNull View view, @Nullable ViewGroup.LayoutParams params) {
        super.addContentView(view, params);
    }

    @CallSuper
    @Override
    public void dismiss() {
        binding = null;
        super.dismiss();
    }

    /**
     * @return 창 밖 배경 어두운 정도
     */
    protected float getDimAmount() {
        return 0.6f;
    }

    /**
     * 아무동작 없음 (데이터바인딩시 창 클릭시 닫기지 않도록 하기 위함)
     */
    public final void nothing() {
        // no-op
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
        Window window = getWindow();
        ViewGroup contentView = findViewById(android.R.id.content);
        if (window != null && contentView != null) {
            window.setStatusBarColor(Color.TRANSPARENT);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
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
        Window window = getWindow();
        ViewGroup contentView = findViewById(android.R.id.content);
        if (window != null && contentView != null) {
            View statusBar = contentView.findViewById(R.id.translucent_status_bar);
            if (statusBar != null) {
                contentView.removeView(statusBar);
            }
            window.setStatusBarColor(Color.TRANSPARENT);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            int flags = window.getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            window.getDecorView().setSystemUiVisibility(flags);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (contentView.findViewById(R.id.translucent_status_bar) == null) {
                    contentView.post(() -> {
                        View statusBarView = new View(contentView.getContext());
                        statusBarView.setId(R.id.translucent_status_bar);
                        statusBarView.setBackgroundColor(0x3f000000);
                        contentView.addView(statusBarView,
                                new ViewGroup.LayoutParams(-1, getStatusBarHeight(window)));
                    });
                }
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
        Window window = getWindow();
        ViewGroup contentView = findViewById(android.R.id.content);
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
     * 토스트 표시
     *
     * @param res 문자열 리소스
     */
    public void showToast(@StringRes int res) {
        Toast.makeText(getContext(), res, Toast.LENGTH_SHORT).show();
    }

    /**
     * 토스트 표시
     *
     * @param message 문자열
     */
    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * @return 바인딩 인스턴스
     */
    @CallSuper
    protected B getBinding() {
        return binding;
    }
}
