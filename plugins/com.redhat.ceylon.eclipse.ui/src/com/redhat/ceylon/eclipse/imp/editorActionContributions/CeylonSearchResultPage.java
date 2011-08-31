package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

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
		viewer.setComparator( new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				CeylonElement ce1 = (CeylonElement) e1;
				CeylonElement ce2 = (CeylonElement) e2;
				int result = ce1.file.getFullPath().toString().compareTo(ce2.file.getFullPath().toString());
				if (result==0) {
					return new Integer(ce1.getLocation()).compareTo(ce2.getLocation());
				}
				else {
					return result;
				}
			}
		});
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
	
	@Override
	protected void showMatch(Match match, int offset, int length, boolean activate)
			throws PartInitException {
        IFile file = ((CeylonElement) match.getElement()).getFile();
        IWorkbenchPage page = getSite().getPage();
        if (offset >= 0 && length != 0) {
            openAndSelect(page, file, offset, length, activate);
        } 
        else {
            open(page, file, activate);
        }
    }
}
