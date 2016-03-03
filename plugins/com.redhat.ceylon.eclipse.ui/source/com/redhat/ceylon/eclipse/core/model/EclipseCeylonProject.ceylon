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
    CeylonEncodingSynchronizer,
    CeylonPlugin
}
import com.redhat.ceylon.eclipse.util {
    withJavaModel,
    eclipsePlatformUtils,
    EclipseProgressMonitor
}
import com.redhat.ceylon.ide.common.model {
    CeylonProject,
    ModuleDependencies,
    CeylonProjectConfig,
    CeylonProjects,
    IdeModuleManager
}
import com.redhat.ceylon.ide.common.vfs {
    FolderVirtualFile
}
import com.redhat.ceylon.model.typechecker.model {
    Package
}

import java.io {
    File
}
import java.lang.ref {
    WeakReference
}

import org.eclipse.core.resources {
    IProject,
    IResource,
    IFolder,
    IContainer,
    IFile,
    IResourceVisitor
}
import org.eclipse.core.runtime {
    NullProgressMonitor,
    CoreException,
    IProgressMonitor,
    Path,
    QualifiedName,
    IPath,
    SubMonitor
}
import org.eclipse.jdt.core {
    JavaCore,
    IClasspathEntry
}
import org.eclipse.jface.dialogs {
    MessageDialog
}
import org.eclipse.swt.widgets {
    Display
}
import com.redhat.ceylon.ide.common.util {
    ProgressMonitor,
    platformUtils,
    BaseProgressMonitor,
    unsafeCast
}
import com.redhat.ceylon.ide.common.model.parsing {
    ModulesScanner,
    ProjectFilesScanner,
    RootFolderScanner
}
import com.redhat.ceylon.model.typechecker.util {
    ModuleManager
}
import com.redhat.ceylon.compiler.typechecker.context {
    Context
}
import com.redhat.ceylon.compiler.typechecker.util {
    ModuleManagerFactory
}
import com.redhat.ceylon.eclipse.core.external {
    ExternalSourceArchiveManager
}

Boolean isCeylonSourceEntry(IClasspathEntry entry) => 
        every {
    entry.entryKind == IClasspathEntry.\iCPE_SOURCE,
    entry.exclusionPatterns.iterable.coalesced.filter((path) => path.string.endsWith(".ceylon")).empty
};

shared object nativeFolderProperties {
    shared QualifiedName packageModel = QualifiedName(CeylonPlugin.\iPLUGIN_ID, "nativeFolder_packageModel");
    shared QualifiedName root = QualifiedName(CeylonPlugin.\iPLUGIN_ID, "nativeFolder_root");
    shared QualifiedName rootIsSource = QualifiedName(CeylonPlugin.\iPLUGIN_ID, "nativeFolder_rootIsSource");
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
                eclipseFolders = sourceNativeFolders;
                defaultEclipsePath = Constants.\iDEFAULT_SOURCE_DIR;
            },
            sameFolders {
                configFolders = config.projectResourceDirectories;
                eclipseFolders = resourceNativeFolders;
                defaultEclipsePath = Constants.\iDEFAULT_RESOURCE_DIR;
            }
        };
    }
    
    shared actual CeylonProjects<IProject,IResource,IFolder,IFile> model => ceylonModel;
    
    shared actual {IFolder*} sourceNativeFolders =>
            let(javaProject = JavaCore.create(ideArtifact))
            if (! javaProject.\iexists())
            then {}
            else (withJavaModel {
                do() =>
                    javaProject.rawClasspath.iterable.coalesced
                        .filter((entry) => isCeylonSourceEntry(entry))
                        .map { 
                            IResource? collecting(IClasspathEntry entry) => 
                                    ideArtifact.findMember(entry.path.makeRelativeTo(ideArtifact.fullPath));
                        }.narrow<IFolder>()
                        .filter((resource) => resource.\iexists());
            } else {});

    shared actual {IFolder*} resourceNativeFolders =>
        if (! ideArtifact.\iexists())
        then {}
        else configuration.resourceDirectories
                .map((resourceInConfig) {
                        value path = Path.fromOSString(resourceInConfig);
                        if (! path.absolute) {
                            return ideArtifact.getFolder(path);
                        } else {
                            object result {
                                shared variable IFolder? resourceFolder = null;
                            }
                            try {
                                ideArtifact.accept(object satisfies IResourceVisitor {
                                    shared actual Boolean visit(IResource resource) {
                                        if (is IProject resource) {
                                            return true;
                                        }
                                        if (is IFolder resource) {
                                            if (! resource.linked) {
                                                return false;
                                            }
                                            IPath? resourceLocation=resource.location;
                                            if (! exists resourceLocation) {
                                                return false;
                                            }
                                            if (! resourceLocation.isPrefixOf(path)) {
                                                return false;
                                            }
                                            if (resourceLocation == path) {
                                                result.resourceFolder = resource;
                                                return false;
                                            }
                                            return true;
                                        }
                                        return false;
                                    }
                                });
                            }
                            catch (CoreException e) {
                                e.printStackTrace();
                            }
                            return result.resourceFolder;
                        }
                }).coalesced.filter((resourceFolder) => resourceFolder.\iexists());

    
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
    
    shared actual void setPackageForNativeFolder(IFolder folder, WeakReference<Package> p) {
        folder.setSessionProperty(nativeFolderProperties.packageModel, p);
    }
    
    shared actual void setRootForNativeFolder(IFolder folder, WeakReference<FolderVirtualFile<IProject,IResource,IFolder,IFile>> root) {
        folder.setSessionProperty(nativeFolderProperties.root, root);
    }
    
    shared actual void setRootIsForSource(IFolder rootFolder, Boolean isSource) { 
        rootFolder.setSessionProperty(nativeFolderProperties.rootIsSource, isSource);
    }

    shared actual void createOverridesProblemMarker(Exception theOverridesException, File absoluteFile, Integer overridesLine, Integer overridesColumn) =>
            CeylonBuilder.createOverridesProblemMarker(ideArtifact, theOverridesException, absoluteFile, overridesLine, overridesColumn);
    
    shared actual void removeOverridesProblemMarker() =>
            CeylonBuilder.removeOverridesProblemMarker(ideArtifact);
    
    shared actual String systemRepository => 
            CeylonBuilder.getInterpolatedCeylonSystemRepo(ideArtifact);
    
    shared actual void scanRootFolder(RootFolderScanner<IProject, IResource, IFolder, IFile> scanner) {
        scanner.nativeRootDir.accept(object satisfies IResourceVisitor {
            shared actual Boolean visit(IResource resource) {
                return scanner.visitNativeResource(resource);
            }
        });
    }
    
    shared actual ModuleManagerFactory moduleManagerFactory =>
            object satisfies ModuleManagerFactory {
                createModuleManager(Context c) => 
                        JDTModuleManager(c, outer);
                
                createModuleManagerUtil(Context c, ModuleManager mm) => 
                        JDTModuleSourceMapper(c, 
                            unsafeCast<IdeModuleManager<IProject,IResource,IFolder,IFile>>(mm));
            };
            
    shared actual void completeCeylonModelParsing(BaseProgressMonitor monitor) {
        ExternalSourceArchiveManager externalArchiveManager = ExternalSourceArchiveManager.externalSourceArchiveManager;
        externalArchiveManager.updateProjectSourceArchives(ideArtifact, unsafeCast<ProgressMonitor<IProgressMonitor>>(monitor).wrapped);
    }
    
 }
