package com.redhat.ceylon.eclipse.code.search;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class CeylonViewerComparator extends ViewerComparator {
    
    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        if (e1 instanceof CeylonSearchMatch) {
            e1 = ((CeylonSearchMatch) e1).getElement();
        }
        if (e2 instanceof CeylonSearchMatch) {
            e2 = ((CeylonSearchMatch) e2).getElement();
        }
        if (e1 instanceof CeylonElement && e2 instanceof CeylonElement) {
            CeylonElement ce1 = (CeylonElement) e1;
            CeylonElement ce2 = (CeylonElement) e2;
            int result = ce1.getVirtualFile().getPath()
                    .compareTo(ce2.getVirtualFile().getPath());
            if (result==0) result = 
                    Integer.compare(ce1.getStartOffset(), 
                            ce2.getStartOffset());
            return result;
        }
        else {
            //TODO: something much better for Units and Packages!
            return e1.toString().compareTo(e2.toString());
        }
    }
    
}