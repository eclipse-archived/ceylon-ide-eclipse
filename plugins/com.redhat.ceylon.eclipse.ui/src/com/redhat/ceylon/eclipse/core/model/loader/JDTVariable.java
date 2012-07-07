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

package com.redhat.ceylon.eclipse.core.model.loader;

import static java.lang.Character.toLowerCase;

import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

import com.redhat.ceylon.compiler.java.metadata.Name;
import com.redhat.ceylon.compiler.loader.mirror.AnnotationMirror;
import com.redhat.ceylon.compiler.loader.mirror.MethodMirror;
import com.redhat.ceylon.compiler.loader.mirror.TypeMirror;
import com.redhat.ceylon.compiler.loader.mirror.VariableMirror;

public class JDTVariable implements VariableMirror {

    private TypeBinding typeBinding;
    private AnnotationBinding[] annotationBindings;
    private Map<String, AnnotationMirror> annotations;
    private TypeMirror type;
    private MethodMirror methodMirror;
    private String name;

    public JDTVariable(TypeBinding typeBinding, AnnotationBinding[] annotationBindings, MethodMirror methodMirror) {
        this.typeBinding = typeBinding;
        this.annotationBindings = annotationBindings;
        this.methodMirror = methodMirror;
        setName();
    }

    @Override
    public AnnotationMirror getAnnotation(String type) {
        if (annotations == null) {
            annotations = JDTUtils.getAnnotations(annotationBindings);
        }
        return annotations.get(type);
    }

    @Override
    public TypeMirror getType() {
        if (type == null) {
            type = new JDTType(typeBinding);
        }
        return type; 
    }

    @Override
    public String getName() {
        if (name == null) {
            setName();
        }
        return name;
    }
    
    private void setName() {
        AnnotationMirror nameAnnotation = getAnnotation(Name.class.getName());
        if(nameAnnotation != null) {
            name = (String) nameAnnotation.getValue();
            return;
        }
        
        String baseName = toParameterName(typeBinding);
        int count = 0;
        String nameToReturn = baseName;
        for (VariableMirror parameter : methodMirror.getParameters()) {
            if (parameter.getName().equals(nameToReturn)) {
                count ++;
                nameToReturn = baseName + Integer.toString(count);
            }
        }
        name = nameToReturn;
    }
    
    private String toParameterName(TypeBinding parameterType) {
        String typeName = new String(parameterType.sourceName());
        StringTokenizer tokens = new StringTokenizer(typeName, "$.[]");
        String result = null;
        while (tokens.hasMoreTokens()) {
            result = tokens.nextToken();
        }
        if (typeName.endsWith("[]")) {
            result = result + "Array";
        }
        return toLowerCase(result.charAt(0)) + 
                result.substring(1);
    }
}
