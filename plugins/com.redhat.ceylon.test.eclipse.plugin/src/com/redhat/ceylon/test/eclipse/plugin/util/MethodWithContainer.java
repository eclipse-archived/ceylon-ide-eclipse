package com.redhat.ceylon.test.eclipse.plugin.util;

import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;

public class MethodWithContainer {

    private final TypeDeclaration container;
    private final Function method;

    public MethodWithContainer(TypeDeclaration container, Function method) {
        this.method = method;
        this.container = container;
    }

    public TypeDeclaration getContainer() {
        return container;
    }

    public Function getMethod() {
        return method;
    }

}