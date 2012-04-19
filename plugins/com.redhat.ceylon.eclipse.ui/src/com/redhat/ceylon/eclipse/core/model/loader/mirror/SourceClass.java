package com.redhat.ceylon.eclipse.core.model.loader.mirror;

import java.util.List;

import com.redhat.ceylon.compiler.loader.mirror.AnnotationMirror;
import com.redhat.ceylon.compiler.loader.mirror.ClassMirror;
import com.redhat.ceylon.compiler.loader.mirror.FieldMirror;
import com.redhat.ceylon.compiler.loader.mirror.MethodMirror;
import com.redhat.ceylon.compiler.loader.mirror.PackageMirror;
import com.redhat.ceylon.compiler.loader.mirror.TypeMirror;
import com.redhat.ceylon.compiler.loader.mirror.TypeParameterMirror;

public class SourceClass implements ClassMirror {

    private String qualifiedName;
    
    public SourceClass(String qualifiedName) {
        this.qualifiedName = qualifiedName; 
    }
    
    @Override
    public AnnotationMirror getAnnotation(String type) {
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public boolean isPublic() {
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public boolean isInterface() {
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public boolean isAbstract() {
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public boolean isStatic() {
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public boolean isInnerClass() {
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public boolean isAnonymous() {
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public String getSimpleName() {
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public String getQualifiedName() {
        return qualifiedName;
    }

    @Override
    public PackageMirror getPackage() {
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public List<MethodMirror> getDirectMethods() {
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public List<FieldMirror> getDirectFields() {
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public List<TypeParameterMirror> getTypeParameters() {
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public List<ClassMirror> getDirectInnerClasses() {
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public TypeMirror getSuperclass() {
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public List<TypeMirror> getInterfaces() {
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public boolean isCeylonToplevelAttribute() {
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public boolean isCeylonToplevelObject() {
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public boolean isCeylonToplevelMethod() {
        throw new IllegalAccessError("Don't use a Source Class Mirror !");
    }

    @Override
    public boolean isLoadedFromSource() {
        return true;
    }
}
