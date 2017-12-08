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

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.env.IDependent;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

import static org.eclipse.ceylon.ide.eclipse.core.model.LookupEnvironmentUtilities.*;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;

import org.eclipse.ceylon.ide.eclipse.core.model.LookupEnvironmentUtilities;
import org.eclipse.ceylon.ide.eclipse.core.model.LookupEnvironmentUtilities.ActionOnClassBinding;
import org.eclipse.ceylon.ide.eclipse.core.model.LookupEnvironmentUtilities.ActionOnResolvedType;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import org.eclipse.ceylon.ide.common.model.unknownClassMirror_;
import org.eclipse.ceylon.ide.common.model.mirror.IdeClassMirror;
import org.eclipse.ceylon.model.loader.AbstractModelLoader;
import org.eclipse.ceylon.model.loader.ModelResolutionException;
import org.eclipse.ceylon.model.loader.NamingBase;
import org.eclipse.ceylon.model.loader.mirror.AnnotationMirror;
import org.eclipse.ceylon.model.loader.mirror.ClassMirror;
import org.eclipse.ceylon.model.loader.mirror.FieldMirror;
import org.eclipse.ceylon.model.loader.mirror.MethodMirror;
import org.eclipse.ceylon.model.loader.mirror.PackageMirror;
import org.eclipse.ceylon.model.loader.mirror.TypeMirror;
import org.eclipse.ceylon.model.loader.mirror.TypeParameterMirror;
import org.eclipse.ceylon.model.typechecker.model.Module;

public class JDTClass implements IdeClassMirror, IBindingProvider {
    public static final ClassMirror UNKNOWN_CLASS = unknownClassMirror_.get_();
    
    private static final short SUPERCLASS_MASK = 1;
    private static final short ENCLOSING_METHOD_MASK = 2;
    private static final short ENCLOSING_CLASS_MASK = 4;
    private static final short FUNCTIONAL_INTERFACE_MASK = 8;
    private static final short IS_INNER_TYPE_MASK = 16;
    private static final short IS_LOCAL_TYPE_MASK = 32;
    private static final short IS_BINARY_MASK = 64;
    private static final short IS_ANONYMOUS_MASK = 128;
    private static final short IS_JAVA_SOURCE_MASK = 256;
    
    // A bit field that allows us to save memory by using the masks above
    private short properties = 0;

    Reference<ReferenceBinding> bindingRef;
    private PackageMirror pkg;
    private TypeMirror superclass;
    private List<MethodMirror> methods;
    private List<TypeMirror> interfaces;
    private Map<String, AnnotationMirror> annotations;
    private List<TypeParameterMirror> typeParams;
    private List<FieldMirror> fields;
    private String qualifiedName;
    private String flatName;
    private String simpleName;
    private List<ClassMirror> innerClasses;
    private String cacheKey;
    private JDTMethod enclosingMethod;
    private JDTClass enclosingClass;
    private String functionalInterface;
  
    private IType type = null;
    private int modifiers;
    private String fileName;
    private String javaModelPath;
    private String fullPath;
    private char[] bindingKey;
    private String sourceFileName=null;
    
    private static final Map<String, AnnotationMirror> noAnnotations = Collections.emptyMap();
    
    public JDTClass(ReferenceBinding klass, IType type, ClassFileReader classFileReader) {
        this(klass, type);
        if (classFileReader != null) {
            sourceFileName = CharOperation.charToString(classFileReader.sourceFileName());
        }
    }
    
    /*
     *  the klass parameter should not be null
     *  the type parameter might be null (in case of a 
     *  MissingBinaryType). In such a case, take care of 
     *  setting in the constructor all the lazy values calculated 
     *  from the type.
     */
    public JDTClass(ReferenceBinding klass, IType type) {
        this.type = type;
        bindingRef = new SoftReference<ReferenceBinding>(klass);
        pkg = new JDTPackage(klass.getPackage());
        simpleName = new String(klass.sourceName());
        qualifiedName = JDTUtils.getFullyQualifiedName(klass);
        flatName = JDTUtils.getFlatName(klass);
        if (flatName.equals(qualifiedName)) {
            flatName = qualifiedName;
        }
        modifiers = klass.modifiers;
        if (klass.isLocalType()) {
        	set(IS_LOCAL_TYPE_MASK);
        }
        if (klass.isBinaryBinding()) {
        	set(IS_BINARY_MASK);
        }
        if (klass.isAnonymousType()) {
        	set(IS_ANONYMOUS_MASK);
        }
        if ((klass instanceof SourceTypeBinding) 
        		&& new String(((SourceTypeBinding) klass).getFileName()).endsWith(".java")) {
        	set(IS_JAVA_SOURCE_MASK);
        }
        bindingKey = klass.computeUniqueKey();

        char[] bindingFileName = klass.getFileName();
        int start = CharOperation.lastIndexOf('/', bindingFileName) + 1;
        if (start == 0 || start < CharOperation.lastIndexOf('\\', bindingFileName))
            start = CharOperation.lastIndexOf('\\', bindingFileName) + 1;
        fileName = new String(CharOperation.subarray(bindingFileName, start, -1));
        
        int jarFileEntrySeparatorIndex = CharOperation.indexOf(IDependent.JAR_FILE_ENTRY_SEPARATOR, bindingFileName);
        if (jarFileEntrySeparatorIndex > 0) {
            char[] jarPart = CharOperation.subarray(bindingFileName, 0, jarFileEntrySeparatorIndex);
            IJavaElement jarPackageFragmentRoot = JavaCore.create(new String(jarPart));
            String jarPath = jarPackageFragmentRoot.getPath().toOSString();
            char[] entryPart = CharOperation.subarray(bindingFileName, jarFileEntrySeparatorIndex + 1, bindingFileName.length);
            fullPath = new StringBuilder(jarPath).append("!/").append(entryPart).toString();
        } else {
            fullPath = new String(bindingFileName);
        }

        ReferenceBinding sourceOrClass = klass;
        if (! klass.isBinaryBinding()) {
            sourceOrClass = klass.outermostEnclosingType();
        }
        char[] classFullName = new char[0];
        for (char[] part : sourceOrClass.compoundName) {
            classFullName = CharOperation.concat(classFullName, part, '/');
        }
        char[][] temp = CharOperation.splitOn('.', sourceOrClass.getFileName());
        String extension = temp.length > 1 ? "." + new String(temp[temp.length-1]) : "";
        javaModelPath = new String(classFullName) + extension;
        
        if (type == null) {
            annotations = new HashMap<>();
            methods = Collections.emptyList();
            interfaces = Collections.emptyList();
            typeParams = Collections.emptyList();
            fields = Collections.emptyList();
            innerClasses = Collections.emptyList();
        }
    }

    @Override
    public AnnotationMirror getAnnotation(String annotationType) {
        retrieveAnnotations();
        return annotations.get(annotationType);
    }

    private synchronized void retrieveAnnotations() {
        if (annotations == null) {
            doWithBindings(new ActionOnClassBinding() {
                @Override
                public void doWithBinding(IType classModel, ReferenceBinding klass) {
                    Map<String, AnnotationMirror> annots = JDTUtils.getAnnotations(klass.getAnnotations());
                    if (sourceFileName != null
                            && qualifiedName.startsWith("ceylon.language") 
                            && annots.containsKey(org.eclipse.ceylon.compiler.java.metadata.Ceylon.class.getName())
                            && sourceFileName.endsWith(".java")) {
                        HashMap<String, Object> values = new HashMap<>();
                        values.put("backend", "jvm");
                        annots.put("ceylon.language.NativeAnnotation$annotation$", new JDTAnnotation(values));
                    }
                    if (annots.isEmpty()) {
                        annots = noAnnotations;
                    }
                    annotations = annots;
                    if (getAnnotation(AbstractModelLoader.CEYLON_CONTAINER_ANNOTATION) != null || klass.isMemberType()) {
                    	set(IS_INNER_TYPE_MASK);
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
    public boolean isPublic() {
        return (this.modifiers & ClassFileConstants.AccPublic) != 0;
    }

    @Override
    public String getQualifiedName() {
        return qualifiedName;
    }

    @Override
    public String getFlatName() {
        return flatName;
    }

    @Override
    public String getName() {
        return simpleName;
    }

    @Override
    public PackageMirror getPackage() {
        return pkg;
    }

    @Override
    public boolean isInterface() {
        return (this.modifiers & ClassFileConstants.AccInterface) != 0;
    }

    @Override
    public boolean isAbstract() {
        return (this.modifiers & ClassFileConstants.AccAbstract) != 0;
    }
    
    @Override
    public boolean isProtected() {
        return (this.modifiers & ClassFileConstants.AccProtected) != 0;
    }
    
    @Override
    public boolean isDefaultAccess() {
        return (this.modifiers & (ClassFileConstants.AccPublic | ClassFileConstants.AccProtected | ClassFileConstants.AccPrivate)) == 0;
    }
    
    public boolean isDeprecated() {
        return (this.modifiers & ClassFileConstants.AccDeprecated) != 0;
    }
    
    public void doWithBindings(final ActionOnClassBinding action) {
        if (!doWithReferenceBinding(type, bindingRef.get(), action)) {
            doWithResolvedType(type, new ActionOnResolvedType() {
                @Override
                public void doWithBinding(ReferenceBinding classBinding) {
                    bindingRef = new WeakReference<ReferenceBinding>(classBinding);
                    action.doWithBinding(type, classBinding);
                }
            });
        }
    }
    
    
    @Override
    public List<MethodMirror> getDirectMethods() {
        if (methods == null) {
            doWithBindings(new ActionOnClassBinding() {
                @Override
                public void doWithBinding(IType classModel, ReferenceBinding klass) {
                    MethodBinding[] directMethods;
                    directMethods = klass.methods();
                    methods = new ArrayList<MethodMirror>(directMethods.length);
                    for(MethodBinding method : directMethods) {
                        if(!method.isBridge() && !method.isSynthetic() && !method.isPrivate())
                            methods.add(new JDTMethod(JDTClass.this, method));
                    }
                }
            });
        }
        return methods;
    }

    @Override
    public TypeMirror getSuperclass() {
        if (!isSet(SUPERCLASS_MASK)) {
            doWithBindings(new ActionOnClassBinding() {
                @Override
                public void doWithBinding(IType classModel, ReferenceBinding klass) {
                    if (klass.isInterface() || "java.lang.Object".equals(getQualifiedName())) {
                        superclass = null;
                    } else {
                        ReferenceBinding superClassBinding = klass.superclass();
                        if (superClassBinding != null) {
                            superClassBinding = JDTUtils.inferTypeParametersFromSuperClass(klass,
                                    superClassBinding);
                            superclass = JDTType.newJDTType(superClassBinding);
                        }
                    }
                }
            });
            set(SUPERCLASS_MASK);
        }
        return superclass;
    }

    @Override
    public List<TypeMirror> getInterfaces() {
        if (interfaces == null) {
            doWithBindings(new ActionOnClassBinding() {
                @Override
                public void doWithBinding(IType classModel, ReferenceBinding klass) {
                    ReferenceBinding[] superInterfaces = klass.superInterfaces();
                    interfaces = new ArrayList<TypeMirror>(superInterfaces.length);
                    for(ReferenceBinding superInterface : superInterfaces)
                        interfaces.add(JDTType.newJDTType(superInterface));
                }
            });
        }
        return interfaces;
    }

    @Override
    public List<TypeParameterMirror> getTypeParameters() {
        if (typeParams == null) {
            doWithBindings(new ActionOnClassBinding() {
                @Override
                public void doWithBinding(IType classModel, ReferenceBinding klass) {
                    TypeVariableBinding[] typeParameters = klass.typeVariables();
                    typeParams = new ArrayList<TypeParameterMirror>(typeParameters.length);
                    for(TypeVariableBinding parameter : typeParameters)
                        typeParams.add(new JDTTypeParameter(parameter));
                }
            });
        }
        return typeParams;
    }

    private boolean isAnnotationPresent(Class<?> clazz) {
        return getAnnotation(clazz.getName()) != null;
    }
    
    @Override
    public boolean isCeylonToplevelAttribute() {
        return !isInnerClass() && isAnnotationPresent(org.eclipse.ceylon.compiler.java.metadata.Attribute.class);
    }

    @Override
    public boolean isCeylonToplevelObject() {
        return !isInnerClass() && isAnnotationPresent(org.eclipse.ceylon.compiler.java.metadata.Object.class);
    }

    @Override
    public boolean isCeylonToplevelMethod() {
        return !isInnerClass() && isAnnotationPresent(org.eclipse.ceylon.compiler.java.metadata.Method.class);
    }

    @Override
    public boolean getIsCeylon() {
        return isAnnotationPresent(org.eclipse.ceylon.compiler.java.metadata.Ceylon.class);
    }

    @Override
    public List<FieldMirror> getDirectFields() {
        if (fields == null) {
            doWithBindings(new ActionOnClassBinding() {
                @Override
                public void doWithBinding(IType classModel, ReferenceBinding klass) {
                    FieldBinding[] directFields = klass.fields();
                    fields = new ArrayList<FieldMirror>(directFields.length);
                    for(FieldBinding field : directFields){
                        if(!field.isSynthetic() && !field.isPrivate()){
                            fields.add(new JDTField(field));
                        }
                    }
                }
            });
        }
        return fields;
    }

    @Override
    public boolean isInnerClass() {
        retrieveAnnotations();
        return isSet(IS_INNER_TYPE_MASK);
    }
    
    @Override
    public ClassMirror getEnclosingClass() {
        if(!isSet(ENCLOSING_CLASS_MASK)){
            doWithBindings(new ActionOnClassBinding() {
                @Override
                public void doWithBinding(IType classModel, ReferenceBinding klass) {
                    ReferenceBinding enclosingType = klass.enclosingType();
                    IType enclosingTypeModel = type.getDeclaringType();
                    if (enclosingType != null) {
                        if (enclosingTypeModel == null) {
                            throw new ModelResolutionException("JDT reference binding without a JDT IType element !");
                        }
                        enclosingClass =  new JDTClass(enclosingType, enclosingTypeModel);
                    } else {
                        enclosingClass = null;
                    }
                }
            });
            set(ENCLOSING_CLASS_MASK);
        }
        return enclosingClass;
    }
    
    @Override
    public MethodMirror getEnclosingMethod() {
        if(!isSet(ENCLOSING_METHOD_MASK)){
            if(isSet(IS_LOCAL_TYPE_MASK)){
                doWithBindings(new ActionOnClassBinding() {
                    @Override
                    public void doWithBinding(IType classModel, ReferenceBinding klass) {
                        LocalTypeBinding localClass = (LocalTypeBinding) klass;
                        MethodBinding enclosingMethodBinding = localClass.enclosingMethod;
                        enclosingMethod = enclosingMethodBinding != null ? new JDTMethod(JDTClass.this, enclosingMethodBinding) : null;
                    }
                });
            }
            set(ENCLOSING_METHOD_MASK);
        }
        return enclosingMethod;
    }


    @Override
    public List<ClassMirror> getDirectInnerClasses() {
        if (innerClasses == null) {
            doWithBindings(new ActionOnClassBinding() {
                @Override
                public void doWithBinding(IType classModel, ReferenceBinding klass) {
                    ReferenceBinding[] memberTypeBindings = klass.memberTypes();
                    innerClasses = new ArrayList<ClassMirror>(memberTypeBindings.length);
                    for(ReferenceBinding memberTypeBinding : memberTypeBindings) {
                        ReferenceBinding classBinding = memberTypeBinding;
                        IType classTypeModel = toType(classBinding);
                        innerClasses.add(new JDTClass(classBinding, classTypeModel));
                    }
                }
            });
        }
        return innerClasses;
    }

    @Override
    public boolean isStatic() {
        return (this.modifiers & ClassFileConstants.AccStatic) != 0;
    }

    @Override
    public boolean isFinal() {
        return (this.modifiers & ClassFileConstants.AccFinal) != 0;
    }
    
    @Override
    public boolean isEnum() {
        return (this.modifiers & ClassFileConstants.AccEnum) != 0;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public boolean getIsBinary() {
        return isSet(IS_BINARY_MASK);
    }

    @Override
    public boolean isLoadedFromSource() {
        return isJavaSource();
    }

    @Override
    public boolean isAnonymous() {
        return isSet(IS_ANONYMOUS_MASK);
    }

    @Override
    public boolean isJavaSource() {
        return isSet(IS_JAVA_SOURCE_MASK);
    }
    
    public String getJavaModelPath() {
        return javaModelPath;
    }

    public String getFullPath() {
        return fullPath;
    }

    @Override
    public boolean isAnnotationType() {
        return (this.modifiers & ClassFileConstants.AccAnnotation) != 0;
    }

    @Override
    public boolean isLocalClass() {
        return getAnnotation(AbstractModelLoader.CEYLON_LOCAL_CONTAINER_ANNOTATION) != null 
                || isSet(IS_LOCAL_TYPE_MASK);
    }
    
    @Override
    public char[] getBindingKey() {
        return bindingKey;
    }

    @Override
    public String getCacheKey(Module module) {
        if(cacheKey == null){
            String className = getQualifiedName();
            cacheKey = AbstractModelLoader.getCacheKeyByModule(module, className);
        }
        return cacheKey;
    }

    public IType getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return "[JDTClass: "+qualifiedName+" ( " + fileName + ")]";
    }
    
    public String isFunctionalInterface() {
        if (!isSet(FUNCTIONAL_INTERFACE_MASK)) {
            doWithBindings(new ActionOnClassBinding() {
                @Override
                public void doWithBinding(IType classModel, ReferenceBinding klass) {
                    try {
                        LookupEnvironment environment = klass.fPackage.environment;
                        Scope scope = new CompilationUnitScope(
                                new CompilationUnitDeclaration(
                                     environment.problemReporter, 
                                    null, 
                                    0), environment);
                        MethodBinding method = klass.getSingleAbstractMethod(scope, true);
                        if (method != null &&
                            method.isValidBinding() &&
                            ! JDTMethod.ignoreMethodInAncestorSearch(method)) {
                            String name = CharOperation.charToString(method.selector);
                            LookupEnvironmentUtilities.Provider modelLoader = modelJ2C().getLookupEnvironmentProvider(type);
                            if(modelLoader != null &&
                                    modelLoader.isGetter(method, name)) {
                                name = NamingBase.getJavaAttributeName(name);
                            }
                            functionalInterface = name;
                        }
                    } catch(Exception e) {
                        CeylonPlugin.log(Status.ERROR, "Exception when trying to retrieve Functional interface of type" + klass.debugName() +
                                              "\n    -> functional interface search skipped:", e);
                    }
                }
            });
            set(FUNCTIONAL_INTERFACE_MASK);
        }
        return functionalInterface;
    }
    
    private boolean isSet(int mask) {
    	return (properties & mask) == mask;
    }
    
    private void set(int mask) {
    	properties |= mask;
    }
}
