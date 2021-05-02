package com.nextstory.app.locale;

import android.content.Context;
import android.content.res.Resources;

import java.util.List;
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
     * 지원되는 로케일 목록 지정
     * (API 24 이전 버전에서 로케일별 리소스 로딩을 개선하기 위하여 지정함)
     *
     * @param locales 로케일 목록
     * @since 1.3
     */
    void registerSupportedLocales(List<Locale> locales);

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
