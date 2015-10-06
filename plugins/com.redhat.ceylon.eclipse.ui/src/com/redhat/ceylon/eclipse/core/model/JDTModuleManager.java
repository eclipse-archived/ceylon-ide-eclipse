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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;

import com.redhat.ceylon.common.Backend;
import com.redhat.ceylon.compiler.java.util.Util;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.model.cmr.JDKUtils;
import com.redhat.ceylon.model.loader.AbstractModelLoader;
import com.redhat.ceylon.model.loader.model.LazyModuleManager;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.ModuleImport;
import com.redhat.ceylon.model.typechecker.model.Modules;
import com.redhat.ceylon.model.typechecker.model.Package;

/**
 * @author david
 *
 */
public class JDTModuleManager extends LazyModuleManager {

    private JDTModelLoader modelLoader;
    private IJavaProject javaProject;
    private Set<String> sourceModules;
    private TypeChecker typeChecker;
    private boolean loadDependenciesFromModelLoaderFirst;
    private JDTModuleSourceMapper moduleSourceMapper;

    public Set<String> getSourceModules() {
        return sourceModules;
    }

    public IJavaProject getJavaProject() {
        return javaProject;
    }

    public JDTModuleManager(Context context, IJavaProject javaProject) {
        super();
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
    }
    /*
     * TODO : Remove when the package creation (and module binding) in ModuleManager will be done with a method 
     * that can be overriden (createPackage, as suggested here - a "" name parameter correspond to the empty package)
     * Then we can only override this new createPackage method with our already-existing one
     */
    
    @Override
    public void initCoreModules(Modules modules) {
        this.modules = modules;
        if ( modules.getLanguageModule() == null) {

            //build default module (module in which packages belong to when not explicitly under a module
            final List<String> defaultModuleName = Collections.singletonList(Module.DEFAULT_MODULE_NAME);
            final JDTModule defaultModule = createModule(defaultModuleName, "unversioned");
            defaultModule.setDefault(true);
            defaultModule.setAvailable(true);
            defaultModule.setProjectModule();
            modules.getListOfModules().add(defaultModule);
            modules.setDefaultModule(defaultModule);

            //create language module and add it as a dependency of defaultModule
            //since packages outside a module cannot declare dependencies
            final List<String> languageName = Arrays.asList("ceylon", "language");
            Module languageModule = createModule(languageName, TypeChecker.LANGUAGE_MODULE_VERSION);
            languageModule.setLanguageModule(languageModule);
            languageModule.setAvailable(false); //not available yet
            modules.setLanguageModule(languageModule);
            modules.getListOfModules().add(languageModule);
            defaultModule.addImport(new ModuleImport(languageModule, false, false));
            defaultModule.setLanguageModule(languageModule);

            //build empty package
            createPackage("", defaultModule);
        }
        super.initCoreModules(modules);
    }
    
    @Override
    public Package createPackage(String pkgName, Module module) {
        return getModelLoader().findOrCreatePackage(module, pkgName);
    }

    void setModelLoader(JDTModelLoader modelLoader) {
        this.modelLoader = modelLoader;
    }
    
    void setModuleSourceMapper(JDTModuleSourceMapper moduleSourceMapper){
        this.moduleSourceMapper = moduleSourceMapper;
    }
    
    @Override
    public synchronized JDTModelLoader getModelLoader() {
        if(modelLoader == null){
            Modules modules = getModules();
            // the JDTModelLoader sets the reference to itself in the ModuleManager 
            // at the beginning of its constructor, so that it's not necessary to assign it here
            // This avoids the weak ref entry of the model loaders static hash map 
            // to be freed just after it's set at the end of the JDTModelLoader constructor.
            return new JDTModelLoader(this, moduleSourceMapper, modules);
        }
        return modelLoader;
    }

    public boolean isExternalModuleLoadedFromSource(String moduleName){
        return sourceModules.contains(moduleName);
    }
    
    /**
     * Return true if this module should be loaded from source we are compiling
     * and not from its compiled artifact at all. Returns false by default, so
     * modules will be loaded from their compiled artifact.
     */
    @Override
    public boolean isModuleLoadedFromSource(String moduleName){
        if (isExternalModuleLoadedFromSource(moduleName)) {
            return true;
        }
        if (isModuleLoadedFromCompiledSource(moduleName)) {
            return true;
        }
        return false;
    }

    public boolean isModuleLoadedFromCompiledSource(String moduleName) {
        if (javaProject == null) {
            return false;
        }
        
        if (moduleFileInProject(moduleName, javaProject)) {
            return true;
        }

        if (!loadDependenciesFromModelLoaderFirst) {
            try {
                IProject project = javaProject.getProject();
                for (IProject p: project.getReferencedProjects()) {
                    if (p.isAccessible() && 
                        moduleFileInProject(moduleName, JavaCore.create(p))) {
                        return true;
                    }
                }
            } 
            catch (CoreException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static boolean moduleFileInProject(String moduleName, IJavaProject p) {
        if (p == null) {
            return false;
        }
        try {
            for (IPackageFragmentRoot sourceFolder: p.getPackageFragmentRoots()) {
                if (!sourceFolder.isArchive() &&
                    sourceFolder.exists() &&
                    sourceFolder.getKind()==IPackageFragmentRoot.K_SOURCE &&
                    sourceFolder.getPackageFragment(moduleName).exists()) {
                    return true;
                }
                /*IPath moduleFile = sourceFolder.append(moduleName.replace('.', '/') + 
                        "/module.ceylon").makeRelativeTo(p.getFullPath());
                if (p.getFile(moduleFile).exists()) {
                    return true;
                }*/
            }
        } 
        catch (JavaModelException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    protected JDTModule createModule(List<String> moduleName, String version) {
        JDTModule module = null;
        String moduleNameString = Util.getName(moduleName);
        List<IPackageFragmentRoot> roots = new ArrayList<IPackageFragmentRoot>();
        if (javaProject != null) {
            try {
                if(moduleNameString.equals(Module.DEFAULT_MODULE_NAME)){
                    // Add the list of source package fragment roots
                    for (IPackageFragmentRoot root : javaProject.getPackageFragmentRoots()) {
                        if (root.exists()
                        		&& javaProject.isOnClasspath(root)) {
                            IClasspathEntry entry = root.getResolvedClasspathEntry();
                            if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE && !root.isExternal()) {
                                roots.add(root);
                            }
                        }
                    }
                } else {
                    for (IPackageFragmentRoot root : javaProject.getPackageFragmentRoots()) {
                        if (root.exists()
                                && javaProject.isOnClasspath(root)) {
                            if(JDKUtils.isJDKModule(moduleNameString)){
                                // find the first package that exists in this root
                                for(String pkg : JDKUtils.getJDKPackagesByModule(moduleNameString)){
                                    if (root.getPackageFragment(pkg).exists()) {
                                        roots.add(root);
                                        break;
                                    }
                                }
                            }else if(JDKUtils.isOracleJDKModule(moduleNameString)){
                                // find the first package that exists in this root
                                for(String pkg : JDKUtils.getOracleJDKPackagesByModule(moduleNameString)){
                                    if (root.getPackageFragment(pkg).exists()) {
                                        roots.add(root);
                                        break;
                                    }
                                }
                            }else if (! (root instanceof JarPackageFragmentRoot)
                                    && ! CeylonBuilder.isInCeylonClassesOutputFolder(root.getPath())) {
                                String packageToSearch = moduleNameString;
                                if (root.getPackageFragment(packageToSearch).exists()) {
                                    roots.add(root);
                                }
                            }
                        }
                    }
                }
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
        
        module = new JDTModule(this, moduleSourceMapper, roots);
        module.setName(moduleName);
        module.setVersion(version);
        setupIfJDKModule(module);
        return module;
    }

    @Override
    public void prepareForTypeChecking() {
        getModelLoader().loadStandardModules();
    }
    
    @Override
    public Iterable<String> getSearchedArtifactExtensions() {
        if (loadDependenciesFromModelLoaderFirst) {
            return Arrays.asList("car", "jar", "src");
        }
        else {
            return Arrays.asList("jar", "src", "car");
        }
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
    public void visitedModule(Module module, boolean forCompiledModule) {
        if(forCompiledModule && AbstractModelLoader.isJDKModule(module.getNameAsString()))
            modelLoader.addJDKModuleToClassPath(module);
    }

    @Override
    public Set<String> getSupportedBackends() {
        // We detect which backends are enabled in the project settings and
        // we return those instead of relying on our super class which will
        // only (and correctly!) return "JVM".
        // This is just a hack of course because we're using this JVM module
        // manager even for the JS backend.
        // TODO At some point we'll need an actual module manager for the
        // JS backend and an IDE that can somehow merge the two when needed
        Set<String> backends = new HashSet<String>();
        if (CeylonBuilder.compileToJava(javaProject.getProject())) {
            backends.add(Backend.Java.nativeAnnotation);
        }
        if (CeylonBuilder.compileToJs(javaProject.getProject())) {
            backends.add(Backend.JavaScript.nativeAnnotation);
        }
        return backends;
    }
}
