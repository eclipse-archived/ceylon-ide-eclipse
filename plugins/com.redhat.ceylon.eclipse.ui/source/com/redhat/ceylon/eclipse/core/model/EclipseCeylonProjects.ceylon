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

shared object ceylonModel extends CeylonProjects<IProject,IResource,IFolder,IFile>() {
    shared actual CeylonProject<IProject,IResource,IFolder,IFile> newNativeProject(IProject nativeProject) =>
            EclipseCeylonProject(nativeProject);
}