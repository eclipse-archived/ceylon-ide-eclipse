/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.navigator;

import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class EmptyLibraryContainerFilter extends ViewerFilter {

    public EmptyLibraryContainerFilter() {
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof PackageFragmentRootContainer && viewer instanceof StructuredViewer) {
            return hasFilteredChildren((StructuredViewer)viewer, (PackageFragmentRootContainer)element);
        }
        return true;
    }
    
    private boolean hasFilteredChildren(StructuredViewer viewer, PackageFragmentRootContainer fragment) {
        Object[] children= getRawChildren(viewer, fragment);
        ViewerFilter[] filters= viewer.getFilters();
        for (int i= 0; i < filters.length; i++) {
            children= filters[i].filter(viewer, fragment, children);
            if (children.length == 0)
                return false;
        }
        return true;
    }

    private Object[] getRawChildren(StructuredViewer viewer, PackageFragmentRootContainer fragment) {
        IStructuredContentProvider provider = (IStructuredContentProvider) viewer.getContentProvider();
        if (provider instanceof ITreeContentProvider) {
            return ((ITreeContentProvider)provider).getChildren(fragment);
        }
        return provider.getElements(fragment);
    }
}
