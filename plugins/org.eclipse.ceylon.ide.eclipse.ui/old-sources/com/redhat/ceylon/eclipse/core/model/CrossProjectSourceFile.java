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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import org.eclipse.ceylon.ide.eclipse.core.typechecker.CrossProjectPhasedUnit;
import org.eclipse.ceylon.ide.eclipse.core.typechecker.ProjectPhasedUnit;

public class CrossProjectSourceFile extends ExternalSourceFile implements ICrossProjectReference, IResourceAware {
    public CrossProjectSourceFile(CrossProjectPhasedUnit phasedUnit) {
        super(phasedUnit);
    }

    @Override
    public IProject getResourceProject() {
        ProjectPhasedUnit ppu = getPhasedUnit().getOriginalProjectPhasedUnit();
        return ppu != null ? ppu.getResourceProject() : null;
    }

    @Override
    public IFolder getResourceRootFolder() {
        ProjectPhasedUnit ppu = getPhasedUnit().getOriginalProjectPhasedUnit();
        return ppu != null ? ppu.getResourceRootFolder() : null;
    }

    @Override
    public IFile getResourceFile() {
        ProjectPhasedUnit ppu = getPhasedUnit().getOriginalProjectPhasedUnit();
        return ppu != null ? ppu.getResourceFile() : null;
    }

    @Override
    public CrossProjectPhasedUnit getPhasedUnit() {
        return (CrossProjectPhasedUnit) super.getPhasedUnit();
    }
    
    public ProjectSourceFile getOriginalSourceFile() {
        ProjectPhasedUnit ppu = getOriginalPhasedUnit();
        return ppu != null ? (ProjectSourceFile) ppu.getUnit() : null;
    }

    @Override
    public ProjectPhasedUnit getOriginalPhasedUnit() {
        return getPhasedUnit().getOriginalProjectPhasedUnit();
    }
}
