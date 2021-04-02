package com.nextstory.util.theme;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 테마 타입
 *
 * @author troy
 * @since 1.0
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({ThemeType.LIGHT, ThemeType.DARK, ThemeType.SYSTEM})
public @interface ThemeType {
    /**
     * 밝은 테마
     */
    int LIGHT = 0;

    /**
     * 어두운 테마
     */
    int DARK = 1;

    /**
     * 시스템 설정에 따름
     */
    int SYSTEM = 2;
}
