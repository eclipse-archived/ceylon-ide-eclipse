package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ITypeRoot;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.ExternalPhasedUnit;

/*
 * Created inside the JDTModelLoader.getCompiledUnit() function if the unit is a ceylon one
 */
public class CeylonBinaryUnit extends CeylonUnit implements IJavaModelAware {
    
    
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
        return null;
    }

    @Override
    protected void setPhasedUnitIfNecessary() {
        // Look into the mapping.txt of the module archive, and get the name of the source unit
        // Then get the PhasedUnits related to this module, and search for the relative path in it.
        // Then set in inot the Weakef with createPhasedUnit
    }
}
