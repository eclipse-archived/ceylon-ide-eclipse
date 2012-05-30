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
package com.redhat.ceylon.eclipse.core.cpcontainer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

import ceylon.language.descriptor.Module;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.eclipse.core.model.loader.model.JDTModuleManager;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.builder.CeylonNature;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

/**
 * Eclipse classpath container that will contain the ceylon resolved entries.
 */
public class CeylonResolveJob extends Job {

    private final CeylonClasspathContainer container;
    private IStatus status;
    
    private void setStatus(IStatus status) {
        this.status = status;
    }

    public CeylonResolveJob(CeylonClasspathContainer container) {
        super("Ceylon resolve job of project " + container.getJavaProject().getElementName());
        this.container = container;
    }

    protected IStatus run(IProgressMonitor monitor) {

        try {
            final IWorkspace workspace = ResourcesPlugin.getWorkspace();
            final Collection<IClasspathEntry> paths = new LinkedHashSet<IClasspathEntry>();

            final IWorkspaceRunnable buildJob = new IWorkspaceRunnable() {
                @Override
                public void run(IProgressMonitor monitor) {
                    try {
                        CeylonBuilder.parseCeylonModel(container.getJavaProject().getProject(), monitor);
                        IProject project = container.getJavaProject().getProject();
                        final TypeChecker typeChecker = CeylonBuilder.getProjectTypeChecker(project);
                        if (typeChecker != null) {
                            final PhasedUnits phasedUnits = typeChecker.getPhasedUnits();
                            final JDTModuleManager moduleManager = (JDTModuleManager) phasedUnits.getModuleManager();
                            for (File archive : moduleManager.getClasspath()) {
                                if (archive.exists()) {
                                    Path classpathArtifact = new Path(archive.getCanonicalPath());
                                    IPath srcArtifact = classpathArtifact.removeFileExtension().addFileExtension("src");
                                    paths.add(JavaCore.newLibraryEntry(classpathArtifact, srcArtifact, null));
                                }
                            }
                            IPath ceylonOutputDirectory = new Path(CeylonBuilder.getCeylonOutputDirectory(container.getJavaProject()).getCanonicalPath());
                            IPath ceylonSourceDirectory = project.getFolder("source").getFullPath();
                            paths.add(JavaCore.newLibraryEntry(ceylonOutputDirectory, ceylonSourceDirectory, null));
                        }
                        else {
                            setStatus(new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID, "Job '" + getName() + "' failed"));
                        }
                    } catch (CoreException e) {
                        setStatus(new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID, "Job '" + getName() + "' failed", e));
                    } catch (IOException e) {
                        setStatus(new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID, "Job '" + getName() + "' failed", e));
                    }
                    setStatus(Status.OK_STATUS);
                }
            };
            try {
                workspace.run(buildJob, monitor);
            } catch (CoreException e) {
                setStatus(new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID, "Job '" + getName() + "' failed", e));
            }
            
            IClasspathEntry[] entries = (IClasspathEntry[]) paths.toArray(new IClasspathEntry[paths.size()]);
            
            if (status == Status.OK_STATUS) {
                container.updateClasspathEntries(entries);
            }
            setResolveStatus(status);
            return status;
        } finally {
            container.resetJob();
            CeylonPlugin.log(IStatus.INFO, "resolved dependencies of project " + container.getJavaProject().getElementName(), null);
        }
    }

    private void setResolveStatus(IStatus status) {
        if (FakeProjectManager.isFake(container.getJavaProject())) {
            return;
        }
        try {
            if (status == Status.OK_STATUS) {
                return;
            }
        } catch (Exception e) {
            CeylonPlugin.log(e);
        }
    }

}
