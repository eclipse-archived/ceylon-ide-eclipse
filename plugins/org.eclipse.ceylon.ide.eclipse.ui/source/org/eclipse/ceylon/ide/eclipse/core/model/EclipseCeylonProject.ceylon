/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import ceylon.collection {
    HashSet
}

import org.eclipse.ceylon.common {
    Constants
}
import org.eclipse.ceylon.compiler.typechecker.context {
    Context
}
import org.eclipse.ceylon.compiler.typechecker.util {
    ModuleManagerFactory
}
import org.eclipse.ceylon.ide.eclipse.core.builder {
    CeylonBuilder
}
import org.eclipse.ceylon.ide.eclipse.core.external {
    ExternalSourceArchiveManager
}
import org.eclipse.ceylon.ide.eclipse.ui {
    CeylonEncodingSynchronizer,
    CeylonPlugin
}
import org.eclipse.ceylon.ide.common.model {
    CeylonProject,
    CeylonProjectConfig,
    BuildHook
}
import org.eclipse.ceylon.ide.common.platform {
    VfsServicesConsumer
}
import org.eclipse.ceylon.ide.common.util {
    unsafeCast,
    BaseProgressMonitorChild,
    ProgressMonitorChild
}
import org.eclipse.ceylon.model.typechecker.util {
    ModuleManager
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
    Path,
    QualifiedName
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

shared object nativeFolderProperties {
    shared QualifiedName packageModel = QualifiedName(CeylonPlugin.\iPLUGIN_ID, "nativeFolder_packageModel");
    shared QualifiedName root = QualifiedName(CeylonPlugin.\iPLUGIN_ID, "nativeFolder_root");
    shared QualifiedName rootIsSource = QualifiedName(CeylonPlugin.\iPLUGIN_ID, "nativeFolder_rootIsSource");
}

// TODO : add the EclipseBuildHook (that manages also the Android Stuff
object eclipseSpecificAnalysisBuildHook 
        satisfies BuildHook<IProject, IResource, IFolder, IFile>
        & VfsServicesConsumer<IProject, IResource, IFolder, IFile> {
    
     
    shared actual Boolean analyzingChanges(
        {ChangeToAnalyze*} changes,  
        CeylonProjectBuildAlias build, 
        CeylonProjectBuildAlias.State state) {
        // Add the preBuildChecks
        for (change in changes) {
            switch(change)
            case(is [NativeResourceChange, IProject]) {
                // Change outside project sources or resources
                let ([nonModelChange, changeProject] = change);
                switch(nonModelChange)
                case(is NativeFolderRemoval) {
                    if (exists fullPath = nonModelChange.resource.fullPath,
                        exists explodedDirPath 
                                = CeylonBuilder.getCeylonClassesOutputFolder(changeProject)?.fullPath, 
                        explodedDirPath.isPrefixOf(fullPath)) {
                        state.buildType.requireFullBuild();
                        state.buildType.requireClasspathResolution();
                    }
                }
                case(is NativeFileChange) {
                    if (vfsServices.getShortName(nonModelChange.resource) == ".classpath") {
                        state.buildType.requireFullBuild();
                        state.buildType.requireClasspathResolution();
                    }
                }
                else {}
                
            }
            else {}
        }
        return true;
    }
}


shared class EclipseCeylonProject(ideArtifact) 
        extends CeylonProject<IProject, IResource, IFolder, IFile>() {

    shared actual IProject ideArtifact;

    shared actual String name => ideArtifact.name;
    
    shared actual File rootDirectory => ideArtifact.location.toFile();

    shared actual Boolean hasConfigFile
        => ideArtifact.findMember(ceylonConfigFileProjectRelativePath) exists;

    shared actual void refreshConfigFile(String projectRelativePath) {
        try {
            IResource? config = ideArtifact.findMember(projectRelativePath);

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
    
    shared actual CeylonProjectsAlias model => ceylonModel;
    
    shared actual Boolean isJavaLikeFileName(String fileName) =>
            JavaCore.isJavaLikeFileName(fileName);
    
    shared actual Boolean compileToJs => CeylonBuilder.compileToJs(ideArtifact);
    shared actual Boolean compileToJava => CeylonBuilder.compileToJava(ideArtifact);
    
    shared actual void createOverridesProblemMarker(Exception theOverridesException, File absoluteFile, Integer overridesLine, Integer overridesColumn) =>
            CeylonBuilder.createOverridesProblemMarker(ideArtifact, theOverridesException, absoluteFile, overridesLine, overridesColumn);
    
    shared actual void removeOverridesProblemMarker() =>
            CeylonBuilder.removeOverridesProblemMarker(ideArtifact);
    
    shared actual String systemRepository => 
            CeylonBuilder.getInterpolatedCeylonSystemRepo(ideArtifact);
    
    shared actual ModuleManagerFactory moduleManagerFactory =>
            object satisfies ModuleManagerFactory {
                createModuleManager(Context c) => 
                        JDTModuleManager(c, outer);
                
                createModuleManagerUtil(Context c, ModuleManager mm) => 
                        JDTModuleSourceMapper(c, 
                            unsafeCast<IdeModuleManagerAlias>(mm));
            };
            
    shared actual void completeCeylonModelParsing(BaseProgressMonitorChild monitor) {
        ExternalSourceArchiveManager externalArchiveManager = ExternalSourceArchiveManager.externalSourceArchiveManager;
        externalArchiveManager.updateProjectSourceArchives(ideArtifact, unsafeCast<ProgressMonitorChild<IProgressMonitor>>(monitor).wrapped);
    }

    shared actual {BuildHook<IProject, IResource, IFolder, IFile>*} buildHooks => {
        eclipseSpecificAnalysisBuildHook
    };
 }
