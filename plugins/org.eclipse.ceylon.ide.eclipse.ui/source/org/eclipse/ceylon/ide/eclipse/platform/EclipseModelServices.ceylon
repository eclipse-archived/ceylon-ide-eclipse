/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.ide.eclipse.util {
    withJavaModel
}
import org.eclipse.ceylon.ide.common.model {
    EditedSourceFile,
    ProjectSourceFile,
    CrossProjectSourceFile,
    CeylonProject
}
import org.eclipse.ceylon.ide.common.model.parsing {
    RootFolderScanner
}
import org.eclipse.ceylon.ide.common.platform {
    ModelServices
}
import org.eclipse.ceylon.ide.common.typechecker {
    EditedPhasedUnit,
    ProjectPhasedUnit,
    CrossProjectPhasedUnit
}

import org.eclipse.core.resources {
    IProject,
    IResource,
    IFolder,
    IFile,
    IResourceVisitor
}
import org.eclipse.core.runtime {
    Path,
    IPath,
    CoreException
}
import org.eclipse.jdt.core {
    IClasspathEntry,
    JavaCore
}
import org.eclipse.ceylon.ide.eclipse.core.model {
    isCeylonSourceEntry
}

object eclipseModelServices 
        satisfies ModelServices<IProject, IResource, IFolder,IFile> {

    shared actual Boolean nativeProjectIsAccessible(IProject nativeProject) => nativeProject.accessible;

    shared actual {IFolder*} sourceNativeFolders(CeylonProject<IProject, IResource, IFolder,IFile> ceylonProject) =>
            let(javaProject = JavaCore.create(ceylonProject.ideArtifact))
    if (! javaProject.\iexists())
    then {}
    else (withJavaModel {
        do() =>
                javaProject.rawClasspath.iterable.coalesced
                .filter((entry) => isCeylonSourceEntry(entry))
                .map { 
            IResource? collecting(IClasspathEntry entry) => 
                    ceylonProject.ideArtifact.findMember(entry.path.makeRelativeTo(ceylonProject.ideArtifact.fullPath));
        }.narrow<IFolder>()
                .filter((resource) => resource.\iexists());
    } else {});
    
    shared actual {IFolder*} resourceNativeFolders(CeylonProject<IProject, IResource, IFolder,IFile> ceylonProject) =>
            if (! ceylonProject.ideArtifact.\iexists())
            then {}
            else ceylonProject.configuration.resourceDirectories
                    .map((resourceInConfig) {
                        value path = Path.fromOSString(resourceInConfig);
                        if (! path.absolute) {
                            return ceylonProject.ideArtifact.getFolder(path);
                        } else {
                            object result {
                                shared variable IFolder? resourceFolder = null;
                            }
                            try {
                                ceylonProject.ideArtifact.accept(object satisfies IResourceVisitor {
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
    
    shared actual {IProject*} referencedNativeProjects(IProject nativeProject) { 
        try {
            return nativeProject.referencedProjects.array.coalesced; 
        } catch(CoreException e) {
            e.printStackTrace();
            return [];
        }
    }
    
    shared actual {IProject*} referencingNativeProjects(IProject nativeProject) { 
        try {
            return nativeProject.referencingProjects.array.coalesced; 
        } catch(CoreException e) {
            e.printStackTrace();
            return [];
        }
    }

    shared actual void scanRootFolder(RootFolderScanner<IProject, IResource, IFolder, IFile> scanner) {
        scanner.nativeRootDir.accept(object satisfies IResourceVisitor {
            shared actual Boolean visit(IResource resource) {
                return scanner.visitNativeResource(resource);
            }
        });
    }
    
    shared actual CrossProjectSourceFile<IProject,IResource,IFolder,IFile> newCrossProjectSourceFile(
        CrossProjectPhasedUnit<IProject,IResource,IFolder,IFile> phasedUnit) => 
            CrossProjectSourceFile<IProject, IResource, IFolder, IFile>(phasedUnit);
    
    shared actual EditedSourceFile<IProject,IResource,IFolder,IFile> newEditedSourceFile(
        EditedPhasedUnit<IProject,IResource,IFolder,IFile> phasedUnit) =>
            EditedSourceFile<IProject, IResource, IFolder, IFile>(phasedUnit);
    
    shared actual ProjectSourceFile<IProject,IResource,IFolder,IFile> newProjectSourceFile(
        ProjectPhasedUnit<IProject,IResource,IFolder,IFile> phasedUnit) => 
            ProjectSourceFile<IProject, IResource, IFolder, IFile>(phasedUnit);
    
    shared actual Boolean isResourceContainedInProject(IResource resource, CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject) =>
            resource.project == ceylonProject.ideArtifact;
}