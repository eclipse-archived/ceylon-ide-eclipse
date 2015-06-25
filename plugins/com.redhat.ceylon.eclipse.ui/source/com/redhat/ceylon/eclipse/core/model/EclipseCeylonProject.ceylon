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
    IProgressMonitor,
    Path
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
import ceylon.collection {
    HashSet
}
import com.redhat.ceylon.eclipse.core.builder {
    CeylonBuilder
}
import ceylon.interop.java {
    CeylonIterable
}
import com.redhat.ceylon.common {
    Constants
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

    shared actual Boolean synchronizedWithConfiguration {
        value config = configuration;
        function sameFolders({String*} configFolders, {IFolder*} eclipseFolders, String defaultEclipsePath)
            => HashSet {
                configFolders.map((p)
                    => Path.fromOSString(p).string)
                }
                ==
                HashSet {
                    * (
                        if (nonempty eclipsePaths =
                            eclipseFolders.map {
                                collecting(IFolder f)
                                        => (if (f.linked) then f.location else f.projectRelativePath).string;
                            }.sequence())
                        then eclipsePaths
                        else { defaultEclipsePath }
                       )
                };

        return every {
            sameFolders {
                configFolders = config.projectSourceDirectories;
                eclipseFolders = CeylonIterable(CeylonBuilder.getSourceFolders(ideArtifact));
                defaultEclipsePath = Constants.\iDEFAULT_SOURCE_DIR;
            },
            sameFolders {
                configFolders = config.projectResourceDirectories;
                eclipseFolders = CeylonIterable(CeylonBuilder.getResourceFolders(ideArtifact));
                defaultEclipsePath = Constants.\iDEFAULT_RESOURCE_DIR;
            }
        };
    }
 }
