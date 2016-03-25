import ceylon.interop.java {
    toStringArray
}

import com.redhat.ceylon.eclipse.core.model {
    nativeFolderProperties
}
import com.redhat.ceylon.eclipse.core.vfs {
    IFolderVirtualFile,
    IFileVirtualFile
}
import com.redhat.ceylon.eclipse.util {
    toEclipsePath
}
import com.redhat.ceylon.ide.common.model {
    CeylonProject
}
import com.redhat.ceylon.ide.common.platform {
    platformUtils,
    Status,
    VfsServices
}
import com.redhat.ceylon.ide.common.util {
    unsafeCast,
    Path
}
import com.redhat.ceylon.ide.common.vfs {
    FolderVirtualFile,
    FileVirtualFile
}
import com.redhat.ceylon.model.typechecker.model {
    Package
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

    shared actual Path getPath(IResource resource) => 
            Path(getPathString(resource));

    // TODO: Check if it's really necessary to only have the project-relative path"
    shared actual String getPathString(IResource resource) => 
            resource.projectRelativePath.string;


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
}