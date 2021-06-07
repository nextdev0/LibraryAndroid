package com.nextstory.util;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.nextstory.annotations.AutoSharedPreferences;

/**
 * {@link AutoSharedPreferences} 유틸리티
 *
 * @author troy
 * @since 1.5
 */
@SuppressWarnings("UnusedDeclaration")
public final class AutoSharedPreferenceUtils {
    private final static Object lockObject = new Object();
    private static Converter converter = null;

    private AutoSharedPreferenceUtils() {
        // no-op
    }

    /**
     * @see Converter#serialize(Object)
     */
    public synchronized static String serialize(Object object) {
        synchronized (lockObject) {
            return converter.serialize(object);
        }
    }

    /**
     * @see Converter#deserialize(String, Class)
     */
    public synchronized static <T> T deserialize(String source, Class<T> klass) {
        synchronized (lockObject) {
            return converter.deserialize(source, klass);
        }
    }

    /**
     * 직렬화/역직렬화 변환 등록
     *
     * @param converter 변환 구현체
     */
    public synchronized static void registerConverter(@Nullable Converter converter) {
        synchronized (lockObject) {
            AutoSharedPreferenceUtils.converter = converter;
        }
    }

    /**
     * {@link Converter}가 {@code null}로 설정되어 있으면 예외발생
     * {@link #registerConverter(Converter)}로 등록해야함.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public static void assertConverterIsNotNull() {
        if (converter == null) {
            throw new NullPointerException("converter is null, " +
                    "use AutoSharedPreferenceUtils.registerConverter(Converter) method.");
        }
    }

    /**
     * @return 객체 Null 체크
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public static boolean isNull(Object o) {
        return o == null;
    }

    /**
     * 직렬화/역직렬화 변환 인터페이스
     */
    public interface Converter {
        /**
         * 직렬화
         *
         * @param object 직렬화할 객체
         * @return 직렬화된 문자열 데이터
         */
        String serialize(Object object);

        /**
         * 역직렬화
         *
         * @param source 직렬화된 문자열 데이터
         * @param klass  역직렬화될 타입 클래스
         * @param <T>    역직렬화될 타입
         * @return 역직렬화된 객체
         */
        <T> T deserialize(String source, Class<T> klass);
    }
}
