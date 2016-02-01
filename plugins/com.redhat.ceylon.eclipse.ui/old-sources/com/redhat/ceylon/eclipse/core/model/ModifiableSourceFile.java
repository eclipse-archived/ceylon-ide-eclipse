package com.redhat.ceylon.eclipse.core.model;

import com.redhat.ceylon.eclipse.core.typechecker.IdePhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.ModifiablePhasedUnit;

public abstract class ModifiableSourceFile extends SourceFile implements IResourceAware {

    public ModifiableSourceFile(IdePhasedUnit phasedUnit) {
        super(phasedUnit);
    }

    @Override
    public ModifiablePhasedUnit getPhasedUnit() {
        return (ModifiablePhasedUnit) super.getPhasedUnit();
    }
}
