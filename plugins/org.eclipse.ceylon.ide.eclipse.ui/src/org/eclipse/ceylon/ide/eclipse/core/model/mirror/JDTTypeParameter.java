/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.model.mirror;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;

import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

import org.eclipse.ceylon.model.loader.mirror.TypeMirror;
import org.eclipse.ceylon.model.loader.mirror.TypeParameterMirror;

public class JDTTypeParameter implements TypeParameterMirror {

    private String name;
    private List<TypeMirror> bounds;

    public JDTTypeParameter(TypeVariableBinding parameter) {
        this(parameter, null, new IdentityHashMap<TypeBinding, JDTType>());
    }
    
    public JDTTypeParameter(TypeVariableBinding parameter, JDTType type, IdentityHashMap<TypeBinding, JDTType> originatingTypes) {
        name = new String(parameter.readableName());
        List<TypeBinding> javaBounds = new ArrayList<TypeBinding>();
        javaBounds.add(parameter.upperBound());
        javaBounds.addAll(Arrays.asList(parameter.otherUpperBounds()));
        bounds = new ArrayList<TypeMirror>(javaBounds.size());
        for(TypeBinding bound : javaBounds)
            bounds.add(JDTType.toTypeMirror(bound, parameter, type, originatingTypes));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<TypeMirror> getBounds() {
        return bounds;
    }
    
    @Override
    public String toString() {
        return "[JDTTypeParameter: "+name+"]";
    }
}
