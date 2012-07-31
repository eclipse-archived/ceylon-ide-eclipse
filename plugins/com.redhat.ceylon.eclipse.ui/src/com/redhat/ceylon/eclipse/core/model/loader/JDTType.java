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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.lang.model.type.TypeKind;

import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

import com.redhat.ceylon.compiler.loader.mirror.TypeMirror;

public class JDTType implements TypeMirror {

    private TypeBinding type;
    private String qualifiedName;
    private List<TypeMirror> typeArguments;
    private TypeKind typeKind;
    private TypeMirror componentType;
    private boolean upperBoundSet = false;
    private boolean lowerBoundSet = false;
    private TypeMirror upperBound;
    private TypeMirror lowerBound;
    

    public JDTType(TypeBinding type) {
        this.type = type;
    }

    @Override
    public String getQualifiedName() {
        if (qualifiedName == null) {
        	// type params are not qualified
        	if(type instanceof TypeVariableBinding)
        		qualifiedName = new String(type.qualifiedSourceName());
        	else
        		qualifiedName = JDTUtils.getFullyQualifiedName(type);
        }
        return qualifiedName;
    }

    @Override
    public List<TypeMirror> getTypeArguments() {
        if (typeArguments == null) {
            if(type instanceof ParameterizedTypeBinding && ! (type instanceof RawTypeBinding)){
                TypeBinding[] javaTypeArguments = ((ParameterizedTypeBinding)type).arguments;
                if (javaTypeArguments == null) {
                    javaTypeArguments = new TypeBinding[0];
                }
                typeArguments = new ArrayList<TypeMirror>(javaTypeArguments.length);
                for(TypeBinding typeArgument : javaTypeArguments)
                    typeArguments.add(typeArgument != type ? new JDTType(typeArgument) : this);
            }
            else  {
                return Collections.emptyList();
            }
        }
        return typeArguments;
    }

    @Override
    public TypeKind getKind() {
        if (typeKind == null) {
            return findKind();
        }
        return typeKind;
    }

    private TypeKind findKind() {
        if(type instanceof ArrayBinding)
            return TypeKind.ARRAY;
        if(type instanceof TypeVariableBinding)
            return TypeKind.TYPEVAR;
        if(type instanceof WildcardBinding)
            return TypeKind.WILDCARD;
        if(type instanceof BaseTypeBinding){
            switch(type.id) {
            case TypeIds.T_boolean : return TypeKind.BOOLEAN;
            case TypeIds.T_byte : return TypeKind.BYTE;
            case TypeIds.T_char : return TypeKind.CHAR;
            case TypeIds.T_short : return TypeKind.SHORT;
            case TypeIds.T_int : return TypeKind.INT;
            case TypeIds.T_long : return TypeKind.LONG;
            case TypeIds.T_float : return TypeKind.FLOAT;
            case TypeIds.T_double : return TypeKind.DOUBLE;
            case TypeIds.T_void : return TypeKind.VOID;
            }
        }
        if(type instanceof ReferenceBinding)
            return TypeKind.DECLARED;
        throw new RuntimeException("Unknown type: "+type);
    }

    @Override
    public TypeMirror getComponentType() {
        if (componentType == null) {
            TypeBinding jdtComponentType = ((ArrayBinding)type).leafComponentType;
            componentType = new JDTType(jdtComponentType);
        }
        return componentType;
    }

    @Override
    public boolean isPrimitive() {
        return type.isBaseType();
    }

    @Override
    public TypeMirror getUpperBound() {
        if (!upperBoundSet) {
            if (type.isWildcard()) {
                WildcardBinding wildcardBinding = (WildcardBinding) type;
                if (wildcardBinding.boundKind == Wildcard.EXTENDS) {
                    TypeBinding upperBoundBinding = wildcardBinding.bound;
                    if (upperBoundBinding != null) {
                        upperBound = new JDTType(upperBoundBinding);
                    }
                }
            }
            upperBoundSet = true;
        }
        return upperBound;
    }

    @Override
    public TypeMirror getLowerBound() {
        if (!lowerBoundSet) {
            if (type.isWildcard()) {
                WildcardBinding wildcardBinding = (WildcardBinding) type;
                if (wildcardBinding.boundKind == Wildcard.SUPER) {
                    TypeBinding lowerBoundBinding = wildcardBinding.bound;
                    if (lowerBoundBinding != null) {
                        lowerBound = new JDTType(lowerBoundBinding);
                    }
                }
            }
            lowerBoundSet = true;
        }
        return lowerBound;
    }

	@Override
	public boolean isRaw() {
		// TODO FIX THIS!!!!!!!!!!
		return false;
	}
}
