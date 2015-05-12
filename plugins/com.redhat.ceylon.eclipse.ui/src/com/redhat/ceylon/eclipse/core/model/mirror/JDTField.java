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

import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;

import com.redhat.ceylon.model.loader.mirror.AnnotationMirror;
import com.redhat.ceylon.model.loader.mirror.FieldMirror;
import com.redhat.ceylon.model.loader.mirror.TypeMirror;

public class JDTField implements FieldMirror {

    private FieldBinding field;
    private JDTType type;
    private Map<String, AnnotationMirror> annotations;
    String name;

    public JDTField(FieldBinding field) {
        this.field = field;
    }

    @Override
    public AnnotationMirror getAnnotation(String type) {
        if (annotations == null) {
            annotations = JDTUtils.getAnnotations(field.getAnnotations());
        }
        return annotations.get(type);
    }

    @Override
    public String getName() {
        if (name == null) {
            name = new String(field.readableName());
        }
        return name;
    }

    @Override
    public boolean isStatic() {
        return field.isStatic();
    }

    @Override
    public boolean isPublic() {
        return field.isPublic();
    }

    @Override
    public boolean isFinal() {
        return field.isFinal();
    }

    @Override
    public TypeMirror getType() {
        if(type == null) {
            type = new JDTType(field.type);
        }
        return type;
    }

    @Override
    public String toString() {
        return "[JDTField: "+field.toString()+"]";
    }

    @Override
    public boolean isProtected() {
        return field.isProtected();
    }

    @Override
    public boolean isDefaultAccess() {
        return field.isDefault();
    }
}
