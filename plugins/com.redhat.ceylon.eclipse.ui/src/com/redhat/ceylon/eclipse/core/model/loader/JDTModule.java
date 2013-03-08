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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.core.PackageFragment;

import com.redhat.ceylon.cmr.api.ArtifactResult;
import com.redhat.ceylon.cmr.api.JDKUtils;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.compiler.loader.AbstractModelLoader;
import com.redhat.ceylon.compiler.loader.model.LazyModule;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathContainer;
import com.redhat.ceylon.eclipse.core.classpath.CeylonRuntimeContainer;

public class JDTModule extends LazyModule {

    private JDTModuleManager moduleManager;
    private List<IPackageFragmentRoot> packageFragmentRoots;
    private File jarPath;

    public JDTModule(JDTModuleManager jdtModuleManager, List<IPackageFragmentRoot> packageFragmentRoots) {
        this.moduleManager = jdtModuleManager;
        this.packageFragmentRoots = packageFragmentRoots;
    }

    public synchronized List<IPackageFragmentRoot> getPackageFragmentRoots() {
        if (packageFragmentRoots.isEmpty() && 
                ! moduleManager.isExternalModuleLoadedFromSource(getNameAsString())) {
            IJavaProject javaProject = moduleManager.getJavaProject();
            if (this.equals(getLanguageModule())) {
                IClasspathEntry runtimeClasspathEntry = null;
                
                try {
                    for (IClasspathEntry entry : javaProject.getRawClasspath()) {
                        if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER &&
                                entry.getPath().segment(0).equals(CeylonRuntimeContainer.CONTAINER_ID)) {
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
                            jarToSearch = CeylonClasspathContainer.getModuleArtifact(repoMgr, this);
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
        return packageFragmentRoots;
    }

    @Override
    protected AbstractModelLoader getModelLoader() {
        return moduleManager.getModelLoader();
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
        }else if(isJava()){
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
}
