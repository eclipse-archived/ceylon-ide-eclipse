/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.select;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.PROJECT;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonNature;

public class ProjectSelectionDialog extends ElementListSelectionDialog {

    private ProjectSelectionDialog(Shell parent) {
        super(parent, new ILabelProvider() {
            @Override
            public void removeListener(ILabelProviderListener listener) {}
            @Override
            public boolean isLabelProperty(Object element, String property) {
                return false;
            }
            @Override
            public void dispose() {}
            @Override
            public void addListener(ILabelProviderListener listener) {}
            @Override
            public String getText(Object element) {
                return ((IProject) element).getName();
            }
            @Override
            public Image getImage(Object element) {
                return PROJECT;
            }
        });
    }
    
    @Override
    public int open() {
        List<IProject> elements = new ArrayList<IProject>();
        for (IProject project: getWorkspace().getRoot().getProjects()) {
            if (project.isOpen() &&
                    CeylonNature.isEnabled(project)) {
                elements.add(project);
            }
        }
        /*Collections.sort(elements, new Comparator<IPackageFragment>() {
            @Override
            public int compare(IPackageFragment pf1, IPackageFragment pf2) {
                return pf1.getElementName().compareTo(pf2.getElementName());
            }
        });*/
        setElements(elements.toArray());
        return super.open();
    }

    public static IProject selectProject(Shell shell) {
        ProjectSelectionDialog dialog = 
                new ProjectSelectionDialog(shell);
        dialog.setMultipleSelection(false);
        dialog.setTitle("Project Selection");
        dialog.setMessage("Select a project:");
        if (dialog.open() == Window.OK) {
            return (IProject) dialog.getFirstResult();
        }
        else {
            return null;
        }
    }
    
}
