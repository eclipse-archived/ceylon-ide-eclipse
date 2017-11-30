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

import java.util.List;

import ceylon.language.meta.model.Interface;

import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.model.loader.AbstractModelLoader;
import org.eclipse.ceylon.model.loader.mirror.AnnotationMirror;
import org.eclipse.ceylon.model.loader.mirror.ClassMirror;
import org.eclipse.ceylon.model.loader.mirror.FieldMirror;
import org.eclipse.ceylon.model.loader.mirror.MethodMirror;
import org.eclipse.ceylon.model.loader.mirror.PackageMirror;
import org.eclipse.ceylon.model.loader.mirror.TypeMirror;
import org.eclipse.ceylon.model.loader.mirror.TypeParameterMirror;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.model.typechecker.model.Scope;
import org.eclipse.ceylon.model.typechecker.model.Class;
import org.eclipse.ceylon.model.typechecker.model.TypeDeclaration;

public class SourceClass implements ClassMirror {

    private SourceDeclarationHolder sourceDeclarationHolder;
    private String cacheKey;
    private String qualifiedName;
    private String flatName;


    public SourceClass(SourceDeclarationHolder sourceDeclarationHolder) {
        this.sourceDeclarationHolder = sourceDeclarationHolder;
    }
    
    @Override
    public AnnotationMirror getAnnotation(String type) {
//        List<CompilerAnnotation> compilerAnnotations = getAstDeclaration().getCompilerAnnotations();
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public boolean isPublic() {
        return getModelDeclaration().isShared();
    }

    @Override
    public boolean isInterface() {
        return getModelDeclaration() instanceof Interface;
    }

    @Override
    public boolean isAbstract() {
        Declaration decl = getModelDeclaration();
        if (! (decl instanceof Class)) {
            return false;
        }
        return ((Class) decl).isAbstract();
    }

    @Override
    public boolean isProtected() {
        return false;
    }
    
    @Override
    public boolean isDefaultAccess() {
        return false;
    }
    
    @Override
    public boolean isStatic() {
        return false;
    }
    
    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public boolean isFinal() {
        Declaration decl = getModelDeclaration();
        if (! (decl instanceof TypeDeclaration)) {
            return false;
        }
        return ((TypeDeclaration)decl).isFinal();
    }

    @Override
    public boolean isInnerClass() {
        return getModelDeclaration().isClassOrInterfaceMember();
    }
    
    @Override
    public boolean isAnonymous() {
        return getModelDeclaration().isAnonymous();
    }

    @Override
    public String getName() {
        return getModelDeclaration().getName();
    }

    @Override
    public String getQualifiedName() {
        if(qualifiedName == null) {
            String ceylonQualifiedName = getModelDeclaration().getQualifiedNameString();
            if (ceylonQualifiedName != null){
                qualifiedName = ceylonQualifiedName.replace("::", ".");
            }
        }
        return qualifiedName;
    }

    @Override
    public String getFlatName() {
        if(flatName == null) {
            String ceylonQualifiedName = getModelDeclaration().getQualifiedNameString();
            if (ceylonQualifiedName != null) {
                String[] packageAndDecl = ceylonQualifiedName.split("::");
                String declName = packageAndDecl[packageAndDecl.length-1].replace('.', '$');
                if (packageAndDecl.length > 1) {
                    flatName = packageAndDecl[0] + "." + declName;
                } else {
                    flatName = declName;
                }
            }
        }
        return flatName;
    }

    @Override
    public PackageMirror getPackage() {
        Declaration decl = getModelDeclaration();
        Scope scope = decl.getContainer();
        while ((scope != null) && ! (scope instanceof Package)) {
            scope = scope.getContainer();
        }
        final String fqn = scope == null ? "" : scope.getQualifiedNameString();
        
        return new PackageMirror() {
            @Override
            public String getQualifiedName() {
                return fqn;
            }
        };
    }

    @Override
    public List<MethodMirror> getDirectMethods() {
        System.out.println("!!!!!!!!!!!!!!!! In SourceClass.getDirectMethods() !!!!!!!!!!!!!!!!!!!!!!!");
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
//        return Collections.emptyList();
//        ClassOrInterface decl = (ClassOrInterface) getDeclarationModel();
//        // ...
//        if (decl instanceof Class) {
//            Class clazz = (Class) decl;
//            clazz.getParameterList();
//            => On ajoute un MethodMirror pour le constructeur 
//        }
//        
    }

    @Override
    public List<FieldMirror> getDirectFields() {
        System.out.println("!!!!!!!!!!!!!!!! In SourceClass.getDirectFields() !!!!!!!!!!!!!!!!!!!!!!!");
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
//        return Collections.emptyList();
    }

    @Override
    public List<TypeParameterMirror> getTypeParameters() {
        System.out.println("!!!!!!!!!!!!!!!! In SourceClass.getTypeParameters() !!!!!!!!!!!!!!!!!!!!!!!");
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
//        return Collections.emptyList();
    }

    @Override
    public List<ClassMirror> getDirectInnerClasses() {
        System.out.println("!!!!!!!!!!!!!!!! In SourceClass.getDirectInnerClasses() !!!!!!!!!!!!!!!!!!!!!!!");
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
//        return Collections.emptyList();
    }

    @Override
    public ClassMirror getEnclosingClass() {
        System.out.println("!!!!!!!!!!!!!!!! In SourceClass.getEnclosingClass() !!!!!!!!!!!!!!!!!!!!!!!");
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public MethodMirror getEnclosingMethod() {
        System.out.println("!!!!!!!!!!!!!!!! In SourceClass.getEnclosingMethod() !!!!!!!!!!!!!!!!!!!!!!!");
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public TypeMirror getSuperclass() {
        System.out.println("!!!!!!!!!!!!!!!! In SourceClass.getSuperclass() !!!!!!!!!!!!!!!!!!!!!!!");
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
//        return null;
    }

    @Override
    public List<TypeMirror> getInterfaces() {
        System.out.println("!!!!!!!!!!!!!!!! In SourceClass.getInterface() !!!!!!!!!!!!!!!!!!!!!!!");
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
//        return Collections.emptyList();
    }

    @Override
    public boolean isCeylonToplevelAttribute() {
        return getAstDeclaration() instanceof Tree.AttributeDeclaration 
                && getModelDeclaration().isToplevel();
    }

    @Override
    public boolean isCeylonToplevelObject() {
        return getAstDeclaration() instanceof Tree.ObjectDefinition
                && getModelDeclaration().isToplevel();
    }

    @Override
    public boolean isCeylonToplevelMethod() {
        return getAstDeclaration() instanceof Tree.MethodDefinition
                && getModelDeclaration().isToplevel();
    }

    public Declaration getModelDeclaration() {
        return sourceDeclarationHolder.getModelDeclaration();
    }

    public Tree.Declaration getAstDeclaration() {
        return sourceDeclarationHolder.getAstDeclaration();
    }

    @Override
    public boolean isLoadedFromSource() {
        return true;
    }

    @Override
    public boolean isJavaSource() {
        return false;
    }

    @Override
    public boolean isAnnotationType() {
        return false; // TODO : is it really right ?
    }

    @Override
    public boolean isLocalClass() {
        //TODO: is this correct!?
        return !getModelDeclaration().isClassOrInterfaceMember() &&
                !getModelDeclaration().isToplevel();
    }

    @Override
    public String getCacheKey(Module module) {
        if(cacheKey == null) {
            String className = getQualifiedName();
            cacheKey = AbstractModelLoader.getCacheKeyByModule(module, className);
        }
        return cacheKey;
    }
    
}