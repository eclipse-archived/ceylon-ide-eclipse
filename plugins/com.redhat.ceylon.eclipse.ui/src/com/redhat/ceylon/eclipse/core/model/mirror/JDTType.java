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

import static com.redhat.ceylon.eclipse.core.model.LookupEnvironmentUtilities.toType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.UnresolvedReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

import com.redhat.ceylon.ide.common.model.UnknownTypeMirror;
import com.redhat.ceylon.model.loader.mirror.ClassMirror;
import com.redhat.ceylon.model.loader.mirror.TypeKind;
import com.redhat.ceylon.model.loader.mirror.TypeMirror;
import com.redhat.ceylon.model.loader.mirror.TypeParameterMirror;

public class JDTType implements TypeMirror {
    public static final TypeMirror UNKNOWN_TYPE = new UnknownTypeMirror();
    
    private String qualifiedName;
    private List<TypeMirror> typeArguments;
    private TypeKind typeKind;
    private TypeMirror componentType;
    private TypeMirror upperBound;
    private TypeMirror lowerBound;
    private JDTClass declaredClass;
    private JDTTypeParameter typeParameter;
    private boolean isPrimitive;
    private boolean isRaw;

    private TypeMirror qualifyingType;
    
    public static TypeMirror newJDTType(TypeBinding type) {
        return newJDTType(type, new IdentityHashMap<TypeBinding, JDTType>());
    }

    private static TypeMirror unknownTypeMirror(TypeBinding type) {
    	try {
    		if (type.qualifiedSourceName() != null) {
                return new UnknownTypeMirror(JDTUtils.getFullyQualifiedName(type));
        	} 
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
        return UNKNOWN_TYPE;
    }
    
    public static TypeMirror newJDTType(TypeBinding type, IdentityHashMap<TypeBinding, JDTType> originatingTypes) {
		if (type instanceof UnresolvedReferenceBinding) {
            type = BinaryTypeBinding.resolveType(type, type.getPackage().environment, false);
        }

        if (type instanceof MissingTypeBinding) {
        	return unknownTypeMirror(type);
        }

        if (type instanceof ParameterizedTypeBinding) {
        	TypeBinding genericType = ((ParameterizedTypeBinding)type).genericType();
            if (genericType instanceof MissingTypeBinding) {
            	return unknownTypeMirror(genericType);
            }
        }

        if (type instanceof ProblemReferenceBinding) {
        	ProblemReferenceBinding prb = (ProblemReferenceBinding) type;
        	TypeBinding closestMatch = prb.closestMatch();
        	if (closestMatch != null && closestMatch != type) {
        		return newJDTType(closestMatch, originatingTypes);
        	}
            return unknownTypeMirror(type);
        }
        
		return new JDTType(type, originatingTypes);
	}

    public static TypeMirror toTypeMirror(TypeBinding typeBinding, TypeBinding currentBinding, JDTType currentType, IdentityHashMap<TypeBinding, JDTType> originatingTypes) {
        if (typeBinding == currentBinding && currentType != null) {
            return currentType;
        }
        
        JDTType originatingType = originatingTypes.get(typeBinding);
        if (originatingType != null) {
            return originatingType;
        }
        return newJDTType(typeBinding, originatingTypes);
    }
    
    public JDTType(TypeBinding type, IdentityHashMap<TypeBinding, JDTType> originatingTypes) {
        originatingTypes.put(type, this);

        if (type instanceof UnresolvedReferenceBinding) {
            type = BinaryTypeBinding.resolveType(type, type.getPackage().environment, false);
        }
        
        // type params are not qualified
        if(type instanceof TypeVariableBinding)
            qualifiedName = new String(type.qualifiedSourceName());
        else
            qualifiedName = JDTUtils.getFullyQualifiedName(type);

        typeKind = findKind(type);

        isPrimitive = type.isBaseType() && 
                type.id != TypeIds.T_void && 
                        type.id != TypeIds.T_null;
        
        isRaw = type.isRawType();

        if(type instanceof ParameterizedTypeBinding && ! (type instanceof RawTypeBinding)){
            TypeBinding[] javaTypeArguments = ((ParameterizedTypeBinding)type).arguments;
            if (javaTypeArguments == null) {
                javaTypeArguments = new TypeBinding[0];
            }
            typeArguments = new ArrayList<TypeMirror>(javaTypeArguments.length);
            for(TypeBinding typeArgument : javaTypeArguments)
                typeArguments.add(toTypeMirror(typeArgument, type, this, originatingTypes));
        }
        else  {
            typeArguments = Collections.emptyList();
        }

        if(type.enclosingType() instanceof ParameterizedTypeBinding){
            boolean isStatic = false;
            if (type instanceof ReferenceBinding) {
                ReferenceBinding referenceBinding = (ReferenceBinding) type;
                if ((referenceBinding.modifiers & ClassFileConstants.AccStatic) != 0) {
                    isStatic = true;
                }
            }
            if (!isStatic) {
                qualifyingType = toTypeMirror(type.enclosingType(), type, this, originatingTypes);
            }
        }
        
        if (type instanceof ArrayBinding) {
            TypeBinding jdtComponentType = ((ArrayBinding)type).elementsType();
            componentType = toTypeMirror(jdtComponentType, type, this, originatingTypes);
        } else {
            componentType = null;
        }

        if (type.isWildcard()) {
            WildcardBinding wildcardBinding = (WildcardBinding) type;
            if (wildcardBinding.boundKind == Wildcard.EXTENDS) {
                TypeBinding upperBoundBinding = wildcardBinding.bound;
                if (upperBoundBinding != null) {
                    upperBound = toTypeMirror(upperBoundBinding, type, this, originatingTypes);
                }
            }
        } else if (type.isTypeVariable()){
            TypeVariableBinding typeVariableBinding = (TypeVariableBinding) type;
            TypeBinding boundBinding = typeVariableBinding.firstBound; // TODO : we should confirm this
            if (boundBinding != null) {
                upperBound = toTypeMirror(boundBinding, type, this, originatingTypes);
            }
        } else {
            upperBound = null;
        }

        if (type.isWildcard()) {
            WildcardBinding wildcardBinding = (WildcardBinding) type;
            if (wildcardBinding.boundKind == Wildcard.SUPER) {
                TypeBinding lowerBoundBinding = wildcardBinding.bound;
                if (lowerBoundBinding != null) {
                    lowerBound = toTypeMirror(lowerBoundBinding, type, this, originatingTypes);
                }
            }
        }
        
        if(type instanceof ParameterizedTypeBinding ||
                type instanceof SourceTypeBinding ||
                type instanceof BinaryTypeBinding){
            ReferenceBinding refBinding = (ReferenceBinding) type;
            declaredClass = new JDTClass(refBinding, toType(refBinding));
        }

        if(type instanceof TypeVariableBinding){
            typeParameter = new JDTTypeParameter((TypeVariableBinding) type, this, originatingTypes);
        }
    }

    @Override
    public String getQualifiedName() {
        return qualifiedName;
    }

    @Override
    public List<TypeMirror> getTypeArguments() {
        return typeArguments;
    }

    @Override
    public TypeKind getKind() {
        return typeKind;
    }

    private TypeKind findKind(TypeBinding type) {
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
            case TypeIds.T_null : return TypeKind.NULL;
            }
        }
        if(type instanceof ReferenceBinding)
            return TypeKind.DECLARED;
        throw new RuntimeException("Unknown type: "+type);
    }

    @Override
    public TypeMirror getComponentType() {
        return componentType;
    }

    @Override
    public boolean isPrimitive() {
        return isPrimitive;
    }

    @Override
    public TypeMirror getUpperBound() {
        return upperBound;
    }

    @Override
    public TypeMirror getLowerBound() {
        return lowerBound;
    }

    @Override
    public boolean isRaw() {
        return isRaw;
    }

    @Override
    public ClassMirror getDeclaredClass() {
        return declaredClass;
    }

    @Override
    public TypeParameterMirror getTypeParameter() {
        return typeParameter;
    }

    @Override
    public TypeMirror getQualifyingType() {
        return qualifyingType;
    }
    
    @Override
    public String toString() {
        return "[JDTType: "+qualifiedName+"]";
    }
}
