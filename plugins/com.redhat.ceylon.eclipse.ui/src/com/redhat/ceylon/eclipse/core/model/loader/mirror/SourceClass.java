package com.redhat.ceylon.eclipse.core.model.loader.mirror;

import java.util.Collections;
import java.util.List;

import com.redhat.ceylon.compiler.loader.AbstractModelLoader;
import com.redhat.ceylon.compiler.loader.mirror.AnnotationMirror;
import com.redhat.ceylon.compiler.loader.mirror.ClassMirror;
import com.redhat.ceylon.compiler.loader.mirror.FieldMirror;
import com.redhat.ceylon.compiler.loader.mirror.MethodMirror;
import com.redhat.ceylon.compiler.loader.mirror.PackageMirror;
import com.redhat.ceylon.compiler.loader.mirror.TypeMirror;
import com.redhat.ceylon.compiler.loader.mirror.TypeParameterMirror;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilerAnnotation;
import com.redhat.ceylon.eclipse.core.model.CeylonDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Class;

public class SourceClass implements ClassMirror {

    private CeylonDeclaration ceylonDeclaration;

    public SourceClass(CeylonDeclaration ceylonDeclaration) {
        this.ceylonDeclaration = ceylonDeclaration;
    }
    
    @Override
    public AnnotationMirror getAnnotation(String type) {
        List<CompilerAnnotation> compilerAnnotations = getAstDeclaration().getCompilerAnnotations();
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
    public boolean isStatic() {
        return false;
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
    public String getSimpleName() {
        return getModelDeclaration().getName();
    }

    @Override
    public String getQualifiedName() {
        return getModelDeclaration().getQualifiedNameString();
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
        return ceylonDeclaration.getModelDeclaration();
    }

    public Tree.Declaration getAstDeclaration() {
        return ceylonDeclaration.getAstDeclaration();
    }

    @Override
    public boolean isLoadedFromSource() {
        return true;
        // return ceylonDeclaration.isSourceToCompile();  
    }

    @Override
    public boolean isJavaSource() {
        return false;
    }
}
