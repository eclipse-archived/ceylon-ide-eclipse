package com.redhat.ceylon.eclipse.core.classpath;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.jdt.launching.VMStandin;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jdt.launching.environments.IExecutionEnvironmentsManager;

/**
 * Resolves a container for a Ceylon Runtime classpath container entry.
 */
public class CeylonRuntimeContainerInitializer extends ClasspathContainerInitializer {

    /**
     * @see ClasspathContainerInitializer#initialize(IPath, IJavaProject)
     */
    public void initialize(IPath containerPath, IJavaProject project) throws CoreException {
        int size = containerPath.segmentCount();
        if (size > 0) {
            if (containerPath.segment(0).equals(CeylonRuntimeContainer.CONTAINER_ID)) {
                CeylonRuntimeContainer container = new CeylonRuntimeContainer(project);
                JavaCore.setClasspathContainer(containerPath, new IJavaProject[] {project}, new IClasspathContainer[] {container}, null);
            }
        }
    }
    
    /**
     * Sets the specified class path container for all of the given projects.
     *  
     * @param containerPath JRE container path
     * @param projects projects set the container on
     * @throws CoreException on failure
     */
    public void initialize(IPath containerPath, IJavaProject[] projects) throws CoreException {
        int size = containerPath.segmentCount();
        if (size > 0) {
            if (containerPath.segment(0).equals(CeylonRuntimeContainer.CONTAINER_ID)) {
                int length = projects.length;
                IClasspathContainer[] containers = new CeylonRuntimeContainer[length];
                for (int i=0; i<length; i++) {
                    containers[i] = new CeylonRuntimeContainer(projects[i]);
                }
                JavaCore.setClasspathContainer(containerPath, projects, containers, null);
            }
        }
    }
    

    /**
     * The container can be updated if it refers to an existing VM.
     * 
     * @see org.eclipse.jdt.core.ClasspathContainerInitializer#canUpdateClasspathContainer(org.eclipse.core.runtime.IPath, org.eclipse.jdt.core.IJavaProject)
     */
    public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {
        return false;
    }
    
    /**
     * @see org.eclipse.jdt.core.ClasspathContainerInitializer#getDescription(org.eclipse.core.runtime.IPath, org.eclipse.jdt.core.IJavaProject)
     */
    public String getDescription(IPath containerPath, IJavaProject project) {
        return "Initializer for the Ceylon Runtime Classpath Container of project " + project.getElementName();
    }
    
    public Object getComparisonID(IPath containerPath, IJavaProject project) {
        return project.getProject().getName() + "/" + containerPath;
    }
}
