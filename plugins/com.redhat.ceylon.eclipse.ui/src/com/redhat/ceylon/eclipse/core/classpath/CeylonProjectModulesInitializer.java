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
import static com.redhat.ceylon.eclipse.core.model.modelJ2C.ceylonModel;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static org.eclipse.jdt.core.JavaCore.getClasspathContainer;
import static org.eclipse.jdt.core.JavaCore.setClasspathContainer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.InitializeAfterLoadJob;
import org.eclipse.jdt.ui.JavaUI;

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
    public void initialize(IPath containerPath, final IJavaProject project) throws CoreException {
        int size = containerPath.segmentCount();
        if (size > 0) {
            if (containerPath.segment(0).equals(CeylonProjectModulesContainer.CONTAINER_ID)) {
                ceylonModel().addProject(project.getProject());
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
                
                final Job initDependenciesJob = new InitDependenciesJob("Initializing dependencies for project " + 
                        project.getElementName(), container);
                initDependenciesJob.setUser(false);
                initDependenciesJob.setPriority(Job.BUILD);
                initDependenciesJob.setRule(getWorkspace().getRoot());

                // Before scheduling the InitDependenciesJob, we will wait for the end of the Java Tooling initialization Job 
                final long waitUntil = System.currentTimeMillis() + 120000;

                Job initDependenciesWhenJavaToolingIsInitialized = new Job("Waiting for the end of the Java Tooling Initialization before initializing Ceylon dependencies for project " + project.getElementName() + " ...") {
                    private Job initJavaToolingJob = null;
                    
                    @Override
                    protected IStatus run(IProgressMonitor monitor) {
                        if (initJavaToolingJob == null) {
                            Job[] jobs = getJobManager().find(JavaUI.ID_PLUGIN);
                            for (Job job : jobs) {
                                if (job.getClass().getEnclosingClass().equals(InitializeAfterLoadJob.class)) {
                                    initJavaToolingJob = job;
                                }
                            }
                        }
                        
                        if (initJavaToolingJob != null) {
                            if (initJavaToolingJob.getState() == Job.WAITING ||
                                initJavaToolingJob.getState() == Job.RUNNING) {
                                if (System.currentTimeMillis() < waitUntil) {
//                                    System.out.println("Waiting 1 seconde more for the end of the Java Tooling Initialization before initializing Ceylon dependencies for project " + project.getElementName() + " ...");
                                    schedule(1000);
                                    return Status.OK_STATUS;
                                }
                                else {
//                                    System.out.println("The Java Tooling is not initialized after 2 minutes, so start initializing Ceylon dependencies for project " + project.getElementName() + " anyway !");
                                }
                            }
                        }
                        
                        boolean shouldSchedule = true;
                        for (Job job : getJobManager().find(initDependenciesJob)) {
                            if (job.getState() == Job.WAITING) {
//                                System.out.println("An InitDependenciesJob for project " + project.getElementName() + " is already scheduled. Finally don't schedule a new one after the Java Tooling has initialized");
                                shouldSchedule = false;
                                break;
                            }
                        }
                
                        if (shouldSchedule) {
//                            System.out.println("Scheduling the initialization of the Ceylon dependencies for project " + project.getElementName() + " after the Java Tooling has been initialized");
                            initDependenciesJob.schedule();
                        }   
                        return Status.OK_STATUS;
                    }
                };
                
                initDependenciesWhenJavaToolingIsInitialized.setPriority(Job.BUILD);
                initDependenciesWhenJavaToolingIsInitialized.setSystem(true);
                initDependenciesWhenJavaToolingIsInitialized.schedule();
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
