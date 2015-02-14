/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Festal - copy for use with the Ceylon CNF extension
 *******************************************************************************/
package com.redhat.ceylon.eclipse.code.navigator;


import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.IExtensionStateModel;
import org.eclipse.ui.navigator.INavigatorContentService;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.navigator.IExtensionStateConstants;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

/**
 *
 * This filter is only applicable to instances of the Common Navigator.
 *
 * This filter will not allow essential elements to be blocked.
 */
public abstract class NonEssentialElementsFilter extends ViewerFilter {

	private static final String CEYLON_EXTENSION_ID = CeylonPlugin.PLUGIN_ID + ".ceylonContent";
	private static final String JAVA_EXTENSION_ID = "org.eclipse.jdt.java.ui.javaContent";

	private boolean isStateModelInitialized = false;
	private IExtensionStateModel fStateModel = null;

	private INavigatorContentService fContentService;

	protected NonEssentialElementsFilter() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean select(Viewer viewer, Object parent, Object element) {
		if (!isStateModelInitialized) {
			initStateModel(viewer);
		}
		if (fContentService == null || fStateModel == null) {
			return true;
		} else if (element instanceof IPackageFragment) {
			if (isApplicable() && viewer instanceof StructuredViewer) {
				boolean isHierarchicalLayout= !fStateModel.getBooleanProperty(IExtensionStateConstants.Values.IS_LAYOUT_FLAT);
				try {
					IPackageFragment fragment = (IPackageFragment) element;
					if (isHierarchicalLayout && fragment.hasSubpackages()) {
						return hasFilteredChildren((StructuredViewer) viewer, fragment);
					}
				} catch (JavaModelException e) {
					return false;
				}
			}
		}
		return doSelect(viewer, parent, element);
	}

	private boolean hasFilteredChildren(StructuredViewer viewer, IPackageFragment fragment) {
		Object[] children= getRawChildren(viewer, fragment);
		ViewerFilter[] filters= viewer.getFilters();
		for (int i= 0; i < filters.length; i++) {
			children= filters[i].filter(viewer, fragment, children);
			if (children.length == 0)
				return false;
		}
		return true;
	}

	private Object[] getRawChildren(StructuredViewer viewer, IPackageFragment fragment) {
		IStructuredContentProvider provider = (IStructuredContentProvider) viewer.getContentProvider();
		if (provider instanceof ITreeContentProvider) {
			return ((ITreeContentProvider)provider).getChildren(fragment);
		}
		return provider.getElements(fragment);
	}

	protected abstract boolean doSelect(Viewer viewer, Object parent, Object element);

	private boolean isApplicable() {
		return fContentService != null && fContentService.isVisible(CEYLON_EXTENSION_ID) && fContentService.isActive(CEYLON_EXTENSION_ID)
				&& fContentService.isVisible(JAVA_EXTENSION_ID) && fContentService.isActive(JAVA_EXTENSION_ID);
	}

	private synchronized void initStateModel(Viewer viewer) {
		if (!isStateModelInitialized) {
			if (viewer instanceof CommonViewer) {
				CommonViewer commonViewer = (CommonViewer) viewer;
				fContentService = commonViewer.getNavigatorContentService();
				fStateModel = fContentService.findStateModel(JAVA_EXTENSION_ID);

				isStateModelInitialized = true;
			}
		}
	}
	
	/**
	 * Tells whether the given package has unfiltered resources.
	 *
	 * @param viewer the viewer
	 * @param pkg the package
	 * @return <code>true</code> if the package has unfiltered resources
	 * @throws JavaModelException if this element does not exist or if an exception occurs while
	 *             accessing its corresponding resource
	 */
	protected boolean hasUnfilteredResources(Viewer viewer, IPackageFragment pkg) throws JavaModelException {
		Object[] resources= null;
		if (pkg.isDefaultPackage()) {
			IPackageFragmentRoot root = (IPackageFragmentRoot) pkg.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
			resources = root.getNonJavaResources();
		} else {
			resources = pkg.getNonJavaResources();
		}
		int length= resources.length;
		if (length == 0)
			return false;

		if (!(viewer instanceof StructuredViewer))
			return true;

		ViewerFilter[] filters= ((StructuredViewer)viewer).getFilters();
		resourceLoop: for (int i= 0; i < length; i++) {
			for (int j= 0; j < filters.length; j++) {
				if (!filters[j].select(viewer, pkg, resources[i]))
					continue resourceLoop;
			}
			return true;

		}
		return false;
	}
}
