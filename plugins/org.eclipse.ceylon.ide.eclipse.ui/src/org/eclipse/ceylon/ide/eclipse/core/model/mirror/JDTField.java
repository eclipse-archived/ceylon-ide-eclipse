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
