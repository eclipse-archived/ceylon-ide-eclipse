package com.redhat.ceylon.eclipse.java2ceylon;

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

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.eclipse.core.model.LookupEnvironmentUtilities;
import com.redhat.ceylon.ide.common.model.BaseIdeModule;
import com.redhat.ceylon.ide.common.model.CeylonBinaryUnit;
import com.redhat.ceylon.ide.common.model.CeylonIdeConfig;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.ide.common.model.CeylonProjectConfig;
import com.redhat.ceylon.ide.common.model.CeylonProjects;
import com.redhat.ceylon.ide.common.model.CrossProjectBinaryUnit;
import com.redhat.ceylon.ide.common.model.IdeModelLoader;
import com.redhat.ceylon.ide.common.model.IdeModuleManager;
import com.redhat.ceylon.ide.common.model.IdeModuleSourceMapper;
import com.redhat.ceylon.ide.common.model.JavaClassFile;
import com.redhat.ceylon.ide.common.model.JavaCompilationUnit;
import com.redhat.ceylon.model.loader.model.LazyPackage;

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