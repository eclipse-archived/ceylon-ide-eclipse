package com.redhat.ceylon.eclipse.imp.open;

import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;

public class DeclarationWithProject {
    
    DeclarationWithProject(Declaration dec, IProject project) {
        this.dec = dec;
        this.project = project;
    }
    
    final Declaration dec;
    final IProject project;
    
    public Declaration getDeclaration() {
        return dec;
    }
    
    public IProject getProject() {
        return project;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DeclarationWithProject) {
            DeclarationWithProject that = (DeclarationWithProject) obj;
            return that.project.equals(project) && that.dec.equals(dec);
        }
        else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return dec.getName().hashCode();
    }
    
}