package com.nextstory.util;

import androidx.annotation.Nullable;

/**
 * 문자열 > 기본형 숫자 변환 유틸
 *
 * @author troy
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
        try {
            return Boolean.parseBoolean(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
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
            return Byte.parseByte(s);
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
            return Integer.parseInt(s);
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
            return Long.parseLong(s);
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
            return Float.parseFloat(s);
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
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
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
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
