/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.model.mirror;

import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;

import org.eclipse.ceylon.model.loader.mirror.AnnotationMirror;
import org.eclipse.ceylon.model.loader.mirror.FieldMirror;
import org.eclipse.ceylon.model.loader.mirror.TypeMirror;

public class JDTField implements FieldMirror {

    private TypeMirror type;
    private Map<String, AnnotationMirror> annotations;
    String name;
    int modifiers;

    public JDTField(FieldBinding field) {
        annotations = JDTUtils.getAnnotations(field.getAnnotations());
        name = new String(field.readableName());
        modifiers = field.modifiers;
        type = JDTType.newJDTType(field.type);
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
    public String getName() {
        return name;
    }

    @Override
    public boolean isStatic() {
        return (this.modifiers & ClassFileConstants.AccStatic) != 0;
    }

    @Override
    public boolean isPublic() {
        return (this.modifiers & ClassFileConstants.AccPublic) != 0;
    }

    @Override
    public boolean isFinal() {
        return (this.modifiers & ClassFileConstants.AccFinal) != 0;
    }

    @Override
    public TypeMirror getType() {
        return type;
    }

    @Override
    public String toString() {
        return "[JDTField: "+name+" ( + " + type != null ? type.getQualifiedName() : "unknown" + ")]";
    }

    @Override
    public boolean isProtected() {
        return (this.modifiers & ClassFileConstants.AccProtected) != 0;
    }

    @Override
    public boolean isDefaultAccess() {
        return !isPublic() && !isProtected() && !isPrivate();
    }

    private boolean isPrivate() {
        return (this.modifiers & ClassFileConstants.AccPrivate) != 0;
    }
}
