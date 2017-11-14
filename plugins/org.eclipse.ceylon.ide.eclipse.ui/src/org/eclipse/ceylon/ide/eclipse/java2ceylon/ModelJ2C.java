/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.java2ceylon;

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

import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.compiler.typechecker.context.Context;
import org.eclipse.ceylon.ide.eclipse.core.model.LookupEnvironmentUtilities;
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

public interface ModelJ2C {

    CeylonProjects<IProject, IResource, IFolder, IFile> ceylonModel();

    CeylonProjectConfig ceylonConfig(IProject project);

    CeylonIdeConfig ideConfig(IProject project);

    List<IPackageFragmentRoot> getModulePackageFragmentRoots(
            BaseIdeModule module);

    IdeModuleManager<IProject, IResource, IFolder, IFile> newModuleManager(
            Context context,
            CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject);

    IdeModuleSourceMapper<IProject, IResource, IFolder, IFile> newModuleSourceMapper(
            Context context,
            IdeModuleManager<IProject, IResource, IFolder, IFile> moduleManager);

    JavaClassFile<IProject, IFolder, IFile, ITypeRoot, IJavaElement> newJavaClassFile(
            ITypeRoot typeRoot, String relativePath, String fileName,
            String fullPath, LazyPackage pkg);

    CeylonBinaryUnit<IProject, ITypeRoot, IJavaElement> newCeylonBinaryUnit(
            ITypeRoot typeRoot, String relativePath, String fileName,
            String fullPath, LazyPackage pkg);

    CrossProjectBinaryUnit<IProject, IResource, IFolder, IFile, ITypeRoot, IJavaElement> newCrossProjectBinaryUnit(
            ITypeRoot typeRoot, String relativePath, String fileName,
            String fullPath, LazyPackage pkg);

    JavaCompilationUnit<IProject, IFolder, IFile, ITypeRoot, IJavaElement> newJavaCompilationUnit(
            ITypeRoot typeRoot, String relativePath, String fileName,
            String fullPath, LazyPackage pkg);

    LookupEnvironmentUtilities.Provider getLookupEnvironmentProvider(IType type);

    IdeModelLoader<IProject, IResource, IFolder, IFile, ITypeRoot, IType> javaProjectModelLoader(IJavaProject javaProject);

    void setTypeCheckerOnCeylonProject(
            CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject,
            TypeChecker typechecker);
}