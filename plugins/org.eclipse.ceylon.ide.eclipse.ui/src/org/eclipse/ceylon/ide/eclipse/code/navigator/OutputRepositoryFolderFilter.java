/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.navigator;

import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputFolder;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonNature;

public class OutputRepositoryFolderFilter extends ViewerFilter {

    public OutputRepositoryFolderFilter() {
        super();
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof IFolder) {
            IFolder folder = (IFolder)element;
            IProject proj = folder.getProject();
            return !CeylonNature.isEnabled(proj) || 
                    !folder.equals(getCeylonModulesOutputFolder(proj));
        }
        return true;
    }
}
