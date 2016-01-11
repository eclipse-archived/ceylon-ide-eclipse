package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.model.typechecker.model.ModelUtil.formatPath;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isCompilable;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.*;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.SubMonitor;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.ide.common.model.BaseIdeModelLoader;
import com.redhat.ceylon.ide.common.model.IdeModuleManager;
import com.redhat.ceylon.ide.common.model.IdeModuleSourceMapper;
import com.redhat.ceylon.ide.common.vfs.FileVirtualFile;
import com.redhat.ceylon.ide.common.vfs.FolderVirtualFile;
import com.redhat.ceylon.ide.common.vfs.ResourceVirtualFile;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.RootFolderType;

final class RootFolderScanner implements IResourceVisitor {
    private final Module defaultModule;
    private final BaseIdeModelLoader modelLoader;
    private final IdeModuleManager<IProject,IResource,IFolder,IFile> moduleManager;
    private final IdeModuleSourceMapper<IProject,IResource,IFolder,IFile> moduleSourceMapper;
    private final FolderVirtualFile<IProject, IResource, IFolder, IFile> rootDir;
    private final TypeChecker typeChecker;
    private final List<IFile> scannedFiles;
    private final PhasedUnits phasedUnits;
    private Module module;
    private SubMonitor monitor;
    private boolean isInResourceForlder = false;
    private boolean isInSourceForlder = false;
    private RootFolderType rootFolderType;
    

    RootFolderScanner(RootFolderType rootFolderType, Module defaultModule, BaseIdeModelLoader modelLoader,
            IdeModuleManager<IProject,IResource,IFolder,IFile> moduleManager, 
            IdeModuleSourceMapper<IProject,IResource,IFolder,IFile> moduleSourceMapper, 
            FolderVirtualFile<IProject, IResource, IFolder, IFile> rootDir, TypeChecker typeChecker,
            List<IFile> scannedFiles, PhasedUnits phasedUnits, SubMonitor monitor) {
        this.rootFolderType = rootFolderType;
        this.isInResourceForlder = rootFolderType.equals(RootFolderType.RESOURCE);
        this.isInSourceForlder = rootFolderType.equals(RootFolderType.SOURCE);
        this.defaultModule = defaultModule;
        this.modelLoader = modelLoader;
        this.moduleManager = moduleManager;
        this.moduleSourceMapper = moduleSourceMapper;
        this.rootDir = rootDir;
        this.typeChecker = typeChecker;
        this.scannedFiles = scannedFiles;
        this.phasedUnits = phasedUnits;
        this.monitor = monitor;
    }

    public boolean visit(IResource resource) throws CoreException {
        Package pkg;

        monitor.setWorkRemaining(10000);
        monitor.worked(1);

        if (resource.equals(rootDir.getNativeResource())) {
            resource.setSessionProperty(CeylonBuilder.RESOURCE_PROPERTY_PACKAGE_MODEL, new WeakReference<Package>(modelLoader.findPackage("")));
            resource.setSessionProperty(CeylonBuilder.RESOURCE_PROPERTY_ROOT_FOLDER, rootDir.getNativeResource());
            resource.setSessionProperty(CeylonBuilder.RESOURCE_PROPERTY_ROOT_FOLDER_TYPE, rootFolderType);
            return true;
        }

        if (resource.getParent().equals(rootDir.getNativeResource())) {
            // We've come back to a source directory child : 
            //  => reset the current Module to default and set the package to emptyPackage
            module = defaultModule;
            pkg = modelLoader.findPackage("");
            assert(pkg != null);
        }

        String pkgNameAsString;
        IFolder pkgFolder;
        if (resource instanceof IFolder) {
            pkgFolder = (IFolder) resource;
        } else {
            pkgFolder = (IFolder) resource.getParent();
        }
        
        List<String> pkgName = getPackageName(pkgFolder);
        pkgNameAsString = formatPath(pkgName);
        if ( module != defaultModule ) {
            if (! pkgNameAsString.startsWith(module.getNameAsString() + ".")) {
                // We've ran above the last module => reset module to default 
                module = defaultModule;
            }
        }
        
        Module realModule = modelLoader.getLoadedModule(pkgNameAsString, null);
        if (realModule != null) {
            // The module descriptor had probably been found in another source directory
            module = realModule;
        }
        pkg = modelLoader.findOrCreatePackage(module, pkgNameAsString);

        if (resource instanceof IFolder) {
            resource.setSessionProperty(CeylonBuilder.RESOURCE_PROPERTY_PACKAGE_MODEL, new WeakReference<Package>(pkg));
            resource.setSessionProperty(CeylonBuilder.RESOURCE_PROPERTY_ROOT_FOLDER, rootDir.getNativeResource());
            return true;
        }

        if (resource instanceof IFile) {
            IFile file = (IFile) resource;
            if (file.exists()) {
                boolean isSourceFile = isInSourceForlder && isCompilable(file);
                if (isInResourceForlder || isSourceFile ) {
                    if (scannedFiles != null) {
                        scannedFiles.add((IFile)resource);
                    }
                    
                    if (isSourceFile) {
                        if (CeylonBuilder.isCeylon(file)) {
                            FileVirtualFile<IProject, IResource, IFolder, IFile> virtualFile = vfsJ2C().createVirtualFile(file);
                            try {
                                PhasedUnit newPhasedUnit = CeylonBuilder.parseFileToPhasedUnit(moduleManager, moduleSourceMapper,
                                        typeChecker, virtualFile, rootDir, pkg);
                                phasedUnits.addPhasedUnit(virtualFile, newPhasedUnit);
                            } 
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private List<String> getPackageName(IContainer container) {
        List<String> pkgName = Arrays.asList(container.getProjectRelativePath()
                .makeRelativeTo(rootDir.getNativeResource().getProjectRelativePath()).segments());
        return pkgName;
    }
}