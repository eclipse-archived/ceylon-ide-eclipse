import ceylon.collection {
    HashSet
}
import ceylon.interop.java {
    CeylonIterable
}

import com.redhat.ceylon.common {
    Constants
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.eclipse.core.builder {
    CeylonBuilder
}
import com.redhat.ceylon.eclipse.ui {
    CeylonEncodingSynchronizer
}
import com.redhat.ceylon.ide.common.model {
    CeylonProject,
    ModuleDependencies,
    CeylonProjectConfig,
    ModelAliases,
    CeylonProjects
}

import java.io {
    File
}

import org.eclipse.core.resources {
    IProject,
    IResource,
    IFolder,
    IContainer,
    IFile
}
import org.eclipse.core.runtime {
    NullProgressMonitor,
    CoreException,
    IProgressMonitor,
    Path
}
import org.eclipse.jdt.core {
    JavaCore
}
import org.eclipse.jface.dialogs {
    MessageDialog
}
import org.eclipse.swt.widgets {
    Display
}

shared class EclipseCeylonProject(ideArtifact) 
        extends CeylonProject<IProject, IResource, IFolder, IFile>() {
    shared actual IProject ideArtifact;

    shared actual String name => ideArtifact.name;
    
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
        CeylonProjectConfig config = configuration;
        Boolean sameFolders({String*} configFolders, {IFolder*} eclipseFolders, String defaultEclipsePath)
            => HashSet<String> {
                * configFolders.map((p)
                    => Path.fromOSString(p).string)
                }
                ==
                HashSet<String> {
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
    
    shared actual CeylonProjects<IProject,IResource,IFolder,IFile> model => ceylonModel;
    
    shared actual Boolean nativeProjectIsAccessible => ideArtifact.accessible;

    shared actual {IProject*} referencedNativeProjects(IProject nativeProject) { 
        try {
            return nativeProject.referencedProjects.array.coalesced; 
        } catch(CoreException e) {
            e.printStackTrace();
            return [];
        }
    }

    shared actual Boolean isJavaLikeFileName(String fileName) =>
            JavaCore.isJavaLikeFileName(fileName);
    
    shared actual {IProject*} referencingNativeProjects(IProject nativeProject) { 
        try {
            return nativeProject.referencingProjects.array.coalesced; 
        } catch(CoreException e) {
            e.printStackTrace();
            return [];
        }
    }

    shared actual Boolean compileToJs => CeylonBuilder.compileToJs(ideArtifact);
    shared actual Boolean compileToJava => CeylonBuilder.compileToJava(ideArtifact);
    
    shared actual ModuleDependencies moduleDependencies => CeylonBuilder.getModuleDependenciesForProject(ideArtifact);
    
    shared actual TypeChecker? typechecker => CeylonBuilder.getProjectTypeChecker(ideArtifact);
 }
