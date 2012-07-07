package com.redhat.ceylon.eclipse.code.search;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;


class CeylonSearchResultContentProvider implements
        CeylonStructuredContentProvider {
	
	private final TableViewer viewer;
	private CeylonSearchResult result;
    private CeylonSearchResultPage page;

	CeylonSearchResultContentProvider(TableViewer viewer, 
	        CeylonSearchResultPage page) {
		this.viewer = viewer;
		this.page = page;
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
		int elementLimit= getElementLimit();
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

    private int getElementLimit() {
        return page.getElementLimit();
    }
    
    @Override
    public void clear() {
        viewer.refresh();
    }
    
}