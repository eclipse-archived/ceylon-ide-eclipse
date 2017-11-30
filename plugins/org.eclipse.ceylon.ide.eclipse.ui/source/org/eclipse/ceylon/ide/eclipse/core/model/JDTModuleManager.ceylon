/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import ceylon.collection {
    MutableList,
    ArrayList
}

import org.eclipse.ceylon.compiler.typechecker.context {
    Context
}
import org.eclipse.ceylon.ide.eclipse.core.builder {
    CeylonBuilder
}
import org.eclipse.ceylon.ide.common.model {
    IdeModuleManager,
    CeylonProject,
    BaseIdeModuleManager,
    BaseIdeModuleSourceMapper,
    BaseCeylonProject
}
import org.eclipse.ceylon.model.cmr {
    JDKUtils
}
import org.eclipse.ceylon.model.typechecker.model {
    Module,
    Modules
}

import java.lang.ref {
    WeakReference
}

import org.eclipse.core.resources {
    IProject,
    IResource,
    IFolder,
    IFile
}
import org.eclipse.jdt.core {
    IClasspathEntry,
    IJavaProject,
    IPackageFragmentRoot,
    JavaCore,
    JavaModelException
}
import org.eclipse.jdt.internal.core {
    JarPackageFragmentRoot
}

shared class JDTModuleManager(Context context, CeylonProject<IProject,IResource,IFolder,IFile>? ceylonProject)
         extends IdeModuleManager<IProject,IResource,IFolder,IFile>(ceylonModel, ceylonProject) {

    shared IJavaProject? javaProject => 
            if (exists nativeProject = ceylonProject?.ideArtifact) 
            then JavaCore.create(nativeProject) 
            else null;

    shared actual JDTModelLoader newModelLoader(BaseIdeModuleManager self, BaseIdeModuleSourceMapper sourceMapper, Modules modules) {
        assert (is JDTModuleSourceMapper sourceMapper);
        assert (is JDTModuleManager self);
        value modelLoader = JDTModelLoader(self, sourceMapper, modules, ceylonProject?.configuration?.jdkProvider);
        if (exists nativeProject = ceylonProject?.ideArtifact) {
            modelLoaders.put(nativeProject, WeakReference(modelLoader));
        }
        return modelLoader;
    }
    
    shared actual Boolean moduleFileInProject(String moduleName, BaseCeylonProject? ceylonProject) {
        if (!exists ceylonProject) {
            return false;
        }
        assert(is EclipseCeylonProject ceylonProject);
        value nativeProject = ceylonProject.ideArtifact;
        IJavaProject javaProject = JavaCore.create(nativeProject);
        try {
            for (sourceFolder in javaProject.packageFragmentRoots.array.coalesced) {
                if (!sourceFolder.archive, 
                    sourceFolder.\iexists(), 
                    sourceFolder.kind == IPackageFragmentRoot.\iK_SOURCE){
                    value pkgFragment = sourceFolder.getPackageFragment(moduleName);
                    // only treat this as containing a module if we do have a module descriptor for it
                    if(pkgFragment.\iexists()){
                        for(resource  in pkgFragment.nonJavaResources){
                            print(className(resource));
                            if(is IFile resource,
                               resource.name == "module.ceylon"){
                                return true;
                            }
                        }
                    }
                }
            }
        }
        catch (JavaModelException e) {
            e.printStackTrace();
        }
        return false;
    }

    shared actual JDTModule newModule(String moduleName, String version) {
        MutableList<IPackageFragmentRoot> roots = ArrayList<IPackageFragmentRoot>();
        if (exists ceylonProject) {
            value javaProject = JavaCore.create(ceylonProject.ideArtifact);
            try {
                if (moduleName.equals(Module.\iDEFAULT_MODULE_NAME)) {
                    for (root in javaProject.packageFragmentRoots.array.coalesced) {
                        if (root.\iexists(), javaProject.isOnClasspath(root)) {
                            value entry = root.resolvedClasspathEntry;
                            if (entry.entryKind == IClasspathEntry.\iCPE_SOURCE,
                                !root.external) {
                                roots.add(root);
                            }
                        }
                    }
                }
                else {
                    for (root in javaProject.packageFragmentRoots.array.coalesced) {
                        if (root.\iexists(), 
                            javaProject.isOnClasspath(root)) {
                            if (JDKUtils.isJDKModule(moduleName)) {
                                for (pkg in JDKUtils.getJDKPackagesByModule(moduleName)) {
                                    if (root.getPackageFragment(pkg.string).\iexists()) {
                                        roots.add(root);
                                        break;
                                    }
                                }
                            }
                            else if (JDKUtils.isOracleJDKModule(moduleName)) {
                                for (pkg in JDKUtils.getOracleJDKPackagesByModule(moduleName)) {
                                    if (root.getPackageFragment(pkg.string).\iexists()) {
                                        roots.add(root);
                                        break;
                                    }
                                }
                            }
                            else if (!(root is JarPackageFragmentRoot), 
                                !CeylonBuilder.isInCeylonClassesOutputFolder(root.path)) {
                                String packageToSearch = moduleName;
                                if (root.getPackageFragment(packageToSearch).\iexists()) {
                                    roots.add(root);
                                }
                            }
                        }
                    }
                }
            }
            catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
        assert(is JDTModuleSourceMapper msm=moduleSourceMapper);
        return JDTModule(this, msm, roots);
    }
}
