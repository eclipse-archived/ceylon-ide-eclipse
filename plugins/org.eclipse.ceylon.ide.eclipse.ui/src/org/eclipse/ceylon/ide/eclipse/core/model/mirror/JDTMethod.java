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

import static org.eclipse.ceylon.ide.eclipse.core.model.LookupEnvironmentUtilities.doWithMethodBinding;
import static org.eclipse.ceylon.ide.eclipse.core.model.LookupEnvironmentUtilities.doWithResolvedType;
import static java.lang.Character.toLowerCase;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

import org.eclipse.ceylon.common.JVMModuleUtil;
import org.eclipse.ceylon.compiler.java.metadata.Ignore;
import org.eclipse.ceylon.compiler.java.metadata.Name;
import org.eclipse.ceylon.ide.eclipse.core.model.LookupEnvironmentUtilities.ActionOnMethodBinding;
import org.eclipse.ceylon.ide.eclipse.core.model.LookupEnvironmentUtilities.ActionOnResolvedType;
import org.eclipse.ceylon.model.loader.AbstractModelLoader;
import org.eclipse.ceylon.model.loader.ModelResolutionException;
import org.eclipse.ceylon.model.loader.mirror.AnnotationMirror;
import org.eclipse.ceylon.model.loader.mirror.ClassMirror;
import org.eclipse.ceylon.model.loader.mirror.MethodMirror;
import org.eclipse.ceylon.model.loader.mirror.TypeMirror;
import org.eclipse.ceylon.model.loader.mirror.TypeParameterMirror;
import org.eclipse.ceylon.model.loader.mirror.VariableMirror;

public class JDTMethod implements MethodMirror, IBindingProvider {

    private static final short IS_OVERRIDING_MASK = 1;
    private static final short IS_OVERRIDING_SET_MASK = 2;
    private static final short IS_OVERLOADING_MASK = 4;
    private static final short IS_OVERLOADING_SET_MASK = 8;
    private static final short IS_CONSTRUCTOR_MASK = 16;
    private static final short IS_STATIC_INIT_MASK = 32;
    private static final short IS_DECLARED_VOID_MASK = 64;
    private static final short IS_VARIADIC_MASK = 128;
    private static final short IS_DEFAULT_MASK = 256;
    private static final short IS_DEFAULT_METHOD_MASK = 512;

    // A bit field that allows us to save memory by using the masks above
    private short properties = 0;

	private Reference<MethodBinding> bindingRef;
    private Map<String, AnnotationMirror> annotations;
    private String name;
    private List<VariableMirror> parameters;
    private TypeMirror returnType;
    private List<TypeParameterMirror> typeParameters;
    private JDTClass enclosingClass;
    private int modifiers;
    private char[] bindingKey;
    private String readableName;
    
    private static final Map<String, AnnotationMirror> noAnnotations = Collections.emptyMap();

    public JDTMethod(JDTClass enclosingClass, MethodBinding method) {
        this.enclosingClass = enclosingClass;
        bindingRef = new SoftReference<MethodBinding>(method);
        name = new String(method.selector);
        readableName = new String(method.readableName());
        modifiers = method.modifiers;
        if (method.isConstructor()) {
        	set(IS_CONSTRUCTOR_MASK);
        }
        if (method.selector == TypeConstants.CLINIT) { // TODO : check if it is right
        	set(IS_STATIC_INIT_MASK);
        }
        if (method.returnType.id == TypeIds.T_void) {
        	set(IS_DECLARED_VOID_MASK);
        }
        if (method.isVarargs()) {
        	set(IS_VARIADIC_MASK);
        }
        if (method.getDefaultValue()!=null) {
        	set(IS_DEFAULT_MASK);
        }
        if (method.isDefaultMethod()) {
        	set(IS_DEFAULT_METHOD_MASK);
        }
        bindingKey = method.computeUniqueKey();
        if (method instanceof ProblemMethodBinding) {
            annotations = new HashMap<>();
            parameters = Collections.emptyList();
            returnType = JDTType.UNKNOWN_TYPE;
            typeParameters = Collections.emptyList();
            set(IS_OVERRIDING_SET_MASK);
            set(IS_OVERLOADING_SET_MASK);
        }
    }

    @Override
    public AnnotationMirror getAnnotation(String type) {
        retrieveAnnotations();
        return annotations.get(type);
    }

    private void retrieveAnnotations() {
        if (annotations == null) {
            doWithBindings(new ActionOnMethodBinding() {
                @Override
                public void doWithBinding(IType declaringClassModel,
                        ReferenceBinding declaringClass,
                        MethodBinding method) {
                    Map<String, AnnotationMirror> annots = JDTUtils.getAnnotations(method.getAnnotations());
                    if (annots.isEmpty()) {
                        annotations = noAnnotations;
                    } else {
                        annotations = annots;
                    }
                }
            });
        }
    }
    
    @Override
    public Set<String> getAnnotationNames() {
        retrieveAnnotations();
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
    public boolean isConstructor() {
        return isSet(IS_CONSTRUCTOR_MASK);
    }

    @Override
    public boolean isStaticInit() {
        return isSet(IS_STATIC_INIT_MASK);
    }

    @Override
    public List<VariableMirror> getParameters() {
        if (parameters == null) {
            doWithBindings(new ActionOnMethodBinding() {
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
                
                @Override
                public void doWithBinding(IType declaringClassModel,
                        ReferenceBinding declaringClassBinding, MethodBinding methodBinding) {
                    TypeBinding[] parameterBindings;
                    AnnotationBinding[][] parameterAnnotationBindings;
                    parameterBindings = ((MethodBinding)methodBinding).parameters;
                    String[] parameterNames = null;
                    parameterAnnotationBindings = ((MethodBinding)methodBinding).getParameterAnnotations();
                    if (parameterAnnotationBindings == null) {
                        parameterAnnotationBindings = new AnnotationBinding[parameterBindings.length][];
                        for (int i=0; i<parameterAnnotationBindings.length; i++) {
                            parameterAnnotationBindings[i] = new AnnotationBinding[0];
                        }
                    }
                    parameters = new ArrayList<VariableMirror>(parameterBindings.length);
                    List<String> givenNames = new ArrayList<>(parameterBindings.length);
                    for(int i=0;i<parameterBindings.length;i++) {
                        Map<String, AnnotationMirror> parameterAnnotations = JDTUtils.getAnnotations(parameterAnnotationBindings[i]);
                        String parameterName;
                        AnnotationMirror nameAnnotation = parameterAnnotations.get(Name.class.getName());
                        AnnotationMirror ignoredAnnotation = parameterAnnotations.get(Ignore.class.getName());
                        TypeBinding parameterTypeBinding = parameterBindings[i];
                        if(nameAnnotation != null) {
                            parameterName = (String) nameAnnotation.getValue();
                            givenNames.add(parameterName);
                        } else {
                            String baseName = null;
                            if (ignoredAnnotation == null) {
                                if (parameterNames == null) {
                                    try {
                                        for (IMethod imethod : declaringClassModel.getMethods()) {
                                            if (new String(methodBinding.signature()).equals(imethod.getSignature())) {
                                                parameterNames = imethod.getParameterNames();
                                                break;
                                            }
                                        }
                                    } catch (JavaModelException e) {
                                    }
                                    if (parameterNames == null) {
                                        parameterNames = new String[0];
                                    }
                                }
                                if (parameterNames.length > i) {
                                    baseName = parameterNames[i];
                                }
                            }
                            if (baseName == null || baseName.isEmpty()){
                                baseName = toParameterName(parameterTypeBinding);
                            }
                            int count = 0;
                            String nameToReturn = baseName;
                            for (String givenName : givenNames) {
                                if (givenName.equals(nameToReturn)) {
                                    count ++;
                                    nameToReturn = baseName + Integer.toString(count);
                                }
                            }
                            parameterName = nameToReturn;
                            givenNames.add(parameterName);
                            if (JVMModuleUtil.isJavaKeyword(parameterName)) {
                                parameterName = parameterName + "_";
                            }
                        }
                        parameters.add(new JDTVariable(parameterName, JDTType.newJDTType(parameterTypeBinding), parameterAnnotations));
                    }
                }
            });
        }
        
        return parameters;
    }

    @Override
    public boolean isAbstract() {
        return (this.modifiers & ClassFileConstants.AccAbstract) != 0;
    }

    @Override
    public boolean isFinal() {
        return (this.modifiers & ClassFileConstants.AccFinal) != 0;
    }

    @Override
    public TypeMirror getReturnType() {
        if (returnType == null) {
            doWithBindings(new ActionOnMethodBinding() {
                @Override
                public void doWithBinding(IType declaringClassModel,
                        ReferenceBinding declaringClassBinding, MethodBinding methodBinding) {
                    returnType = JDTType.newJDTType(methodBinding.returnType);
                }
            });
        }
        return returnType;
    }

    @Override
    public List<TypeParameterMirror> getTypeParameters() {
        if (typeParameters == null) {
            doWithBindings(new ActionOnMethodBinding() {
                @Override
                public void doWithBinding(IType declaringClassModel,
                        ReferenceBinding declaringClassBinding, MethodBinding methodBinding) {
                    TypeVariableBinding[] jdtTypeParameters = methodBinding.typeVariables();
                    typeParameters = new ArrayList<TypeParameterMirror>(jdtTypeParameters.length);
                    for(TypeVariableBinding jdtTypeParameter : jdtTypeParameters)
                        typeParameters.add(new JDTTypeParameter(jdtTypeParameter));
                }
            });
        }
        return typeParameters;
    }

    public boolean isOverridingMethod() {
        if (!isSet(IS_OVERRIDING_SET_MASK)) {
    		doWithBindings(new ActionOnMethodBinding() {
                @Override
                public void doWithBinding(IType declaringClassModel,
                        ReferenceBinding declaringClass,
                        MethodBinding method) {

                    if (CharOperation.equals(declaringClass.readableName(), "ceylon.language.Identifiable".toCharArray())) {
                        if ("equals".equals(name) 
                                || "hashCode".equals(name)) {
                            set(IS_OVERRIDING_MASK);
                            return;
                        }
                    }
                    if (CharOperation.equals(declaringClass.readableName(), "ceylon.language.Object".toCharArray())) {
                        if ("equals".equals(name) 
                                || "hashCode".equals(name)
                                || "toString".equals(name)) {
                            //isOverriding = false;
                            return;
                        }
                    }
                    
                    // try the superclass first
                    if (isDefinedInSuperClass(declaringClass, method)) {
                    	set(IS_OVERRIDING_MASK);
                    } 
                    if (isDefinedInSuperInterfaces(declaringClass, method)) {
                    	set(IS_OVERRIDING_MASK);
                    }
                }
            });
            set(IS_OVERRIDING_SET_MASK);
        }
        return isSet(IS_OVERRIDING_MASK);
    }

    private void doWithBindings(final ActionOnMethodBinding action) {
        final IType declaringClassModel = enclosingClass.getType();
        if (!doWithMethodBinding(declaringClassModel, bindingRef.get(), action)) {
            doWithResolvedType(declaringClassModel, new ActionOnResolvedType() {
                @Override
                public void doWithBinding(ReferenceBinding declaringClass) {
                    MethodBinding method = null;
                    for (MethodBinding m : declaringClass.methods()) {
                        if (CharOperation.equals(m.computeUniqueKey(), bindingKey)) {
                            method = m;
                            break;
                        }
                    }
                    if (method == null) {
                        throw new ModelResolutionException("Function '" + readableName + "' not found in the binding of class '" + declaringClassModel.getFullyQualifiedName() + "'");
                    }

                    bindingRef = new SoftReference<MethodBinding>(method);
                    action.doWithBinding(declaringClassModel, declaringClass, method);
                }
            });
        }
    }
    
    public boolean isOverloadingMethod() {
        if (!isSet(IS_OVERLOADING_SET_MASK)) {
            doWithBindings(new ActionOnMethodBinding() {
                @Override
                public void doWithBinding(IType declaringClassModel,
                        ReferenceBinding declaringClass,
                        MethodBinding method) {

                    // Exception has a pretend supertype of Object, unlike its Java supertype of java.lang.RuntimeException
                    // so we stop there for it, especially since it does not have any overloading
                    if(CharOperation.equals(declaringClass.qualifiedSourceName(), "ceylon.language.Exception".toCharArray())) {
                        //isOverloading = false;
                        return;
                    }

                    // try the superclass first
                    if (isOverloadingInSuperClasses(declaringClass, method)) {
                    	set(IS_OVERLOADING_MASK);
                    } 
                    if (isOverloadingInSuperInterfaces(declaringClass, method)) {
                    	set(IS_OVERLOADING_MASK);
                    }
                }
            });
            set(IS_OVERLOADING_SET_MASK);
        }
        return isSet(IS_OVERLOADING_MASK);
    }

    public static boolean ignoreMethodInAncestorSearch(MethodBinding methodBinding) {
        String name = CharOperation.charToString(methodBinding.selector);
        if(name.equals("finalize")
                || name.equals("clone")){
            if(methodBinding.declaringClass != null && CharOperation.toString(methodBinding.declaringClass.compoundName).equals("java.lang.Object")) {
                return true;
            }
        }
        // skip ignored methods too
        if(JDTUtils.hasAnnotation(methodBinding, AbstractModelLoader.CEYLON_IGNORE_ANNOTATION)) {
            return true;
        }
        return false;
    }
    
    private boolean isDefinedInType(ReferenceBinding superClass, MethodBinding method) {
        MethodVerifier methodVerifier = superClass.getPackage().environment.methodVerifier();
        for (MethodBinding inheritedMethod : superClass.methods()) {
            // skip ignored methods
            if(ignoreMethodInAncestorSearch(inheritedMethod)) {
                continue;
            }

            if (methodVerifier.doesMethodOverride(method, inheritedMethod)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOverloadingInType(ReferenceBinding superClass, MethodBinding method) {
        MethodVerifier methodVerifier = superClass.getPackage().environment.methodVerifier();
        for (MethodBinding inheritedMethod : superClass.methods()) {
            if(inheritedMethod.isPrivate()
                    || inheritedMethod.isStatic()
                    || inheritedMethod.isConstructor()
                    || inheritedMethod.isBridge()
                    || inheritedMethod.isSynthetic()
                    || !Arrays.equals(inheritedMethod.constantPoolName(), method.selector))
                continue;

            // skip ignored methods
            if(ignoreMethodInAncestorSearch(inheritedMethod)) {
                continue;
            }

            // if it does not override it and has the same name, it's overloading
            if (!methodVerifier.doesMethodOverride(method, inheritedMethod)) {
                return true;
            }
        }
        return false;
    }

    boolean isDefinedInSuperClass(ReferenceBinding declaringClass, MethodBinding method) {
        ReferenceBinding superClass = declaringClass.superclass();
        if (superClass == null) {
            return false;
        }

        superClass = JDTUtils.inferTypeParametersFromSuperClass(declaringClass,
                superClass);
        
        if (isDefinedInType(superClass, method)) {
            return true;
        }
        if (isDefinedInSuperInterfaces(superClass, method)) {
            return true;
        }
        return isDefinedInSuperClass(superClass, method);
    }

    boolean isDefinedInSuperInterfaces(ReferenceBinding declaringType, MethodBinding method) {
        
        ReferenceBinding[] superInterfaces = declaringType.superInterfaces();
        if (superInterfaces == null) {
            return false;
        }
        
        for (ReferenceBinding superInterface : superInterfaces) {
            if (isDefinedInType(superInterface, method)) {
                return true;
            }
            if (isDefinedInSuperInterfaces(superInterface, method)) {
                return true;
            }
        }
        return false;
    }

    boolean isOverloadingInSuperClasses(ReferenceBinding declaringClass, MethodBinding method) {
        ReferenceBinding superClass = declaringClass.superclass();
        if (superClass == null) {
            return false;
        }

        // Exception has a pretend supertype of Object, unlike its Java supertype of java.lang.RuntimeException
        // so we stop there for it, especially since it does not have any overloading
        if(CharOperation.equals(superClass.qualifiedSourceName(), "ceylon.language.Exception".toCharArray()))
            return false;

        superClass = JDTUtils.inferTypeParametersFromSuperClass(declaringClass,
                superClass);
        
        if (isOverloadingInType(superClass, method)) {
            return true;
        }
        return isOverloadingInSuperClasses(superClass, method);
    }

    boolean isOverloadingInSuperInterfaces(ReferenceBinding declaringType, MethodBinding method) {
        ReferenceBinding[] superInterfaces = declaringType.superInterfaces();
        if (superInterfaces == null) {
            return false;
        }
        
        for (ReferenceBinding superInterface : superInterfaces) {
            if (isOverloadingInType(superInterface, method)) {
                return true;
            }
            if (isOverloadingInSuperInterfaces(superInterface, method)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isProtected() {
        return (this.modifiers & ClassFileConstants.AccProtected) != 0;
    }

    @Override
    public boolean isDefaultAccess() {
        return !isPublic() && !isProtected() && !isPrivate();
    }
    
    public final boolean isPrivate() {
        return (this.modifiers & ClassFileConstants.AccPrivate) != 0;
    }

    public boolean isDeprecated() {
        return (this.modifiers & ClassFileConstants.AccDeprecated) != 0;
    }
    
    @Override
    public boolean isDeclaredVoid() {
        return isSet(IS_DECLARED_VOID_MASK);
    }

    @Override
    public boolean isVariadic() {
        return isSet(IS_VARIADIC_MASK);
    }

    @Override
    public boolean isDefault() {
        return isSet(IS_DEFAULT_MASK);
    }
    
    @Override
    public char[] getBindingKey() {
        return bindingKey;
    }

    @Override
    public ClassMirror getEnclosingClass() {
        return enclosingClass;
    }

    @Override
    public boolean isDefaultMethod() {
        return isSet(IS_DEFAULT_METHOD_MASK);
    }
    
    
    private boolean isSet(int mask) {
    	return (properties & mask) == mask;
    }
    
    private void set(int mask) {
    	properties |= mask;
    }
}
