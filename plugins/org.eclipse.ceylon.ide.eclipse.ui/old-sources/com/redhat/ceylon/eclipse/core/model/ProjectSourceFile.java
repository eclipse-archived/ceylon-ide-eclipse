/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.model;

import org.antlr.runtime.CommonTokenStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import org.eclipse.ceylon.compiler.java.loader.UnknownTypeCollector;
import org.eclipse.ceylon.compiler.java.runtime.metamodel.ModelError;
import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.model.typechecker.util.ModuleManager;
import org.eclipse.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnit;
import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnits;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import org.eclipse.ceylon.ide.eclipse.core.typechecker.IdePhasedUnitUtils;
import org.eclipse.ceylon.ide.eclipse.core.typechecker.ProjectPhasedUnit;
import org.eclipse.ceylon.ide.eclipse.core.vfs.vfsJ2C;
import org.eclipse.ceylon.ide.common.model.ModifiableSourceFile;
import org.eclipse.ceylon.ide.common.model.delta.CompilationUnitDelta;
import org.eclipse.ceylon.ide.common.model.delta.DeltaBuilderFactory;
import org.eclipse.ceylon.ide.common.vfs.FileVirtualFile;
import org.eclipse.ceylon.ide.common.vfs.FolderVirtualFile;
import org.eclipse.ceylon.ide.eclipse.util.CeylonSourceParser;
import org.eclipse.ceylon.ide.eclipse.util.SingleSourceUnitPackage;

public class ProjectSourceFile extends ModifiableSourceFile {
    static private DeltaBuilderFactory deltaBuilderFactory = 
            new DeltaBuilderFactory();
    
    public ProjectSourceFile(ProjectPhasedUnit phasedUnit) {
        super(phasedUnit);
    }
    
    @Override
    public ProjectPhasedUnit getPhasedUnit() {
        return (ProjectPhasedUnit) super.getPhasedUnit();
    }

    @Override
    public IProject getResourceProject() {
        ProjectPhasedUnit phasedUnit = getPhasedUnit();
        return phasedUnit==null ? null :
            phasedUnit.getResourceProject();
    }

    
    @Override
    public IFile getResourceFile() {
        ProjectPhasedUnit phasedUnit = getPhasedUnit();
        return phasedUnit==null ? null :
            phasedUnit.getResourceFile();
    }

    @Override
    public IFolder getResourceRootFolder() {
        ProjectPhasedUnit phasedUnit = getPhasedUnit();
        return phasedUnit==null ? null :
            phasedUnit.getResourceRootFolder();
    }
    
    public CompilationUnitDelta buildDeltaAgainstModel() {
        try {
            final ProjectPhasedUnit modelPhaseUnit = getPhasedUnit();
            if (modelPhaseUnit != null) {
                final FileVirtualFile<IResource, IFolder, IFile> 
                virtualSrcFile = 
                        vfsJ2C.createVirtualFile(
                                modelPhaseUnit.getResourceFile());
                final FolderVirtualFile<IResource, IFolder, IFile> 
                virtualSrcDir = 
                        vfsJ2C.createVirtualFolder(
                                modelPhaseUnit.getResourceRootFolder());
                final TypeChecker currentTypechecker = 
                        modelPhaseUnit.getTypeChecker();
                PhasedUnits phasedUnits = 
                        currentTypechecker.getPhasedUnits();
                final ModuleManager currentModuleManager = 
                        phasedUnits.getModuleManager();
                final ModuleSourceMapper currentModuleSourceMapper = 
                        phasedUnits.getModuleSourceMapper();
                Package singleSourceUnitPackage = 
                        new SingleSourceUnitPackage(getPackage(), 
                                virtualSrcFile.getPath());
                PhasedUnit lastPhasedUnit = 
                        new CeylonSourceParser<PhasedUnit>() {
                    
                    @Override
                    protected String getCharset() {
                        try {
                            return modelPhaseUnit.getResourceProject()
                                    .getDefaultCharset();
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    
                    @SuppressWarnings("unchecked")
                    @Override
                    protected PhasedUnit createPhasedUnit(
                            CompilationUnit cu, Package pkg, 
                            CommonTokenStream tokenStream) {
                        return new PhasedUnit(virtualSrcFile, 
                                virtualSrcDir, cu, pkg, 
                                currentModuleManager, 
                                currentModuleSourceMapper,
                                currentTypechecker.getContext(),
                                tokenStream.getTokens()) {
                            @Override
                            protected boolean isAllowedToChangeModel(Declaration declaration) {
                                return ! IdePhasedUnitUtils.isCentralModelDeclaration(declaration);
                            }
                        };
                    }
                }.parseFileToPhasedUnit(
                        currentModuleManager, 
                        currentTypechecker, 
                        virtualSrcFile, 
                        virtualSrcDir, 
                        singleSourceUnitPackage);

                if (lastPhasedUnit != null) {
                    lastPhasedUnit.validateTree();
                    lastPhasedUnit.visitSrcModulePhase();
                    lastPhasedUnit.visitRemainingModulePhase();
                    lastPhasedUnit.scanDeclarations();
                    lastPhasedUnit.scanTypeDeclarations();
                    lastPhasedUnit.validateRefinement();
                    lastPhasedUnit.analyseTypes();
                    lastPhasedUnit.analyseFlow();
                    UnknownTypeCollector utc = new UnknownTypeCollector();
                    lastPhasedUnit.getCompilationUnit().visit(utc);

                    if (lastPhasedUnit.getCompilationUnit()
                            .getErrors().isEmpty()) {
                        return deltaBuilderFactory.buildDeltas(
                                modelPhaseUnit, lastPhasedUnit);
                    }
                }
            }
        } catch(Exception e) {
        } catch(ceylon.language.AssertionError | ModelError e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
