/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.builder;

import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getPackage;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getRootFolder;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.isInSourceFolder;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.isResourceFile;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.isSourceFile;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.vfsJ2C;
import static org.eclipse.ceylon.ide.eclipse.util.InteropUtils.toJavaString;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import org.eclipse.ceylon.common.FileUtil;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.BooleanHolder;
import org.eclipse.ceylon.ide.common.model.CeylonProject;
import org.eclipse.ceylon.ide.common.model.CeylonProjectConfig;
import org.eclipse.ceylon.ide.common.model.IResourceAware;
import org.eclipse.ceylon.ide.common.model.ProjectSourceFile;
import org.eclipse.ceylon.ide.common.model.delta.CompilationUnitDelta;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.model.typechecker.util.ModuleManager;

final class DeltaScanner implements IResourceDeltaVisitor {
    private final BooleanHolder mustDoFullBuild;
    CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject;
    private final IProject project;
    private final BooleanHolder somethingToBuild;
    private final BooleanHolder mustResolveClasspathContainer;
    private IPath explodedDirPath;
    private Map<IProject, IPath> modulesDirPathByProject = new HashMap<>();
    private boolean astAwareIncrementalBuild = true;
    private IFile overridesResource = null;
    
    DeltaScanner(BooleanHolder mustDoFullBuild, CeylonProject<IProject, IResource, IFolder, IFile> ceylonProject,
            BooleanHolder somethingToBuild,
            BooleanHolder mustResolveClasspathContainer) {
        this.mustDoFullBuild = mustDoFullBuild;
        this.ceylonProject = ceylonProject;
        this.project = ceylonProject.getIdeArtifact();
        this.somethingToBuild = somethingToBuild;
        this.mustResolveClasspathContainer = mustResolveClasspathContainer;
        IFolder explodedFolder = CeylonBuilder.getCeylonClassesOutputFolder(project);
        explodedDirPath = explodedFolder != null ? explodedFolder.getFullPath() : null;
        IFolder modulesFolder = CeylonBuilder.getCeylonModulesOutputFolder(project);
        IPath modulesDirPath = modulesFolder != null ? modulesFolder.getFullPath() : null;
        modulesDirPathByProject.put(project, modulesDirPath);
        try {
            for (IProject referencedProject : project.getReferencedProjects()) {
                if (modelJ2C().ceylonModel().getProject(referencedProject) != null) {
                    modulesFolder = CeylonBuilder.getCeylonModulesOutputFolder(referencedProject);
                }
                modulesDirPath = modulesFolder != null ? modulesFolder.getFullPath() : null;
                modulesDirPathByProject.put(referencedProject, modulesDirPath);
            }
        } catch (CoreException e) {
        }
        astAwareIncrementalBuild = CeylonBuilder.areAstAwareIncrementalBuildsEnabled(project);
        CeylonProjectConfig projectConfig = modelJ2C().ceylonModel().getProject(project).getConfiguration();
        if (projectConfig != null) {
            String overridesFilePath = toJavaString(projectConfig.getOverrides());
            if (overridesFilePath != null) {
                File overridesFile = FileUtil.absoluteFile(FileUtil.applyCwd(project.getLocation().toFile(), new File(overridesFilePath)));
                overridesResource = CeylonBuilder.fileToIFile(overridesFile, project);
            }
        }
        
    }

    @Override
    public boolean visit(IResourceDelta resourceDelta) 
            throws CoreException {
        IResource resource = resourceDelta.getResource();
        
        if (resource instanceof IProject) { 
            if ((resourceDelta.getFlags() & IResourceDelta.DESCRIPTION)!=0) {
                //some project setting changed : don't do anything, 
                // since the possibly impacting changes have already been
                // checked by JavaProjectStateManager.hasClasspathChanges()
            }
            else if (!resource.equals(project)) {
                //this is some kind of multi-project build,
                //indicating a change in a project we
                //depend upon
                /*mustDoFullBuild.value = true;
                mustResolveClasspathContainer.value = true;*/
            }
        }
        
        if (resource instanceof IFolder) {
            IFolder folder = (IFolder) resource; 
            if (resourceDelta.getKind()==IResourceDelta.REMOVED) {
                Package pkg = getPackage(folder);
                if (pkg!=null) {
                    //a package has been removed
                    mustDoFullBuild.value = true;
                } 
                
                IPath fullPath = resource.getFullPath();
                if (fullPath != null) {
                    if (explodedDirPath != null && explodedDirPath.isPrefixOf(fullPath)) {
                        mustDoFullBuild.value = true;
                        mustResolveClasspathContainer.value = true;
                    }

                    for (Map.Entry<IProject, IPath> entry : modulesDirPathByProject.entrySet()) {
                        IPath modulesDirPath = entry.getValue();
                        if (modulesDirPath != null && modulesDirPath.isPrefixOf(fullPath)) {
                            mustDoFullBuild.value = true;
                            mustResolveClasspathContainer.value = true;
                        }
                    }
                }
            } else {
                if (folder.exists() 
                        && folder.getProject().equals(project)
                        && vfsJ2C().createVirtualFolder(folder, project).isDescendantOfAny(ceylonProject.getRootFolders())) {
                    if (getPackage(folder) == null || getRootFolder(folder) == null) {
                        ceylonProject.addFolderToModel(folder);
                    }
                }
            }
            
        }
        
        if (resource instanceof IFile) {
            IFile file = (IFile) resource;
            String fileName = file.getName();
            if (isInSourceFolder(file)) {
                if (fileName.equals(ModuleManager.PACKAGE_FILE) || fileName.equals(ModuleManager.MODULE_FILE)) {
                    //a package or module descriptor has been added, removed, or changed
                    boolean descriptorContentChanged = true;
                    if (astAwareIncrementalBuild && file.isAccessible() &&  file.findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_ZERO) <= IMarker.SEVERITY_WARNING) {
                        IResourceAware unit = CeylonBuilder.getUnit(file);
                        if (unit instanceof ProjectSourceFile) {
                            ProjectSourceFile projectSourceFile = (ProjectSourceFile) unit;
                            CompilationUnitDelta delta = projectSourceFile.buildDeltaAgainstModel();
                            if (delta != null
                                    && delta.getChanges().getSize() == 0
                                    && delta.getChildrenDeltas().getSize() == 0) {
                                descriptorContentChanged = false;
                            }
                        }
                    }
                    if (descriptorContentChanged) {
                        mustDoFullBuild.value = true;
                        if (fileName.equals(ModuleManager.MODULE_FILE)) {
                            mustResolveClasspathContainer.value = true;
                        }
                    }
                }
            }
            if (fileName.equals(".classpath") ||
                    fileName.equals("config") ||
                    fileName.equals("ide-config") ||
                    file.equals(overridesResource)) {
                //the classpath changed
                mustDoFullBuild.value = true;
                mustResolveClasspathContainer.value = true;
                if (fileName.equals("ide-config")) {
                    ceylonProject.getIdeConfiguration().refresh();
                }
            }
            if (isSourceFile(file) || 
                    isResourceFile(file)) {
                // a source file or a resource was modified, 
                // we should at least compile incrementally
                somethingToBuild.value = true;
            }
        }
        
        return true;
    }
}
