/*
 * Copyright Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the authors tag. All rights reserved.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License version 2.
 * 
 * This particular file is subject to the "Classpath" exception as provided in the 
 * LICENSE file that accompanied this code.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License,
 * along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package com.redhat.ceylon.eclipse.core.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.CommonTokenStream;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;

import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.compiler.java.loader.model.LazyModuleSourceMapper;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleHelper;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.io.impl.ZipFileVirtualFile;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.util.ModuleManagerFactory;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.typechecker.CrossProjectPhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.CeylonSourceParser;
import com.redhat.ceylon.model.cmr.ArtifactResult;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.ModuleImport;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.util.ModuleManager;

/**
 * @author david
 *
 */
public class JDTModuleSourceMapper extends LazyModuleSourceMapper {

    private IJavaProject javaProject;
    private Set<String> sourceModules;
    private TypeChecker typeChecker;
    private boolean loadDependenciesFromModelLoaderFirst;

    public Set<String> getSourceModules() {
        return sourceModules;
    }

    public IJavaProject getJavaProject() {
        return javaProject;
    }

    public JDTModuleSourceMapper(Context context, IJavaProject javaProject, JDTModuleManager moduleManager) {
        super(context, moduleManager);
        this.javaProject = javaProject;
        if (javaProject == null) {
            loadDependenciesFromModelLoaderFirst = false;
        } else {
            loadDependenciesFromModelLoaderFirst = CeylonBuilder.loadDependenciesFromModelLoaderFirst(javaProject.getProject());
        }
        sourceModules = new HashSet<String>();
        if (! loadDependenciesFromModelLoaderFirst) {
            sourceModules.add(Module.LANGUAGE_MODULE_NAME);
        }
        // OK this sucks, but it's the best I could find :(
        moduleManager.setModuleSourceMapper(this);
    }
    
    @Override
    protected JDTModuleManager getModuleManager() {
        return (JDTModuleManager) super.getModuleManager();
    }

    @Override
    public void resolveModule(ArtifactResult artifact, Module module, ModuleImport moduleImport, 
        LinkedList<Module> dependencyTree, List<PhasedUnits> phasedUnitsOfDependencies, boolean forCompiledModule) {
        File artifactFile = artifact.artifact();
        if (getModuleManager().isModuleLoadedFromSource(module.getNameAsString()) && artifactFile.getName().endsWith(ArtifactContext.CAR)) {
            ArtifactContext artifactContext = new ArtifactContext(module.getNameAsString(), module.getVersion(), ArtifactContext.SRC);
            RepositoryManager repositoryManager = getContext().getRepositoryManager();
            Exception exceptionOnGetArtifact = null;
            ArtifactResult sourceArtifact = null;
            try {
                sourceArtifact = repositoryManager.getArtifactResult(artifactContext);
            } catch (Exception e) {
                exceptionOnGetArtifact = e;
            }
            if ( sourceArtifact == null ) {
                ModuleHelper.buildErrorOnMissingArtifact(artifactContext, module, moduleImport, dependencyTree, exceptionOnGetArtifact, this);
            } else {
                artifact = sourceArtifact;
            }
            
        }
        if (module instanceof JDTModule) {
            ((JDTModule) module).setArtifact(artifact);
        }
        if (! getModuleManager().isModuleLoadedFromCompiledSource(module.getNameAsString())) {
            File file = artifact.artifact();
            if (artifact.artifact().getName().endsWith(".src")) {
                sourceModules.add(module.getNameAsString());
                file = new File(file.getAbsolutePath().replaceAll("\\.src$", ".car"));
            }
        }
        try {
            super.resolveModule(artifact, module, moduleImport, dependencyTree, phasedUnitsOfDependencies, forCompiledModule);
        } catch(Exception e) {
            if (module instanceof JDTModule) {
                CeylonPlugin.getInstance().getLog().log(new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID, "Failed resolving module " + module.getSignature(), e));
                ((JDTModule) module).setResolutionException(e);
            }
        }
    }

    @Override
    public void visitModuleFile() {
        Package currentPkg = getCurrentPackage();
        sourceModules.add(currentPkg.getNameAsString());
        super.visitModuleFile();
    }
    

    // Todo : to push into the base ModelManager class
    public void addTopLevelModuleError() {
        addErrorToModule(new ArrayList<String>(), 
                "A module cannot be defined at the top level of the hierarchy");
    }
    
    public class ExternalModulePhasedUnits extends PhasedUnits {
        private IProject referencedProject = null;
//        private VirtualFile sourceDirectory = null;

        public ExternalModulePhasedUnits(Context context,
                ModuleManagerFactory moduleManagerFactory) {
            super(context, moduleManagerFactory);
        }

        @Override
        protected void parseFile(final VirtualFile file, final VirtualFile srcDir)
                throws Exception {
            if (file.getName().endsWith(".ceylon")) {
                parseFile(file, srcDir, getCurrentPackage());
            }
        }
        
        /*
         *  This additional method is when we have to parse a new file, into a specific package of an existing archive  
         */
        public void parseFile(final VirtualFile file, final VirtualFile srcDir, final Package pkg) {
            PhasedUnit phasedUnit = new CeylonSourceParser<PhasedUnit>() {
                @Override
                protected String getCharset() {
                    try {
                        //TODO: is this correct? does this file actually
                        //      live in the project, or is it external?
                        //       should VirtualFile have a getCharset()?
                        return javaProject != null ?
                                javaProject.getProject().getDefaultCharset()
                                : ResourcesPlugin.getWorkspace().getRoot().getDefaultCharset();
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                
                @SuppressWarnings("unchecked")
                @Override
                protected PhasedUnit createPhasedUnit(CompilationUnit cu, Package pkg, CommonTokenStream tokenStream) {
                    if (referencedProject == null) {
                        return new ExternalPhasedUnit(file, srcDir, cu, 
                                pkg, getModuleManager(), JDTModuleSourceMapper.this,
                                getTypeChecker(), tokenStream.getTokens());
                    }
                    else {
                        return new CrossProjectPhasedUnit(file, srcDir, cu, 
                                pkg, getModuleManager(), JDTModuleSourceMapper.this,
                                getTypeChecker(), tokenStream.getTokens(), referencedProject);
                    }
                }
            }.parseFileToPhasedUnit(getModuleManager(), getTypeChecker(), file, srcDir, pkg);

            addPhasedUnit(file, phasedUnit);
        }

        @Override
        public void parseUnit(VirtualFile srcDir) {
//            sourceDirectory = srcDir;
            if (srcDir instanceof ZipFileVirtualFile && javaProject != null) {
                ZipFileVirtualFile zipFileVirtualFile = (ZipFileVirtualFile) srcDir;
                String archiveName = zipFileVirtualFile.getPath();
                try {
                    for (IProject refProject : javaProject.getProject().getReferencedProjects()) {
                        if (archiveName.contains(CeylonBuilder.getCeylonModulesOutputDirectory(refProject).getAbsolutePath())) {
                            referencedProject = refProject;
                            break;
                        }
                    }
                } catch (CoreException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            super.parseUnit(srcDir);
        }
    }

    @Override
    protected PhasedUnits createPhasedUnits() {
        ModuleManagerFactory moduleManagerFactory = new ModuleManagerFactory() {
            @Override
            public ModuleManager createModuleManager(Context context) {
                return getModuleManager();
            }

            @Override
            public ModuleSourceMapper createModuleManagerUtil(Context context, ModuleManager moduleManager) {
                return JDTModuleSourceMapper.this;
            }
        };
        

        return new ExternalModulePhasedUnits(getContext(), moduleManagerFactory);
    }

    public TypeChecker getTypeChecker() {
        return typeChecker;
    }

    public void setTypeChecker(TypeChecker typeChecker) {
        this.typeChecker = typeChecker;
    }

    public boolean isLoadDependenciesFromModelLoaderFirst() {
        return loadDependenciesFromModelLoaderFirst;
    }

    public JDTModule getArchiveModuleFromSourcePath(String sourceUnitPath) {
        for (Module m : typeChecker.getContext().getModules().getListOfModules()) {
            if (m instanceof JDTModule) {
                JDTModule module = (JDTModule) m;
                if (module.isCeylonArchive()) {
                    if (sourceUnitPath.startsWith(module.getSourceArchivePath() + "!")) {
                        return module;
                    }
                }
            }
        }
        return null; 
    }
    
    public JDTModule getArchiveModuleFromSourcePath(IPath sourceUnitPath) {
        return getArchiveModuleFromSourcePath(sourceUnitPath.toOSString());
    }

    @Override
    protected void addToPhasedUnitsOfDependencies(
            PhasedUnits modulePhasedUnits,
            List<PhasedUnits> phasedUnitsOfDependencies, Module module) {
        super.addToPhasedUnitsOfDependencies(modulePhasedUnits,
                phasedUnitsOfDependencies, module);
        if (module instanceof JDTModule) {
            ((JDTModule) module).setSourcePhasedUnits((ExternalModulePhasedUnits) modulePhasedUnits);
        }
    }
    
    @Override
    public Context getContext() {
        return super.getContext();
    }
}
