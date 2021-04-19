package com.nextstory.annotationprocessor;

import com.nextstory.annotationprocessor.util.Android;
import com.nextstory.annotationprocessor.util.ElementNames;
import com.nextstory.annotations.IntentBuilder;
import com.nextstory.annotations.IntentExtra;
import com.nextstory.util.LibraryInitializer;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * {@link IntentBuilder} 어노테이션 프로세서
 *
 * @author troy
 * @author 1.0
 */
public final class IntentProcessor extends AbstractProcessor {
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>() {
            {
                add(IntentBuilder.class.getCanonicalName());
            }
        };
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<ElementNames> intentBuilderSet = new LinkedHashSet<>();
        Map<ElementNames, List<Element>> intentExtraMap = new LinkedHashMap<>();
        for (Element element : roundEnvironment.getElementsAnnotatedWith(IntentBuilder.class)) {
            ElementNames elementNames = ElementNames.fromElement(element);
            intentBuilderSet.add(elementNames);
        }
        for (Element element : roundEnvironment.getElementsAnnotatedWith(IntentExtra.class)) {
            String parentName = element.getEnclosingElement().toString();
            for (ElementNames elementNames : intentBuilderSet) {
                if (elementNames.getFullName().equals(parentName)) {
                    if (!intentExtraMap.containsKey(elementNames)) {
                        intentExtraMap.put(elementNames, new ArrayList<>());
                    }
                    List<Element> elements = intentExtraMap.get(elementNames);
                    elements.add(element);
                }
            }
        }

        try {
            generateIntents(intentExtraMap);
            JavaFile.builder("com.nextstory.util", TypeSpec.classBuilder("IntentBuilderInitializer")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(LibraryInitializer.class)
                    .addSuperinterface(Android.ActivityLifecycleCallbacks)
                    .addMethod(createInitializerMethodSpec())
                    .addMethod(createOnActivityCreatedMethodSpec(intentBuilderSet))
                    .addMethod(createEtcCallbacksMethodSpec("onActivityStarted", false))
                    .addMethod(createEtcCallbacksMethodSpec("onActivityResumed", false))
                    .addMethod(createEtcCallbacksMethodSpec("onActivityPaused", false))
                    .addMethod(createEtcCallbacksMethodSpec("onActivityStopped", false))
                    .addMethod(createEtcCallbacksMethodSpec("onActivitySaveInstanceState", true))
                    .addMethod(createEtcCallbacksMethodSpec("onActivityDestroyed", false))
                    .build())
                    .build()
                    .writeTo(processingEnv.getFiler());
        } catch (IOException ignore) {
        }

        return true;
    }

    private String getIntentBuilderName(ElementNames elementNames) {
        return elementNames.getSimpleName() + "IntentBuilder";
    }

    /**
     * intent 클래스 생성
     *
     * @param intentExtraMap -
     */
    private void generateIntents(Map<ElementNames, List<Element>> intentExtraMap) {
        for (ElementNames elementNames : intentExtraMap.keySet()) {
            try {
                String packageName = elementNames.getPackageName();
                String name = getIntentBuilderName(elementNames);
                ClassName className = ClassName.bestGuess(packageName + "." + name);
                ClassName activityClassName = ClassName.bestGuess(elementNames.getFullName());
                TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(name)
                        .addJavadoc("@see $T", activityClassName)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addField(Android.Intent, "intent", Modifier.PRIVATE, Modifier.FINAL)
                        .addMethod(MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(Android.Context, "context")
                                .addStatement("this.intent = new Intent(context, $T.class)",
                                        activityClassName)
                                .build())
                        .addMethod(createIntentInjectMethodSpec(
                                activityClassName, intentExtraMap.get(elementNames)));
                for (Element element : intentExtraMap.get(elementNames)) {
                    MethodSpec methodSpec =
                            createIntentMethodSpec(className, element);
                    typeSpecBuilder.addMethod(methodSpec);
                }
                typeSpecBuilder.addMethod(MethodSpec.methodBuilder("create")
                        .returns(Android.Intent)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return this.intent")
                        .build());
                typeSpecBuilder.addMethod(MethodSpec.methodBuilder("build")
                        .returns(Android.Intent)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return this.intent")
                        .build());
                JavaFile.builder(packageName, typeSpecBuilder.build())
                        .build()
                        .writeTo(processingEnv.getFiler());
            } catch (IOException ignore) {
            }
        }
    }

    private MethodSpec createIntentInjectMethodSpec(ClassName activityClassName,
                                                    List<Element> elements) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(activityClassName, "activity");
        builder.addStatement("$T intent = activity.getIntent()", Android.Intent);
        for (Element element : elements) {
            builder.beginControlFlow("if (intent.hasExtra(\"extra_$N\"))", element.toString())
                    .addStatement("activity.$N = ($T) intent.getSerializableExtra(\"extra_$N\")",
                            element.toString(), TypeName.get(element.asType()), element.toString())
                    .endControlFlow();
        }
        return builder.build();
    }

    private MethodSpec createIntentMethodSpec(ClassName className, Element fieldElement) {
        String fieldName = fieldElement.toString();
        String firstUpperFieldName = fieldName.substring(0, 1).toUpperCase()
                + fieldName.substring(1);
        MethodSpec.Builder builder = MethodSpec.methodBuilder("set" + firstUpperFieldName)
                .returns(className)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(fieldElement.asType()), fieldName)
                .addStatement("intent.putExtra(\"extra_$N\", $N)", fieldName, fieldName)
                .addStatement("return this");
        return builder.build();
    }

    /**
     * 초기화 메서드 생성
     *
     * @return MethodSpec
     */
    private MethodSpec createInitializerMethodSpec() {
        return MethodSpec.methodBuilder("onInitialized")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Android.Context, "context")
                .addParameter(String.class, "argument")
                .addStatement("$T application = ($T) context",
                        Android.Application, Android.Application)
                .addStatement("application.registerActivityLifecycleCallbacks(this)")
                .build();
    }

    /**
     * 액티비티 intent 주입 메서드 생성
     *
     * @return MethodSpec
     */
    private MethodSpec createOnActivityCreatedMethodSpec(Set<ElementNames> intentBuilderSet) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("onActivityCreated")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Android.Activity, "activity")
                .addParameter(Android.Bundle, "savedInstanceState");

        for (ElementNames elementNames : intentBuilderSet) {
            String builderName = getIntentBuilderName(elementNames);
            ClassName className = ClassName.bestGuess(elementNames.getFullName());
            ClassName builderClassName =
                    ClassName.bestGuess(elementNames.getPackageName() + "." + builderName);
            builder.beginControlFlow("if (activity instanceof $T)", className)
                    .addStatement("$T.inject(($T) activity)", builderClassName, className)
                    .endControlFlow();
        }

        return builder.build();
    }

    /**
     * 미사용 액티비티 콜백 메서드 생성
     *
     * @return MethodSpec
     */
    private MethodSpec createEtcCallbacksMethodSpec(String name, boolean hasBundle) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder(name)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Android.Activity, "activity");
        if (hasBundle) {
            builder.addParameter(Android.Bundle, "savedInstanceState");
        }
        return builder
                .addComment("no-op")
                .build();
    }
}
