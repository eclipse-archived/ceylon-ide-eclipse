import com.redhat.ceylon.ide.common.model {
    CeylonProjects,
    CeylonProject
}
import org.eclipse.core.resources {
    IProject,
    IResource,
    IFolder,
    IFile
}
import com.redhat.ceylon.ide.common.util {
    Path
}
import com.redhat.ceylon.eclipse.core.vfs {
    IFileVirtualFile,
    IFolderVirtualFile
}
import com.redhat.ceylon.eclipse.util {
    toEclipsePath
}
import ceylon.interop.java {
    toStringArray
}
import com.redhat.ceylon.ide.common.vfs {
    FileVirtualFile,
    FolderVirtualFile
}

shared object ceylonModel extends CeylonProjects<IProject,IResource,IFolder,IFile>() {
    variable VirtualFileSystem? _vfs = null;
    
    shared actual class VirtualFileSystem() 
            extends super.VirtualFileSystem() {
        shared actual FileVirtualFile<IProject,IResource,IFolder,IFile>
        createVirtualFile(IFile file, CeylonProject<IProject,IResource,IFolder,IFile> unused)
                => IFileVirtualFile(file);
        
        shared actual FileVirtualFile<IProject,IResource,IFolder,IFile> createVirtualFileFromProject(IProject project, Path path) =>
                IFileVirtualFile.fromProject(project, toEclipsePath(path));
        
        shared actual FolderVirtualFile<IProject,IResource,IFolder,IFile>
        createVirtualFolder(IFolder folder, CeylonProject<IProject,IResource,IFolder,IFile> unused)
                => IFolderVirtualFile(folder);
        
        shared actual FolderVirtualFile<IProject,IResource,IFolder,IFile> createVirtualFolderFromProject(IProject project, Path path) =>
                IFolderVirtualFile.fromProject(project, toEclipsePath(path));
        
        shared actual Boolean existsOnDisk(IResource resource) => resource.accessible;
        
        shared actual IFile? findFile(IFolder resource, String fileName) =>
                if (exists nativeFile = resource.getFile(fileName),
                    nativeFile.accessible)
                    then nativeFile
                    else null;
        
        shared actual IFolder? getParent(IResource resource) => 
                    if (is IFolder p=resource.parent)
                    then p
                    else null;
        
        shared actual Boolean isFolder(IResource resource) => resource is IFolder;
        
        shared actual String[] toPackageName(IFolder resource, IFolder sourceDir) =>
                toStringArray(resource.projectRelativePath
                .makeRelativeTo(sourceDir.projectRelativePath)
                    .segments()).coalesced.sequence();
        
    }
    
    shared actual VirtualFileSystem vfs {
        if (exists theVFS = _vfs) {
            return theVFS;
        } else {
            value theVFS = VirtualFileSystem();
            _vfs = theVFS;
            return theVFS;
        }
    }
    
    shared actual CeylonProject<IProject,IResource,IFolder,IFile> newNativeProject(IProject nativeProject)
            => EclipseCeylonProject(nativeProject);
    
}