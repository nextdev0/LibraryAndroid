package com.nextstory.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.appcompat.app.AppCompatDelegate;

import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 리소스 컨트롤러
 * <p>
 * 설정에 따라 리소스를 불러오는 방법을 제어함.
 * <p>
 * 변경가능한 설정
 * - 앱 테마 : 라이트/다크 모드 설정
 * - 로케일 : 지역화 설정
 *
 * @author troy
 * @since 2.0
 */
@SuppressWarnings("UnusedDeclaration")
public final class ResourcesController {
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_SYSTEM = 2;

    private static WeakReference<LocalizationActivityDelegate> localizationActivityDelegate;
    private static List<Locale> supportedLocales = null;
    private final Context context;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public ResourcesController(Context context) {
        this.context = context;
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    void initializeApplication() {
        ((Application) context).registerActivityLifecycleCallbacks(
                new Application.ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityCreated(@NonNull Activity activity,
                                                  @Nullable Bundle savedInstanceState) {
                        // no-op
                    }

                    @Override
                    public void onActivityStarted(@NonNull Activity activity) {
                        // no-op
                    }

                    @Override
                    public void onActivityResumed(@NonNull Activity activity) {
                        if (activity instanceof BaseActivity) {
                            localizationActivityDelegate =
                                    new WeakReference<>(((AbstractBaseActivity) activity)
                                            .getLocalizationActivityDelegate());
                        }
                    }

                    @Override
                    public void onActivityPaused(@NonNull Activity activity) {
                        // no-op
                    }

                    @Override
                    public void onActivityStopped(@NonNull Activity activity) {
                        // no-op
                    }

                    @Override
                    public void onActivitySaveInstanceState(@NonNull Activity activity,
                                                            @NonNull Bundle outState) {
                        // no-op
                    }

                    @Override
                    public void onActivityDestroyed(@NonNull Activity activity) {
                        // no-op
                    }
                });
    }

    /**
     * 현재 적용되어있는 테마 타입 반환
     *
     * @return 테마 타입
     * @see ThemeType
     */
    @SuppressWarnings("deprecation")
    @ThemeType
    public int getAppTheme() {
        switch (AppCompatDelegate.getDefaultNightMode()) {
            case AppCompatDelegate.MODE_NIGHT_AUTO_TIME:
            case AppCompatDelegate.MODE_NIGHT_NO:
                return THEME_LIGHT;
            case AppCompatDelegate.MODE_NIGHT_YES:
                return THEME_DARK;
            default:
            case AppCompatDelegate.MODE_NIGHT_UNSPECIFIED:
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
            case AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY:
                return THEME_SYSTEM;
        }
    }

    /**
     * 테마 설정
     *
     * @param themeType 테마 타입
     * @return 체이닝을 위한 인스턴스 반환
     * @see ThemeType
     */
    @SuppressWarnings("UnusedReturnValue")
    public ResourcesController applyAppTheme(@ThemeType int themeType) {
        switch (themeType) {
            default:
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_SYSTEM:
                AppCompatDelegate.setDefaultNightMode(
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                                ? AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                                : AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                break;
        }
        return this;
    }

    /**
     * 지원되는 로케일 목록 지정
     *
     * @param locales 로케일 목록
     * @return 체이닝을 위한 인스턴스 반환
     */
    public ResourcesController registerSupportedLocales(Locale... locales) {
        return registerSupportedLocales(Arrays.asList(locales));
    }

    /**
     * 지원되는 로케일 목록 지정
     *
     * @param locales 로케일 목록
     * @return 체이닝을 위한 인스턴스 반환
     */
    public ResourcesController registerSupportedLocales(List<Locale> locales) {
        supportedLocales = locales;
        return this;
    }

    /**
     * @return 로케일
     */
    public Locale getLocale() {
        if (localizationActivityDelegate == null || localizationActivityDelegate.get() == null) {
            return Locale.getDefault();
        }
        return localizationActivityDelegate.get().getLanguage(context);
    }

    /**
     * 로케일 적용
     *
     * @param locale 로케일
     * @return 체이닝을 위한 인스턴스 반환
     */
    @SuppressWarnings("UnusedReturnValue")
    public ResourcesController applyLocale(Locale locale) {
        String language = locale.getLanguage();

        // 미지원 로케일 적용
        if (supportedLocales != null) {
            List<Locale> sameLanguages = new ArrayList<>();
            boolean isSupportedLocale = false;
            for (Locale supported : supportedLocales) {
                if (supported.getLanguage().equals(language)) {
                    sameLanguages.add(supported);
                }
                if (supported.getCountry().equals(language)) {
                    isSupportedLocale = true;
                    break;
                }
            }
            if (!isSupportedLocale && sameLanguages.size() > 0) {
                locale = sameLanguages.get(0);
            }
        }

        if (localizationActivityDelegate != null && localizationActivityDelegate.get() != null) {
            localizationActivityDelegate.get().setLanguage(context, locale);
        }

        return this;
    }

    /**
     * 테마 타입 어노테이션
     */
    @IntDef({
            THEME_LIGHT,
            THEME_DARK,
            THEME_SYSTEM
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ThemeType {
    }
}
