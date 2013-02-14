package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IResource;

import com.redhat.ceylon.eclipse.core.typechecker.ExternalPhasedUnit;

/*
 * Used when the external declarations come from a source file that doesn't have any binary version,
 *     or the binary car isn't taken in account (current behavior) => this unit will contain declarations
 *   
 */
public class ExternalSourceFile extends SourceFile {

    public ExternalSourceFile(ExternalPhasedUnit phasedUnit) {
        super(phasedUnit);
    }

    @Override
    public ExternalPhasedUnit getPhasedUnit() {
        return (ExternalPhasedUnit) super.getPhasedUnit();
    }
    
    @Override
    public IResource getResource() {
        return null;
    }
}
