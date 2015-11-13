import com.redhat.ceylon.ide.common.model {
    CeylonProjects,
    ModelAliases,
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

shared object ceylonModel extends CeylonProjects<IProject,IResource,IFolder,IFile>() {
    variable VirtualFileSystem? _vfs = null;
    
    shared actual class VirtualFileSystem() extends super.VirtualFileSystem() {
        shared actual FileVirtualFileAlias createVirtualFile(IFile file) =>
                IFileVirtualFile(file);
        
        shared actual FileVirtualFileAlias createVirtualFileFromProject(IProject project, Path path) =>
                IFileVirtualFile.fromProject(project, toEclipsePath(path));
        
        shared actual FolderVirtualFileAlias createVirtualFolder(IFolder folder) =>
                IFolderVirtualFile(folder);
        
        shared actual FolderVirtualFileAlias createVirtualFolderFromProject(IProject project, Path path) =>
                IFolderVirtualFile.fromProject(project, toEclipsePath(path));
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