package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.compiler.typechecker.io.impl.Helper.computeRelativePath;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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
import com.redhat.ceylon.eclipse.core.model.JDTModelLoader;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.core.model.ProjectSourceFile;
import com.redhat.ceylon.eclipse.core.typechecker.ProjectPhasedUnit;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Position;
import com.sun.tools.javac.util.Position.LineMap;

final class JdtCompilerDelegate implements CompilerDelegate {
    private final IProject project;
    private final TypeChecker typeChecker;
    private final WeakReference<com.sun.tools.javac.util.Context> contextRef;
    private final Collection<PhasedUnit> unitsTypecheckedIncrementally;

    JdtCompilerDelegate(JDTModelLoader modelLoader,
            IProject project, TypeChecker typeChecker,
            com.sun.tools.javac.util.Context context,
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
                ProjectPhasedUnit projectPhasedUnit = (ProjectPhasedUnit) phasedUnit;
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
    
    private void buildListOfCompiledModules(Module module, Set<ProjectSourceFile> listOfModules) {
        if (module instanceof JDTModule && ((JDTModule) module).isProjectModule()) {
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
        Set<ProjectSourceFile> compiledModules = new HashSet<>();  
        for (PhasedUnit pu : phasedUnits.getPhasedUnits()) {
            buildListOfCompiledModules(pu.getPackage().getModule(), compiledModules);
        }
        
        for (ProjectSourceFile compiledModule : compiledModules) {
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
    public void setupSourceFileObjects(com.sun.tools.javac.util.List<JCCompilationUnit> trees,
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