package com.nextstory.util;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 문자열 > 기본형 변환 유틸
 *
 * @author troy
 * @since 1.2
 */
@SuppressWarnings("UnusedDeclaration")
public final class Convert {
    private static final Pattern HEXADECIMAL_PATTERN = Pattern.compile("\\p{XDigit}+");

    private Convert() {
        // no-op
    }

    /**
     * boolean 형식으로 변환
     *
     * @param s 값이 포함된 문자열
     * @return boolean 값, 예외시 {@code false} 반환
     */
    public static boolean toBoolean(@Nullable String s) {
        return toBoolean(s, false);
    }

    /**
     * boolean 형식으로 변환
     *
     * @param s            값이 포함된 문자열
     * @param defaultValue 변환 실패시 반환할 기본값
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
     * @param s 값이 포함된 문자열
     * @return byte 값, 예외시 {@code 0} 반환
     */
    public static byte toByte(@Nullable String s) {
        return toByte(s, (byte) 0);
    }

    /**
     * byte 형식으로 변환
     *
     * @param s            값이 포함된 문자열
     * @param defaultValue 변환 실패시 반환할 기본값
     * @return byte 값, 예외시 지정한 기본값 반환
     */
    public static byte toByte(String s, int defaultValue) {
        if (defaultValue < Byte.MIN_VALUE || defaultValue > Byte.MAX_VALUE) {
            throw new IllegalArgumentException("defaultValue 8-bit integer constant");
        }
        if (s == null || s.trim().isEmpty()) {
            return (byte) defaultValue;
        }
        try {
            return Byte.parseByte(s.trim());
        } catch (NumberFormatException e) {
            return (byte) defaultValue;
        }
    }

    /**
     * 16진수 문자열을 byte 형식으로 변환
     *
     * @param s 값이 포함된 문자열
     * @return byte 값, 예외시 {@code 0} 반환
     * @since 1.5
     */
    public static byte toHexByte(String s) {
        return toHexByte(s, (byte) 0);
    }

    /**
     * 16진수 문자열을 byte 형식으로 변환
     *
     * @param s            값이 포함된 문자열
     * @param defaultValue 변환 실패시 반환할 기본값
     * @return byte 값, 예외시 지정한 기본값 반환
     * @since 1.5
     */
    public static byte toHexByte(String s, int defaultValue) {
        if (defaultValue < Byte.MIN_VALUE || defaultValue > Byte.MAX_VALUE) {
            throw new IllegalArgumentException("defaultValue 8-bit integer constant");
        }
        if (s == null || s.trim().isEmpty()) {
            return (byte) defaultValue;
        }
        try {
            return Byte.parseByte(s.trim(), 16);
        } catch (NumberFormatException e) {
            return (byte) defaultValue;
        }
    }

    /**
     * integer 형식으로 변환
     *
     * @param s 값이 포함된 문자열
     * @return integer 값, 예외시 {@code 0} 반환
     */
    public static int toInt(@Nullable String s) {
        return toInt(s, 0);
    }

    /**
     * integer 형식으로 변환
     *
     * @param s            값이 포함된 문자열
     * @param defaultValue 변환 실패시 반환할 기본값
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
     * 16진수 문자열을 integer 형식으로 변환
     *
     * @param s 값이 포함된 문자열
     * @return integer 값, 예외시 {@code 0} 반환
     * @since 1.5
     */
    public static int toHexInt(String s) {
        return toHexInt(s, 0);
    }

    /**
     * 16진수 문자열을 integer 형식으로 변환
     *
     * @param s            값이 포함된 문자열
     * @param defaultValue 변환 실패시 반환할 기본값
     * @return integer 값, 예외시 지정한 기본값 반환
     * @since 1.5
     */
    public static int toHexInt(String s, int defaultValue) {
        if (s == null || s.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(s.trim(), 16);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * long 형식으로 변환
     *
     * @param s 값이 포함된 문자열
     * @return long 값, 예외시 {@code 0} 반환
     */
    public static long toLong(@Nullable String s) {
        return toLong(s, 0);
    }

    /**
     * long 형식으로 변환
     *
     * @param s            값이 포함된 문자열
     * @param defaultValue 변환 실패시 반환할 기본값
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
     * 16진수 문자열을 long 형식으로 변환
     *
     * @param s 값이 포함된 문자열
     * @return long 값, 예외시 {@code 0} 반환
     * @since 1.5
     */
    public static long toHexLong(String s) {
        return toHexLong(s, 0);
    }

    /**
     * 16진수 문자열을 long 형식으로 변환
     *
     * @param s            값이 포함된 문자열
     * @param defaultValue 변환 실패시 반환할 기본값
     * @return long 값, 예외시 지정한 기본값 반환
     * @since 1.5
     */
    public static long toHexLong(String s, long defaultValue) {
        if (s == null || s.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(s.trim(), 16);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * float 형식으로 변환
     *
     * @param s 값이 포함된 문자열
     * @return float 값, 예외시 {@code 0} 반환
     */
    public static float toFloat(String s) {
        return toFloat(s, 0f);
    }

    /**
     * float 형식으로 변환
     *
     * @param s            값이 포함된 문자열
     * @param defaultValue 변환 실패시 반환할 기본값
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
     * @param s 값이 포함된 문자열
     * @return double 값, 예외시 {@code 0} 반환
     */
    public static double toDouble(String s) {
        return toDouble(s, 0f);
    }

    /**
     * double 형식으로 변환
     *
     * @param s            값이 포함된 문자열
     * @param defaultValue 변환 실패시 반환할 기본값
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
     * 컬러값 변환
     *
     * @param s 문자열
     * @return 컬러값, 변환할 수 없을 경우 {@code Color.TRANSPARENT}를 반환
     * @since 1.5
     */
    @ColorInt
    public static int toColorInt(String s) {
        return toColorInt(s, Color.TRANSPARENT);
    }

    /**
     * 컬러값 변환
     *
     * @param s            문자열
     * @param defaultValue 변환 실패시 반환할 기본값
     * @return 컬러값, 변환할 수 없을 경우 지정한 기본값 반환
     * @since 1.5
     */
    @ColorInt
    public static int toColorInt(String s, int defaultValue) {
        if (s == null) {
            return defaultValue;
        }
        String trim = s.trim();
        if (trim.isEmpty()) {
            return defaultValue;
        }

        // "255, 255, 255, 0.5" 형식
        if (trim.contains(",")) {
            String[] colors = trim.split(",");
            if (colors.length >= 3) {
                int color = 0;
                color |= toInt(colors[0], 0) << 16;
                color |= toInt(colors[1], 0) << 8;
                color |= toInt(colors[2], 0);
                if (colors.length == 4) {
                    double alpha = toFloat(colors[3], 0f);
                    color |= (int) (alpha * 0xff) << 24;
                }
                return color;
            }
            return defaultValue;
        }

        // "#AARRGGBB" 형식
        try {
            if (trim.startsWith("#")) {
                return Color.parseColor(trim);
            }
            return Color.parseColor("#" + trim);
        } catch (Throwable ignore) {
        }

        return defaultValue;
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

    /**
     * 문자열이 16진수 숫자인지 체크
     *
     * @param s 문자열
     * @return 16진수 숫자유무
     * @since 1.5
     */
    private boolean isHexadecimal(String s) {
        final Matcher matcher = HEXADECIMAL_PATTERN.matcher(s);
        return matcher.matches();
    }
}
