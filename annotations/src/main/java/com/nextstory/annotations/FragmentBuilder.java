package com.nextstory.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Arguments 전달을 위한 프래그먼트 빌더 생성 어노테이션
 *
 * @author troy
 * @since 1.4
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface FragmentBuilder {
}
