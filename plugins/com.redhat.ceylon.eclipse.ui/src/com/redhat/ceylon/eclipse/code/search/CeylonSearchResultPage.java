package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.gotoLocation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;

public class CeylonSearchResultPage extends AbstractTextSearchViewPage {
	
	static class CeylonViewerComparator extends ViewerComparator {
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            if (e1 instanceof CeylonElement && e2 instanceof CeylonElement) {
            	CeylonElement ce1 = (CeylonElement) e1;
            	CeylonElement ce2 = (CeylonElement) e2;
            	//IFile f1 = ce1.getFile();
				//IFile f2 = ce2.getFile();
				int result;
				/*if (f1!=null && f2!=null) {
            		result = f1.getFullPath().toString()
            				.compareTo(f2.getFullPath().toString());
            	}
            	else {*/
            		result = ce1.getVirtualFile().getPath()
            				.compareTo(ce2.getVirtualFile().getPath());
            	//}
            	return result!=0 ? result :
            		    Integer.compare(ce1.getLocation(), ce2.getLocation());
            }
            else {
                //TODO: something much better for Units and Packages!
                return e1.toString().compareTo(e2.toString());
            }
        }
    }

    private CeylonStructuredContentProvider contentProvider;
	
	public CeylonSearchResultPage() {
		super(FLAG_LAYOUT_FLAT|FLAG_LAYOUT_TREE);
		setElementLimit(50);
	}
	
	@Override
	protected void clear() {
	    if (contentProvider!=null) {
	        contentProvider.clear();
	    }
		//getViewer().refresh();
	}

    private void configureViewer(StructuredViewer viewer) {
        viewer.setContentProvider(contentProvider);
        viewer.setLabelProvider(new CeylonLabelProvider(true));
        viewer.setComparator(new CeylonViewerComparator());
    }

	@Override
	protected void configureTableViewer(final TableViewer viewer) {
		contentProvider = new CeylonSearchResultContentProvider(viewer, this);
		configureViewer(viewer);
	}

	@Override
	protected void configureTreeViewer(TreeViewer viewer) {
        contentProvider = new CeylonSearchResultTreeContentProvider(viewer, this);
        configureViewer(viewer);
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
        CeylonElement element = (CeylonElement) match.getElement();
		IFile file = element.getFile();
        if (file==null) {
        	Path path = new Path(element.getVirtualFile().getPath());
        	gotoLocation(path, offset, length);
        }
        else {
        	IWorkbenchPage page = getSite().getPage();
        	if (offset >= 0 && length != 0) {
        		openAndSelect(page, file, offset, length, activate);
        	} 
        	else {
        		open(page, file, activate);
        	}
        }
    }
}
