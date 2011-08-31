package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;

import com.redhat.ceylon.eclipse.imp.treeModelBuilder.CeylonLabelProvider;

public class CeylonSearchResultPage extends AbstractTextSearchViewPage {
	
	private CeylonSearchResultContentProvider contentProvider;
	
	public CeylonSearchResultPage() {
		super(FLAG_LAYOUT_FLAT);
	}
	
	@Override
	protected void clear() {
		getViewer().refresh();
	}

	@Override
	protected void configureTableViewer(final TableViewer viewer) {
		contentProvider = new CeylonSearchResultContentProvider(viewer);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new CeylonLabelProvider());
	}

	@Override
	protected void configureTreeViewer(TreeViewer viewer) {
		throw new RuntimeException();
	}

	@Override
	protected void elementsChanged(Object[] elements) {
		if (contentProvider!=null) {
			contentProvider.elementsChanged(elements);
		}
		getViewer().refresh();
	}
	

}
