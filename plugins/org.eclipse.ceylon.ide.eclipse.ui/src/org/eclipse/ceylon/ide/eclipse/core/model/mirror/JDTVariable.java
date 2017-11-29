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

import java.util.Map;
import java.util.Set;

import org.eclipse.ceylon.model.loader.mirror.AnnotationMirror;
import org.eclipse.ceylon.model.loader.mirror.TypeMirror;
import org.eclipse.ceylon.model.loader.mirror.VariableMirror;

public class JDTVariable implements VariableMirror {

    private Map<String, AnnotationMirror> annotations;
    private TypeMirror type;
    private String name;

    public JDTVariable(String name, TypeMirror type, Map<String, AnnotationMirror> annotations) {
        this.name = name;
        this.type = type;
        this.annotations = annotations;
    }

    @Override
    public AnnotationMirror getAnnotation(String type) {
        return annotations.get(type);
    }
    
    @Override
    public Set<String> getAnnotationNames() {
        return annotations.keySet();
    }

    @Override
    public TypeMirror getType() {
        return type; 
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "[JDTVariable: "+name+" ( + " + type != null ? type.getQualifiedName() : "unknown" + ")]";
    }
}
