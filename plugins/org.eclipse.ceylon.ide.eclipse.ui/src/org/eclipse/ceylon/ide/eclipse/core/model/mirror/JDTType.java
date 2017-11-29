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

import static org.eclipse.ceylon.ide.eclipse.core.model.LookupEnvironmentUtilities.toType;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;

import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.UnresolvedReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

import org.eclipse.ceylon.ide.eclipse.core.model.LookupEnvironmentUtilities;
import org.eclipse.ceylon.ide.eclipse.core.model.ModelLoaderNameEnvironment;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import org.eclipse.ceylon.ide.common.model.UnknownTypeMirror;
import org.eclipse.ceylon.model.loader.AbstractModelLoader;
import org.eclipse.ceylon.model.loader.mirror.ClassMirror;
import org.eclipse.ceylon.model.loader.mirror.FunctionalInterfaceType;
import org.eclipse.ceylon.model.loader.mirror.TypeKind;
import org.eclipse.ceylon.model.loader.mirror.TypeMirror;
import org.eclipse.ceylon.model.loader.mirror.TypeParameterMirror;

public class JDTType implements TypeMirror {
    public static final TypeMirror UNKNOWN_TYPE = new UnknownTypeMirror();
    
    private String qualifiedName;
    private List<TypeMirror> typeArguments;
    private TypeKind typeKind;
    private TypeMirror componentType;
    private TypeMirror upperBound;
    private TypeMirror lowerBound;
    private ClassMirror declaredClass;
    private JDTTypeParameter typeParameter;
    private boolean isPrimitive;
    private boolean isRaw;    
    private TypeMirror qualifyingType;
    private FunctionalInterfaceType functionalInterfaceType = null;
    
    public FunctionalInterfaceType getFunctionalInterfaceType() {
        return functionalInterfaceType;
    }

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

    static private LookupEnvironmentUtilities.Provider getModelLoader(TypeBinding binding) {
        PackageBinding pkgBinding = binding.getPackage();
        if (pkgBinding == null) {
            return null;
        }
        LookupEnvironment lookupEnv = pkgBinding.environment;
        INameEnvironment nameEnv = lookupEnv.nameEnvironment;
        if (! (nameEnv instanceof ModelLoaderNameEnvironment)) {
            return null;
        }
        IJavaProject javaProject = ((ModelLoaderNameEnvironment)nameEnv).getJavaProject();
        AbstractModelLoader modelLoader = modelJ2C().javaProjectModelLoader(javaProject);
        if (modelLoader instanceof LookupEnvironmentUtilities.Provider) {
            return (LookupEnvironmentUtilities.Provider) modelLoader;
        }
        return null;
    }
    
    static TypeMirror newJDTType(TypeBinding type, IdentityHashMap<TypeBinding, JDTType> originatingTypes) {
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
        
        TypeMirror typeMirror = null;
        char[] bindingKey = null;
        LookupEnvironmentUtilities.Provider modelLoader = getModelLoader(type);
        if (modelLoader != null) {
            bindingKey = type.computeUniqueKey();
            typeMirror = modelLoader.getCachedTypeMirror(bindingKey);
        }
        if (typeMirror == null) {
            typeMirror = createJDTType(type, originatingTypes);
            if (modelLoader != null) {
                modelLoader.cacheTypeMirror(bindingKey, typeMirror);
            }
        }
        return typeMirror;
    }

    private static TypeMirror createJDTType(TypeBinding type,
            IdentityHashMap<TypeBinding, JDTType> originatingTypes) {
        JDTType typeMirror = new JDTType();

        originatingTypes.put(type, typeMirror);

        if (type instanceof UnresolvedReferenceBinding) {
            type = BinaryTypeBinding.resolveType(type, type.getPackage().environment, false);
        }
        
        // type params are not qualified
        if(type instanceof TypeVariableBinding)
            typeMirror.qualifiedName = new String(type.qualifiedSourceName());
        else
            typeMirror.qualifiedName = JDTUtils.getFullyQualifiedName(type);

        typeMirror.typeKind = findKind(type);

        typeMirror.isPrimitive = type.isBaseType() && 
                type.id != TypeIds.T_void && 
                        type.id != TypeIds.T_null;
        
        typeMirror.isRaw = type.isRawType();

        if(type instanceof ParameterizedTypeBinding && ! (type instanceof RawTypeBinding)){
            TypeBinding[] javaTypeArguments = ((ParameterizedTypeBinding)type).typeArguments();
            if (javaTypeArguments == null) {
                javaTypeArguments = new TypeBinding[0];
            }
            typeMirror.typeArguments = new ArrayList<TypeMirror>(javaTypeArguments.length);
            for(TypeBinding typeArgument : javaTypeArguments)
                typeMirror.typeArguments.add(toTypeMirror(typeArgument, type, typeMirror, originatingTypes));
        }
        else  {
            typeMirror.typeArguments = Collections.emptyList();
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
                typeMirror.qualifyingType = toTypeMirror(type.enclosingType(), type, typeMirror, originatingTypes);
            }
        }
        
        if (type instanceof ArrayBinding) {
            TypeBinding jdtComponentType = ((ArrayBinding)type).elementsType();
            typeMirror.componentType = toTypeMirror(jdtComponentType, type, typeMirror, originatingTypes);
        } else {
            typeMirror.componentType = null;
        }

        if (type.isWildcard()) {
            WildcardBinding wildcardBinding = (WildcardBinding) type;
            if (wildcardBinding.boundKind == Wildcard.EXTENDS) {
                TypeBinding upperBoundBinding = wildcardBinding.bound;
                if (upperBoundBinding != null) {
                    typeMirror.upperBound = toTypeMirror(upperBoundBinding, type, typeMirror, originatingTypes);
                }
            }
        } else if (type.isTypeVariable()){
            TypeVariableBinding typeVariableBinding = (TypeVariableBinding) type;
            TypeBinding boundBinding = typeVariableBinding.firstBound; // TODO : we should confirm this
            if (boundBinding != null) {
                typeMirror.upperBound = toTypeMirror(boundBinding, type, typeMirror, originatingTypes);
            }
        } else {
            typeMirror.upperBound = null;
        }

        if (type.isWildcard()) {
            WildcardBinding wildcardBinding = (WildcardBinding) type;
            if (wildcardBinding.boundKind == Wildcard.SUPER) {
                TypeBinding lowerBoundBinding = wildcardBinding.bound;
                if (lowerBoundBinding != null) {
                    typeMirror.lowerBound = toTypeMirror(lowerBoundBinding, type, typeMirror, originatingTypes);
                }
            }
        }
        
        if(type instanceof ParameterizedTypeBinding) {
            ParameterizedTypeBinding refBinding = (ParameterizedTypeBinding) type;
            TypeBinding genericType = refBinding.genericType();
            if (genericType instanceof MissingTypeBinding) {
                return unknownTypeMirror(genericType);
            } else {
                typeMirror.declaredClass = new JDTClass(refBinding, toType(refBinding.genericType()));
            }
        } else if(type instanceof SourceTypeBinding ||
                type instanceof BinaryTypeBinding){
            ReferenceBinding refBinding = (ReferenceBinding) type;
            typeMirror.declaredClass = new JDTClass(refBinding, toType(refBinding));
        }

        if(type instanceof TypeVariableBinding){
            typeMirror.typeParameter = new JDTTypeParameter((TypeVariableBinding) type, typeMirror, originatingTypes);
        }
        
        if (type instanceof ReferenceBinding) {
            ReferenceBinding referenceBinding = (ReferenceBinding) type;
            PackageBinding p = referenceBinding.getPackage();
            if (p != null) {
                LookupEnvironment environment = p.environment;
                
                if (referenceBinding.isInterface()) {
                    try {
                        Scope scope = new CompilationUnitScope(
                                new CompilationUnitDeclaration(
                                     environment.problemReporter, 
                                    null, 
                                    0), environment);
                        MethodBinding method = referenceBinding.getSingleAbstractMethod(scope, true);
                        if (method != null &&
                            method.isValidBinding()) {
                            ReferenceBinding enclosingClass = method.declaringClass;
                            TypeBinding[] parameters = method.parameters;
                            ArrayList<TypeMirror> jdtTypes = new ArrayList<TypeMirror>();
                            if (parameters.length > 0) {
                                for (int i=0; i<parameters.length -1; i++) {
                                    jdtTypes.add(toTypeMirror(parameters[i], type, typeMirror, originatingTypes));
                                }
                                TypeBinding lastParameterBinding = parameters[parameters.length-1];
                                if (method.isVarargs() &&
                                    lastParameterBinding instanceof ArrayBinding) {
                                    jdtTypes.add(toTypeMirror(((ArrayBinding)lastParameterBinding).elementsType(), type, typeMirror, originatingTypes));
                                } else {
                                    jdtTypes.add(toTypeMirror(lastParameterBinding, type, typeMirror, originatingTypes));
                                }
                            }

                            typeMirror.functionalInterfaceType = new FunctionalInterfaceType(
                                new JDTMethod(new JDTClass(enclosingClass, LookupEnvironmentUtilities.toType(enclosingClass)), method),
                                toTypeMirror(method.returnType, type, typeMirror, originatingTypes),
                                jdtTypes,
                                method.isVarargs());
                        }
                    } catch(Exception e) {
                        CeylonPlugin.log(Status.ERROR, "Exception when trying to retrieve Functional interface of type" + referenceBinding.debugName() +
                                              "\n    -> functional interface search skipped:", e);
                    }
                }
            }
        }
        return typeMirror;
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

    private static TypeKind findKind(TypeBinding type) {
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
