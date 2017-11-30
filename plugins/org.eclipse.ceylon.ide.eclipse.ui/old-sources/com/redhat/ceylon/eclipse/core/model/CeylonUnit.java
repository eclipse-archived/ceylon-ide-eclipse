/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
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
