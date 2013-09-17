package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.jdt.core.IClassFile;

import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.core.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.IdePhasedUnit;

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
    protected void setPhasedUnitIfNecessary() {
        if (phasedUnitRef == null) {
            // Look into the mapping.txt of the module archive, and get the name of the source unit
            // Then get the PhasedUnits related to this module, and search for the relative path in it.
            // Then set it into the WeakRef with createPhasedUnit
            
            String[] splittedPath = getFullPath().split("!");
            if (splittedPath.length == 2) {
//                String carPath = splittedPath[0];
                try {
//                    Properties mapping = CarUtils.retrieveMappingFile(new File(carPath));
//                    String sourceFileRelativePath = mapping.getProperty(splittedPath[1]);
                    Package pkg = getPackage();
                    if (pkg != null) {
//                        Module module = pkg.getModule();
                        // TODO : retrieve the PhasedUnits object related to this module
                        // get the PhasedUnit object through its src-relative path
                        IdePhasedUnit pu = null; // replace by the right value
                        createPhasedUnitRef(pu);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
