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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IClassFile;

import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder;
import org.eclipse.ceylon.ide.common.typechecker.CrossProjectPhasedUnit;
import org.eclipse.ceylon.ide.common.typechecker.ProjectPhasedUnit;
import org.eclipse.ceylon.model.typechecker.model.Package;

public class CrossProjectBinaryUnit extends CeylonBinaryUnit implements ICrossProjectReference {
    private WeakReference<ProjectPhasedUnit> originalProjectPhasedUnitRef = new WeakReference<ProjectPhasedUnit>(null);

    public CrossProjectBinaryUnit(IClassFile typeRoot, String fileName, String relativePath, String fullPath, Package pkg) {
        super(typeRoot, fileName, relativePath, fullPath, pkg);
    }
    
    @Override
    public IProject getResourceProject() {
        CrossProjectPhasedUnit<IProject,IResource,IFolder,IFile> pu = getPhasedUnit();
        ProjectPhasedUnit<IProject,IResource,IFolder,IFile> ppu = pu!=null ? pu.getOriginalProjectPhasedUnit() : null;
        return ppu != null ? ppu.getResourceProject() : null;
    }

    @Override
    public IFolder getResourceRootFolder() {
        CrossProjectPhasedUnit<IProject,IResource,IFolder,IFile> pu = getPhasedUnit();
        ProjectPhasedUnit<IProject,IResource,IFolder,IFile> ppu = pu!=null ? pu.getOriginalProjectPhasedUnit() : null;
        return ppu != null ? ppu.getResourceRootFolder() : null;
    }

    @Override
    public IFile getResourceFile() {
        ProjectPhasedUnit<IProject,IResource,IFolder,IFile> ppu = 
                getPhasedUnit()
                    .getOriginalProjectPhasedUnit();
        return ppu != null ? ppu.getResourceFile() : null;
    }

    @Override
    public CrossProjectPhasedUnit<IProject,IResource,IFolder,IFile> getPhasedUnit() {
        return (CrossProjectPhasedUnit<IProject,IResource,IFolder,IFile>) super.getPhasedUnit();
    }
    
    public ProjectSourceFile getOriginalSourceFile() {
        ProjectPhasedUnit<IProject,IResource,IFolder,IFile> ppu = getOriginalPhasedUnit();
        return ppu != null ? (ProjectSourceFile) ppu.getUnit() : null;
    }

    @Override
    public ProjectPhasedUnit getOriginalPhasedUnit() {
        ProjectPhasedUnit originalPhasedUnit = originalProjectPhasedUnitRef.get();
        if (originalPhasedUnit == null) {
            JDTModule module = getModule();        
            IProject originalProject = module.getOriginalProject();
            if (originalProject != null) {
                TypeChecker originalTypeChecker = CeylonBuilder.getProjectTypeChecker(originalProject);
                if (originalTypeChecker != null) {
                    String sourceRelativePath = module.toSourceUnitRelativePath(getRelativePath());
                    originalPhasedUnit = (ProjectPhasedUnit) originalTypeChecker.getPhasedUnitFromRelativePath(sourceRelativePath);
                    if (originalPhasedUnit != null) {
                        originalProjectPhasedUnitRef = new WeakReference<ProjectPhasedUnit>(originalPhasedUnit);
                    }
                }
            }
        }

        return originalPhasedUnit;
    }
}
