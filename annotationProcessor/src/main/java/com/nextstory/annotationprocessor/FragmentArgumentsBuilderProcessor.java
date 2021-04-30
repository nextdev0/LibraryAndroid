package com.nextstory.annotationprocessor;

import com.nextstory.annotationprocessor.util.ClassNames;
import com.nextstory.annotationprocessor.util.ElementHelper;
import com.nextstory.annotations.FragmentArgument;
import com.nextstory.annotations.FragmentArgumentsBuilder;
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
 * 프래그먼트 Arguments 빌더 어노테이션 프로세서
 *
 * @author troy
 * @author 1.1
 */
public class FragmentArgumentsBuilderProcessor extends AbstractProcessor {
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>() {
            {
                add(FragmentArgumentsBuilder.class.getCanonicalName());
                add(FragmentArgument.class.getCanonicalName());
            }
        };
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment re) {
        Set<ElementHelper> fragmentArgumentsBuilderSet = new LinkedHashSet<>();
        Map<ElementHelper, List<Element>> fragmentArgumentMap = new LinkedHashMap<>();

        for (Element e : re.getElementsAnnotatedWith(FragmentArgumentsBuilder.class)) {
            ElementHelper elementHelper = ElementHelper.fromElement(e);
            fragmentArgumentsBuilderSet.add(elementHelper);
        }

        for (Element e : re.getElementsAnnotatedWith(FragmentArgument.class)) {
            String parentName = e.getEnclosingElement().toString();
            for (ElementHelper elementHelper : fragmentArgumentsBuilderSet) {
                if (elementHelper.getFullName().equals(parentName)) {
                    if (!fragmentArgumentMap.containsKey(elementHelper)) {
                        fragmentArgumentMap.put(elementHelper, new ArrayList<>());
                    }
                    List<Element> elements = fragmentArgumentMap.get(elementHelper);
                    elements.add(e);
                }
            }
        }

        try {
            generateBuilder(fragmentArgumentsBuilderSet, fragmentArgumentMap);
            JavaFile.builder("com.nextstory.util", TypeSpec
                    .classBuilder("ArgumentsBuilderInitializer")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(LibraryInitializer.class)
                    .addSuperinterface(ClassNames.SimpleFragmentLifecycleCallbacks)
                    .addMethod(createInitializerMethodSpec())
                    .addMethod(createOnFragmentAttachedMethodSpec(fragmentArgumentsBuilderSet))
                    .build())
                    .build()
                    .writeTo(processingEnv.getFiler());
        } catch (IOException ignore) {
        }

        return true;
    }

    private String getIntentBuilderName(ElementHelper elementHelper) {
        return elementHelper.getSimpleName() + "ArgumentsBuilder";
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
                .addStatement("$T.registerFragmentLifecycleCallbacks(this)",
                        ClassNames.LifecycleCallbacks)
                .build();
    }

    /**
     * Arguments 주입 메서드 생성
     *
     * @return MethodSpec
     */
    private MethodSpec createOnFragmentAttachedMethodSpec(Set<ElementHelper> builderSet) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("onFragmentAttached")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassNames.FragmentManager, "fragmentManager")
                .addParameter(ClassNames.Fragment, "fragment")
                .addParameter(ClassNames.Context, "context");

        for (ElementHelper elementHelper : builderSet) {
            String builderName = getIntentBuilderName(elementHelper);
            ClassName className = ClassName.bestGuess(elementHelper.getFullName());
            ClassName builderClassName =
                    ClassName.bestGuess(elementHelper.getPackageName() + "." + builderName);
            builder.beginControlFlow("if (fragment instanceof $T)", className)
                    .addStatement("$T.inject(($T) fragment)", builderClassName, className)
                    .endControlFlow();
        }

        return builder.build();
    }

    /**
     * 빌더 클래스 생성
     *
     * @param intentExtraMap -
     */
    private void generateBuilder(Set<ElementHelper> intentBuilderSet,
                                 Map<ElementHelper, List<Element>> intentExtraMap) {
        for (ElementHelper elementHelper : intentBuilderSet) {
            try {
                String packageName = elementHelper.getPackageName();
                String name = getIntentBuilderName(elementHelper);
                ClassName className = ClassName.bestGuess(packageName + "." + name);
                ClassName fragmentClassName = ClassName.bestGuess(elementHelper.getFullName());
                TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(name)
                        .addJavadoc("@see $T", fragmentClassName)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addField(fragmentClassName, "fragment", Modifier.PRIVATE, Modifier.FINAL)
                        .addField(ClassNames.Bundle, "arguments", Modifier.PRIVATE, Modifier.FINAL)
                        .addMethod(MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PUBLIC)
                                .addStatement("this.fragment = new $T()", fragmentClassName)
                                .addStatement("this.arguments = new $T()", ClassNames.Bundle)
                                .build())
                        .addMethod(createIntentInjectMethodSpec(
                                fragmentClassName, elementHelper, intentExtraMap));
                if (intentExtraMap.containsKey(elementHelper)) {
                    for (Element element : intentExtraMap.get(elementHelper)) {
                        MethodSpec methodSpec = createIntentMethodSpec(className, element);
                        typeSpecBuilder.addMethod(methodSpec);
                    }
                }
                typeSpecBuilder.addMethod(MethodSpec.methodBuilder("create")
                        .returns(fragmentClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("this.fragment.setArguments(this.arguments)")
                        .addStatement("return this.fragment")
                        .build());
                typeSpecBuilder.addMethod(MethodSpec.methodBuilder("build")
                        .returns(fragmentClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("this.fragment.setArguments(this.arguments)")
                        .addStatement("return this.fragment")
                        .build());
                JavaFile.builder(packageName, typeSpecBuilder.build())
                        .build()
                        .writeTo(processingEnv.getFiler());
            } catch (IOException ignore) {
            }
        }
    }

    private MethodSpec createIntentInjectMethodSpec(
            ClassName fragmentClassName,
            ElementHelper intentBuilder,
            Map<ElementHelper, List<Element>> intentExtraMap
    ) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(fragmentClassName, "fragment");
        if (intentExtraMap.containsKey(intentBuilder)) {
            builder.addStatement("$T arguments = fragment.getArguments()", ClassNames.Bundle)
                    .beginControlFlow("if (arguments != null)");
            for (Element element : intentExtraMap.get(intentBuilder)) {
                builder.beginControlFlow("if (arguments.containsKey(\"argument_$N\"))",
                        element.toString())
                        .addStatement("fragment.$N = " +
                                        "($T) arguments.getSerializable(\"argument_$N\")",
                                element.toString(),
                                TypeName.get(element.asType()),
                                element.toString())
                        .endControlFlow();
            }
            builder.endControlFlow();
        } else {
            builder.addComment("no has arguments");
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
                .addStatement("arguments.putSerializable(\"argument_$N\", $N)",
                        fieldName, fieldName)
                .addStatement("return this");
        return builder.build();
    }
}
