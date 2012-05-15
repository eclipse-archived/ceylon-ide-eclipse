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
import com.redhat.ceylon.eclipse.core.model.CeylonDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Class;

public class SourceClass implements ClassMirror {

    private CeylonDeclaration ceylonDeclaration;
    private Tree.Declaration astDeclaration;
    private PhasedUnit phasedUnit;
    private Declaration modelDeclaration = null;

    public SourceClass(CeylonDeclaration ceylonDeclaration) {
        this.ceylonDeclaration = ceylonDeclaration;
        astDeclaration = ceylonDeclaration.getAstDeclaration();
        phasedUnit = ceylonDeclaration.getPhasedUnit();
    }
    
    @Override
    public AnnotationMirror getAnnotation(String type) {
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public boolean isPublic() {
        return getDeclarationModel().isShared();
    }

    @Override
    public boolean isInterface() {
        return getDeclarationModel() instanceof Interface;
    }

    @Override
    public boolean isAbstract() {
        Declaration decl = getDeclarationModel();
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
        return getDeclarationModel().isClassOrInterfaceMember();
    }

    @Override
    public boolean isAnonymous() {
        return false; // TODO : is it really right ?
    }

    @Override
    public String getSimpleName() {
        return getDeclarationModel().getName();
    }

    @Override
    public String getQualifiedName() {
        return getDeclarationModel().getQualifiedNameString();
    }

    @Override
    public PackageMirror getPackage() {
        Declaration decl = getDeclarationModel();
        Scope scope = decl.getContainer();
        while (scope instanceof ClassOrInterface) {
            scope = scope.getContainer();
        }
        final String fqn = scope.getQualifiedNameString();
        
        return new PackageMirror() {
            @Override
            public String getQualifiedName() {
                return fqn;
            }
        };
    }

    @Override
    public List<MethodMirror> getDirectMethods() {
        return Collections.emptyList();
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
        return Collections.emptyList();
    }

    @Override
    public List<TypeParameterMirror> getTypeParameters() {
        return Collections.emptyList();
    }

    @Override
    public List<ClassMirror> getDirectInnerClasses() {
        return Collections.emptyList();
    }

    @Override
    public TypeMirror getSuperclass() {
        return null;
    }

    @Override
    public List<TypeMirror> getInterfaces() {
        return Collections.emptyList();
    }

    @Override
    public boolean isCeylonToplevelAttribute() {
        return astDeclaration instanceof Tree.AttributeDeclaration 
                && getDeclarationModel().isToplevel();
    }

    @Override
    public boolean isCeylonToplevelObject() {
        return astDeclaration instanceof Tree.ObjectDefinition
                && getDeclarationModel().isToplevel();
    }

    @Override
    public boolean isCeylonToplevelMethod() {
        return astDeclaration instanceof Tree.MethodDefinition
                && getDeclarationModel().isToplevel();
    }

    public Declaration getDeclarationModel() {
        if (modelDeclaration != null) {
            return modelDeclaration;
        }
        if (! phasedUnit.isDeclarationsScanned()) {
            phasedUnit.scanDeclarations();
        }
        if (! phasedUnit.isTypeDeclarationsScanned()) {
            phasedUnit.scanTypeDeclarations();
        }
        if (! phasedUnit.isRefinementValidated()) {
            phasedUnit.validateRefinement();
        }
        if (! phasedUnit.isFullyTyped()) {
            phasedUnit.analyseTypes();
        }
        modelDeclaration = astDeclaration.getDeclarationModel();
        return modelDeclaration;
    }

    @Override
    public boolean isLoadedFromSource() {
        return true;
    }
}
