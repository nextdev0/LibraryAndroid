package com.nextstory.util.theme;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 테마 타입
 *
 * @author troy
 * @version 1.0
 * @since 1.0
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({ThemeType.LIGHT, ThemeType.DARK, ThemeType.SYSTEM})
public @interface ThemeType {
    int LIGHT = 0;
    int DARK = 1;
    int SYSTEM = 2;
}
