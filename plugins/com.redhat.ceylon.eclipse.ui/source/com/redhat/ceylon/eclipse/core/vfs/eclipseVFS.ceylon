import com.redhat.ceylon.eclipse.core.model {
    ceylonModel,
    nativeFolderProperties,
    JDTModelLoader
}
import com.redhat.ceylon.ide.common.model {
    CeylonProject,
    CeylonProjects
}
import com.redhat.ceylon.ide.common.util {
    unsafeCast,
    Status
}
import com.redhat.ceylon.ide.common.vfs {
    FolderVirtualFile,
    ResourceVirtualFile,
    FileVirtualFile
}
import com.redhat.ceylon.model.typechecker.model {
    Package
}

import java.io {
    InputStream
}
import java.lang {
    RuntimeException
}
import java.lang.ref {
    WeakReference
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
import com.redhat.ceylon.eclipse.core.external {
    ExternalSourceArchiveManager
}
import com.redhat.ceylon.eclipse.util {
    eclipsePlatformUtils
}
import com.redhat.ceylon.eclipse.core.builder {
    CeylonBuilder
}
import ceylon.interop.java {
    CeylonIterable
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
                children.add(ceylonModel.vfs.createVirtualResource(childResource, nativeProject));
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return children;
    }
    shared actual String name => nativeResource.name;
    shared actual String path => nativeResource.projectRelativePath.string;
    shared actual Boolean equals(Object that)
            => (super of FolderVirtualFile<IProject,IResource, IFolder, IFile>).equals(that);
    shared actual Integer hash
            => (super of FolderVirtualFile<IProject,IResource, IFolder, IFile>).hash;

    shared actual Boolean? isSource => 
            let (root = rootFolder) 
            if (exists root)
            then
                if (root == this)
                then unsafeCast<Boolean>(nativeResource.getSessionProperty(nativeFolderProperties.rootIsSource))
                else root.isSource
            else null;
    
    shared actual FolderVirtualFile<IProject,IResource,IFolder,IFile>? rootFolder {
        value folder = nativeResource;
        if (folder.isLinked(IResource.\iCHECK_ANCESTORS) 
            && ExternalSourceArchiveManager.isInSourceArchive(folder)) {
            return null;
        }
        if (! folder.\iexists()) {
            value searchedFullPath = folder.fullPath;
            return ceylonProject?.rootFolders?.find((rootFolder) => folder.fullPath.isPrefixOf(searchedFullPath));
        }
        
        try {
            return unsafeCast<WeakReference<FolderVirtualFile<IProject,IResource,IFolder,IFile>>?>(
                nativeResource.getSessionProperty(nativeFolderProperties.root))?.get();
        } catch (CoreException e) {
            eclipsePlatformUtils.log(Status._WARNING, "Unexpected exception", e);
        }
        return null;
    }
    
    shared actual Package? ceylonPackage { 
        if (nativeResource.isLinked(IResource.\iCHECK_ANCESTORS) 
            && ExternalSourceArchiveManager.isInSourceArchive(nativeResource)) {
            return null;
        }
        if (! nativeResource.\iexists()) {
            if (exists theRootFolder = rootFolder) {
                IPath rootRelativePath = nativeResource.fullPath.makeRelativeTo(theRootFolder.nativeResource.fullPath);
                return CeylonBuilder.getProjectModelLoader(nativeResource.project)
                        ?.findPackage(".".join(rootRelativePath.segments().array.coalesced));
            }
            return null;
        }
        try {
            return unsafeCast<WeakReference<Package>?>(
                nativeResource.getSessionProperty(
                    nativeFolderProperties.packageModel))?.get();
        } catch (CoreException e) {
            eclipsePlatformUtils.log(Status._WARNING, "Unexpected exception", e);
        }
        return null;
    }
    
    shared actual CeylonProjects<IProject,IResource,IFolder,IFile>.VirtualFileSystem vfs => ceylonModel.vfs;
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

    shared actual Boolean equals(Object that)
            => (super of FileVirtualFile<IProject,IResource, IFolder, IFile>).equals(that);
    shared actual Integer hash
            => (super of FileVirtualFile<IProject,IResource, IFolder, IFile>).hash;
    shared actual InputStream inputStream {
        try {
            return nativeResource.getContents(true);
        } catch (CoreException e) {
            throw RuntimeException(e);
        }
    }
    shared actual String name => nativeResource.name;
    shared actual String path => nativeResource.projectRelativePath.string;
    shared actual String charset {
        try {
            return nativeResource.project.defaultCharset; // in the future, we could return the charset of the file
        }
        catch (Exception e) {
            throw RuntimeException(e);
        }

    }
    
    shared actual CeylonProjects<IProject,IResource,IFolder,IFile>.VirtualFileSystem vfs => ceylonModel.vfs;
}

