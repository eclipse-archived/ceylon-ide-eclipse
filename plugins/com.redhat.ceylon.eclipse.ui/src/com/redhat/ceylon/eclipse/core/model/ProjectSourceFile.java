package com.redhat.ceylon.eclipse.core.model;

import org.antlr.runtime.CommonTokenStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.compiler.java.loader.UnknownTypeCollector;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.model.typechecker.util.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.core.typechecker.IdePhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.eclipse.core.vfs.ResourceVirtualFile;
import com.redhat.ceylon.ide.common.model.delta.CompilationUnitDelta;
import com.redhat.ceylon.ide.common.model.delta.DeltaBuilderFactory;
import com.redhat.ceylon.eclipse.util.CeylonSourceParser;
import com.redhat.ceylon.eclipse.util.SingleSourceUnitPackage;

public class ProjectSourceFile extends SourceFile implements IResourceAware {
    static private DeltaBuilderFactory deltaBuilderFactory = new DeltaBuilderFactory();
    
    public ProjectSourceFile(ProjectPhasedUnit phasedUnit) {
        super(phasedUnit);
    }
    
    @Override
    public ProjectPhasedUnit getPhasedUnit() {
        return (ProjectPhasedUnit) super.getPhasedUnit();
    }

    @Override
    public IProject getProjectResource() {
        return getPhasedUnit().getProjectResource();
    }

    
    @Override
    public IFile getFileResource() {
        return getPhasedUnit().getSourceFileResource();
    }

    @Override
    public IFolder getRootFolderResource() {
        return getPhasedUnit().getSourceFolderResource();
    }
    
    public CompilationUnitDelta buildDeltaAgainstModel() {
        try {
            final ProjectPhasedUnit modelPhaseUnit = getPhasedUnit();
            if (modelPhaseUnit != null) {
                final ResourceVirtualFile virtualSrcFile = ResourceVirtualFile.createResourceVirtualFile(modelPhaseUnit.getSourceFileResource());
                final ResourceVirtualFile virtualSrcDir = ResourceVirtualFile.createResourceVirtualFile(modelPhaseUnit.getSourceFolderResource());
                final TypeChecker currentTypechecker = modelPhaseUnit.getTypeChecker();
                final ModuleManager currentModuleManager = currentTypechecker.getPhasedUnits().getModuleManager();
                final ModuleSourceMapper currentModuleSourceMapper = currentTypechecker.getPhasedUnits().getModuleSourceMapper();
                Package singleSourceUnitPackage = new SingleSourceUnitPackage(getPackage(), virtualSrcFile.getPath());
                PhasedUnit lastPhasedUnit = new CeylonSourceParser<PhasedUnit>() {
                    
                    @Override
                    protected String getCharset() {
                        try {
                            return modelPhaseUnit.getProjectResource().getDefaultCharset();
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    
                    @SuppressWarnings("unchecked")
                    @Override
                    protected PhasedUnit createPhasedUnit(CompilationUnit cu, Package pkg, CommonTokenStream tokenStream) {
                        return new PhasedUnit(virtualSrcFile, 
                                virtualSrcDir, cu, pkg, 
                                currentModuleManager, 
                                currentModuleSourceMapper,
                                currentTypechecker.getContext(),
                                tokenStream.getTokens()) {
                            @Override
                            protected boolean isAllowedToChangeModel(Declaration declaration) {
                                return ! IdePhasedUnit.isCentralModelDeclaration(declaration);
                            }

                            @Override
                            public void scanDeclarations() {
                                super.scanDeclarations();
                                IdePhasedUnit.addCentralModelOverloads(getUnit());
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

                    if (lastPhasedUnit.getCompilationUnit().getErrors().isEmpty()) {
                        return deltaBuilderFactory.buildDeltas(modelPhaseUnit, lastPhasedUnit);
                    }
                }
            }
        } catch(Exception e) {
        } catch(ceylon.language.AssertionError e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
