import com.redhat.ceylon.ide.common.model {
    EditedSourceFile,
    ProjectSourceFile,
    CrossProjectSourceFile
}
import com.redhat.ceylon.ide.common.platform {
    ModelServices
}
import com.redhat.ceylon.ide.common.typechecker {
    EditedPhasedUnit,
    ProjectPhasedUnit,
    CrossProjectPhasedUnit
}

import org.eclipse.core.resources {
    IProject,
    IResource,
    IFolder,
    IFile
}

object eclipseModelServices 
        satisfies ModelServices<IProject, IResource, IFolder,IFile> {

    shared actual CrossProjectSourceFile<IProject,IResource,IFolder,IFile> newCrossProjectSourceFile(
        CrossProjectPhasedUnit<IProject,IResource,IFolder,IFile> phasedUnit) => 
            CrossProjectSourceFile<IProject, IResource, IFolder, IFile>(phasedUnit);
    
    shared actual EditedSourceFile<IProject,IResource,IFolder,IFile> newEditedSourceFile(
        EditedPhasedUnit<IProject,IResource,IFolder,IFile> phasedUnit) =>
            EditedSourceFile<IProject, IResource, IFolder, IFile>(phasedUnit);
    
    shared actual ProjectSourceFile<IProject,IResource,IFolder,IFile> newProjectSourceFile(
        ProjectPhasedUnit<IProject,IResource,IFolder,IFile> phasedUnit) => 
            ProjectSourceFile<IProject, IResource, IFolder, IFile>(phasedUnit);
}