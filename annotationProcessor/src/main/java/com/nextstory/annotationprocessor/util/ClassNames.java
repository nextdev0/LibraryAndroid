package com.nextstory.annotationprocessor.util;

import com.squareup.javapoet.ClassName;

/**
 * @author troy
 * @since 1.0
 */
public final class ClassNames {
    public static final ClassName Activity = ClassName.bestGuess("android.app.Activity");
    public static final ClassName Application = ClassName.bestGuess("android.app.Application");
    public static final ClassName ActivityLifecycleCallbacks =
            ClassName.bestGuess("Application.ActivityLifecycleCallbacks");
    public static final ClassName Context = ClassName.bestGuess("android.content.Context");
    public static final ClassName Intent = ClassName.bestGuess("android.content.Intent");
    public static final ClassName Bundle = ClassName.bestGuess("android.os.Bundle");
}