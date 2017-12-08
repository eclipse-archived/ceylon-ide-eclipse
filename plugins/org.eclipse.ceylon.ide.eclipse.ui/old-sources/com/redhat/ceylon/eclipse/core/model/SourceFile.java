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

import org.eclipse.ceylon.model.typechecker.util.ModuleManager;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.ide.eclipse.core.typechecker.IdePhasedUnit;
import org.eclipse.ceylon.ide.eclipse.util.SingleSourceUnitPackage;

public abstract class SourceFile extends CeylonUnit {
    
    public SourceFile(IdePhasedUnit phasedUnit) {
        createPhasedUnitRef(phasedUnit);
    }

    @Override
    protected IdePhasedUnit setPhasedUnitIfNecessary() { return phasedUnitRef.get(); }
    
    @Override
    public String getSourceFileName() {
        return getFilename();
    }

    @Override
    public String getSourceRelativePath() {
        return getRelativePath();
    }

    @Override
    public String getSourceFullPath() {
        return getFullPath();
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
