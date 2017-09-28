package org.eclipse.ceylon.ide.eclipse.core.model;

import org.eclipse.ceylon.ide.eclipse.core.typechecker.IdePhasedUnit;
import org.eclipse.ceylon.ide.eclipse.core.typechecker.ModifiablePhasedUnit;

public abstract class ModifiableSourceFile extends SourceFile implements IResourceAware {

    public ModifiableSourceFile(IdePhasedUnit phasedUnit) {
        super(phasedUnit);
    }

    @Override
    public ModifiablePhasedUnit getPhasedUnit() {
        return (ModifiablePhasedUnit) super.getPhasedUnit();
    }
}
