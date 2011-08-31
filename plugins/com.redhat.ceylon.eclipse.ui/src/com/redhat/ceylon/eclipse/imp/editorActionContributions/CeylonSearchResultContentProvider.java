package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

class CeylonSearchResultContentProvider implements
		IStructuredContentProvider {
	
	private final TableViewer viewer;
	private CeylonSearchResult result;

	CeylonSearchResultContentProvider(TableViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void dispose() {}

	@Override
	public Object[] getElements(Object input) {
		CeylonSearchResult csr = (CeylonSearchResult) input;
		return csr.getElements();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof CeylonSearchResult) {
			result = (CeylonSearchResult) newInput;
		}
	}
	
	public void elementsChanged(Object[] updatedElements) {
		int elementLimit= -1;
		boolean tableLimited= elementLimit != -1;
		for (int i= 0; i < updatedElements.length; i++) {
			if (result.getMatchCount(updatedElements[i]) > 0) {
				if (viewer.testFindItem(updatedElements[i]) != null) {
					viewer.update(updatedElements[i], null);
				}
				else {
					if (!tableLimited || viewer.getTable().getItemCount() < elementLimit) {
						viewer.add(updatedElements[i]);
					}
				}
			} 
			else {
				viewer.remove(updatedElements[i]);
			}
		}
	}

}