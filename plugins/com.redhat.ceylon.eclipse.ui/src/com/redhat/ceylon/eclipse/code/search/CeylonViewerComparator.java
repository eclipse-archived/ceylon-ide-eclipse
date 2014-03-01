package com.redhat.ceylon.eclipse.code.search;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class CeylonViewerComparator extends ViewerComparator {
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