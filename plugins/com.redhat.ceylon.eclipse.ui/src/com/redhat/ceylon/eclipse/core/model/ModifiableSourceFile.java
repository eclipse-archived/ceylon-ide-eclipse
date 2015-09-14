package com.redhat.ceylon.eclipse.core.model;

import com.redhat.ceylon.eclipse.core.typechecker.IdePhasedUnit;

public abstract class ModifiableSourceFile extends SourceFile implements IResourceAware {

    public ModifiableSourceFile(IdePhasedUnit phasedUnit) {
        super(phasedUnit);
    }

}
