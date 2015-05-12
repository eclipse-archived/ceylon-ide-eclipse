package com.redhat.ceylon.eclipse.core.model.mirror;

import java.util.List;

import ceylon.language.meta.model.Interface;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.loader.AbstractModelLoader;
import com.redhat.ceylon.model.loader.mirror.AnnotationMirror;
import com.redhat.ceylon.model.loader.mirror.ClassMirror;
import com.redhat.ceylon.model.loader.mirror.FieldMirror;
import com.redhat.ceylon.model.loader.mirror.MethodMirror;
import com.redhat.ceylon.model.loader.mirror.PackageMirror;
import com.redhat.ceylon.model.loader.mirror.TypeMirror;
import com.redhat.ceylon.model.loader.mirror.TypeParameterMirror;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;

public class SourceClass implements ClassMirror {

    private SourceDeclarationHolder sourceDeclarationHolder;
    private String cacheKey;


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
        return false; // TODO : is it really right ?
    }

    @Override
    public String getName() {
        return getModelDeclaration().getName();
    }

    @Override
    public String getQualifiedName() {
        return getModelDeclaration().getQualifiedNameString();
    }

    @Override
    public String getFlatName() {
        // should be good
        return getQualifiedName();
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
        // return sourceDeclarationHolder.isSourceToCompile();  
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