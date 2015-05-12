package com.redhat.ceylon.test.eclipse.plugin.util;

import com.redhat.ceylon.model.typechecker.model.Method;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;

public class MethodWithContainer {

    private final TypeDeclaration container;
    private final Method method;

    public MethodWithContainer(TypeDeclaration container, Method method) {
        this.method = method;
        this.container = container;
    }

    public TypeDeclaration getContainer() {
        return container;
    }

    public Method getMethod() {
        return method;
    }

}