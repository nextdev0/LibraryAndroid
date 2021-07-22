package com.nextstory.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;
import androidx.appcompat.app.AppCompatDelegate;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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

    private static final String LANGUAGE = "language";
    private static final String COUNTRY = "country";

    private static List<Locale> supportedLocales = null;
    private final Context context;

    private SharedPreferences localePreferences = null;

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public ResourcesController(Context context) {
        this.context = context;
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
    @SuppressWarnings("deprecation")
    public Locale getLocale() {
        String language = getLocalePreferences(context).getString(LANGUAGE, "");
        String country = getLocalePreferences(context).getString(COUNTRY, "");
        Locale locale = null;
        for (Locale item : Locale.getAvailableLocales()) {
            if (item == null) {
                continue;
            }
            if (item.getCountry().equals(country) && item.getLanguage().equals(language)) {
                locale = item;
                break;
            }
        }
        if (locale != null) {
            return locale;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context
                    .getApplicationContext()
                    .getResources()
                    .getConfiguration()
                    .getLocales()
                    .get(0);
        } else {
            return context
                    .getApplicationContext()
                    .getResources()
                    .getConfiguration().locale;
        }
    }

    private SharedPreferences getLocalePreferences(Context context) {
        if (localePreferences == null) {
            localePreferences = context.getSharedPreferences("app_locale", Context.MODE_PRIVATE);
        }
        return localePreferences;
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
        String country = locale.getCountry();

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
                Locale firstSameLocale = sameLanguages.get(0);
                getLocalePreferences(context)
                        .edit()
                        .putString(LANGUAGE, firstSameLocale.getLanguage())
                        .putString(COUNTRY, firstSameLocale.getCountry())
                        .apply();
                return this;
            }
        }
        getLocalePreferences(context)
                .edit()
                .putString(LANGUAGE, locale.getLanguage())
                .putString(COUNTRY, locale.getCountry())
                .apply();

        return this;
    }

    /**
     * @return 로케일이 적용된 컨텍스트
     */
    public Context getLocaleContext() {
        return wrap(context, context.getApplicationContext().getResources());
    }

    /**
     * @return 로케일이 적용된 Resources
     */
    public Resources getLocaleResources() {
        return getLocaleContext().getResources();
    }

    /**
     * @return 로케일이 적용된 Resources
     */
    public Resources getLocaleResources(Resources resources) {
        return wrap(context, resources).getResources();
    }

    private Context wrap(Context context, Resources resources) {
        Locale.getAvailableLocales();

        Locale locale = getLocaleInternal(context, resources);
        Configuration configuration = new Configuration(resources.getConfiguration());
        Locale.setDefault(locale);
        configuration.locale = locale;
        configuration.setLocale(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
        } else {
            configuration.locale = locale;
            resources.updateConfiguration(configuration, null);
        }

        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private Locale getLocaleInternal(Context context, Resources resources) {
        String language = getLocalePreferences(context).getString(LANGUAGE, "");
        String country = getLocalePreferences(context).getString(COUNTRY, "");
        Locale locale = null;
        for (Locale item : Locale.getAvailableLocales()) {
            if (item == null) {
                continue;
            }
            if (item.getCountry().equals(country) && item.getLanguage().equals(language)) {
                locale = item;
                break;
            }
        }
        if (locale != null) {
            return locale;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return resources
                    .getConfiguration()
                    .getLocales()
                    .get(0);
        } else {
            return resources.getConfiguration().locale;
        }
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
