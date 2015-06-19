import com.redhat.ceylon.ide.common.model {
    CeylonProject
}
import org.eclipse.core.resources {
    IProject, IResource,
    IFolder,
    IContainer
}
import java.io {
    File
}
import org.eclipse.core.runtime {
    NullProgressMonitor,
    CoreException,
    IProgressMonitor
}
import org.eclipse.jface.dialogs {
    MessageDialog
}
import org.eclipse.swt.widgets {
    Display
}
import com.redhat.ceylon.eclipse.ui {
    CeylonEncodingSynchronizer
}

shared class EclipseCeylonProject(ideArtifact) extends CeylonProject<IProject>() {
    shared actual IProject ideArtifact;

    shared actual File rootDirectory => ideArtifact.location.toFile();

    shared actual Boolean hasConfigFile
        => ideArtifact.findMember(ceylonConfigFileProjectRelativePath) exists;

    shared actual void refreshConfigFile() {
        try {
            IResource? config = ideArtifact.findMember(ceylonConfigFileProjectRelativePath);

            if (exists config) {
                config.refreshLocal(IResource.\iDEPTH_ZERO,
                    NullProgressMonitor());
            }
            else {
                ideArtifact.refreshLocal(IResource.\iDEPTH_INFINITE,
                    NullProgressMonitor());
            }
        }
        catch (CoreException e) {
            e.printStackTrace();
        }
    }

    shared actual void fixHiddenOutputFolder(String folderProjectRelativePath) {
        IFolder oldOutputRepoFolder = ideArtifact.getFolder(folderProjectRelativePath);
        if (oldOutputRepoFolder.\iexists() && oldOutputRepoFolder.hidden) {
            try {
                oldOutputRepoFolder.hidden = false;
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }

    void createDerivedFolder(IFolder folder, Boolean force, Boolean local, IProgressMonitor? monitor) {
        if (!folder.\iexists()) {
            IContainer parent= folder.parent;
            if (is IFolder parent) {
                createDerivedFolder(parent, force, local, null);
            }
            folder.create(force then (IResource.\iFORCE.or(IResource.\iDERIVED)) else IResource.\iDERIVED, local, monitor);
        }
    }


    shared actual void createNewOutputFolder(String folderProjectRelativePath) {
        IFolder newOutputRepoFolder =
                ideArtifact.getFolder(folderProjectRelativePath);
        try {
            newOutputRepoFolder.refreshLocal(IResource.\iDEPTH_ONE,
                NullProgressMonitor());
        }
        catch (CoreException ce) {
            ce.printStackTrace();
        }
        if (!newOutputRepoFolder.\iexists()) {
            try {
                createDerivedFolder(newOutputRepoFolder, true, true, null);
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
        CeylonEncodingSynchronizer.instance.refresh(ideArtifact, null);
    }

    shared actual void deleteOldOutputFolder(String folderProjectRelativePath) {
        IFolder oldOutputRepoFolder = ideArtifact.getFolder(folderProjectRelativePath);
        if( oldOutputRepoFolder.\iexists() ) {
            Boolean remove = MessageDialog.openQuestion(Display.default.activeShell,
                "Changing Ceylon output repository",
                "The Ceylon output repository has changed. Do you want to remove the old output repository folder '" +
                        oldOutputRepoFolder.fullPath.string + "' and all its contents?");
            if (remove) {
                try {
                    oldOutputRepoFolder.delete(true, null);
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        }
        if (oldOutputRepoFolder.\iexists() && oldOutputRepoFolder.derived) {
            try {
                oldOutputRepoFolder.setDerived(false, null);
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }
 }
