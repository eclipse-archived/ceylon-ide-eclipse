package com.redhat.ceylon.eclipse.core.model;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import net.lingala.zip4j.exception.ZipException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ITypeRoot;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.core.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.eclipse.util.CarUtils;

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
        // Then set it into the WeakRef with createPhasedUnit
        
        String[] splittedPath = getFullPath().split("!");
        if (splittedPath.length == 2) {
            String carPath = splittedPath[0];
            try {
                Properties mapping = CarUtils.retrieveMappingFile(new File(carPath));
                String sourceFileRelativePath = mapping.getProperty(splittedPath[1]);
                Package pkg = getPackage();
                if (pkg != null) {
                    Module module = pkg.getModule();
                    // TODO : retrieve the PhasedUnits object related to this module
                    // get the PhasedUnit object through its src-relative path
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
