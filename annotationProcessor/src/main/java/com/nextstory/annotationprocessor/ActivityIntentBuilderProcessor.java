package com.nextstory.annotationprocessor;

import com.nextstory.annotationprocessor.util.ClassNames;
import com.nextstory.annotationprocessor.util.ElementHelper;
import com.nextstory.annotations.ActivityIntentBuilder;
import com.nextstory.annotations.ActivityIntentExtra;
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
 * 액티비티 Intent 빌더 어노테이션 프로세서
 *
 * @author troy
 * @author 1.0
 */
@SuppressWarnings("deprecation")
public final class ActivityIntentBuilderProcessor extends AbstractProcessor {
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>() {
            {
                add(IntentBuilder.class.getCanonicalName());
                add(IntentExtra.class.getCanonicalName());
                add(ActivityIntentBuilder.class.getCanonicalName());
                add(ActivityIntentExtra.class.getCanonicalName());
            }
        };
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<ElementHelper> intentBuilderSet = new LinkedHashSet<>();
        Map<ElementHelper, List<Element>> intentExtraMap = new LinkedHashMap<>();

        // todo annotation 1.3 버전부터 삭제 (사유 : deprecated 처리)
        for (Element e : roundEnvironment.getElementsAnnotatedWith(IntentBuilder.class)) {
            ElementHelper elementHelper = ElementHelper.fromElement(e);
            intentBuilderSet.add(elementHelper);
        }

        // todo annotation 1.3 버전부터 삭제 (사유 : deprecated 처리)
        for (Element e : roundEnvironment.getElementsAnnotatedWith(IntentExtra.class)) {
            String parentName = e.getEnclosingElement().toString();
            for (ElementHelper elementHelper : intentBuilderSet) {
                if (elementHelper.getFullName().equals(parentName)) {
                    if (!intentExtraMap.containsKey(elementHelper)) {
                        intentExtraMap.put(elementHelper, new ArrayList<>());
                    }
                    List<Element> elements = intentExtraMap.get(elementHelper);
                    elements.add(e);
                }
            }
        }

        for (Element e : roundEnvironment.getElementsAnnotatedWith(ActivityIntentBuilder.class)) {
            ElementHelper elementHelper = ElementHelper.fromElement(e);
            intentBuilderSet.add(elementHelper);
        }

        for (Element e : roundEnvironment.getElementsAnnotatedWith(ActivityIntentExtra.class)) {
            String parentName = e.getEnclosingElement().toString();
            for (ElementHelper elementHelper : intentBuilderSet) {
                if (elementHelper.getFullName().equals(parentName)) {
                    if (!intentExtraMap.containsKey(elementHelper)) {
                        intentExtraMap.put(elementHelper, new ArrayList<>());
                    }
                    List<Element> elements = intentExtraMap.get(elementHelper);
                    elements.add(e);
                }
            }
        }

        try {
            generateIntents(intentBuilderSet, intentExtraMap);
            JavaFile.builder("com.nextstory.util", TypeSpec.classBuilder("IntentBuilderInitializer")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(LibraryInitializer.class)
                    .addSuperinterface(ClassNames.ActivityLifecycleCallbacks)
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

    private String getIntentBuilderName(ElementHelper elementHelper) {
        return elementHelper.getSimpleName() + "IntentBuilder";
    }

    /**
     * intent 클래스 생성
     *
     * @param intentExtraMap -
     */
    private void generateIntents(Set<ElementHelper> intentBuilderSet,
                                 Map<ElementHelper, List<Element>> intentExtraMap) {
        for (ElementHelper elementHelper : intentBuilderSet) {
            try {
                String packageName = elementHelper.getPackageName();
                String name = getIntentBuilderName(elementHelper);
                ClassName className = ClassName.bestGuess(packageName + "." + name);
                ClassName activityClassName = ClassName.bestGuess(elementHelper.getFullName());
                TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(name)
                        .addJavadoc("@see $T", activityClassName)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addField(ClassNames.Intent, "intent", Modifier.PRIVATE, Modifier.FINAL)
                        .addMethod(MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(ClassNames.Context, "context")
                                .addStatement("this.intent = new Intent(context, $T.class)",
                                        activityClassName)
                                .build())
                        .addMethod(createIntentInjectMethodSpec(
                                activityClassName, elementHelper, intentExtraMap));
                if (intentExtraMap.containsKey(elementHelper)) {
                    for (Element element : intentExtraMap.get(elementHelper)) {
                        MethodSpec methodSpec =
                                createIntentMethodSpec(className, element);
                        typeSpecBuilder.addMethod(methodSpec);
                    }
                }
                typeSpecBuilder.addMethod(MethodSpec.methodBuilder("create")
                        .returns(ClassNames.Intent)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("return this.intent")
                        .build());
                typeSpecBuilder.addMethod(MethodSpec.methodBuilder("build")
                        .returns(ClassNames.Intent)
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

    private MethodSpec createIntentInjectMethodSpec(
            ClassName activityClassName,
            ElementHelper intentBuilder,
            Map<ElementHelper, List<Element>> intentExtraMap
    ) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(activityClassName, "activity");
        if (intentExtraMap.containsKey(intentBuilder)) {
            builder.addStatement("$T intent = activity.getIntent()", ClassNames.Intent);
            for (Element element : intentExtraMap.get(intentBuilder)) {
                builder.beginControlFlow("if (intent.hasExtra(\"extra_$N\"))", element.toString())
                        .addStatement("activity.$N = " +
                                        "($T) intent.getSerializableExtra(\"extra_$N\")",
                                element.toString(),
                                TypeName.get(element.asType()),
                                element.toString())
                        .endControlFlow();
            }
        } else {
            builder.addComment("no has extra");
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
                .addParameter(ClassNames.Context, "context")
                .addParameter(String.class, "argument")
                .addStatement("$T application = ($T) context",
                        ClassNames.Application, ClassNames.Application)
                .addStatement("application.registerActivityLifecycleCallbacks(this)")
                .build();
    }

    /**
     * 액티비티 intent 주입 메서드 생성
     *
     * @return MethodSpec
     */
    private MethodSpec createOnActivityCreatedMethodSpec(Set<ElementHelper> intentBuilderSet) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("onActivityCreated")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassNames.Activity, "activity")
                .addParameter(ClassNames.Bundle, "savedInstanceState");

        for (ElementHelper elementHelper : intentBuilderSet) {
            String builderName = getIntentBuilderName(elementHelper);
            ClassName className = ClassName.bestGuess(elementHelper.getFullName());
            ClassName builderClassName =
                    ClassName.bestGuess(elementHelper.getPackageName() + "." + builderName);
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
                .addParameter(ClassNames.Activity, "activity");
        if (hasBundle) {
            builder.addParameter(ClassNames.Bundle, "savedInstanceState");
        }
        return builder
                .addComment("no-op")
                .build();
    }
}
