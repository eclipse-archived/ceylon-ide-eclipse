/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.classpath;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * Initializes a classpath container for the Ceylon language runtime archive.
 */
public class CeylonLanguageModuleInitializer extends ClasspathContainerInitializer {

    /**
     * @see ClasspathContainerInitializer#initialize(IPath, IJavaProject)
     */
    public void initialize(IPath containerPath, IJavaProject project) throws CoreException {
        int size = containerPath.segmentCount();
        if (size > 0) {
            if (containerPath.segment(0).equals(CeylonLanguageModuleContainer.CONTAINER_ID)) {
                CeylonLanguageModuleContainer container = new CeylonLanguageModuleContainer(project.getProject());
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
            if (containerPath.segment(0).equals(CeylonLanguageModuleContainer.CONTAINER_ID)) {
                int length = projects.length;
                IClasspathContainer[] containers = new CeylonLanguageModuleContainer[length];
                for (int i=0; i<length; i++) {
                    containers[i] = new CeylonLanguageModuleContainer(projects[i].getProject());
                }
                JavaCore.setClasspathContainer(containerPath, projects, containers, null);
            }
        }
    }
    

    /**
     * The container cannot be updated.
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
        return "Initializer for the Ceylon Language Module Classpath Container of project " + project.getElementName();
    }
    
    public Object getComparisonID(IPath containerPath, IJavaProject project) {
        return project.getProject().getName() + "/" + containerPath;
    }
}
