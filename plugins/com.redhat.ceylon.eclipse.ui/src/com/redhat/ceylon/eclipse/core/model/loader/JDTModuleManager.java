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

package com.redhat.ceylon.eclipse.core.model.loader;

import static com.redhat.ceylon.compiler.typechecker.model.Util.formatPath;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;

import com.redhat.ceylon.cmr.api.ArtifactResult;
import com.redhat.ceylon.cmr.api.JDKUtils;
import com.redhat.ceylon.compiler.java.util.Util;
import com.redhat.ceylon.compiler.loader.AbstractModelLoader;
import com.redhat.ceylon.compiler.loader.model.LazyModuleManager;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.io.impl.ZipFileVirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.parser.LexError;
import com.redhat.ceylon.compiler.typechecker.parser.ParseError;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.util.ModuleManagerFactory;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.typechecker.CrossProjectPhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.ExternalPhasedUnit;

/**
 * @author david
 *
 */
public class JDTModuleManager extends LazyModuleManager {

    private JDTModelLoader modelLoader;
    private IJavaProject javaProject;
    private Set<String> sourceModules;
    private Set<File> classpath;
    private TypeChecker typeChecker;
    private boolean loadDependenciesFromModelLoaderFirst;

    public Set<File> getClasspath() {
        return classpath;
    }

    public Set<String> getSourceModules() {
        return sourceModules;
    }

    public IJavaProject getJavaProject() {
        return javaProject;
    }

    public JDTModuleManager(Context context, IJavaProject javaProject) {
        super(context);
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
        classpath = new HashSet<File>();
    }
    /*
     * TODO : Remove when the package creation (and module binding) in ModuleManager will be done with a method 
     * that can be overriden (createPackage, as suggested here - a "" name parameter correspond to the empty package)
     * Then we can only override this new createPackage method with our already-existing one
     */
    
    @Override
    public void initCoreModules() {
        Modules modules = getContext().getModules();
        if ( modules == null ) {
            modules = new Modules();

            //build default module (module in which packages belong to when not explicitly under a module
            final List<String> defaultModuleName = Collections.singletonList(Module.DEFAULT_MODULE_NAME);
            final Module defaultModule = createModule(defaultModuleName, "unversioned");
            defaultModule.setDefault(true);
            defaultModule.setAvailable(true);
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
            defaultModule.getImports().add(new ModuleImport(languageModule, false, false));
            defaultModule.setLanguageModule(languageModule);
            getContext().setModules(modules);

            //build empty package
            createPackage("", defaultModule);
        }
        super.initCoreModules();
    }
    
    @Override
    protected Package createPackage(String pkgName, Module module) {
        return getModelLoader().findOrCreatePackage(module, pkgName);
    }

    @Override
    public synchronized JDTModelLoader getModelLoader() {
        if(modelLoader == null){
            Modules modules = getContext().getModules();
            modelLoader = new JDTModelLoader(this, modules);
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
    protected boolean isModuleLoadedFromSource(String moduleName){
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
    protected Module createModule(List<String> moduleName, String version) {
        JDTModule module = null;
        String moduleNameString = Util.getName(moduleName);
        List<IPackageFragmentRoot> roots = new ArrayList<IPackageFragmentRoot>();
        if (javaProject != null) {
            try {
                if(moduleNameString.equals(Module.DEFAULT_MODULE_NAME)){
                    // Add the list of source package fragment roots
                    for (IPackageFragmentRoot root : javaProject.getPackageFragmentRoots()) {
                        if (root.exists()) {
                            IClasspathEntry entry = root.getResolvedClasspathEntry();
                            if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE && !root.isExternal()) {
                                roots.add(root);
                            }
                        }
                    }
                } else {
                    for (IPackageFragmentRoot root : javaProject.getPackageFragmentRoots()) {
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
                        }else if (! (root instanceof JarPackageFragmentRoot)) {
                            String packageToSearch = moduleNameString;
                            if (root.getPackageFragment(packageToSearch).exists()) {
                                roots.add(root);
                            }
                        }
                    }
                }
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
        
        module = new JDTModule(this, roots);
        module.setName(moduleName);
        module.setVersion(version);
        setupIfJDKModule(module);
        return module;
    }

    @Override
    public void resolveModule(ArtifactResult artifact, Module module, ModuleImport moduleImport, 
		LinkedList<Module> dependencyTree, List<PhasedUnits> phasedUnitsOfDependencies, boolean forCompiledModule) {
        if (! isModuleLoadedFromCompiledSource(module.getNameAsString())) {
            File file = artifact.artifact();
            if (artifact.artifact().getName().endsWith(".src")) {
                sourceModules.add(module.getNameAsString());
                file = new File(file.getAbsolutePath().replaceAll("\\.src$", ".car"));
            }
            classpath.add(file);
        }
        super.resolveModule(artifact, module, moduleImport, dependencyTree, phasedUnitsOfDependencies, forCompiledModule);
    }

    @Override
    public void prepareForTypeChecking() {
        getModelLoader().loadStandardModules();
    }
    
    @Override
    public Iterable<String> getSearchedArtifactExtensions() {
        if (loadDependenciesFromModelLoaderFirst) {
            return Arrays.asList("car", "src", "jar");
        }
        else {
            return Arrays.asList("src", "car", "jar");
        }
    }
    
    public void visitModuleFile() {
        Package currentPkg = getCurrentPackage();
        sourceModules.add(currentPkg.getNameAsString());
        super.visitModuleFile();
    }
    

    private Method addErrorToModuleMethod = null;
    
    // Todo : to be suppressed when the base one will be done protected. 
    private void addErrorToModule(List<String> moduleName, String error) {
        if (addErrorToModuleMethod == null) {
            try {
                addErrorToModuleMethod = ModuleManager.class.getDeclaredMethod("addErrorToModule", 
                		new Class[] {List.class, String.class});
                addErrorToModuleMethod.setAccessible(true);
                addErrorToModuleMethod.invoke(this, new Object[] {moduleName, error});
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    // Todo : to push into the base ModelManager class
    public void addTopLevelModuleError() {
        addErrorToModule(new ArrayList<String>(), 
        		"A module cannot be defined at the top level of the hierarchy");
    }
    public void addTwoModulesInHierarchyError(List<String> existingModuleName, 
    		List<String> newModulePackageName) {
        StringBuilder error = new StringBuilder("Found two modules within the same hierarchy: '");
        error.append( formatPath( existingModuleName ) )
            .append( "' and '" )
            .append( formatPath( newModulePackageName ) )
            .append("'");
        addErrorToModule(existingModuleName, error.toString());
        addErrorToModule(newModulePackageName, error.toString());
    }
    
    @Override
    protected PhasedUnits createPhasedUnits() {
        ModuleManagerFactory moduleManagerFactory = new ModuleManagerFactory() {
            @Override
            public ModuleManager createModuleManager(Context context) {
                return JDTModuleManager.this;
            }
        };
        
        return new PhasedUnits(getContext(), moduleManagerFactory) {

            private IProject referencedProject = null;
            
            @Override
            protected void parseFile(VirtualFile file, VirtualFile srcDir)
                    throws Exception {
                if (file.getName().endsWith(".ceylon")) {
                    //TODO: is this correct? does this file actually
                    //      live in the project, or is it external?
                    //       should VirtualFile have a getCharset()?
                    String charSet = javaProject != null ?
                            javaProject.getProject().getDefaultCharset()
                            : ResourcesPlugin.getWorkspace().getRoot().getDefaultCharset();
                    CeylonLexer lexer = new CeylonLexer(new ANTLRInputStream(file.getInputStream(),
                    		charSet));
                    CommonTokenStream tokenStream = new CommonTokenStream(lexer);
                    CeylonParser parser = new CeylonParser(tokenStream);
                    Tree.CompilationUnit cu = parser.compilationUnit();
                    List<CommonToken> tokens = tokenStream.getTokens();
                    PhasedUnit phasedUnit = null;
                    if (referencedProject == null) {
                        phasedUnit = new ExternalPhasedUnit(file, srcDir, cu, 
                                getModuleManager().getCurrentPackage(), getModuleManager(),
                                getTypeChecker(), tokens);
                    }
                    else {
                        phasedUnit = new CrossProjectPhasedUnit(file, srcDir, cu, 
                                getModuleManager().getCurrentPackage(), getModuleManager(),
                                getTypeChecker(), tokens, referencedProject);
                    }

                    addPhasedUnit(file, phasedUnit);

                    List<LexError> lexerErrors = lexer.getErrors();
                    for (LexError le : lexerErrors) {
                        cu.addLexError(le);
                    }
                    lexerErrors.clear();

                    List<ParseError> parserErrors = parser.getErrors();
                    for (ParseError pe : parserErrors) {
                        cu.addParseError(pe);
                    }
                    parserErrors.clear();

                }
            }

            @Override
            public void parseUnit(VirtualFile srcDir) {
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
            
        };
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
}
