package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.ui.text.Match;

class CeylonSearchResultContentProvider implements
		IStructuredContentProvider/*, IFileSearchContentProvider*/ {
	private final TableViewer viewer;
	private CeylonSearchResult result;

	CeylonSearchResultContentProvider(TableViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void dispose() {}

	@Override
	public Object[] getElements(Object input) {
		List<Object> result = new ArrayList<Object>();
		CeylonSearchResult csr = (CeylonSearchResult) input;
		Object[] elements = csr.getElements();
		for (Object e: elements) {
			for (Match m: csr.getMatches(e)) {
				result.add(m);
			}
		}
		return result.toArray();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof CeylonSearchResult) {
			result= (CeylonSearchResult) newInput;
		}
	}
	
	//@Override
	public void elementsChanged(Object[] updatedElements) {
		int elementLimit= -1;
		boolean tableLimited= elementLimit != -1;
		for (int i= 0; i < updatedElements.length; i++) {
			if (result.getMatchCount(updatedElements[i]) > 0) {
				if (viewer.testFindItem(updatedElements[i]) != null)
					viewer.update(updatedElements[i], null);
				else {
					if (!tableLimited || viewer.getTable().getItemCount() < elementLimit)
						viewer.add(updatedElements[i]);
				}
			} else
				viewer.remove(updatedElements[i]);
		}
	}

	/*@Override
	public void clear() {
		viewer.refresh();
	}
	*/
}