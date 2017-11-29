/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.typechecker;

import java.lang.ref.WeakReference;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnit;
import org.eclipse.ceylon.compiler.typechecker.context.TypecheckerUnit;
import org.eclipse.ceylon.compiler.typechecker.io.VirtualFile;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import org.eclipse.ceylon.ide.eclipse.core.model.EditedSourceFile;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.model.typechecker.util.ModuleManager;

public class EditedPhasedUnit extends ModifiablePhasedUnit {
    WeakReference<ProjectPhasedUnit> savedPhasedUnitRef;
    
    public EditedPhasedUnit(VirtualFile unitFile, VirtualFile srcDir,
            CompilationUnit cu, Package p, ModuleManager moduleManager,
            ModuleSourceMapper moduleSourceMapper,
            TypeChecker typeChecker, List<CommonToken> tokenStream, ProjectPhasedUnit savedPhasedUnit) {
        super(unitFile, srcDir, cu, p, moduleManager, moduleSourceMapper, typeChecker, tokenStream);
        savedPhasedUnitRef = new WeakReference<ProjectPhasedUnit>(savedPhasedUnit);
        if (savedPhasedUnit!=null) {
            savedPhasedUnit.addWorkingCopy(this);
        }
    }
    
    public EditedPhasedUnit(PhasedUnit other) {
        super(other);
    }

    @Override
    public TypecheckerUnit newUnit() {
        return new EditedSourceFile(this);
    }
    
    @Override
    public EditedSourceFile getUnit() {
        return (EditedSourceFile) super.getUnit();
    }

    public ProjectPhasedUnit getOriginalPhasedUnit() {
        return savedPhasedUnitRef.get();
    }
    
    @Override
    public IFile getResourceFile() {
        ProjectPhasedUnit originalPhasedUnit = 
                getOriginalPhasedUnit();
        return originalPhasedUnit == null ? null
                : originalPhasedUnit.getResourceFile();
    }
    

    @Override
    public IFolder getResourceRootFolder() {
        return getOriginalPhasedUnit() == null ? null
                : getOriginalPhasedUnit().getResourceRootFolder();
    }
    

    @Override
    public IProject getResourceProject() {
        return getOriginalPhasedUnit() == null ? null
                : getOriginalPhasedUnit().getResourceProject();
    }
    
    @Override
    public boolean isAllowedToChangeModel(Declaration declaration) {
        return !IdePhasedUnit.isCentralModelDeclaration(declaration);
    }
}
