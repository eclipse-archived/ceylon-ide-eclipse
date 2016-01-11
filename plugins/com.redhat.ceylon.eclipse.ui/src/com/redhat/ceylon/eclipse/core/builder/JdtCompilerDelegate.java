package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.compiler.typechecker.io.impl.Helper.computeRelativePath;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.redhat.ceylon.javax.tools.JavaFileManager;
import com.redhat.ceylon.javax.tools.JavaFileObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.util.Util;

import com.redhat.ceylon.compiler.java.loader.CeylonClassReader;
import com.redhat.ceylon.compiler.java.loader.CeylonEnter;
import com.redhat.ceylon.compiler.java.loader.CeylonModelLoader;
import com.redhat.ceylon.compiler.java.tools.CeylonPhasedUnit;
import com.redhat.ceylon.compiler.java.tools.LanguageCompiler.CompilerDelegate;
import com.redhat.ceylon.model.loader.AbstractModelLoader;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.AnalysisError;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import com.redhat.ceylon.model.typechecker.util.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.ModuleImport;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.parser.RecognitionError;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.ModelState;
import com.redhat.ceylon.ide.common.model.BaseIdeModelLoader;
import com.redhat.ceylon.ide.common.model.BaseIdeModule;
import com.redhat.ceylon.ide.common.model.ProjectSourceFile;
import com.redhat.ceylon.ide.common.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.langtools.tools.javac.file.JavacFileManager;
import com.redhat.ceylon.langtools.tools.javac.tree.JCTree.JCCompilationUnit;
import com.redhat.ceylon.langtools.tools.javac.util.Context;
import com.redhat.ceylon.langtools.tools.javac.util.Names;
import com.redhat.ceylon.langtools.tools.javac.util.Position;
import com.redhat.ceylon.langtools.tools.javac.util.Position.LineMap;

final class JdtCompilerDelegate implements CompilerDelegate {
    private final IProject project;
    private final TypeChecker typeChecker;
    private final WeakReference<com.redhat.ceylon.langtools.tools.javac.util.Context> contextRef;
    private final Collection<PhasedUnit> unitsTypecheckedIncrementally;

    JdtCompilerDelegate(BaseIdeModelLoader modelLoader,
            IProject project, TypeChecker typeChecker,
            com.redhat.ceylon.langtools.tools.javac.util.Context context,
            Collection<PhasedUnit> unitsTypechecked) {
        this.project = project;
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
        if (CeylonBuilder.getModelState(project).ordinal() < ModelState.Compiled.ordinal()) {
            needingAdditionalCompilerPhases = CeylonBuilder.getUnits(project);
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
            CeylonBuilder.modelStates.put(project, ModelState.Compiled);
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
            ceylonEnter.addOutputModuleToClassPath(pu.getPackage().getModule());
        }
    }

    @Override
    public void loadStandardModules(AbstractModelLoader modelLoader) {}
    @Override
    public void setupSourceFileObjects(com.redhat.ceylon.langtools.tools.javac.util.List<JCCompilationUnit> trees,
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