/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.navigator;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.Viewer;

public class NonEssentialEmptyPackageFilter extends NonEssentialElementsFilter {

	public NonEssentialEmptyPackageFilter() {
		super();
	}

	@Override
	protected boolean doSelect(Viewer viewer, Object parent, Object element) {
		if (element instanceof IPackageFragment) {
			IPackageFragment pkg= (IPackageFragment)element;
			try {
				return pkg.hasChildren() || 
				        pkg.hasSubpackages() || 
				        hasUnfilteredResources(viewer, pkg);
			} catch (JavaModelException e) {
				return false;
			}
		}
		return true;
	}
}
