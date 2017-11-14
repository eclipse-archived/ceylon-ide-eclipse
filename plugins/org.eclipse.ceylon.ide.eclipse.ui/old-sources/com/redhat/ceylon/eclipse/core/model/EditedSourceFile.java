/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import org.eclipse.ceylon.ide.eclipse.core.typechecker.EditedPhasedUnit;
import org.eclipse.ceylon.ide.eclipse.core.typechecker.ProjectPhasedUnit;

public class EditedSourceFile extends ModifiableSourceFile {
    public EditedSourceFile(EditedPhasedUnit phasedUnit) {
        super(phasedUnit);
    }

    @Override
    public EditedPhasedUnit getPhasedUnit() {
        return (EditedPhasedUnit) super.getPhasedUnit();
    }
    
    public ProjectSourceFile getOriginalSourceFile() {
        final EditedPhasedUnit pu = getPhasedUnit();
        ProjectPhasedUnit originalPhasedUnit = pu==null ? null : pu.getOriginalPhasedUnit();
        return originalPhasedUnit == null ? null : (ProjectSourceFile) originalPhasedUnit.getUnit();
    }
    
    @Override
    public IProject getResourceProject() {
        EditedPhasedUnit pu = getPhasedUnit();
        return pu==null ? null : pu.getResourceProject();
    }

    
    @Override
    public IFile getResourceFile() {
        EditedPhasedUnit pu = getPhasedUnit();
        return pu==null ? null : pu.getResourceFile();
    }

    @Override
    public IFolder getResourceRootFolder() {
        EditedPhasedUnit pu = getPhasedUnit();
        return pu==null ? null : pu.getResourceRootFolder();
    }
}
