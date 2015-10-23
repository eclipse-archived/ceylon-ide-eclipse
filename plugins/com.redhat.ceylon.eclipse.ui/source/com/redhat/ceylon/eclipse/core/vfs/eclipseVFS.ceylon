import com.redhat.ceylon.ide.common.vfs {
    FolderVirtualFile,
    ResourceVirtualFile,
    FileVirtualFile,
    BaseFolderVirtualFile
}
import org.eclipse.core.resources {
    IResource, IFolder, IFile,
    IProject
}
import ceylon.interop.java {
    toStringArray
}

import java.util {
    JList = List,
    ArrayList
}
import java.lang {
    RuntimeException
}
import java.io {
    InputStream
}
import org.eclipse.core.runtime {
    IPath,
    CoreException
}
import com.redhat.ceylon.eclipse.core.model {
    ceylonModel
}

shared interface IResourceVirtualFile {
    shared formal IProject project;
}

shared class IFolderVirtualFile
        satisfies FolderVirtualFile<IResource, IFolder, IFile> & IResourceVirtualFile {
    shared actual IFolder nativeResource;
    shared actual IProject project;

    shared new(IFolder nativeResource) {
        this.nativeResource = nativeResource;
        project = nativeResource.project;
    }

    shared new fromProject(IProject project, IPath projectRelativePath) {
        this.project = project;
        this.nativeResource = project.getFolder(projectRelativePath);
    }

    shared actual IFolderVirtualFile? parent
            => if (is IFolder folderParent = nativeResource.parent)
                    then IFolderVirtualFile(folderParent)
                    else null;
    shared actual FileVirtualFile<IResource,IFolder,IFile>? findFile(String fileName)
            => if (exists nativeFile = nativeResource.getFile(fileName))
                    then IFileVirtualFile(nativeFile)
                    else null;
    shared actual JList<out ResourceVirtualFile<IResource, IFolder, IFile>> children {
        value children = ArrayList<ResourceVirtualFile<IResource, IFolder, IFile>>();
        try {
            for (childResource in nativeResource.members().iterable) {
                assert (exists childResource);
                children.add(ceylonModel.vfs.createVirtualResource(childResource));
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return children;
    }
    shared actual String name => nativeResource.name;
    shared actual String path => nativeResource.projectRelativePath.string;
    shared actual Boolean equals(Object that)
            => (super of FolderVirtualFile<IResource, IFolder, IFile>).equals(that);
    shared actual Integer hash
            => (super of FolderVirtualFile<IResource, IFolder, IFile>).hash;

    shared actual [String*] toPackageName(BaseFolderVirtualFile srcDir) {
        assert(is IFolderVirtualFile srcDir);
        return toStringArray(nativeResource.projectRelativePath
            .makeRelativeTo(srcDir.nativeResource.projectRelativePath)
                .segments()).coalesced.sequence();
    }
    shared actual Boolean \iexists() => nativeResource.accessible;
}

shared class IFileVirtualFile
        satisfies FileVirtualFile<IResource, IFolder, IFile> & IResourceVirtualFile {
    shared actual IFile nativeResource;
    shared actual IProject project;

    shared new(IFile nativeResource) {
        this.nativeResource = nativeResource;
        project = nativeResource.project;
    }

    shared new fromProject(IProject project, IPath projectRelativePath) {
        this.project = project;
        this.nativeResource = project.getFile(projectRelativePath);
    }

    shared actual IFolderVirtualFile? parent
            => if (is IFolder folderParent = nativeResource.parent)
    then IFolderVirtualFile(folderParent)
    else null;

    shared actual Boolean equals(Object that)
            => (super of FileVirtualFile<IResource, IFolder, IFile>).equals(that);
    shared actual Integer hash
            => (super of FileVirtualFile<IResource, IFolder, IFile>).hash;
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
    shared actual Boolean \iexists() => nativeResource.accessible;
}

