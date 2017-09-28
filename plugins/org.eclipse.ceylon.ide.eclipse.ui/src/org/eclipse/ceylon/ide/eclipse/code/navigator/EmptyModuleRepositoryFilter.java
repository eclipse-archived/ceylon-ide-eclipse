package org.eclipse.ceylon.ide.eclipse.code.navigator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class EmptyModuleRepositoryFilter extends ViewerFilter {

	public EmptyModuleRepositoryFilter() {
		super();
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof RepositoryNode) {
			return ! ((RepositoryNode) element).getModules().isEmpty();
		}
		return true;
	}

}
