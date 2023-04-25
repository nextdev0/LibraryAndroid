package com.nextstory.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link AutoSharedPreferences} 생성시 포함할 항목 어노테이션
 *
 * @author troy
 * @since 1.3
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface AutoSharedPreferencesItem {
  String value() default "";
}
