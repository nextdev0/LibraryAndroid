package com.nextstory.util.locale;

import android.content.Context;
import android.content.res.Resources;

import java.util.Locale;

/**
 * 로케일 관리자 인터페이스
 *
 * @author troy
 * @since 1.2
 */
@SuppressWarnings("UnusedDeclaration")
public interface LocaleManager {
    /**
     * @return 시스템 로케일
     */
    Locale getLocale();

    /**
     * 로케일 적용
     *
     * @param locale 로케일
     */
    void applyLocale(Locale locale);

    /**
     * 로케일이 적용된 컨텍스트 반환
     *
     * @param context   컨텍스트
     * @param resources 리소스
     * @return 적용된 컨텍스트
     */
    Context wrapContext(Context context, Resources resources);
}
