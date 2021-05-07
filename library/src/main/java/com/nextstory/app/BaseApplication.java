package com.nextstory.app;

import android.app.Application;
import android.content.res.Resources;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.nextstory.app.locale.LocaleManager;
import com.nextstory.app.locale.LocaleManagerImpl;
import com.nextstory.app.theme.ThemeHelpers;
import com.nextstory.app.theme.ThemeType;
import com.nextstory.util.LifecycleCallbacks;
import com.nextstory.util.SimpleActivityLifecycleCallbacks;
import com.nextstory.util.SimpleFragmentLifecycleCallbacks;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 기본 애플리케이션
 *
 * @author troy
 * @since 1.3
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class BaseApplication extends Application
        implements SimpleActivityLifecycleCallbacks, SimpleFragmentLifecycleCallbacks {
    private final ThemeHelpers themeHelpers = new ThemeHelpers();
    private final LocaleManager localeManager = new LocaleManagerImpl(this);

    @Override
    public void onCreate() {
        super.onCreate();

        LifecycleCallbacks.registerActivityLifecycleCallbacks(this);
        LifecycleCallbacks.registerFragmentLifecycleCallbacks(this);
    }

    /**
     * @return {@link Application#getResources()}
     * @see #getLocaleResources() 로케일 설정이 적용된 리소스 반환 시 사용
     */
    @Override
    public Resources getResources() {
        return super.getResources();
    }

    /**
     * 로케일 설정이 적용된 리소스
     *
     * @return 리소스
     */
    public Resources getLocaleResources() {
        return localeManager.wrapContext(this, super.getResources()).getResources();
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
     * 토스트 표시
     *
     * @param res 문자열 리소스
     */
    public void showToast(@StringRes int res) {
        Toast.makeText(this, getLocaleResources().getString(res), Toast.LENGTH_SHORT).show();
    }

    /**
     * 토스트 표시
     *
     * @param message 문자열
     */
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
