package com.redhat.ceylon.eclipse.core.model;

import com.redhat.ceylon.eclipse.core.typechecker.EditedPhasedUnit;

public class EditedSourceFile extends SourceFile {
    public EditedSourceFile(EditedPhasedUnit phasedUnit) {
        super(phasedUnit);
    }

    @Override
    public EditedPhasedUnit getPhasedUnit() {
        return (EditedPhasedUnit) super.getPhasedUnit();
    }
}
