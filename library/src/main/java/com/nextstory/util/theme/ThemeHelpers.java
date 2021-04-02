package com.nextstory.util.theme;

import android.os.Build;

import androidx.annotation.RestrictTo;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * 테마 도우미 클래스
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class ThemeHelpers {
    /**
     * 현재 적용되어있는 테마의 타입을 반환
     *
     * @return 테마 타입
     * @see ThemeType
     */
    @SuppressWarnings("deprecation")
    @ThemeType
    public int getCurrentTheme() {
        switch (AppCompatDelegate.getDefaultNightMode()) {
            case AppCompatDelegate.MODE_NIGHT_AUTO_TIME:
            case AppCompatDelegate.MODE_NIGHT_NO:
                return ThemeType.LIGHT;
            case AppCompatDelegate.MODE_NIGHT_YES:
                return ThemeType.DARK;
            default:
            case AppCompatDelegate.MODE_NIGHT_UNSPECIFIED:
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
            case AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY:
                return ThemeType.SYSTEM;
        }
    }

    /**
     * 테마 설정
     *
     * @param themeType 테마 타입
     * @see ThemeType
     */
    public void applyTheme(@ThemeType int themeType) {
        switch (themeType) {
            default:
            case ThemeType.LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case ThemeType.DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case ThemeType.SYSTEM:
                AppCompatDelegate.setDefaultNightMode(
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                                ? AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                                : AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                break;
        }
    }
}
