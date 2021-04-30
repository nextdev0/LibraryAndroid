package com.nextstory.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 액티비티 값 전달용 Intent 빌더 생성 어노테이션
 *
 * @author troy
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ActivityIntentBuilder {
}
