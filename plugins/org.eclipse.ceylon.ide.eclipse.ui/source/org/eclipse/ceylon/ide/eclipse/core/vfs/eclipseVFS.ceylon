/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.ide.eclipse.core.external {
    ExternalSourceArchiveManager
}
import org.eclipse.ceylon.ide.eclipse.core.model {
    ceylonModel
}
import org.eclipse.ceylon.ide.common.model {
    CeylonProject
}
import org.eclipse.ceylon.ide.common.vfs {
    FolderVirtualFile,
    ResourceVirtualFile,
    FileVirtualFile
}
import org.eclipse.ceylon.model.typechecker.model {
    Package
}

import java.util {
    JList=List,
    ArrayList
}

import org.eclipse.core.resources {
    IResource,
    IFolder,
    IFile,
    IProject
}
import org.eclipse.core.runtime {
    IPath,
    CoreException
}

shared class IFolderVirtualFile
        satisfies FolderVirtualFile<IProject, IResource, IFolder, IFile> {
    shared actual IFolder nativeResource;
    shared actual IProject nativeProject;
    shared actual CeylonProject<IProject, IResource, IFolder, IFile>? ceylonProject;
    shared new(IFolder nativeResource) {
        this.nativeResource = nativeResource;
        nativeProject = nativeResource.project;
        ceylonProject = ceylonModel.getProject(nativeResource.project);
    }

    shared new fromProject(IProject project, IPath projectRelativePath) {
        nativeResource = project.getFolder(projectRelativePath);
        nativeProject = project;
        ceylonProject = ceylonModel.getProject(nativeResource.project);
    }

    shared actual JList<out ResourceVirtualFile<IProject,IResource, IFolder, IFile>> children {
        value children = ArrayList<ResourceVirtualFile<IProject,IResource, IFolder, IFile>>();
        try {
            for (childResource in nativeResource.members().iterable) {
                assert (exists childResource);
                children.add(vfsServices.createVirtualResource(childResource, nativeProject));
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return children;
    }
    
    shared actual Boolean equals(Object that)
            => (super of FolderVirtualFile<IProject,IResource, IFolder, IFile>).equals(that);
    shared actual Integer hash
            => (super of FolderVirtualFile<IProject,IResource, IFolder, IFile>).hash;

    shared actual FolderVirtualFile<IProject,IResource,IFolder,IFile>? rootFolder {
        value folder = nativeResource;
        if (folder.isLinked(IResource.\iCHECK_ANCESTORS) 
            && ExternalSourceArchiveManager.isInSourceArchive(folder)) {
            return null;
        }
        
        return super.rootFolder;
    }
    
    shared actual Package? ceylonPackage { 
        if (nativeResource.isLinked(IResource.\iCHECK_ANCESTORS) 
            && ExternalSourceArchiveManager.isInSourceArchive(nativeResource)) {
            return null;
        }
        return super.ceylonPackage;
    }
}

shared class IFileVirtualFile
        satisfies FileVirtualFile<IProject,IResource, IFolder, IFile> {
    shared actual IFile nativeResource;
    shared actual IProject nativeProject;
    shared actual CeylonProject<IProject, IResource, IFolder, IFile>? ceylonProject;

    shared new(IFile nativeResource) {
        this.nativeResource = nativeResource;
        nativeProject = nativeResource.project;
        ceylonProject = ceylonModel.getProject(nativeProject);
    }

    shared new fromProject(IProject project, IPath projectRelativePath) {
        nativeResource = project.getFile(projectRelativePath);
        nativeProject = project;
        ceylonProject = ceylonModel.getProject(nativeProject);
    }

    equals(Object that) => (super of FileVirtualFile<IProject,IResource, IFolder, IFile>).equals(that);
    hash => (super of FileVirtualFile<IProject,IResource, IFolder, IFile>).hash;
    
    inputStream => nativeResource.getContents(true);

    charset => nativeResource.project.defaultCharset; // in the future, we could return the charset of the file
}

