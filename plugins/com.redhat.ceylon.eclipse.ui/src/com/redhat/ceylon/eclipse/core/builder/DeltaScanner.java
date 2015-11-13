package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getPackage;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectModelLoader;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getRootFolder;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isInSourceFolder;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isResourceFile;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isSourceFile;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;
import static com.redhat.ceylon.ide.common.util.toJavaString_.toJavaString;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.redhat.ceylon.common.FileUtil;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.BooleanHolder;
import com.redhat.ceylon.ide.common.model.CeylonProjectConfig;
import com.redhat.ceylon.ide.common.model.IResourceAware;
import com.redhat.ceylon.ide.common.model.ProjectSourceFile;
import com.redhat.ceylon.ide.common.model.delta.CompilationUnitDelta;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.util.ModuleManager;

final class DeltaScanner implements IResourceDeltaVisitor {
    private final BooleanHolder mustDoFullBuild;
    private final IProject project;
    private final BooleanHolder somethingToBuild;
    private final BooleanHolder mustResolveClasspathContainer;
    private IPath explodedDirPath;
    private Map<IProject, IPath> modulesDirPathByProject = new HashMap<>();
    private boolean astAwareIncrementalBuild = true;
    private IFile overridesResource = null;
    
    DeltaScanner(BooleanHolder mustDoFullBuild, IProject project,
            BooleanHolder somethingToBuild,
            BooleanHolder mustResolveClasspathContainer) {
        this.mustDoFullBuild = mustDoFullBuild;
        this.project = project;
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
                if (folder.exists() && folder.getProject().equals(project)) {
                    if (getPackage(folder) == null || getRootFolder(folder) == null) {
                        IContainer parent = folder.getParent();
                        if (parent instanceof IFolder) {
                            Package parentPkg = getPackage((IFolder)parent);
                            IFolder rootFolder = getRootFolder((IFolder)parent);
                            if (parentPkg != null && rootFolder != null) {
                                Package pkg = getProjectModelLoader(project).findOrCreatePackage(parentPkg.getModule(), parentPkg.getNameAsString() + "." + folder.getName());
                                resource.setSessionProperty(CeylonBuilder.RESOURCE_PROPERTY_PACKAGE_MODEL, new WeakReference<Package>(pkg));
                                resource.setSessionProperty(CeylonBuilder.RESOURCE_PROPERTY_ROOT_FOLDER, rootFolder);
                            }
                        }
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
                    file.equals(overridesResource)) {
                //the classpath changed
                mustDoFullBuild.value = true;
                mustResolveClasspathContainer.value = true;
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
