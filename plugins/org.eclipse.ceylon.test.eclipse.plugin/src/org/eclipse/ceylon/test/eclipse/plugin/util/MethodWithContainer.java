/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
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