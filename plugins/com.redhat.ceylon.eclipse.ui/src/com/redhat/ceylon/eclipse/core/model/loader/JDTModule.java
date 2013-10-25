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

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.core.IClasspathEntry;

import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.cmr.api.ArtifactResult;
import com.redhat.ceylon.cmr.api.JDKUtils;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.compiler.loader.AbstractModelLoader;
import com.redhat.ceylon.compiler.loader.model.LazyModule;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnitMap;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.io.ClosableVirtualFile;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Getter;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Setter;
import com.redhat.ceylon.compiler.typechecker.model.TypeAlias;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.Util;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.classpath.CeylonLanguageModuleContainer;
import com.redhat.ceylon.eclipse.core.classpath.CeylonProjectModulesContainer;
import com.redhat.ceylon.eclipse.core.model.CeylonBinaryUnit;
import com.redhat.ceylon.eclipse.core.model.ExternalSourceFile;
import com.redhat.ceylon.eclipse.core.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.IdePhasedUnit;
import com.redhat.ceylon.eclipse.util.CarUtils;
import com.redhat.ceylon.eclipse.util.SingleSourceUnitPackage;

public class JDTModule extends LazyModule {
    private JDTModuleManager moduleManager;
    private List<IPackageFragmentRoot> packageFragmentRoots;
    private File jarPath;
    private File artifact;
    private PhasedUnitMap<ExternalPhasedUnit, WeakReference<ExternalPhasedUnit>> sourceModulePhasedUnits; // Here we have a weak ref on the PhasedUnits because there are already stored as strong references in the TypeChecker phasedUnitsOfDependencies list. 
                                                                    // But in the future we might remove the strong reference in the typeChecker and use strong references here, which would be 
                                                                    // much more modular (think of several versions of a module imported in a non-shared way in the same projet).
    private PhasedUnitMap<ExternalPhasedUnit, SoftReference<ExternalPhasedUnit>> binaryModulePhasedUnits;
    private Properties classesToSources = null;
    private String sourceArchivePath = null;
    
    public JDTModule(JDTModuleManager jdtModuleManager, List<IPackageFragmentRoot> packageFragmentRoots) {
        this.moduleManager = jdtModuleManager;
        this.packageFragmentRoots = packageFragmentRoots;
    }

    synchronized void setArtifact(File artifact) {
        this.artifact = artifact;
        if (isBinaryArchive()){
            String carPath = artifact.getPath();
            sourceArchivePath = carPath.substring(0, carPath.length()-ArtifactContext.CAR.length()) + ArtifactContext.SRC;
            try {
                classesToSources = CarUtils.retrieveMappingFile(artifact);
            } catch (Exception e) {
                e.printStackTrace();
                classesToSources = new Properties();
            }
            class BinaryPhasedUnits extends PhasedUnitMap<ExternalPhasedUnit, SoftReference<ExternalPhasedUnit>> {
                Set<String> sourceCannotBeResolved = new HashSet<String>();
                
                public BinaryPhasedUnits() {
                    String fullPathPrefix = sourceArchivePath + "!/"; 
                    for (Object value : classesToSources.values()) {
                        String sourceRelativePath = (String) value;
                        if (sourceRelativePath.endsWith(".ceylon")) {
                            String path = fullPathPrefix + sourceRelativePath;
                            phasedUnitPerPath.put(path, new SoftReference<ExternalPhasedUnit>(null));
                            relativePathToPath.put(sourceRelativePath, path);
                        }
                    }
                }
                
                @Override
                protected ExternalPhasedUnit fromStoredType(SoftReference<ExternalPhasedUnit> storedValue, String path) {
                    ExternalPhasedUnit result = storedValue.get();
                    if (result == null) {
                        if (!sourceCannotBeResolved.contains(path)) {
                            result = buildPhasedUnit(path);
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
            };
            binaryModulePhasedUnits = new BinaryPhasedUnits(); 
        }
        
        if (isSourceArchive()) {
            sourceArchivePath = artifact.getPath();
            sourceModulePhasedUnits = new PhasedUnitMap<ExternalPhasedUnit, WeakReference<ExternalPhasedUnit>>() {
                
                @Override
                protected ExternalPhasedUnit fromStoredType(WeakReference<ExternalPhasedUnit> storedValue, String path) {
                    return storedValue.get();
                }
                
                @Override
                protected void addInReturnedList(List<ExternalPhasedUnit> list,
                        ExternalPhasedUnit phasedUnit) {
                    if (phasedUnit != null) {
                        list.add(phasedUnit);
                    }
                }

                @Override
                protected WeakReference<ExternalPhasedUnit> toStoredType(ExternalPhasedUnit phasedUnit) {
                    return new WeakReference<ExternalPhasedUnit>(phasedUnit);
                } 
            };
        }
    }
    
    synchronized void setSourcePhasedUnits(final PhasedUnits modulePhasedUnits) {
        if (isSourceArchive()) {
            synchronized (sourceModulePhasedUnits) {
                for (PhasedUnit pu : modulePhasedUnits.getPhasedUnits()) {
                    if (pu instanceof ExternalPhasedUnit) {
                        sourceModulePhasedUnits.addPhasedUnit(pu.getUnitFile(), (ExternalPhasedUnit) pu);
                    }
                }
            }
        }
    }
    
    public File getArtifact() {
        return artifact;
    }
    
    public String toSourceUnitRelativePath(String binaryUnitRelativePath) {
        return classesToSources.getProperty(binaryUnitRelativePath);
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
                                    if (root.getRawClasspathEntry().equals(runtimeClasspathEntry)) {
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
                    try {
                        File jarToSearch = jarPath;
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
                        e.printStackTrace();
                    }
                }
            }
        }
        return packageFragmentRoots;
    }

    @Override
    protected AbstractModelLoader getModelLoader() {
        return moduleManager.getModelLoader();
    }

    public JDTModuleManager getModuleManager() {
        return moduleManager;
    }

    @Override
    public List<Package> getAllPackages() {
        synchronized (getModelLoader()) {
            // force-load every package from the module if we can
            loadAllPackages();
            // now force-load other modules
            for (ModuleImport mi: getImports()) {
                if(mi.getModule() instanceof JDTModule){
                    ((JDTModule)mi.getModule()).loadAllPackages();
                }
            }
            // now delegate
            return super.getAllPackages();
        }
    }

    private void loadAllPackages() {
        Set<String> packageList = new TreeSet<String>();
        String name = getNameAsString();
        if(JDKUtils.isJDKModule(name)){
            packageList.addAll(JDKUtils.getJDKPackagesByModule(name));
        }else if(JDKUtils.isOracleJDKModule(name)){
            packageList.addAll(JDKUtils.getOracleJDKPackagesByModule(name));
        } else if(isJava()){
            for(IPackageFragmentRoot fragmentRoot : getPackageFragmentRoots()){
                if(!fragmentRoot.exists())
                    continue;
                IParent parent = fragmentRoot;
                listPackages(packageList, parent);
            }
        }
        for (String packageName : packageList) {
            getPackage(packageName);
        }
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
        super.loadPackageList(artifact);
        jarPath = artifact.artifact();
    }

    public boolean isArchive() {
        return isBinaryArchive() || isSourceArchive();
    }
    
    public boolean isBinaryArchive() {
        return artifact != null && artifact.getName().endsWith(ArtifactContext.CAR);
    }
    
    public boolean isSourceArchive() {
        return artifact != null && artifact.getName().endsWith(ArtifactContext.SRC);
    }
    
    public synchronized List<ExternalPhasedUnit> getPhasedUnits() {
        PhasedUnitMap<ExternalPhasedUnit, ?> phasedUnitMap = null;
        if (isBinaryArchive()) {
            phasedUnitMap = binaryModulePhasedUnits;
        }
        if (isSourceArchive()) {
            phasedUnitMap = sourceModulePhasedUnits;
        }
        if (phasedUnitMap != null) {
            synchronized (phasedUnitMap) {
                return phasedUnitMap.getPhasedUnits();
            }
        }
        return Collections.emptyList();
    }

    public ExternalPhasedUnit getPhasedUnit(IPath path) {
        PhasedUnitMap<ExternalPhasedUnit, ?> phasedUnitMap = null;
        if (isBinaryArchive()) {
            phasedUnitMap = binaryModulePhasedUnits;
        }
        if (isSourceArchive()) {
            phasedUnitMap = sourceModulePhasedUnits;
        }
        if (phasedUnitMap != null) {
            IPath sourceArchiveIPath = new Path(sourceArchivePath+"!");
            synchronized (phasedUnitMap) {
                return phasedUnitMap.getPhasedUnitFromRelativePath(path.makeRelativeTo(sourceArchiveIPath).toString());
            }
        }
        return null;
    }

    public ExternalPhasedUnit getPhasedUnit(String path) {
        PhasedUnitMap<ExternalPhasedUnit, ?> phasedUnitMap = null;
        if (isBinaryArchive()) {
            phasedUnitMap = binaryModulePhasedUnits;
        }
        if (isSourceArchive()) {
            phasedUnitMap = sourceModulePhasedUnits;
        }
        if (phasedUnitMap != null) {
            synchronized (phasedUnitMap) {
                return phasedUnitMap.getPhasedUnit(path);
            }
        }
        return null;
    }

    public ExternalPhasedUnit getPhasedUnit(VirtualFile file) {
        PhasedUnitMap<ExternalPhasedUnit, ?> phasedUnitMap = null;
        if (isBinaryArchive()) {
            phasedUnitMap = binaryModulePhasedUnits;
        }
        if (isSourceArchive()) {
            phasedUnitMap = sourceModulePhasedUnits;
        }
        if (phasedUnitMap != null) {
            synchronized (phasedUnitMap) {
                return phasedUnitMap.getPhasedUnit(file);
            }
        }
        return null;
    }
    
    public ExternalPhasedUnit getPhasedUnitFromRelativePath(String relativePathToSource) {
        PhasedUnitMap<ExternalPhasedUnit, ?> phasedUnitMap = null;
        if (isBinaryArchive()) {
            phasedUnitMap = binaryModulePhasedUnits;
        }
        if (isSourceArchive()) {
            phasedUnitMap = sourceModulePhasedUnits;
        }
        if (phasedUnitMap != null) {
            synchronized (phasedUnitMap) {
                return phasedUnitMap.getPhasedUnitFromRelativePath(relativePathToSource);
            }
        }
        return null;
    }
    
    /*
    private class ReplaceDeclarationsVisitor extends Visitor {
        
        private com.redhat.ceylon.compiler.typechecker.model.Package binaryPackage;
        private Unit sourceUnit;
        private Set<Declaration> replacedDeclarations = new HashSet<Declaration>();
        
        public ReplaceDeclarationsVisitor(com.redhat.ceylon.compiler.typechecker.model.Package binaryPackage, Unit sourceUnit) {
            this.binaryPackage = binaryPackage;
            this.sourceUnit = sourceUnit;
        }
        
        public Set<Declaration> getReplacedDeclarations() {
            return replacedDeclarations;
        }
        
        public Declaration getCorrespondingBinaryDeclaration(Declaration sourceDeclaration) {
            for (Declaration dec : binaryPackage.getMembers()) {
                if(! sourceDeclaration.getClass().isInstance(dec))
                    continue;
                String sourceName = sourceDeclaration.getName();
                String binaryName = dec.getName();
                if (sourceName != null && binaryName != null &&
                        sourceName.equals(binaryName) &&
                        sourceDeclaration.getDeclarationKind()== dec.getDeclarationKind() &&
                        sourceDeclaration.getQualifiedNameString().equals(dec.getQualifiedNameString())) {
                    return dec;
                }
            }
            return null;
        }

        private boolean isInProxyPackage(Declaration dec) {
            return dec!=null && dec.getUnit().equals(sourceUnit);
        }
        
        @Override
        public void visit(Tree.AttributeDeclaration that) {
            if (isInProxyPackage(that.getDeclarationModel())) {
                Declaration binaryDeclaration = getCorrespondingBinaryDeclaration(that.getDeclarationModel());
                if (binaryDeclaration != null) {
                    replacedDeclarations.add(that.getDeclarationModel());
                    that.setDeclarationModel((Value)binaryDeclaration);
                }
            }
            super.visit(that);
        }
        
        @Override
        public void visit(Tree.AttributeGetterDefinition that) {
            if (isInProxyPackage(that.getDeclarationModel())) {
                Declaration binaryDeclaration = getCorrespondingBinaryDeclaration(that.getDeclarationModel());
                if (binaryDeclaration != null) {
                    replacedDeclarations.add(that.getDeclarationModel());
                    that.setDeclarationModel((Getter)binaryDeclaration);
                }
            }
            super.visit(that);
        }

        @Override
        public void visit(Tree.AttributeSetterDefinition that) {
            if (isInProxyPackage(that.getDeclarationModel())) {
                Declaration binaryDeclaration = getCorrespondingBinaryDeclaration(that.getDeclarationModel());
                if (binaryDeclaration != null) {
                    replacedDeclarations.add(that.getDeclarationModel());
                    that.setDeclarationModel((Setter)binaryDeclaration);
                }
            }
            super.visit(that);
        }

        @Override
        public void visit(Tree.AnyMethod that) {
            if (isInProxyPackage(that.getDeclarationModel())) {
                Declaration binaryDeclaration = getCorrespondingBinaryDeclaration(that.getDeclarationModel());
                if (binaryDeclaration != null) {
                    replacedDeclarations.add(that.getDeclarationModel());
                    that.setDeclarationModel((Method)binaryDeclaration);
                }
            }
            super.visit(that);
        }

        @Override
        public void visit(Tree.AnyClass that) {
            if (isInProxyPackage(that.getDeclarationModel())) {
                Declaration binaryDeclaration = getCorrespondingBinaryDeclaration(that.getDeclarationModel());
                if (binaryDeclaration != null) {
                    replacedDeclarations.add(that.getDeclarationModel());
                    that.setDeclarationModel((com.redhat.ceylon.compiler.typechecker.model.Class)binaryDeclaration);
                }
            }
            super.visit(that);
        }

        @Override
        public void visit(Tree.AnyInterface that) {
            if (isInProxyPackage(that.getDeclarationModel())) {
                Declaration binaryDeclaration = getCorrespondingBinaryDeclaration(that.getDeclarationModel());
                if (binaryDeclaration != null) {
                    replacedDeclarations.add(that.getDeclarationModel());
                    that.setDeclarationModel((Interface)binaryDeclaration);
                }
            }
            super.visit(that);
        }

        @Override
        public void visit(Tree.ObjectDefinition that) {
            if (isInProxyPackage(that.getDeclarationModel())) {
                Declaration binaryDeclaration = getCorrespondingBinaryDeclaration(that.getDeclarationModel());
                if (binaryDeclaration != null) {
                    replacedDeclarations.add(that.getDeclarationModel());
                    that.setDeclarationModel((Value)binaryDeclaration);
                }
            }
            super.visit(that);
        }

        @Override
        public void visit(Tree.Variable that) {
            if (isInProxyPackage(that.getDeclarationModel())) {
                Declaration binaryDeclaration = getCorrespondingBinaryDeclaration(that.getDeclarationModel());
                if (binaryDeclaration != null) {
                    replacedDeclarations.add(that.getDeclarationModel());
                    that.setDeclarationModel((Value)binaryDeclaration);
                }
            }
            super.visit(that);
        }
        
        @Override
        public void visit(Tree.TypeAliasDeclaration that) {
            if (isInProxyPackage(that.getDeclarationModel())) {
                Declaration binaryDeclaration = getCorrespondingBinaryDeclaration(that.getDeclarationModel());
                if (binaryDeclaration != null) {
                    replacedDeclarations.add(that.getDeclarationModel());
                    that.setDeclarationModel((TypeAlias)binaryDeclaration);
                }
            }
            super.visit(that);
        }

        @Override
        public void visit(Tree.TypeConstraint that) {
            if (isInProxyPackage(that.getDeclarationModel())) {
                Declaration binaryDeclaration = getCorrespondingBinaryDeclaration(that.getDeclarationModel());
                if (binaryDeclaration != null) {
                    replacedDeclarations.add(that.getDeclarationModel());
                    that.setDeclarationModel((TypeParameter)binaryDeclaration);
                }
            }
            super.visit(that);
        }

        @Override
        public void visit(Tree.TypeParameterDeclaration that) {
            if (isInProxyPackage(that.getDeclarationModel())) {
                Declaration binaryDeclaration = getCorrespondingBinaryDeclaration(that.getDeclarationModel());
                if (binaryDeclaration != null) {
                    replacedDeclarations.add(that.getDeclarationModel());
                    that.setDeclarationModel((TypeParameter)binaryDeclaration);
                }
            }
            super.visit(that);
        }

        public void visitAny(Node node) {
            super.visitAny(node);
        }
    }
*/
    private ExternalPhasedUnit buildPhasedUnit(String sourceUnitFullPath) {
        if (sourceArchivePath == null || sourceUnitFullPath == null) {
            return null;
        }
        
        if (! sourceUnitFullPath.startsWith(sourceArchivePath)) {
            return null;
        }
        
        ExternalPhasedUnit phasedUnit = null;
        String sourceUnitRelativePath = sourceUnitFullPath.replace(sourceArchivePath + "!/", "");
        List<String> pathParts = Arrays.asList(sourceUnitRelativePath.split("/"));
        String packageName = Util.formatPath(pathParts.subList(0, pathParts.size()-1));
        Package pkg = getPackage(packageName);
        if (pkg != null) {
            try {
                JDTModuleManager moduleManager = getModuleManager();
                ClosableVirtualFile sourceArchive = null;
                VirtualFile archiveEntry = null; 
                try {
                    sourceArchive = moduleManager.getContext().getVfs().getFromZipFile(new File(sourceArchivePath));
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
                    
                    if (archiveEntry != null) {
                        IProject project = moduleManager.getJavaProject().getProject();
                        CeylonLexer lexer = new CeylonLexer(new ANTLRInputStream(archiveEntry.getInputStream(), project.getDefaultCharset()));
                        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
                        CeylonParser parser = new CeylonParser(tokenStream);
                        Tree.CompilationUnit cu = parser.compilationUnit();
                        List<CommonToken> tokens = new ArrayList<CommonToken>(tokenStream.getTokens().size()); 
                        tokens.addAll(tokenStream.getTokens());
                        phasedUnit = new ExternalPhasedUnit(archiveEntry, sourceArchive, cu, 
                                new SingleSourceUnitPackage(pkg, sourceUnitFullPath), moduleManager, CeylonBuilder.getProjectTypeChecker(project), tokens);
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
                    phasedUnit.scanDeclarations();
                    phasedUnit.scanTypeDeclarations();
                    phasedUnit.validateRefinement();
/*
                    ExternalSourceFile sourceUnit = phasedUnit.getUnit();
                    com.redhat.ceylon.compiler.typechecker.model.Package proxyPackage = sourceUnit.getPackage();
                    ReplaceDeclarationsVisitor visitor = new ReplaceDeclarationsVisitor(pkg, sourceUnit);
                    phasedUnit.getCompilationUnit().visit(visitor);
                    proxyPackage.removeUnit(sourceUnit);
                    Set<Declaration> replacedDeclarations = visitor.getReplacedDeclarations();
                    for (Declaration d : replacedDeclarations) {
                        sourceUnit.removeDeclaration(d);
                    }
                    proxyPackage.addUnit(sourceUnit);
                    */
                }
            } catch (Exception e) {
                e.printStackTrace();
                phasedUnit = null;
            }
        }
        return phasedUnit;
    }

    public String getSourceArchivePath() {
        return sourceArchivePath;
    }
}
