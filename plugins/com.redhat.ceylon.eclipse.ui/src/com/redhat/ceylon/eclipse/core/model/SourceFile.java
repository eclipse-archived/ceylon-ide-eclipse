package com.redhat.ceylon.eclipse.core.model;

import com.redhat.ceylon.eclipse.core.typechecker.IdePhasedUnit;

public abstract class SourceFile extends CeylonUnit {
    
    public SourceFile(IdePhasedUnit phasedUnit) {
        createPhasedUnitRef(phasedUnit);
    }

    @Override
    protected IdePhasedUnit setPhasedUnitIfNecessary() { return phasedUnitRef.get(); }
    
}
