package com.nextstory.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author troy
 * @since 1.4
 */
@SuppressWarnings({"unchecked", "UnusedDeclaration"})
public final class Unsafe {
    private Unsafe() {
    }

    /**
     * 타입 캐스팅
     *
     * @param o   캐스팅할 객체
     * @param <T> 변환 타입
     * @return 변환된 인스턴스
     */
    public static <T> T cast(Object o) {
        return (T) o;
    }

    /**
     * 인스턴스 생성
     *
     * @param klass 클래스
     * @param <T>   타입
     * @return 인스턴스
     */
    public static <T> T newInstance(@NonNull Class<?> klass) {
        try {
            return (T) klass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * static 메소드 실행
     *
     * @param staticClass 클래스
     * @param methodName  메소드명
     * @param args        메소드 인자값
     */
    @SuppressWarnings("UnusedReturnValue")
    public static <T> T invoke(@NonNull Class<?> staticClass,
                               @NonNull String methodName,
                               Object... args) {
        try {
            Method method = null;
            for (Method m : staticClass.getDeclaredMethods()) {
                Class<?>[] parameters = m.getParameterTypes();
                if (m.getName().equals(methodName) && parameters.length == args.length) {
                    int sameParameterCount = 0;
                    for (int i = 0; i < args.length; i++) {
                        sameParameterCount += parameters[i].isInstance(args[i]) ? 1 : 0;
                    }
                    if (sameParameterCount == args.length) {
                        method = m;
                        break;
                    }
                }
            }
            if (method != null) {
                boolean isAccessible = method.isAccessible();
                method.setAccessible(true);
                Object ret = method.invoke(null, args);
                method.setAccessible(isAccessible);
                return (T) ret;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 내부 메소드 실행
     *
     * @param o          메소드가 포함된 객체
     * @param methodName 메소드명
     * @param args       메소드 인자값
     */
    public static <T> T invoke(@NonNull Object o, String methodName, Object... args) {
        Class<?> klass = Objects.requireNonNull(o).getClass();
        try {
            Method method = null;
            for (Method m : klass.getDeclaredMethods()) {
                Class<?>[] parameters = m.getParameterTypes();
                if (m.getName().equals(methodName) && parameters.length == args.length) {
                    int sameParameterCount = 0;
                    for (int i = 0; i < args.length; i++) {
                        sameParameterCount += parameters[i].isInstance(args[i]) ? 1 : 0;
                    }
                    if (sameParameterCount == args.length) {
                        method = m;
                        break;
                    }
                }
            }
            if (method != null) {
                boolean isAccessible = method.isAccessible();
                method.setAccessible(true);
                Object ret = method.invoke(o, args);
                method.setAccessible(isAccessible);
                return (T) ret;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 제너릭 클래스 반환
     *
     * @param o        제너릭이 포함된 객체
     * @param position 제너릭 클래스 위치
     * @param <T>      타입
     * @return 제너릭 클래스
     */
    @Nullable
    public static <T> Class<T> getGenericClass(Object o, int position) {
        if (o == null) {
            return null;
        }
        try {
            Class<?> klass = o.getClass();
            Type genericSuperclass = klass.getGenericSuperclass();
            if (genericSuperclass == null) {
                return null;
            }
            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
                Type[] types = parameterizedType.getActualTypeArguments();
                if (position < types.length) {
                    return (Class<T>) parameterizedType.getActualTypeArguments()[position];
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
