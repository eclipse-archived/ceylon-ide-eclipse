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

import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import org.eclipse.ceylon.ide.common.model.BaseCeylonProject;
import org.eclipse.ceylon.ide.common.model.CeylonProjectConfig;

import ceylon.interop.java.CeylonStringIterable;


public class EnableCeylonNature implements IWorkbenchWindowActionDelegate {
    
    private IProject fProject;
    
    public void dispose() {}
    
    public void init(IWorkbenchWindow window) {}
    
    public void run(IAction action) {
        modelJ2C().ceylonModel().addProject(fProject);
        BaseCeylonProject ceylonProject = modelJ2C().ceylonModel().getProject(fProject);
        CeylonProjectConfig config = ceylonProject.getConfiguration();
        List<String> sourceFolders = new ArrayList<>();
        for (IFolder sourceFolder : CeylonBuilder.getSourceFolders(fProject)) {
            if (sourceFolder.isLinked()) {
                sourceFolders.add(sourceFolder.getLocation().toOSString());
            } else {
                sourceFolders.add(sourceFolder.getProjectRelativePath().toString());
            }
        }
        config.setProjectSourceDirectories(new CeylonStringIterable(sourceFolders));
        config.save();
        new CeylonNature().addToProject(fProject);
    }
    
    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection) selection;
            Object first = ss.getFirstElement();
            
            if (first instanceof IProject) {
                fProject = (IProject) first;
            } else if (first instanceof IJavaProject) {
                fProject = ((IJavaProject) first).getProject();
            }
        }
    }
}
