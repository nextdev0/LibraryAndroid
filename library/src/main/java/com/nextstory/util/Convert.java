package com.nextstory.util;

import androidx.annotation.Nullable;

/**
 * 문자열 > 기본형 변환 유틸
 *
 * @author troy
 * @since 1.2
 */
@SuppressWarnings("UnusedDeclaration")
public final class Convert {
    private Convert() {
        // no-op
    }

    /**
     * boolean 형식으로 변환
     *
     * @return boolean 값, 예외시 {@code false} 반환
     */
    public static boolean toBoolean(@Nullable String s) {
        return toBoolean(s, false);
    }

    /**
     * boolean 형식으로 변환
     *
     * @return byte 값, 예외시 지정한 기본값 반환
     */
    public static boolean toBoolean(@Nullable String s, boolean defaultValue) {
        if (s == null || s.trim().isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(s.trim());
    }

    /**
     * byte 형식으로 변환
     *
     * @return byte 값, 예외시 {@code 0} 반환
     */
    public static int toByte(@Nullable String s) {
        return toByte(s, (byte) 0);
    }

    /**
     * byte 형식으로 변환
     *
     * @return byte 값, 예외시 지정한 기본값 반환
     */
    public static int toByte(String s, byte defaultValue) {
        if (s == null || s.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Byte.parseByte(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * integer 형식으로 변환
     *
     * @return integer 값, 예외시 {@code 0} 반환
     */
    public static int toInt(@Nullable String s) {
        return toInt(s, 0);
    }

    /**
     * integer 형식으로 변환
     *
     * @return integer 값, 예외시 지정한 기본값 반환
     */
    public static int toInt(String s, int defaultValue) {
        if (s == null || s.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * long 형식으로 변환
     *
     * @return long 값, 예외시 {@code 0} 반환
     */
    public static long toLong(@Nullable String s) {
        return toLong(s, 0);
    }

    /**
     * long 형식으로 변환
     *
     * @return long 값, 예외시 지정한 기본값 반환
     */
    public static long toLong(String s, int defaultValue) {
        if (s == null || s.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * float 형식으로 변환
     *
     * @return float 값, 예외시 {@code 0} 반환
     */
    public static float toFloat(String s) {
        return toFloat(s, 0f);
    }

    /**
     * float 형식으로 변환
     *
     * @return float 값, 예외시 지정한 기본값 반환
     */
    public static float toFloat(String s, float defaultValue) {
        if (s == null || s.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * double 형식으로 변환
     *
     * @return double 값, 예외시 {@code 0} 반환
     */
    public static double toDouble(String s) {
        return toDouble(s, 0f);
    }

    /**
     * double 형식으로 변환
     *
     * @return double 값, 예외시 지정한 기본값 반환
     */
    public static double toDouble(String s, double defaultValue) {
        if (s == null || s.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * unsafe 타입 캐스팅
     *
     * @param o   캐스팅할 객체
     * @param <T> 변환 타입
     * @return 변환된 인스턴스
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o) {
        return (T) o;
    }

    /**
     * 문자열이 boolean 형식인지 체크
     *
     * @param s 문자열
     * @return boolean 유무
     */
    public static boolean isBoolean(@Nullable String s) {
        if (s == null || s.trim().isEmpty()) {
            return false;
        }
        if (s.trim().equalsIgnoreCase("true")) {
            return true;
        }
        return s.trim().equalsIgnoreCase("false");
    }

    /**
     * 문자열이 숫자인지 체크
     *
     * @param s 문자열
     * @return 숫자유무
     */
    public static boolean isNumeric(String s) {
        if (s == null || s.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(s.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
