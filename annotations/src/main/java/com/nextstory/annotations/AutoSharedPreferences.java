package com.nextstory.annotations;

import android.content.SharedPreferences;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link SharedPreferences} Getter, Setter 생성 어노테이션
 *
 * @author troy
 * @since 1.2
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface AutoSharedPreferences {
    String value() default "";
}
