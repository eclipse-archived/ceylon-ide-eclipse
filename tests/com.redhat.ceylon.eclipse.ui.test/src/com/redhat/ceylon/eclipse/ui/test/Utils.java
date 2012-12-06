package com.redhat.ceylon.eclipse.ui.test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

public class Utils {

    public static IProject importProject(final IWorkspace workspace,
            IPath projectDescriptionPath) throws CoreException,
            InvocationTargetException, InterruptedException {
        IProjectDescription description =
    	        workspace.loadProjectDescription(projectDescriptionPath);
    	
    	String projectName = description.getName();
        final IProject project = workspace.getRoot().getProject(projectName);
    
        // import from file system
        File importSource = null;
    
        // import project from location copying files - use default project
        // location for this workspace
        URI locationURI = description.getLocationURI();
        // if location is null, project already exists in this location or
        // some error condition occured.
        if (locationURI != null) {
            importSource = new File(locationURI);
            IProjectDescription desc = workspace
                    .newProjectDescription(projectName);
            desc.setBuildSpec(description.getBuildSpec());
            desc.setComment(description.getComment());
            desc.setDynamicReferences(description
                    .getDynamicReferences());
            desc.setNatureIds(description.getNatureIds());
            desc.setReferencedProjects(description
                    .getReferencedProjects());
            desc.setLocation(workspace.getRoot().getLocation().append("model-loader-tests/" + projectName + "/"));
            description = desc;
        }
    
        project.create(description, null);
        project.open(IResource.BACKGROUND_REFRESH, null);
    
        // import operation to import project files if copy checkbox is selected
        if (importSource != null) {
            List filesToImport = FileSystemStructureProvider.INSTANCE
                    .getChildren(importSource);
            ImportOperation operation = new ImportOperation(project
                    .getFullPath(), importSource,
                    FileSystemStructureProvider.INSTANCE, new IOverwriteQuery() {
                        @Override
                        public String queryOverwrite(String pathString) {
                            return IOverwriteQuery.ALL;
                        }
            }, filesToImport);
            operation.setContext(PlatformUI.getWorkbench().
                    getActiveWorkbenchWindow().getShell());
            operation.setOverwriteResources(true); // need to overwrite
            // .project, .classpath
            // files
            operation.setCreateContainerStructure(false);
            operation.run(null);
        }
        return project;
    }

}
