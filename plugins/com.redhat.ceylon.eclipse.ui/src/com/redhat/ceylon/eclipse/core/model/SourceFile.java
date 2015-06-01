package com.redhat.ceylon.eclipse.core.model;

import com.redhat.ceylon.model.typechecker.util.ModuleManager;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.eclipse.core.typechecker.IdePhasedUnit;
import com.redhat.ceylon.eclipse.util.SingleSourceUnitPackage;

public abstract class SourceFile extends CeylonUnit {
    
    public SourceFile(IdePhasedUnit phasedUnit) {
        createPhasedUnitRef(phasedUnit);
    }

    @Override
    protected IdePhasedUnit setPhasedUnitIfNecessary() { return phasedUnitRef.get(); }
    
    @Override
    public String getSourceFullPath() {
        return getFullPath();
    }

    @Override
    public String getSourceRelativePath() {
        return getRelativePath();
    }

    @Override
    public String getCeylonSourceRelativePath() {
        return getRelativePath();
    }
    
    @Override
    public String getCeylonSourceFullPath() {
        return getSourceFullPath();
    }

    @Override
    public String getCeylonFileName() {
        return getFilename();
    }
    
    @Override
    public void setPackage(Package p) {
        super.setPackage(p);
        if (p instanceof SingleSourceUnitPackage && p.getUnit() == null
                && getFilename().equals(ModuleManager.PACKAGE_FILE)) {
            SingleSourceUnitPackage ssup = (SingleSourceUnitPackage) p;
            if (ssup.getFullPathOfSourceUnitToTypecheck().equals(getFullPath())) {
                p.setUnit(this);
            }
        }
    }
}
