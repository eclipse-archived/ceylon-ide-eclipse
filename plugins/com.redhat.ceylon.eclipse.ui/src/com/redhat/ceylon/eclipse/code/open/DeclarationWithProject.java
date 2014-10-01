package com.redhat.ceylon.eclipse.code.open;

import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;

//TODO: remove this class, because the information
//      should now be available in the JDTModule
@Deprecated
class DeclarationWithProject {
    
    DeclarationWithProject(Declaration dec, 
            IProject project, String version,
            String path) {
        this.dec = dec;
        this.project = project;
        this.version = version;
        this.path = path;
    }
    
    private final Declaration dec;
    private final IProject project;
    private final String version;
    private final String path;
    
    public Declaration getDeclaration() {
        return dec;
    }
    
    public IProject getProject() {
        return project;
    }
    
    public String getPath() {
        return path;
    }
    
    public String getVersion() {
        return version;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DeclarationWithProject) {
            DeclarationWithProject that = 
                    (DeclarationWithProject) obj;
            return (this.path==null && that.path==null || //both binary (hack!)
                    that.project==project || 
                    that.project!=null && project!=null && 
                    that.project.equals(project)) && 
                    (that.version==version || 
                    that.version!=null && version!=null && 
                    that.version.equals(version)) && 
                    that.dec.equals(dec);
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