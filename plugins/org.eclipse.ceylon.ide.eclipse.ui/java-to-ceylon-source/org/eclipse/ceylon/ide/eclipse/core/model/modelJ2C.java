/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.model;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;

import org.eclipse.ceylon.compiler.java.runtime.model.TypeDescriptor;
import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.compiler.typechecker.context.Context;
import org.eclipse.ceylon.ide.eclipse.java2ceylon.ModelJ2C;
import org.eclipse.ceylon.ide.common.model.BaseIdeModule;
import org.eclipse.ceylon.ide.common.model.CeylonBinaryUnit;
import org.eclipse.ceylon.ide.common.model.CeylonIdeConfig;
import org.eclipse.ceylon.ide.common.model.CeylonProject;
import org.eclipse.ceylon.ide.common.model.CeylonProjectConfig;
import org.eclipse.ceylon.ide.common.model.CeylonProjects;
import org.eclipse.ceylon.ide.common.model.CrossProjectBinaryUnit;
import org.eclipse.ceylon.ide.common.model.IdeModelLoader;
import org.eclipse.ceylon.ide.common.model.IdeModuleManager;
import org.eclipse.ceylon.ide.common.model.IdeModuleSourceMapper;
import org.eclipse.ceylon.ide.common.model.JavaClassFile;
import org.eclipse.ceylon.ide.common.model.JavaCompilationUnit;
import org.eclipse.ceylon.model.loader.model.LazyPackage;

import ceylon.interop.java.JavaList;

public class modelJ2C implements ModelJ2C {
    @Override
    public CeylonProjects<IProject, IResource,IFolder,IFile> ceylonModel() {
        return ceylonModel_.get_();
    }

    @Override
    public CeylonProjectConfig ceylonConfig(IProject project) {
        CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject = ceylonModel_.get_().getProject(project);
        if (ceylonProject != null) {
            return ceylonProject.getConfiguration();
        }
        return null;
    }

    @Override
    public CeylonIdeConfig ideConfig(IProject project) {
        CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject = ceylonModel_.get_().getProject(project);
        if (ceylonProject != null) {
            return ceylonProject.getIdeConfiguration();
        }
        return null;
    }
    
    @Override
    public List<IPackageFragmentRoot> getModulePackageFragmentRoots(BaseIdeModule module) {
        return new JavaList<IPackageFragmentRoot>(TypeDescriptor.klass(IPackageFragmentRoot.class),((JDTModule) module).getPackageFragmentRoots());
    }

    @Override
    public IdeModuleManager<IProject,IResource,IFolder,IFile> newModuleManager(Context context, CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject) {
        return new JDTModuleManager(context, ceylonProject);
    }

    @Override
    public IdeModuleSourceMapper<IProject,IResource,IFolder,IFile> newModuleSourceMapper(Context context, IdeModuleManager<IProject,IResource,IFolder,IFile> moduleManager) {
        return new JDTModuleSourceMapper(context, moduleManager);
    }

    @Override
    public JavaClassFile<IProject, IFolder, IFile, ITypeRoot, IJavaElement> newJavaClassFile(ITypeRoot typeRoot,
            String relativePath, String fileName, String fullPath, LazyPackage pkg) {
        return new EclipseJavaClassFile(typeRoot, fileName, relativePath, fullPath, pkg);
    }

    @Override
    public CeylonBinaryUnit<IProject, ITypeRoot, IJavaElement> newCeylonBinaryUnit(
            ITypeRoot typeRoot, String relativePath, String fileName,
            String fullPath, LazyPackage pkg) {
        return new EclipseCeylonBinaryUnit(typeRoot, fileName, relativePath, fullPath, pkg);
    }

    @Override
    public CrossProjectBinaryUnit<IProject, IResource, IFolder, IFile, ITypeRoot, IJavaElement> newCrossProjectBinaryUnit(
            ITypeRoot typeRoot, String relativePath, String fileName,
            String fullPath, LazyPackage pkg) {
        return new EclipseCrossProjectBinaryUnit(typeRoot, fileName, relativePath, fullPath, pkg);
    }

    @Override
    public JavaCompilationUnit<IProject, IFolder, IFile, ITypeRoot, IJavaElement> newJavaCompilationUnit(
            ITypeRoot typeRoot, String relativePath, String fileName,
            String fullPath, LazyPackage pkg) {
        return new EclipseJavaCompilationUnit(typeRoot, fileName, relativePath, fullPath, pkg);
    }

    @Override
    public LookupEnvironmentUtilities.Provider getLookupEnvironmentProvider(IType type) {
        return typeModelLoader_.typeModelLoader(type);
    }
    
    @Override
    public IdeModelLoader<IProject, IResource, IFolder, IFile, ITypeRoot, IType> javaProjectModelLoader(IJavaProject javaProject) {
        return javaProjectModelLoader_.javaProjectModelLoader(javaProject);
    }
    
    @Override
    public void setTypeCheckerOnCeylonProject(CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject, TypeChecker typechecker) {
        ((EclipseCeylonProject) ceylonProject).setTypechecker(typechecker);
    }
}
