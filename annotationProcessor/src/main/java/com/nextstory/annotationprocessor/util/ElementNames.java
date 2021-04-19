package com.nextstory.annotationprocessor.util;

import javax.lang.model.element.Element;

/**
 * @author troy
 * @since 1.0
 */
public final class ElementNames {
    private final String fullName;
    private final String packageName;
    private final String simpleName;

    public ElementNames(String fullName, String packageName, String simpleName) {
        this.fullName = fullName;
        this.packageName = packageName;
        this.simpleName = simpleName;
    }

    public static ElementNames fromElement(Element element) {
        String fullName = element.toString();
        String packageName = element.getEnclosingElement().toString();
        String simpleName = element.getSimpleName().toString();
        return new ElementNames(fullName, packageName, simpleName);
    }

    public String getFullName() {
        return fullName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getSimpleName() {
        return simpleName;
    }
}
