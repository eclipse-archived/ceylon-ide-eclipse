/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.modulesearch;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.ceylon.ide.common.modulesearch.ModuleNode;
import org.eclipse.ceylon.ide.common.modulesearch.ModuleVersionNode;

public class ModuleSearchViewContentProvider implements ITreeContentProvider {

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof List) {
            return ((List<?>) inputElement).toArray();
        }
        return null;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof ModuleNode) {
            return ((ModuleNode) parentElement).getVersions().toArray();
        }
        return null;
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof ModuleVersionNode) {
            return ((ModuleVersionNode) element).getModule();
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof ModuleNode) {
            return true;
        }
        return false;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // noop
    }

    @Override
    public void dispose() {
        // noop
    }

}