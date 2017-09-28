package org.eclipse.ceylon.ide.eclipse.core.model;

import java.lang.ref.WeakReference;

import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.core.typechecker.IdePhasedUnit;

public abstract class CeylonUnit extends IdeUnit {
    
    public CeylonUnit() {
        phasedUnitRef = null;
    }
    
    protected WeakReference<IdePhasedUnit> phasedUnitRef;
    
    final protected <PhasedUnitType extends IdePhasedUnit> 
    PhasedUnitType createPhasedUnitRef(PhasedUnitType phasedUnit) {
        phasedUnitRef = new WeakReference<IdePhasedUnit>(phasedUnit);
        return phasedUnit;
    }
    
    protected abstract IdePhasedUnit setPhasedUnitIfNecessary();
    
    public IdePhasedUnit getPhasedUnit() {
        return setPhasedUnitIfNecessary();
    }
    
    abstract public String getCeylonFileName();
    abstract public String getCeylonSourceRelativePath();
    abstract public String getCeylonSourceFullPath();
    
    public Tree.CompilationUnit getCompilationUnit() {
        IdePhasedUnit pu = getPhasedUnit();
        if (pu == null) {
            return null;
        }
        return pu.getCompilationUnit();
    }
    
}
