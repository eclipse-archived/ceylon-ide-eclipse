/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import ceylon.interop.java {
    toStringArray
}

import org.eclipse.ceylon.ide.eclipse.core.model {
    nativeFolderProperties
}
import org.eclipse.ceylon.ide.eclipse.core.vfs {
    IFolderVirtualFile,
    IFileVirtualFile
}
import org.eclipse.ceylon.ide.eclipse.util {
    toEclipsePath,
    fromEclipsePath
}
import org.eclipse.ceylon.ide.common.model {
    CeylonProject
}
import org.eclipse.ceylon.ide.common.platform {
    platformUtils,
    Status,
    VfsServices
}
import org.eclipse.ceylon.ide.common.util {
    unsafeCast,
    Path
}
import org.eclipse.ceylon.ide.common.vfs {
    FolderVirtualFile,
    FileVirtualFile
}
import org.eclipse.ceylon.model.typechecker.model {
    Package
}

import java.io {
    File
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
import org.eclipse.core.runtime {
    CoreException
}

object eclipseVfsServices 
        satisfies VfsServices<IProject, IResource, IFolder,IFile> {

    shared actual FileVirtualFile<IProject,IResource,IFolder,IFile> createVirtualFile(IFile file, IProject unused) =>
            IFileVirtualFile(file);
    
    shared actual FileVirtualFile<IProject,IResource,IFolder,IFile> createVirtualFileFromProject(IProject project, Path path) =>
            IFileVirtualFile.fromProject(project, toEclipsePath(path));
    
    shared actual FolderVirtualFile<IProject,IResource,IFolder,IFile> createVirtualFolder(IFolder folder, IProject unused) =>
            IFolderVirtualFile(folder);
    
    shared actual FolderVirtualFile<IProject,IResource,IFolder,IFile> createVirtualFolderFromProject(IProject project, Path path) =>
            IFolderVirtualFile.fromProject(project, toEclipsePath(path));
    
    shared actual Boolean existsOnDisk(IResource resource) =>
            resource.accessible;
    
    shared actual IFile? findFile(IFolder resource, String fileName) =>
            if (exists nativeFile = resource.getFile(fileName),
        nativeFile.accessible)
    then nativeFile
    else null;

    shared actual IResource? findChild(IFolder|IProject parent, Path path) =>
            if (exists nativeResource = parent.findMember(path.string),
        nativeResource.accessible)
    then nativeResource
    else null;
    
    shared actual IFolder? getParent(IResource resource) => 
            if (is IFolder p=resource.parent)
    then p
    else null;
    
    shared actual Boolean isFolder(IResource resource) =>
            resource is IFolder;
    
    shared actual String[] toPackageName(IFolder resource, IFolder sourceDir) =>
            toStringArray(resource.projectRelativePath
        .makeRelativeTo(sourceDir.projectRelativePath)
            .segments()).coalesced.sequence();
    
    shared actual String getShortName(IResource resource) => 
            resource.name;

    // TODO: Check if it's really necessary to only have the project-relative path"
    shared actual Path getVirtualFilePath(IResource resource) => 
            Path(getVirtualFilePathString(resource));

    // TODO: Check if it's really necessary to only have the project-relative path"
    shared actual String getVirtualFilePathString(IResource resource) => 
            resource.projectRelativePath.string;
    
    shared actual Path getProjectRelativePath(IResource resource, CeylonProjectAlias|IProject project) =>
            Path(getProjectRelativePathString(resource, project));
    
    shared actual String getProjectRelativePathString(IResource resource, CeylonProjectAlias|IProject project) =>
            resource.projectRelativePath.string;

    shared actual File? getJavaFile(IResource resource) =>
           resource.location?.toFile();

    shared actual IResource? fromJavaFile(File javaFile, IProject project) {
        value projectLocation = fromEclipsePath(project.location);
        value absolutePath = Path(javaFile.absolutePath);
        
        if (projectLocation.isPrefixOf(absolutePath)) {
            value projectRelativePath = absolutePath.removeFirstSegments(projectLocation.segmentCount);
            IResource? resource = project.findMember(projectRelativePath.string);
            if (exists resource,
                resource.accessible) {
                return resource;
            }
        }
        return null;
    }
    
    shared actual void setPackagePropertyForNativeFolder(CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject, IFolder folder, WeakReference<Package> p) {
        folder.setSessionProperty(nativeFolderProperties.packageModel, p);
    }
    
    shared actual void removePackagePropertyForNativeFolder(CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject, IFolder folder) {
        if (folder.\iexists()) {
            folder.setSessionProperty(nativeFolderProperties.packageModel, null);
        }
    }

    shared actual WeakReference<Package>? getPackagePropertyForNativeFolder(CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject, IFolder folder) {
        try {
            return unsafeCast<WeakReference<Package>?>(folder.getSessionProperty(nativeFolderProperties.packageModel));
        } catch (CoreException e) {
            platformUtils.log(Status._WARNING, "Unexpected exception", e);
            return null;
        }
    }
    
    shared actual void setRootPropertyForNativeFolder(CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject, IFolder folder, WeakReference<FolderVirtualFile<IProject,IResource,IFolder,IFile>> root) {
        folder.setSessionProperty(nativeFolderProperties.root, root);
    }
    
    shared actual void removeRootPropertyForNativeFolder(CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject, IFolder folder) {
        if (folder.\iexists()) {
            folder.setSessionProperty(nativeFolderProperties.root, null);
        }
    }

    shared actual WeakReference<FolderVirtualFile<IProject,IResource,IFolder,IFile>>? getRootPropertyForNativeFolder(CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject, IFolder folder) { 
        try {
            return unsafeCast<WeakReference<FolderVirtualFile<IProject,IResource,IFolder,IFile>>?>(folder.getSessionProperty(nativeFolderProperties.root));
        } catch (CoreException e) {
            platformUtils.log(Status._WARNING, "Unexpected exception", e);
            return null;
        }
    }
    
    shared actual void setRootIsSourceProperty(CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject, IFolder rootFolder, Boolean isSource) {
        rootFolder.setSessionProperty(nativeFolderProperties.rootIsSource, isSource);
    }

    shared actual void removeRootIsSourceProperty(CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject, IFolder rootFolder) { 
        if (rootFolder.\iexists()) {
            rootFolder.setSessionProperty(nativeFolderProperties.rootIsSource, null);
        }            
    }

    shared actual Boolean? getRootIsSourceProperty(CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject, IFolder rootFolder) { 
        try {
            return unsafeCast<Boolean?>(rootFolder.getSessionProperty(nativeFolderProperties.rootIsSource));
        } catch (CoreException e) {
            platformUtils.log(Status._WARNING, "Unexpected exception", e);
            return null;
        }
    }
    shared actual Boolean flushIfNecessary(IResource resource) => true;
    
}