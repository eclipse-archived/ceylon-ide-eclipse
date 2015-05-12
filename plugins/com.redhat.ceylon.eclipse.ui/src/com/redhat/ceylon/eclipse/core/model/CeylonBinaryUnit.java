package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;

import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.eclipse.core.typechecker.ExternalPhasedUnit;

/*
 * Created inside the JDTModelLoader.getCompiledUnit() function if the unit is a ceylon one
 */
public class CeylonBinaryUnit extends CeylonUnit implements IJavaModelAware {
    
    IClassFile classFileElement;
    
    public CeylonBinaryUnit(IClassFile typeRoot, String fileName, String relativePath, String fullPath, Package pkg) {
        super();
        this.classFileElement = typeRoot;
        setFilename(fileName);
        setRelativePath(relativePath);
        setFullPath(fullPath);
        setPackage(pkg);
    }

    
    /*
     * Might be null if no source is linked to this ModelLoader-originating unit
     * 
     * (non-Javadoc)
     * @see com.redhat.ceylon.eclipse.core.model.CeylonUnit#getPhasedUnit()
     */
    
    @Override
    public ExternalPhasedUnit getPhasedUnit() {
        return (ExternalPhasedUnit) super.getPhasedUnit();
    }
    
    public IClassFile getTypeRoot() {
        return classFileElement;
    }

    public IProject getProject() {
        if (getTypeRoot() != null) {
            return (IProject) getTypeRoot().getJavaProject().getProject();
        }
        return null;
    }
    
    @Override
    protected ExternalPhasedUnit setPhasedUnitIfNecessary() {
        ExternalPhasedUnit phasedUnit = null;
        if (phasedUnitRef != null) {
            phasedUnit = (ExternalPhasedUnit) phasedUnitRef.get();
        }
        
        if (phasedUnit == null) {
            try {
                JDTModule module = getModule();
                if (module.getArtifact() != null) {
                    String binaryUnitRelativePath = getFullPath().replace(module.getArtifact().getPath() + "!/", "");
                    String sourceUnitRelativePath = module.toSourceUnitRelativePath(binaryUnitRelativePath);
                    if (sourceUnitRelativePath != null) {
                        phasedUnit = (ExternalPhasedUnit) module.getPhasedUnitFromRelativePath(sourceUnitRelativePath);
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return phasedUnit != null ? createPhasedUnitRef(phasedUnit) : null;
    }

    public String getSourceRelativePath() {
        return getModule().toSourceUnitRelativePath(getRelativePath());
    }

    public String getCeylonSourceRelativePath() {
        return getModule().getCeylonDeclarationFile(getSourceRelativePath());
    }
    
    @Override
    public String getSourceFullPath() {
        String sourceArchivePath = getModule().getSourceArchivePath();
        if (sourceArchivePath == null) {
            return null;
        }
        return sourceArchivePath + "!/" + getSourceRelativePath();
    }


    @Override
    public IJavaElement toJavaElement(Declaration ceylonDeclaration) {
        return new CeylonToJavaMatcher(this).searchInClass(ceylonDeclaration);
    }


    @Override
    public String getCeylonFileName() {
        String ceylonSourceRelativePath = getCeylonSourceRelativePath();
        if (ceylonSourceRelativePath == null || ceylonSourceRelativePath.isEmpty()) {
            return null;
        }
        String[] splitedPath = ceylonSourceRelativePath.split("/");
        return splitedPath[splitedPath.length-1];
    }
}
