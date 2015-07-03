package com.redhat.ceylon.eclipse.code.navigator;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.Viewer;

public class NonEssentialEmptyInnerPackageFilter extends NonEssentialElementsFilter {

	public NonEssentialEmptyInnerPackageFilter() {
		super();
	}

	@Override
	protected boolean doSelect(Viewer viewer, Object parent, Object element) {
		if (element instanceof IPackageFragment) {
			IPackageFragment pkg= (IPackageFragment)element;
			try {
				if (pkg.isDefaultPackage()) {
				    if (pkg.hasChildren() || hasUnfilteredResources(viewer, pkg)) {
				        return true;
				    }
				    if (pkg instanceof SourceModuleNode) {
				        for (IPackageFragment pf : ((SourceModuleNode)pkg).getPackageFragments()) {
				            if (! pf.isDefaultPackage()) {
				                return true;
				            }
				        }
				    }
				    return false;
				}
				return pkg instanceof SourceModuleNode || !pkg.hasSubpackages() || pkg.hasChildren() || hasUnfilteredResources(viewer, pkg);
			} catch (JavaModelException e) {
				return false;
			}
		}
	
		return true;
	}
}
