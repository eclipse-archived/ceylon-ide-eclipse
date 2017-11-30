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

import static org.eclipse.ceylon.ide.eclipse.core.classpath.CeylonClasspathUtil.getCeylonClasspathContainers;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.*;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class ProjectChangeListener implements IResourceChangeListener {
    
    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        try {
            event.getDelta().accept(new IResourceDeltaVisitor() {                    
                @Override
                public boolean visit(IResourceDelta delta) throws CoreException {
                    final IWorkspaceRoot workspaceRoot = getWorkspace().getRoot();
                    IResource resource = delta.getResource();
                    if (resource.equals(workspaceRoot)) {
                        return true;
                    }
                    if (resource instanceof IProject && delta.getKind()==IResourceDelta.REMOVED) {
                        CeylonBuilder.removeProject((IProject) resource);
                        modelJ2C().ceylonModel().removeProject((IProject) resource); 
                    }
                    else if (resource instanceof IProject && (delta.getFlags() & IResourceDelta.OPEN) != 0) {
                        final IProject project = (IProject) resource;
                        if (!project.isOpen()) {
                            CeylonBuilder.removeProject(project);
                            modelJ2C().ceylonModel().removeProject((IProject) resource); 
                        }
                        else if (CeylonNature.isEnabled(project)) {
                            IJavaProject javaProject = JavaCore.create(project);
                            modelJ2C().ceylonModel().addProject((IProject) resource); 
                            if (javaProject != null) {
                                getCeylonClasspathContainers(javaProject);
                            }
                        }
                    }
                    return false;
                }
            });
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
    
}