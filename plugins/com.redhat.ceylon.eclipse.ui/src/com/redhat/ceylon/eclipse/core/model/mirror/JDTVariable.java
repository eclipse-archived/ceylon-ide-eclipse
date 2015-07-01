/*
 * Copyright Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the authors tag. All rights reserved.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License version 2.
 * 
 * This particular file is subject to the "Classpath" exception as provided in the 
 * LICENSE file that accompanied this code.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License,
 * along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package com.redhat.ceylon.eclipse.core.model.mirror;

import java.util.Map;

import com.redhat.ceylon.model.loader.mirror.AnnotationMirror;
import com.redhat.ceylon.model.loader.mirror.TypeMirror;
import com.redhat.ceylon.model.loader.mirror.VariableMirror;

public class JDTVariable implements VariableMirror {

    private Map<String, AnnotationMirror> annotations;
    private TypeMirror type;
    private String name;

    public JDTVariable(String name, JDTType type, Map<String, AnnotationMirror> annotations) {
        this.name = name;
        this.type = type;
        this.annotations = annotations;
    }

    @Override
    public AnnotationMirror getAnnotation(String type) {
        return annotations.get(type);
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
