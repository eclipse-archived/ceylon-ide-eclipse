package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClassFile;

import com.redhat.ceylon.eclipse.core.model.loader.JDTModule;
import com.redhat.ceylon.eclipse.core.typechecker.ExternalPhasedUnit;

/*
 * Created inside the JDTModelLoader.getCompiledUnit() function if the unit is a ceylon one
 */
public class CeylonBinaryUnit extends CeylonUnit implements IJavaModelAware {
    
    IClassFile classFileElement;
    
    public CeylonBinaryUnit(IClassFile typeRoot) {
        super();
        this.classFileElement = typeRoot;
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
    
    public IClassFile getJavaElement() {
        return classFileElement;
    }

    @Override
    protected ExternalPhasedUnit setPhasedUnitIfNecessary() {
        ExternalPhasedUnit phasedUnit = null;
        if (phasedUnitRef != null) {
            phasedUnit = (ExternalPhasedUnit) phasedUnitRef.get();
        }
        
        if (phasedUnit == null) {
            try {
                JDTModule module = (JDTModule) getPackage().getModule();
                
                String binaryUnitRelativePath = getFullPath().replace(module.getArtifact().getPath() + "!/", "");
                String sourceUnitRelativePath = module.toSourceUnitRelativePath(binaryUnitRelativePath);
                phasedUnit = (ExternalPhasedUnit) module.getPhasedUnitFromRelativePath(sourceUnitRelativePath);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return phasedUnit != null ? createPhasedUnitRef(phasedUnit) : null;
    }

    @Override
    public IProject getProjectResource() {
        return getJavaElement().getJavaProject().getProject();
    }
    
    public String getSourceRelativePath() {
        return ((JDTModule) getPackage().getModule()).toSourceUnitRelativePath(getRelativePath());
    }

    @Override
    public String getSourceFullPath() {
        String sourceArchivePath = ((JDTModule) getPackage().getModule()).getSourceArchivePath();
        if (sourceArchivePath == null) {
            return null;
        }
        return sourceArchivePath + "!/" + getSourceRelativePath();
    }
}
