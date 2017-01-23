package com.redhat.ceylon.eclipse.core.model;

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

import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.eclipse.java2ceylon.ModelJ2C;
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
