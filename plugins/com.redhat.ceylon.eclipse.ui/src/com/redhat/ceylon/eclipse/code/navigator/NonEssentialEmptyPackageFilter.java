package com.redhat.ceylon.eclipse.code.navigator;

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
