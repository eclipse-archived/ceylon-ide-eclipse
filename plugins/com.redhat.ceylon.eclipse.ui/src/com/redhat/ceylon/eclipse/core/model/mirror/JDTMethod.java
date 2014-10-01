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

import static java.lang.Character.toLowerCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

import com.redhat.ceylon.compiler.java.metadata.Name;
import com.redhat.ceylon.compiler.loader.AbstractModelLoader;
import com.redhat.ceylon.compiler.loader.ModelResolutionException;
import com.redhat.ceylon.compiler.loader.mirror.AnnotationMirror;
import com.redhat.ceylon.compiler.loader.mirror.ClassMirror;
import com.redhat.ceylon.compiler.loader.mirror.MethodMirror;
import com.redhat.ceylon.compiler.loader.mirror.TypeMirror;
import com.redhat.ceylon.compiler.loader.mirror.TypeParameterMirror;
import com.redhat.ceylon.compiler.loader.mirror.VariableMirror;
import com.redhat.ceylon.eclipse.core.model.JDTModelLoader;

public class JDTMethod implements MethodMirror, IBindingProvider {

    private Map<String, AnnotationMirror> annotations;
    private String name;
    private List<VariableMirror> parameters;
    private JDTType returnType;
    private List<TypeParameterMirror> typeParameters;
    Boolean isOverriding;
    private Boolean isOverloading;
    private JDTClass enclosingClass;
    private boolean isStatic;
    private boolean isPublic;
    private boolean isConstructor;
    private boolean isStaticInit;
    private boolean isAbstract;
    private boolean isFinal;
    private char[] bindingKey;
    private String readableName;
    private boolean isProtected;
    private boolean isDefaultAccess;
    private boolean isDeclaredVoid;
    private boolean isVariadic;
    private boolean isDefault;

    public JDTMethod(JDTClass enclosingClass, MethodBinding method) {
        this.enclosingClass = enclosingClass;
        name = new String(method.selector);
        readableName = new String(method.readableName());
        isStatic = method.isStatic();
        isPublic = method.isPublic();
        isConstructor = method.isConstructor();
        isStaticInit = method.selector == TypeConstants.CLINIT; // TODO : check if it is right
        isAbstract = method.isAbstract();
        isFinal = method.isFinal();
        isProtected  = method.isProtected();
        isDefaultAccess = method.isDefault();
        isDeclaredVoid = method.returnType.id == TypeIds.T_void;
        isVariadic = method.isVarargs();
        isDefault = method.getDefaultValue()!=null;
        bindingKey = method.computeUniqueKey();
    }

    
    
    @Override
    public AnnotationMirror getAnnotation(String type) {
        if (annotations == null) {
            doWithBindings(new ActionWithBindings() {
                @Override
                public void doAction(IType declaringClassModel,
                        ReferenceBinding declaringClass,
                        MethodBinding method) {
                    annotations = JDTUtils.getAnnotations(method.getAnnotations());
                }
            });
        }
        return annotations.get(type);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public boolean isPublic() {
        return isPublic;
    }

    @Override
    public boolean isConstructor() {
        return isConstructor;
    }

    @Override
    public boolean isStaticInit() {
        return isStaticInit;
    }

    @Override
    public List<VariableMirror> getParameters() {
        if (parameters == null) {
            doWithBindings(new ActionWithBindings() {
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
                public void doAction(IType declaringClassModel,
                        ReferenceBinding declaringClassBinding, MethodBinding methodBinding) {
                    TypeBinding[] parameterBindings;
                    AnnotationBinding[][] parameterAnnotationBindings;
                    parameterBindings = ((MethodBinding)methodBinding).parameters;
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
                        AnnotationMirror nameAnnotation = getAnnotation(Name.class.getName());
                        TypeBinding parameterTypeBinding = parameterBindings[i];
                        if(nameAnnotation != null) {
                            parameterName = (String) nameAnnotation.getValue();
                        } else {
                            String baseName = toParameterName(parameterTypeBinding);
                            int count = 0;
                            String nameToReturn = baseName;
                            for (String givenName : givenNames) {
                                if (givenName.equals(nameToReturn)) {
                                    count ++;
                                    nameToReturn = baseName + Integer.toString(count);
                                }
                            }
                            parameterName = nameToReturn;
                        }
                        givenNames.add(parameterName);
                        parameters.add(new JDTVariable(parameterName, new JDTType(parameterTypeBinding), parameterAnnotations));
                    }
                }
            });
        }
        
        return parameters;
    }

    @Override
    public boolean isAbstract() {
        return isAbstract;
    }

    @Override
    public boolean isFinal() {
        return isFinal;
    }

    @Override
    public TypeMirror getReturnType() {
        if (returnType == null) {
            doWithBindings(new ActionWithBindings() {
                @Override
                public void doAction(IType declaringClassModel,
                        ReferenceBinding declaringClassBinding, MethodBinding methodBinding) {
                    returnType = new JDTType(methodBinding.returnType);
                }
            });
        }
        return returnType;
    }

    @Override
    public List<TypeParameterMirror> getTypeParameters() {
        if (typeParameters == null) {
            doWithBindings(new ActionWithBindings() {
                @Override
                public void doAction(IType declaringClassModel,
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
        if (isOverriding == null) {
            isOverriding = false;

            doWithBindings(new ActionWithBindings() {
                @Override
                public void doAction(IType declaringClassModel,
                        ReferenceBinding declaringClass,
                        MethodBinding method) {

                    if (CharOperation.equals(declaringClass.readableName(), "ceylon.language.Identifiable".toCharArray())) {
                        if ("equals".equals(name) 
                                || "hashCode".equals(name)) {
                            isOverriding = true;
                            return;
                        }
                    }
                    if (CharOperation.equals(declaringClass.readableName(), "ceylon.language.Object".toCharArray())) {
                        if ("equals".equals(name) 
                                || "hashCode".equals(name)
                                || "toString".equals(name)) {
                            isOverriding = false;
                            return;
                        }
                    }
                    
                    // try the superclass first
                    if (isDefinedInSuperClasses(declaringClass, method)) {
                        isOverriding = true;
                    } 
                    if (isDefinedInSuperInterfaces(declaringClass, method)) {
                        isOverriding = true;
                    }
                }
            });
        }
        return isOverriding.booleanValue();
    }

    private static interface ActionWithBindings {
        void doAction(IType declaringClassModel, ReferenceBinding declaringClassBinding, MethodBinding methodBinding);
    }
    
    private void doWithBindings(final ActionWithBindings action) {
        final IType declaringClassModel = enclosingClass.getType();

        JDTModelLoader.doWithResolvedType(declaringClassModel, new JDTModelLoader.ActionOnResolvedType() {
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
                    throw new ModelResolutionException("Method '" + readableName + "' not found in the binding of class '" + declaringClassModel.getFullyQualifiedName() + "'");
                }

                action.doAction(declaringClassModel, declaringClass, method);
            }
        });
    }
    
    public boolean isOverloadingMethod() {
        if (isOverloading == null) {
            isOverloading = Boolean.FALSE;

            doWithBindings(new ActionWithBindings() {
                @Override
                public void doAction(IType declaringClassModel,
                        ReferenceBinding declaringClass,
                        MethodBinding method) {

                    // Exception has a pretend supertype of Object, unlike its Java supertype of java.lang.RuntimeException
                    // so we stop there for it, especially since it does not have any overloading
                    if(CharOperation.equals(declaringClass.qualifiedSourceName(), "ceylon.language.Exception".toCharArray())) {
                        isOverloading = false;
                        return;
                    }

                    // try the superclass first
                    if (isOverloadingInSuperClasses(declaringClass, method)) {
                        isOverloading = Boolean.TRUE;
                    } 
                    if (isOverloadingInSuperInterfaces(declaringClass, method)) {
                        isOverloading = Boolean.TRUE;
                    }
                }
            });
        }
        return isOverloading.booleanValue();
    }

    private boolean ignoreMethodInAncestorSearch(MethodBinding methodBinding) {
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

    boolean isDefinedInSuperClasses(ReferenceBinding declaringClass, MethodBinding method) {
        ReferenceBinding superClass = declaringClass.superclass();
        if (superClass == null) {
            return false;
        }

        superClass = JDTUtils.inferTypeParametersFromSuperClass(declaringClass,
                superClass);
        
        if (isDefinedInType(superClass, method)) {
            return true;
        }
        return isDefinedInSuperClasses(superClass, method);
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
        return isProtected;
    }

    @Override
    public boolean isDefaultAccess() {
        return isDefaultAccess;
    }
    
    @Override
    public boolean isDeclaredVoid() {
        return isDeclaredVoid;
    }

    @Override
    public boolean isVariadic() {
        return isVariadic;
    }

    @Override
    public boolean isDefault() {
        return isDefault;
    }
    
    @Override
    public char[] getBindingKey() {
        return bindingKey;
    }

    @Override
    public ClassMirror getEnclosingClass() {
        return enclosingClass;
    }
}