/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.redhat.ceylon.eclipse.core.classpath;

import static com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathUtil.getCeylonClasspathEntry;
import static com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathUtil.isCeylonClasspathContainer;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static org.eclipse.jdt.core.JavaCore.getClasspathContainer;
import static org.eclipse.jdt.core.JavaCore.setClasspathContainer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.redhat.ceylon.eclipse.core.classpath.InitDependenciesJob;

/**
 * Initializes the Ceylon class path container. It will create 
 * a container from the persisted class path entries (the 
 * .classpath file), and then schedule the refresh of the 
 * container.
 */
public class CeylonProjectModulesInitializer extends ClasspathContainerInitializer {

    /**
     * Initialize the container with the "persisted" classpath 
     * entries, and then schedule the refresh.
     */
    public void initialize(IPath containerPath, IJavaProject project) throws CoreException {
        int size = containerPath.segmentCount();
        if (size > 0) {
            if (containerPath.segment(0).equals(CeylonProjectModulesContainer.CONTAINER_ID)) {
                IClasspathContainer c = getClasspathContainer(containerPath, project);
                CeylonProjectModulesContainer container;
                if (c instanceof CeylonProjectModulesContainer) {
                    container = (CeylonProjectModulesContainer) c;
                } 
                else {
                    IClasspathEntry entry = getCeylonClasspathEntry(containerPath, project);
                    IClasspathAttribute[] attributes = entry == null ? 
                            new IClasspathAttribute[0] : entry.getExtraAttributes();
                    if (c == null) {
                        container = new CeylonProjectModulesContainer(project, containerPath,
                                new IClasspathEntry[0], attributes);
                    } 
                    else {
                        // this might be the persisted one: reuse the persisted entries
                        container = new CeylonProjectModulesContainer(project, containerPath, 
                                c.getClasspathEntries(), attributes);
                    }                    
                }
                
                setClasspathContainer(containerPath, new IJavaProject[] { project },
                        new IClasspathContainer[] {container}, null);
                
                Job job = new InitDependenciesJob("Initializing dependencies for project " + 
                        project.getElementName(), container);
                job.setUser(false);
                job.setPriority(Job.BUILD);
                job.setRule(getWorkspace().getRoot());
                job.schedule();
            }
        }
    }

    public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {
        return false;
    }

    public Object getComparisonID(IPath containerPath, IJavaProject project) {
        return project.getProject().getName() + "/" + containerPath;
    }
}
