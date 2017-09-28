package org.eclipse.ceylon.test.eclipse.plugin.util;

import org.eclipse.ceylon.model.typechecker.model.Function;
import org.eclipse.ceylon.model.typechecker.model.TypeDeclaration;

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