/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.typechecker;

import java.lang.ref.WeakReference;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IProject;

import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import org.eclipse.ceylon.compiler.typechecker.context.TypecheckerUnit;
import org.eclipse.ceylon.compiler.typechecker.io.VirtualFile;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder;
import org.eclipse.ceylon.ide.eclipse.core.model.CrossProjectSourceFile;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.model.typechecker.util.ModuleManager;

public class CrossProjectPhasedUnit extends ExternalPhasedUnit {

    private WeakReference<IProject> originalProjectRef = new WeakReference<IProject>(null);
    private WeakReference<ProjectPhasedUnit> originalProjectPhasedUnitRef = new WeakReference<ProjectPhasedUnit>(null);
    
    public CrossProjectPhasedUnit(CrossProjectPhasedUnit other) {
        super(other);
        originalProjectRef = new WeakReference<IProject>(other.originalProjectRef.get());
        originalProjectPhasedUnitRef = new WeakReference<ProjectPhasedUnit>(other.getOriginalProjectPhasedUnit());
        
    }

    public CrossProjectPhasedUnit(VirtualFile unitFile, VirtualFile srcDir,
            CompilationUnit cu, Package p, ModuleManager moduleManager,
            ModuleSourceMapper moduleSourceMapper, TypeChecker typeChecker, List<CommonToken> tokenStream, IProject originalProject) {
        super(unitFile, srcDir, cu, p, moduleManager, moduleSourceMapper, typeChecker, tokenStream);
        originalProjectRef = new WeakReference<IProject>(originalProject);
    }
    
    public ProjectPhasedUnit getOriginalProjectPhasedUnit() {
        ProjectPhasedUnit originalPhasedUnit = originalProjectPhasedUnitRef.get(); 
        if (originalPhasedUnit == null) {
            IProject originalProject = originalProjectRef.get();
            if (originalProject != null) {
                TypeChecker originalTypeChecker = CeylonBuilder.getProjectTypeChecker(originalProject);
                if (originalTypeChecker != null) {
                    originalPhasedUnit = (ProjectPhasedUnit) originalTypeChecker.getPhasedUnitFromRelativePath(getPathRelativeToSrcDir());
                    originalProjectPhasedUnitRef = new WeakReference<ProjectPhasedUnit>(originalPhasedUnit);
                }
            }
        }
        return originalPhasedUnit;
    }
    
    @Override
    protected TypecheckerUnit newUnit() {
        return new CrossProjectSourceFile(this);
    }

    @Override
    public CrossProjectSourceFile getUnit() {
        return (CrossProjectSourceFile) super.getUnit();
    }
}
