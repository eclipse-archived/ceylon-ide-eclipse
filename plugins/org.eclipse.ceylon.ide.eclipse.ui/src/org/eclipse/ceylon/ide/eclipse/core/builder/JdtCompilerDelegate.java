package org.eclipse.ceylon.ide.eclipse.core.builder;

import static org.eclipse.ceylon.compiler.typechecker.io.impl.Helper.computeRelativePath;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.util.Util;

import org.eclipse.ceylon.compiler.java.loader.CeylonClassReader;
import org.eclipse.ceylon.compiler.java.loader.CeylonEnter;
import org.eclipse.ceylon.compiler.java.loader.CeylonModelLoader;
import org.eclipse.ceylon.compiler.java.tools.CeylonPhasedUnit;
import org.eclipse.ceylon.compiler.java.tools.LanguageCompiler;
import org.eclipse.ceylon.compiler.java.tools.LanguageCompiler.CompilerDelegate;
import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.compiler.typechecker.analyzer.AnalysisError;
import org.eclipse.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnit;
import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnits;
import org.eclipse.ceylon.compiler.typechecker.io.VirtualFile;
import org.eclipse.ceylon.compiler.typechecker.parser.RecognitionError;
import org.eclipse.ceylon.compiler.typechecker.tree.Message;
import org.eclipse.ceylon.ide.common.model.BaseIdeModelLoader;
import org.eclipse.ceylon.ide.common.model.BaseIdeModule;
import org.eclipse.ceylon.ide.common.model.CeylonProject;
import org.eclipse.ceylon.ide.common.model.ProjectSourceFile;
import org.eclipse.ceylon.ide.common.model.ProjectState;
import org.eclipse.ceylon.ide.common.typechecker.ProjectPhasedUnit;
import org.eclipse.ceylon.javax.tools.JavaFileManager;
import org.eclipse.ceylon.javax.tools.JavaFileObject;
import org.eclipse.ceylon.langtools.tools.javac.file.JavacFileManager;
import org.eclipse.ceylon.langtools.tools.javac.tree.JCTree.JCCompilationUnit;
import org.eclipse.ceylon.langtools.tools.javac.util.Context;
import org.eclipse.ceylon.langtools.tools.javac.util.Names;
import org.eclipse.ceylon.langtools.tools.javac.util.Position;
import org.eclipse.ceylon.langtools.tools.javac.util.Position.LineMap;
import org.eclipse.ceylon.model.loader.AbstractModelLoader;
import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.model.typechecker.model.ModuleImport;
import org.eclipse.ceylon.model.typechecker.model.Unit;
import org.eclipse.ceylon.model.typechecker.util.ModuleManager;

final class JdtCompilerDelegate implements CompilerDelegate {
    private final CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject;
    private final TypeChecker typeChecker;
    private final WeakReference<org.eclipse.ceylon.langtools.tools.javac.util.Context> contextRef;
    private final Collection<PhasedUnit> unitsTypecheckedIncrementally;

    JdtCompilerDelegate(BaseIdeModelLoader modelLoader,
            CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject, TypeChecker typeChecker,
            org.eclipse.ceylon.langtools.tools.javac.util.Context context,
            Collection<PhasedUnit> unitsTypechecked) {
        this.ceylonProject = ceylonProject;
        this.typeChecker = typeChecker;
        contextRef = new WeakReference<Context>(context);
        this.unitsTypecheckedIncrementally = unitsTypechecked;
    }

    @Override
    public ModuleManager getModuleManager() {
        return typeChecker.getPhasedUnits().getModuleManager();
    }

    @Override
    public PhasedUnit getExternalSourcePhasedUnit(
            VirtualFile srcDir, VirtualFile file) {
        return typeChecker.getPhasedUnits()
                .getPhasedUnitFromRelativePath(computeRelativePath(file, srcDir));
    }
    
    @Override
    public void typeCheck(java.util.List<PhasedUnit> listOfUnits) {
        Context context = contextRef.get();
        assert(context != null);
        Collection<PhasedUnit> needingAdditionalCompilerPhases = Collections.emptyList();
        if (! ceylonProject.getCompiled()) {
            needingAdditionalCompilerPhases = CeylonBuilder.getUnits(ceylonProject.getIdeArtifact());
        } else if (! unitsTypecheckedIncrementally.isEmpty()) {
            needingAdditionalCompilerPhases = unitsTypecheckedIncrementally;
        }
        if (! needingAdditionalCompilerPhases.isEmpty()) {
            for (PhasedUnit phasedUnit : needingAdditionalCompilerPhases) {
                assert(phasedUnit  instanceof ProjectPhasedUnit);
                ProjectPhasedUnit<IProject, IResource, IFolder, IFile> projectPhasedUnit = 
                        (ProjectPhasedUnit<IProject, IResource, IFolder, IFile>) phasedUnit;
                IFile resource = projectPhasedUnit.getResourceFile();
                File file = resource.getRawLocation().toFile();
                JavacFileManager fileManager = (JavacFileManager) context.get(JavaFileManager.class);
                Iterator<? extends JavaFileObject> files = fileManager.getJavaFileObjects(file).iterator();
                if (files.hasNext()) {
                    JavaFileObject fileObject = files.next();                    
                    char[] chars = new char[0];
                    try {
                        chars = Util.getResourceContentsAsCharArray(resource);
                    } catch (JavaModelException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    LineMap map = Position.makeLineMap(chars, chars.length, false);
                    CeylonPhasedUnit unitToAdd = new CeylonPhasedUnit(projectPhasedUnit, fileObject, map);
                    boolean alreadyInList = false;
                    for (PhasedUnit unitInList : listOfUnits) {
                        if (unitInList.getUnitFile().getPath().equals(unitToAdd.getUnitFile().getPath())) {
                            alreadyInList = true;
                            break;
                        }
                    }
                    if (! alreadyInList) {
                        listOfUnits.add(unitToAdd);
                    }
                }
            }
            ceylonProject.setState(ProjectState.getProjectState$compiled());
        }
    }
    
    private void buildListOfCompiledModules(Module module, Set<ProjectSourceFile<IProject,IResource,IFolder,IFile>> listOfModules) {
        if (module instanceof BaseIdeModule && ((BaseIdeModule) module).getIsProjectModule()) {
            Unit moduleUnit = module.getUnit();
            if (moduleUnit instanceof ProjectSourceFile && !listOfModules.contains(moduleUnit)) {
                listOfModules.add((ProjectSourceFile) moduleUnit);
                for (ModuleImport imp : module.getImports()) {
                    Module importedModule =imp.getModule();
                    buildListOfCompiledModules(importedModule, listOfModules);
                }
            }
        }
    }
    
    @Override
    public void visitModules(PhasedUnits phasedUnits) {
        Context context = contextRef.get();
        CeylonEnter ceylonEnter = CeylonEnter.instance(context);
        LanguageCompiler languageCompiler = (LanguageCompiler) LanguageCompiler.instance(context);
        assert(context != null);
        Set<ProjectSourceFile<IProject,IResource,IFolder,IFile>> compiledModules = new HashSet<>();  
        for (PhasedUnit pu : phasedUnits.getPhasedUnits()) {
            buildListOfCompiledModules(pu.getPackage().getModule(), compiledModules);
        }
        
        for (ProjectSourceFile<IProject,IResource,IFolder,IFile> compiledModule : compiledModules) {
            PhasedUnit pu = compiledModule.getPhasedUnit();
            boolean hasErrors = false;
            for (Message e : pu.getCompilationUnit().getErrors()) {
                if ( (e instanceof RecognitionError) || 
                     (e instanceof AnalysisError)) {
                    hasErrors = true;
                }
            }
            if (! hasErrors) {
                IFile moduleResource = compiledModule.getResourceFile();
                File moduleFile = moduleResource.getRawLocation().toFile();
                JavacFileManager fileManager = (JavacFileManager) context.get(JavaFileManager.class);
                Iterator<? extends JavaFileObject> files = fileManager.getJavaFileObjects(moduleFile).iterator();
                if (files.hasNext()) {
                    JavaFileObject fileObject = files.next();                    
                    char[] chars = new char[0];
                    try {
                        chars = Util.getResourceContentsAsCharArray(moduleResource);
                    } catch (JavaModelException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    LineMap map = Position.makeLineMap(chars, chars.length, false);
                    CeylonPhasedUnit cpu = new CeylonPhasedUnit(pu, fileObject, map);
                    phasedUnits.addPhasedUnit(pu.getUnitFile(), cpu);
                }
            }
            Module m = pu.getPackage().getModule();
            ceylonEnter.addOutputModuleToClassPath(m);
            languageCompiler.getCompiledModules().add(m);
            
        }
    }

    @Override
    public void loadStandardModules(AbstractModelLoader modelLoader) {}
    @Override
    public void setupSourceFileObjects(org.eclipse.ceylon.langtools.tools.javac.util.List<JCCompilationUnit> trees,
            AbstractModelLoader modelLoader) {
        final Context context = contextRef.get();
        if (context == null) return;
        CeylonModelLoader.setupSourceFileObjects(trees, CeylonClassReader.instance(context), Names.instance(context));
    }
    @Override
    public void resolveModuleDependencies(PhasedUnits phasedUnits) {}
    @Override
    public void loadPackageDescriptors(AbstractModelLoader modelLoader) {}

    @Override
    public ModuleSourceMapper getModuleSourceMapper() {
        return typeChecker.getPhasedUnits().getModuleSourceMapper();
    }
}