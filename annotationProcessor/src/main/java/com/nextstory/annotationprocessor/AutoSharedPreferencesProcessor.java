package com.nextstory.annotationprocessor;

import com.nextstory.annotationprocessor.util.ClassNames;
import com.nextstory.annotationprocessor.util.ElementHelper;
import com.nextstory.annotations.AutoSharedPreferences;
import com.nextstory.annotations.AutoSharedPreferencesItem;
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
 * SharedPreferences getter, setter 자동 생성용 어노테이션 프로세서
 *
 * @author troy
 * @since 1.3
 */
public final class AutoSharedPreferencesProcessor extends AbstractProcessor {
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>() {
            {
                add(AutoSharedPreferences.class.getCanonicalName());
                add(AutoSharedPreferencesItem.class.getCanonicalName());
            }
        };
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<ElementHelper> annotatedClassesSet = new LinkedHashSet<>();
        Map<ElementHelper, List<Element>> annotatedMap = new LinkedHashMap<>();

        for (Element e : roundEnvironment.getElementsAnnotatedWith(AutoSharedPreferences.class)) {
            ElementHelper elementHelper = ElementHelper.fromElement(e);
            annotatedClassesSet.add(elementHelper);
            AutoSharedPreferences annotation = e.getAnnotation(AutoSharedPreferences.class);
            elementHelper.setTag(annotation.value());
            if (!e.getKind().isInterface()) {
                throw new IllegalStateException("must be interface when used " +
                        "@AutoSharedPreference.");
            }
        }

        for (Element e :
                roundEnvironment.getElementsAnnotatedWith(AutoSharedPreferencesItem.class)) {
            String parentName = e.getEnclosingElement().toString();
            for (ElementHelper elementHelper : annotatedClassesSet) {
                if (elementHelper.getFullName().equals(parentName)) {
                    if (!annotatedMap.containsKey(elementHelper)) {
                        annotatedMap.put(elementHelper, new ArrayList<>());
                    }
                    List<Element> elements = annotatedMap.get(elementHelper);
                    elements.add(e);
                }
            }
        }

        for (ElementHelper elementHelper : annotatedClassesSet) {
            try {
                String packageName = elementHelper.getPackageName();
                String name = getSharedPreferenceClassName(elementHelper);
                String prefName = getSharedPreferenceClassPrefName(elementHelper);
                ClassName entityClassName = ClassName.bestGuess(elementHelper.getFullName());
                TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(name)
                        .addJavadoc("@see $T", entityClassName)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addField(ClassNames.SharedPreferences, "sharedPreferences",
                                Modifier.PRIVATE, Modifier.FINAL)
                        .addMethod(MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(ClassNames.Context, "context")
                                .addStatement("this.sharedPreferences = context" +
                                                ".getSharedPreferences(\"$N\", $T.MODE_PRIVATE)",
                                        prefName, ClassNames.Context)
                                .build());
                if (annotatedMap.containsKey(elementHelper)) {
                    for (Element element : annotatedMap.get(elementHelper)) {
                        AutoSharedPreferencesItem annotation =
                                element.getAnnotation(AutoSharedPreferencesItem.class);
                        String prefItemName = annotation.value().trim().isEmpty()
                                ? element.getSimpleName().toString()
                                : annotation.value().trim();
                        String fieldName = element.toString();
                        String getterName = "get" + fieldName.substring(0, 1).toUpperCase()
                                + fieldName.substring(1);
                        String setterName = "set" + fieldName.substring(0, 1).toUpperCase()
                                + fieldName.substring(1);

                        // getter
                        typeSpecBuilder.addMethod(MethodSpec.methodBuilder(getterName)
                                .returns(TypeName.get(element.asType()))
                                .addModifiers(Modifier.PUBLIC)
                                .addStatement("$T.assertConverterIsNotNull()",
                                        ClassNames.AutoSharedPreferenceUtils)
                                .addStatement("String source = " +
                                                "this.sharedPreferences.getString(\"$N\", \"\")",
                                        prefItemName)
                                .beginControlFlow("if (source.trim().isEmpty())")
                                .addStatement("return $T.$N", entityClassName, fieldName)
                                .endControlFlow()
                                .beginControlFlow("try")
                                .addStatement("return $T.deserialize($N, $T.class)",
                                        ClassNames.AutoSharedPreferenceUtils,
                                        "source",
                                        TypeName.get(element.asType()))
                                .endControlFlow()
                                .beginControlFlow("catch (Throwable ignore)")
                                .addStatement("return $T.$N", entityClassName, fieldName)
                                .endControlFlow()
                                .build());

                        // setter
                        typeSpecBuilder.addMethod(MethodSpec.methodBuilder(setterName)
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(TypeName.get(element.asType()), fieldName)
                                .addStatement("$T.assertConverterIsNotNull()",
                                        ClassNames.AutoSharedPreferenceUtils)
                                .addStatement("String source = $T.serialize($N)",
                                        ClassNames.AutoSharedPreferenceUtils, fieldName)
                                .addStatement("this.sharedPreferences\n" +
                                                ".edit()\n" +
                                                ".putString(\"$N\", source)\n" +
                                                ".apply()",
                                        prefItemName)
                                .build());
                    }
                }
                JavaFile.builder(packageName, typeSpecBuilder.build())
                        .build()
                        .writeTo(processingEnv.getFiler());
            } catch (IOException ignore) {
            }
        }

        return true;
    }

    private String getSharedPreferenceClassName(ElementHelper elementHelper) {
        return elementHelper.getSimpleName() + "SharedPreferences";
    }

    private String getSharedPreferenceClassPrefName(ElementHelper elementHelper) {
        if (elementHelper.getTag() == null) {
            return getSharedPreferenceClassName(elementHelper);
        }
        String value = (String) elementHelper.getTag();
        if (value.trim().isEmpty()) {
            return getSharedPreferenceClassName(elementHelper);
        }
        return value.trim();
    }
}
