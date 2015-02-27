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
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.PackageFragment;

import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.cmr.api.ArtifactResult;
import com.redhat.ceylon.cmr.api.ArtifactResultType;
import com.redhat.ceylon.cmr.api.ImportType;
import com.redhat.ceylon.cmr.api.JDKUtils;
import com.redhat.ceylon.cmr.api.PathFilter;
import com.redhat.ceylon.cmr.api.Repository;
import com.redhat.ceylon.cmr.api.RepositoryException;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.cmr.api.VisibilityType;
import com.redhat.ceylon.compiler.loader.model.LazyModule;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnitMap;
import com.redhat.ceylon.compiler.typechecker.io.ClosableVirtualFile;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.Util;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.util.NewlineFixingStringStream;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.classpath.CeylonLanguageModuleContainer;
import com.redhat.ceylon.eclipse.core.classpath.CeylonProjectModulesContainer;
import com.redhat.ceylon.eclipse.core.model.JDTModuleManager.ExternalModulePhasedUnits;
import com.redhat.ceylon.eclipse.core.model.ModuleDependencies.TraversalAction;
import com.redhat.ceylon.eclipse.core.typechecker.CrossProjectPhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.CarUtils;
import com.redhat.ceylon.eclipse.util.SingleSourceUnitPackage;

public class JDTModule extends LazyModule {
    private JDTModuleManager moduleManager;
    private List<IPackageFragmentRoot> packageFragmentRoots;
    private File artifact;
    private String repositoryDisplayString = "";
    private WeakReference<ExternalModulePhasedUnits> sourceModulePhasedUnits; // Here we have a weak ref on the PhasedUnits because there are already stored as strong references in the TypeChecker phasedUnitsOfDependencies list. 
                                                                    // But in the future we might remove the strong reference in the typeChecker and use strong references here, which would be 
                                                                    // much more modular (think of several versions of a module imported in a non-shared way in the same projet).
    private PhasedUnitMap<ExternalPhasedUnit, SoftReference<ExternalPhasedUnit>> binaryModulePhasedUnits;
    private Properties classesToSources = new Properties();
    private Map<String, String> javaImplFilesToCeylonDeclFiles = new HashMap<String, String>();
    private String sourceArchivePath = null;
    private IProject originalProject = null;
    private JDTModule originalModule = null;
    private Set<String> originalUnitsToRemove = new LinkedHashSet<>();
    private Set<String> originalUnitsToAdd = new LinkedHashSet<>();
    private ArtifactResultType artifactType = ArtifactResultType.OTHER;
    private Exception resolutionException = null;
    
    public JDTModule(JDTModuleManager jdtModuleManager, List<IPackageFragmentRoot> packageFragmentRoots) {
        this.moduleManager = jdtModuleManager;
        this.packageFragmentRoots = packageFragmentRoots;
    }

    private File returnCarFile() {
        if (isCeylonBinaryArchive()) {
            return artifact;
        }
        if (isSourceArchive()) {
            return new File(sourceArchivePath.substring(0, sourceArchivePath.length()-ArtifactContext.SRC.length()) + ArtifactContext.CAR);
        }
        return null;
    }
    
    synchronized void setArtifact(ArtifactResult artifactResult) {
        artifact = artifactResult.artifact();
        repositoryDisplayString = artifactResult.repositoryDisplayString();

        if (artifact.getName().endsWith(ArtifactContext.SRC)) {
            moduleType = ModuleType.CEYLON_SOURCE_ARCHIVE;
        } else if(artifact.getName().endsWith(ArtifactContext.CAR)) {
            moduleType = ModuleType.CEYLON_BINARY_ARCHIVE;
        } else if(artifact.getName().endsWith(ArtifactContext.JAR)) {
            moduleType = ModuleType.JAVA_BINARY_ARCHIVE;
        }
        
        artifactType = artifactResult.type();
        if (isCeylonBinaryArchive()){
            String carPath = artifact.getPath();
            sourceArchivePath = carPath.substring(0, carPath.length()-ArtifactContext.CAR.length()) + ArtifactContext.SRC;
            try {
                classesToSources = CarUtils.retrieveMappingFile(returnCarFile());
                javaImplFilesToCeylonDeclFiles = CarUtils.searchCeylonFilesForJavaImplementations(classesToSources, new File(sourceArchivePath));
            } catch (Exception e) {
                CeylonPlugin.getInstance().getLog().log(new Status(IStatus.WARNING, CeylonPlugin.PLUGIN_ID, "Cannot find the source archive for the Ceylon binary module " + getSignature(), e));
            }
            class BinaryPhasedUnits extends PhasedUnitMap<ExternalPhasedUnit, SoftReference<ExternalPhasedUnit>> {
                Set<String> sourceCannotBeResolved = new HashSet<String>();
                
                public BinaryPhasedUnits() {
                    String fullPathPrefix = sourceArchivePath + "!/"; 
                    for (Object value : classesToSources.values()) {
                        String sourceRelativePath = (String) value;
                        String path = fullPathPrefix + sourceRelativePath;
                        phasedUnitPerPath.put(path, new SoftReference<ExternalPhasedUnit>(null));
                        relativePathToPath.put(sourceRelativePath, path);
                    }
                }
                
                @Override
                public ExternalPhasedUnit getPhasedUnit(String path) {
                    if (! phasedUnitPerPath.containsKey(path)) {
                        if (path.endsWith(".ceylon")) {
                            // Case of a Ceylon file with a Java implementation, the classesToSources key is the Java source file.
                            String javaFileRelativePath = getJavaImplementationFile(path.replace(sourceArchivePath + "!/", ""));                        
                            if (javaFileRelativePath != null) {
                                return super.getPhasedUnit(sourceArchivePath + "!/" + javaFileRelativePath);
                            }
                        }
                        return null;
                    }
                    return super.getPhasedUnit(path);
                }

                @Override
                public ExternalPhasedUnit getPhasedUnitFromRelativePath(String relativePath) {
                    if (relativePath.startsWith("/")) {
                        relativePath = relativePath.substring(1);
                    }
                    if (! relativePathToPath.containsKey(relativePath)) {
                        if (relativePath.endsWith(".ceylon")) {
                            // Case of a Ceylon file with a Java implementation, the classesToSources key is the Java source file.
                            String javaFileRelativePath = getJavaImplementationFile(relativePath);                        
                            if (javaFileRelativePath != null) {
                                return super.getPhasedUnitFromRelativePath(javaFileRelativePath);
                            }
                        }
                        return null;
                    }
                    return super.getPhasedUnitFromRelativePath(relativePath);
                }
                
                @Override
                protected ExternalPhasedUnit fromStoredType(SoftReference<ExternalPhasedUnit> storedValue, String path) {
                    ExternalPhasedUnit result = storedValue.get();
                    if (result == null) {
                        if (!sourceCannotBeResolved.contains(path)) {
                            result = buildPhasedUnitForBinaryUnit(path);
                            if (result != null) {
                                phasedUnitPerPath.put(path, toStoredType(result));
                            } else {
                                sourceCannotBeResolved.add(path);
                            }
                        }
                    }
                    return result;
                }
                
                @Override
                protected void addInReturnedList(List<ExternalPhasedUnit> list,
                        ExternalPhasedUnit phasedUnit) {
                    if (phasedUnit != null) {
                        list.add(phasedUnit);
                    }
                }

                @Override
                protected SoftReference<ExternalPhasedUnit> toStoredType(ExternalPhasedUnit phasedUnit) {
                    return new SoftReference<ExternalPhasedUnit>(phasedUnit);
                }

                @Override
                public void removePhasedUnitForRelativePath(String relativePath) {
                    // Don't clean the Package since we are in the binary case 
                    String path = relativePathToPath.get(relativePath);
                    relativePathToPath.remove(relativePath);
                    phasedUnitPerPath.remove(path);
                }
                
                
            };
            binaryModulePhasedUnits = new BinaryPhasedUnits(); 
        }
        
        if (isSourceArchive()) {
            sourceArchivePath = artifact.getPath();
            try {
                classesToSources = CarUtils.retrieveMappingFile(returnCarFile());
                javaImplFilesToCeylonDeclFiles = CarUtils.searchCeylonFilesForJavaImplementations(classesToSources, artifact);                
            } catch (Exception e) {
                e.printStackTrace();
            }
            sourceModulePhasedUnits = new WeakReference<ExternalModulePhasedUnits>(null);
        }
        
        try {
            IJavaProject javaProject = moduleManager.getJavaProject();
            if (javaProject != null) {
                for (IProject refProject : javaProject.getProject().getReferencedProjects()) {
                    if (artifact.getAbsolutePath().contains(CeylonBuilder.getCeylonModulesOutputDirectory(refProject).getAbsolutePath())) {
                        originalProject = refProject;
                    }
                }
            }
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        if (isJavaBinaryArchive()){
            String carPath = artifact.getPath();
            sourceArchivePath = carPath.substring(0, carPath.length()-ArtifactContext.JAR.length())
                    + (artifactResult.type().equals(ArtifactResultType.MAVEN) ? ArtifactContext.MAVEN_SRC : ArtifactContext.SRC);
        }
    }
    
    public String getRepositoryDisplayString() {
        if (isJDKModule()) {
            return "Java SE Modules";                    
        }
        return repositoryDisplayString;
    }

    synchronized void setSourcePhasedUnits(final ExternalModulePhasedUnits modulePhasedUnits) {
            sourceModulePhasedUnits = new WeakReference<ExternalModulePhasedUnits>(modulePhasedUnits);
    }
    
    public File getArtifact() {
        return artifact;
    }

    public ArtifactResultType getArtifactType() {
        return artifactType;
    }
    
    private Properties getClassesToSources() {
        if (classesToSources.isEmpty() && getNameAsString().equals("java.base")) {
            for (Map.Entry<Object, Object> entry : ((JDTModule)getLanguageModule()).getClassesToSources().entrySet()) {
                if (entry.getKey().toString().startsWith("com/redhat/ceylon/compiler/java/language/")) {
                    classesToSources.put(entry.getKey().toString().replace("com/redhat/ceylon/compiler/java/language/", "java/lang/"), 
                                            entry.getValue().toString());
                }
            }
        }
        return classesToSources;
    }
    
    public String toSourceUnitRelativePath(String binaryUnitRelativePath) {
        return getClassesToSources().getProperty(binaryUnitRelativePath);
    }
    
    public String getJavaImplementationFile(String ceylonFileRelativePath) {
        String javaFileRelativePath = null;
        for (Entry<String, String> entry : javaImplFilesToCeylonDeclFiles.entrySet()) {
            if (entry.getValue().equals(ceylonFileRelativePath)) {
                javaFileRelativePath = entry.getKey();
            }
        }
        return javaFileRelativePath;
    }
    
    public String getCeylonDeclarationFile(String sourceUnitRelativePath) {
        if (sourceUnitRelativePath==null||sourceUnitRelativePath.endsWith(".ceylon")) {
            return sourceUnitRelativePath;
        }
        return javaImplFilesToCeylonDeclFiles.get(sourceUnitRelativePath);
    }

    public List<String> toBinaryUnitRelativePaths(String sourceUnitRelativePath) {
        if (sourceUnitRelativePath == null) {
            return Collections.emptyList();
        }
        
        List<String> result = new ArrayList<String>(); 
        for (Entry<Object, Object> entry : classesToSources.entrySet()) {
            if (sourceUnitRelativePath.equals(entry.getValue())) {
                result.add((String) entry.getKey());
            }
        }
        return result;
    }
    
    public synchronized List<IPackageFragmentRoot> getPackageFragmentRoots() {
        if (packageFragmentRoots.isEmpty() && 
                ! moduleManager.isExternalModuleLoadedFromSource(getNameAsString())) {
            IJavaProject javaProject = moduleManager.getJavaProject();
            if (javaProject != null) {
                if (this.equals(getLanguageModule())) {
                    IClasspathEntry runtimeClasspathEntry = null;
                    
                    try {
                        for (IClasspathEntry entry : javaProject.getRawClasspath()) {
                            if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER &&
                                    entry.getPath().segment(0).equals(CeylonLanguageModuleContainer.CONTAINER_ID)) {
                                runtimeClasspathEntry = entry;
                                break;
                            }
                        }
                        
                        if (runtimeClasspathEntry != null) {
                            for (IPackageFragmentRoot root : javaProject.getPackageFragmentRoots()) {
                                    if (root.exists() && 
                                            javaProject.isOnClasspath(root) &&
                                            root.getRawClasspathEntry().equals(runtimeClasspathEntry)) {
                                        packageFragmentRoots.add(root);
                                    }
                            }
                        }
                    } catch (JavaModelException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                else {
                    File jarToSearch = null;
                    try {
                        jarToSearch = returnCarFile();
                        if (jarToSearch == null) {
                            RepositoryManager repoMgr = CeylonBuilder.getProjectRepositoryManager(javaProject.getProject());
                            if (repoMgr != null) {
                                jarToSearch = CeylonProjectModulesContainer.getModuleArtifact(repoMgr, this);
                            }
                        }
                        
                        if (jarToSearch != null) {
                            IPackageFragmentRoot root = moduleManager.getJavaProject().getPackageFragmentRoot(jarToSearch.toString());
                            if (root instanceof JarPackageFragmentRoot) {
                                JarPackageFragmentRoot jarRoot = (JarPackageFragmentRoot) root;
                                if (jarRoot.getJar().getName().equals(jarToSearch.getPath())) {
                                    packageFragmentRoots.add(root);
                                }
                            }
                        }
                    } catch (CoreException e) {
                        if (jarToSearch != null) {
                            System.err.println("Exception trying to get Jar file '" + jarToSearch + "' :");
                        }
                        e.printStackTrace();
                    }
                }
            }
        }
        return packageFragmentRoots;
    }

    @Override
    protected JDTModelLoader getModelLoader() {
        return moduleManager.getModelLoader();
    }

    public JDTModuleManager getModuleManager() {
        return moduleManager;
    }

    
    @Override
    public List<Package> getAllPackages() {
        synchronized (getModelLoader()) {
            // force-load every package from the module if we can
            loadAllPackages(new HashSet<String>());

            // now delegate
            return super.getAllPackages();
        }
    }

    private void loadAllPackages(Set<String> alreadyScannedModules) {
        Set<String> packageList = listPackages();
        for (String packageName : packageList) {
            getPackage(packageName);
        }
        
        // now force-load other modules
        for (ModuleImport mi: getImports()) {
            Module importedModule = mi.getModule();
            if(importedModule instanceof JDTModule &&
                    alreadyScannedModules.add(importedModule.getNameAsString())){
                ((JDTModule)importedModule).loadAllPackages(alreadyScannedModules);
            }
        }
    }

    private Set<String> listPackages() {
        Set<String> packageList = new TreeSet<String>();
        String name = getNameAsString();
        if(JDKUtils.isJDKModule(name)){
            packageList.addAll(JDKUtils.getJDKPackagesByModule(name));
        }else if(JDKUtils.isOracleJDKModule(name)){
            packageList.addAll(JDKUtils.getOracleJDKPackagesByModule(name));
        } else if(isJava() || true){
            for(IPackageFragmentRoot fragmentRoot : getPackageFragmentRoots()){
                if(!fragmentRoot.exists())
                    continue;
                IParent parent = fragmentRoot;
                listPackages(packageList, parent);
            }
        }
        return packageList;
    }

    private void listPackages(Set<String> packageList, IParent parent) {
        try {
            for (IJavaElement child : parent.getChildren()) {
                if (child instanceof PackageFragment) {
                    packageList.add(child.getElementName());
                    listPackages(packageList, (IPackageFragment) child);
                }
            }
        } catch (JavaModelException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadPackageList(ArtifactResult artifact) {
        try {
            super.loadPackageList(artifact);
        } catch(Exception e) {
            CeylonPlugin.getInstance().getLog().log(new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID, "Failed loading the package list of module " + getSignature(), e));
        }
        JDTModelLoader modelLoader = getModelLoader();
        if (modelLoader != null) {
            synchronized(modelLoader){
                String name = getNameAsString();
                for(String pkg : jarPackages){
                    if(name.equals("ceylon.language") && ! pkg.startsWith("ceylon.language")) {
                        // special case for the language module to hide stuff
                        continue;
                    }
                    modelLoader.findOrCreatePackage(this, pkg);
                }
            }
        }
    }

    private ModuleType moduleType;
    
    private enum ModuleType {
        PROJECT_MODULE,
        CEYLON_SOURCE_ARCHIVE,
        CEYLON_BINARY_ARCHIVE,
        JAVA_BINARY_ARCHIVE,
        SDK_MODULE,
        UNKNOWN
    }
    
    public void setProjectModule() {
        moduleType = ModuleType.PROJECT_MODULE;
    }
    
    public boolean isCeylonArchive() {
        return isCeylonBinaryArchive() || isSourceArchive();
    }
    
    public boolean isDefaultModule() {
        return this.equals(moduleManager.getContext().getModules().getDefaultModule());
    }
    
    public boolean isProjectModule() {
        return ModuleType.PROJECT_MODULE.equals(moduleType);
    }
    
    public boolean isJDKModule() {
        synchronized (this) {
            if (moduleType == null) {
                if (JDKUtils.isJDKModule(getNameAsString()) || 
                    JDKUtils.isOracleJDKModule(getNameAsString())) {
                    moduleType = ModuleType.SDK_MODULE;
                }
            }
        }
        return ModuleType.SDK_MODULE.equals(moduleType);
    }
    
    public boolean isUnresolved() {
        return artifact == null && ! isAvailable();
    }
    
    public boolean isJavaBinaryArchive() {
        return ModuleType.JAVA_BINARY_ARCHIVE.equals(moduleType);
    }
    
    public boolean isCeylonBinaryArchive() {
        return ModuleType.CEYLON_BINARY_ARCHIVE.equals(moduleType);
    }
    
    public boolean isSourceArchive() {
        return ModuleType.CEYLON_SOURCE_ARCHIVE.equals(moduleType);
    }
    
    public synchronized List<? extends PhasedUnit> getPhasedUnits() {
        PhasedUnitMap<? extends PhasedUnit, ?> phasedUnitMap = null;
        if (isCeylonBinaryArchive()) {
            phasedUnitMap = binaryModulePhasedUnits;
        }
        if (isSourceArchive()) {
            phasedUnitMap = sourceModulePhasedUnits.get();
        }
        if (phasedUnitMap != null) {
            synchronized (phasedUnitMap) {
                return phasedUnitMap.getPhasedUnits();
            }
        }
        return Collections.emptyList();
    }

    public ExternalPhasedUnit getPhasedUnit(IPath path) {
        PhasedUnitMap<? extends PhasedUnit, ?> phasedUnitMap = null;
        if (isCeylonBinaryArchive()) {
            phasedUnitMap = binaryModulePhasedUnits;
        }
        if (isSourceArchive()) {
            phasedUnitMap = sourceModulePhasedUnits.get();
        }
        if (phasedUnitMap != null) {
            IPath sourceArchiveIPath = new Path(sourceArchivePath+"!");
            synchronized (phasedUnitMap) {
                return (ExternalPhasedUnit) phasedUnitMap.getPhasedUnitFromRelativePath(path.makeRelativeTo(sourceArchiveIPath).toString());
            }
        }
        return null;
    }

    public ExternalPhasedUnit getPhasedUnit(String path) {
        PhasedUnitMap<? extends PhasedUnit, ?> phasedUnitMap = null;
        if (isCeylonBinaryArchive()) {
            phasedUnitMap = binaryModulePhasedUnits;
        }
        if (isSourceArchive()) {
            phasedUnitMap = sourceModulePhasedUnits.get();
        }
        if (phasedUnitMap != null) {
            synchronized (phasedUnitMap) {
                return (ExternalPhasedUnit) phasedUnitMap.getPhasedUnit(path);
            }
        }
        return null;
    }

    public ExternalPhasedUnit getPhasedUnit(VirtualFile file) {
        PhasedUnitMap<? extends PhasedUnit, ?> phasedUnitMap = null;
        if (isCeylonBinaryArchive()) {
            phasedUnitMap = binaryModulePhasedUnits;
        }
        if (isSourceArchive()) {
            phasedUnitMap = sourceModulePhasedUnits.get();
        }
        if (phasedUnitMap != null) {
            synchronized (phasedUnitMap) {
                return (ExternalPhasedUnit) phasedUnitMap.getPhasedUnit(file);
            }
        }
        return null;
    }
    
    public ExternalPhasedUnit getPhasedUnitFromRelativePath(String relativePathToSource) {
        PhasedUnitMap<? extends PhasedUnit, ?> phasedUnitMap = null;
        if (isCeylonBinaryArchive()) {
            phasedUnitMap = binaryModulePhasedUnits;
        }
        if (isSourceArchive()) {
            phasedUnitMap = sourceModulePhasedUnits.get();
        }
        if (phasedUnitMap != null) {
            synchronized (phasedUnitMap) {
                return (ExternalPhasedUnit) phasedUnitMap.getPhasedUnitFromRelativePath(relativePathToSource);
            }
        }
        return null;
    }
    
    public void removedOriginalUnit(String relativePathToSource) {
        if (isProjectModule()) {
            return;
        }
        originalUnitsToRemove.add(relativePathToSource);

        try {
            if (isCeylonBinaryArchive() || JavaCore.isJavaLikeFileName(relativePathToSource)) {
                List<String> unitPathsToSearch = new ArrayList<>();
                unitPathsToSearch.add(relativePathToSource);
                unitPathsToSearch.addAll(toBinaryUnitRelativePaths(relativePathToSource));
                for (String relativePathOfUnitToRemove : unitPathsToSearch) {
                    Package p = getPackageFromRelativePath(relativePathOfUnitToRemove);
                    if (p != null) {
                        Set<Unit> units = new HashSet<>();
                        try {
                            for(Declaration d : p.getMembers()) {
                                Unit u = d.getUnit();
                                if (u.getRelativePath().equals(relativePathOfUnitToRemove)) {
                                    units.add(u);
                                }
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        
                        for (Unit u : units) {
                            try {
                                for (Declaration d : u.getDeclarations()) {
                                    d.getMembers(); 
                                    // Just to fully load the declaration before 
                                    // the corresponding class is removed (so that 
                                    // the real removing from the model loader
                                    // will not require reading the bindings.
                                }
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public void addedOriginalUnit(String relativePathToSource) {
        if (isProjectModule()) {
            return;
        }
        originalUnitsToAdd.add(relativePathToSource);
    }
    
    public void refresh() {
        if (originalUnitsToAdd.size() + originalUnitsToRemove.size() == 0) {
            // Nothing to refresh
            return;
        }
        
        try {
            PhasedUnitMap<? extends PhasedUnit, ?> phasedUnitMap = null;
            if (isCeylonBinaryArchive()) {
                JavaModelManager.getJavaModelManager().resetClasspathListCache();
                JavaModelManager.getJavaModelManager().getJavaModel().refreshExternalArchives(getPackageFragmentRoots().toArray(new IPackageFragmentRoot[0]), null);
                phasedUnitMap = binaryModulePhasedUnits;
            }
            if (isSourceArchive()) {
                phasedUnitMap = sourceModulePhasedUnits.get();
            }
            if (phasedUnitMap != null) {
                synchronized (phasedUnitMap) {
                    for (String relativePathToRemove : originalUnitsToRemove) {
                        if (isCeylonBinaryArchive() || JavaCore.isJavaLikeFileName(relativePathToRemove)) {
                        	List<String> unitPathsToSearch = new ArrayList<>();
                        	unitPathsToSearch.add(relativePathToRemove);
                        	unitPathsToSearch.addAll(toBinaryUnitRelativePaths(relativePathToRemove));
                            for (String relativePathOfUnitToRemove : unitPathsToSearch) {
                                Package p = getPackageFromRelativePath(relativePathOfUnitToRemove);
                                if (p != null) {
                                    Set<Unit> units = new HashSet<>();
                                    for(Declaration d : p.getMembers()) {
                                        Unit u = d.getUnit();
                                        if (u.getRelativePath().equals(relativePathOfUnitToRemove)) {
                                            units.add(u);
                                        }
                                    }
                                    for (Unit u : units) {
                                        try {
                                            p.removeUnit(u);
                                            // In the future, when we are sure that we cannot add several unit objects with the 
                                            // same relative path, we can add a break.
                                        } catch(Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    System.out.println("WARNING : The package of the following binary unit (" + relativePathOfUnitToRemove + ") "
                                            + "cannot be found in module " + getNameAsString() + 
                                            artifact != null ? " (artifact=" + artifact.getAbsolutePath() + ")" : "");
                                }
                            }
                        }
                        phasedUnitMap.removePhasedUnitForRelativePath(relativePathToRemove);
                    }
                    
                    if (isSourceArchive()) {
                        ClosableVirtualFile sourceArchive = null;
                        try {
                            sourceArchive = moduleManager.getContext().getVfs().getFromZipFile(new File(sourceArchivePath));
                            for (String relativePathToAdd : originalUnitsToAdd) {
                                VirtualFile archiveEntry = null;
                                archiveEntry = searchInSourceArchive(
                                        relativePathToAdd, sourceArchive);
                                
                                if (archiveEntry != null) {
                                    Package pkg = getPackageFromRelativePath(relativePathToAdd);
                                    ((ExternalModulePhasedUnits)phasedUnitMap).parseFile(archiveEntry, sourceArchive, pkg);
                                }                            
                            }
                        } catch (Exception e) {
                            StringBuilder error = new StringBuilder("Unable to read source artifact from ");
                            error.append(sourceArchive);
                            error.append( "\ndue to connection error: ").append(e.getMessage());
                            throw e;
                        } finally {
                            if (sourceArchive != null) {
                                sourceArchive.close();
                            }
                        }
                    }
                    
                    classesToSources = CarUtils.retrieveMappingFile(returnCarFile());
                    javaImplFilesToCeylonDeclFiles = CarUtils.searchCeylonFilesForJavaImplementations(classesToSources, new File(sourceArchivePath));                
                    
                    originalUnitsToRemove.clear();
                    originalUnitsToAdd.clear();
                }
            }
            if (isCeylonBinaryArchive() || isJavaBinaryArchive()) {
                jarPackages.clear();
                loadPackageList(new ArtifactResult() {
                    @Override
                    public VisibilityType visibilityType() {
                        return null;
                    }
                    @Override
                    public String version() {
                        return null;
                    }
                    @Override
                    public ArtifactResultType type() {
                        return null;
                    }
                    @Override
                    public String name() {
                        return null;
                    }
                    @Override
                    public ImportType importType() {
                        return null;
                    }
                    @Override
                    public List<ArtifactResult> dependencies() throws RepositoryException {
                        return null;
                    }
                    @Override
                    public File artifact() throws RepositoryException {
                        return artifact;
                    }
                    @Override
                    public String repositoryDisplayString() {                        
                        return "";
                    }
                    @Override
                    public PathFilter filter() {
                        return null;
                    }
                    @Override
                    public Repository repository() {
                        return null;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Package getPackageFromRelativePath(
            String relativePathOfClassToRemove) {
        List<String> pathElements = Arrays.asList(relativePathOfClassToRemove.split("/"));
        String packageName = Util.formatPath(pathElements.subList(0, pathElements.size()-1));
        Package p = findPackageNoLazyLoading(packageName);
        return p;
    }

    private ExternalPhasedUnit buildPhasedUnitForBinaryUnit(String sourceUnitFullPath) {
        if (sourceArchivePath == null || sourceUnitFullPath == null) {
            return null;
        }
        
        if (! sourceUnitFullPath.startsWith(sourceArchivePath)) {
            return null;
        }
        
        File sourceArchiveFile = new File(sourceArchivePath);
        if (! sourceArchiveFile.exists()) {
            return null;
        }
        
        ExternalPhasedUnit phasedUnit = null;
        String sourceUnitRelativePath = sourceUnitFullPath.replace(sourceArchivePath + "!/", "");
        Package pkg = getPackageFromRelativePath(sourceUnitRelativePath);
        if (pkg != null) {
            try {
                JDTModuleManager moduleManager = getModuleManager();
                ClosableVirtualFile sourceArchive = null;
                try {
                    sourceArchive = moduleManager.getContext().getVfs().getFromZipFile(sourceArchiveFile);
                    
                    String ceylonSourceUnitRelativePath = sourceUnitRelativePath; 
                    if (sourceUnitRelativePath.endsWith(".java")) {
                        ceylonSourceUnitRelativePath = javaImplFilesToCeylonDeclFiles.get(sourceUnitRelativePath);
                    }

                    VirtualFile archiveEntry = null;
                    if (ceylonSourceUnitRelativePath != null) {
                        archiveEntry = searchInSourceArchive(
                                ceylonSourceUnitRelativePath, sourceArchive);
                    }
                    
                    if (archiveEntry != null) {
                        IProject project = moduleManager.getJavaProject().getProject();
                        CeylonLexer lexer = new CeylonLexer(NewlineFixingStringStream.fromStream(archiveEntry.getInputStream(), project.getDefaultCharset()));
                        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
                        CeylonParser parser = new CeylonParser(tokenStream);
                        Tree.CompilationUnit cu = parser.compilationUnit();
                        List<CommonToken> tokens = new ArrayList<CommonToken>(tokenStream.getTokens().size()); 
                        tokens.addAll(tokenStream.getTokens());
                        if(originalProject == null) {
                            phasedUnit = new ExternalPhasedUnit(archiveEntry, sourceArchive, cu, 
                                    new SingleSourceUnitPackage(pkg, sourceUnitFullPath), moduleManager, CeylonBuilder.getProjectTypeChecker(project), tokens) {
                                @Override
                                protected boolean reuseExistingDescriptorModels() {                                    
                                    return true;
                                }
                            };
                        } else {
                            phasedUnit = new CrossProjectPhasedUnit(archiveEntry, sourceArchive, cu, 
                                    new SingleSourceUnitPackage(pkg, sourceUnitFullPath), moduleManager, CeylonBuilder.getProjectTypeChecker(project), tokens, originalProject) {
                                @Override
                                protected boolean reuseExistingDescriptorModels() {                                    
                                    return true;
                                }
                            };
                        }
                    }
                } catch (Exception e) {
                    StringBuilder error = new StringBuilder("Unable to read source artifact from ");
                    error.append(sourceArchive);
                    error.append( "\ndue to connection error: ").append(e.getMessage());
                    System.err.println(error);
                } finally {
                    if (sourceArchive != null) {
                        sourceArchive.close();
                    }
                }
                if (phasedUnit != null) {
                    phasedUnit.validateTree();
                    phasedUnit.visitSrcModulePhase();
                    phasedUnit.visitRemainingModulePhase();
                    phasedUnit.scanDeclarations();
                    phasedUnit.scanTypeDeclarations();
                    phasedUnit.validateRefinement();
                    phasedUnit.analyseTypes(); // Needed to have the right values in the Value.trans field (set in ExpressionVisitor)
                    // in the case of specifier statements for refined members
                }
            } catch (Exception e) {
                e.printStackTrace();
                phasedUnit = null;
            }
        }
        return phasedUnit;
    }

    private VirtualFile searchInSourceArchive(String sourceUnitRelativePath,
            ClosableVirtualFile sourceArchive) {
        VirtualFile archiveEntry;
        archiveEntry = sourceArchive; 
        for (String part : sourceUnitRelativePath.split("/")) {
            boolean found = false;
            for (VirtualFile vf : archiveEntry.getChildren()) {
                if (part.equals(vf.getName().replace("/", ""))) {
                    archiveEntry = vf;
                    found = true;
                    break;
                }
            }
            if (!found) {
                archiveEntry = null;
                break;
            }
        }
        return archiveEntry;
    }

    public String getSourceArchivePath() {
        return sourceArchivePath;
    }
    

    public IProject getOriginalProject() {
        return originalProject;
    }
    
    public JDTModule getOriginalModule() {
        if (originalProject != null) {
            if (originalModule == null) {
                for (Module m : CeylonBuilder.getProjectModules(originalProject).getListOfModules()) {
                    // TODO : in the future : manage version ?? in case we reference 2 identical projects with different version in the workspace
                    if (m.getNameAsString().equals(getNameAsString())) {  
                        assert(m instanceof JDTModule);
                        if (((JDTModule) m).isProjectModule()) {
                            originalModule = (JDTModule) m;
                            break;
                        }
                    }
                }
            }
            return originalModule;
        }
        return null;
    }
    
    public boolean containsClass(String className) {
        return className != null && 
                (classesToSources != null ?  classesToSources.containsKey(className) : false);
    }

    @Override
    public List<Package> getPackages() {
        return super.getPackages();
    }

    @Override
    public void clearCache(final TypeDeclaration declaration) {
        clearCacheLocally(declaration);
        if (getProjectModuleDependencies() != null) {
            getProjectModuleDependencies().doWithReferencingModules(this, new TraversalAction<Module>() {
                @Override
                public void applyOn(Module module) {
                    assert(module instanceof JDTModule);
                    if (module instanceof JDTModule) {
                        ((JDTModule) module).clearCacheLocally(declaration); 
                    }
                }
            }); 
            getProjectModuleDependencies().doWithTransitiveDependencies(this, new TraversalAction<Module>() {
                @Override
                public void applyOn(Module module) {
                    assert(module instanceof JDTModule);
                    if (module instanceof JDTModule) {
                        ((JDTModule) module).clearCacheLocally(declaration); 
                    }
                }
            });
            ((JDTModule)getLanguageModule()).clearCacheLocally(declaration);
        }
    }

    private void clearCacheLocally(final TypeDeclaration declaration) {
        super.clearCache(declaration);
    }
    
    private ModuleDependencies projectModuleDependencies = null;
    private ModuleDependencies getProjectModuleDependencies() {
        if (projectModuleDependencies == null) {
            IJavaProject javaProject = moduleManager.getJavaProject();
            if (javaProject != null) {
             projectModuleDependencies = CeylonBuilder.getModuleDependenciesForProject(javaProject.getProject());
            }
        }
        return projectModuleDependencies;
    }
    
    public Iterable<Module> getReferencingModules() {
        if (getProjectModuleDependencies() != null) {
            return getProjectModuleDependencies().getReferencingModules(this); 
        }
        return Collections.emptyList();
    }

    public boolean resolutionFailed() {
        return resolutionException != null;
    }

    public void setResolutionException(Exception resolutionException) {
        if (resolutionException instanceof RuntimeException)
        this.resolutionException = resolutionException;
    }
    
    public List<JDTModule> getModuleInReferencingProjects() {
    	if (! isProjectModule()) {
    		return Collections.emptyList();
    	}
        IProject project = moduleManager.getJavaProject().getProject();
        IProject[] referencingProjects = project.getReferencingProjects();
    	if (referencingProjects.length == 0) {
    		return Collections.emptyList();
    	}
        
        List<JDTModule> result = new ArrayList<>();
        for(IProject referencingProject : referencingProjects) {
            JDTModule referencingModule = null;
            Modules referencingProjectModules = CeylonBuilder.getProjectModules(referencingProject);
            if (referencingProjectModules != null) {
                for (Module m : referencingProjectModules.getListOfModules()) {
                    if (m.getSignature().equals(getSignature())) {
                        assert(m instanceof JDTModule);
                        referencingModule = (JDTModule) m;
                        break;
                    }
                }
            }
            if (referencingModule != null) {
                result.add(referencingModule);
            }
        }
        return result;
    }
    
}
