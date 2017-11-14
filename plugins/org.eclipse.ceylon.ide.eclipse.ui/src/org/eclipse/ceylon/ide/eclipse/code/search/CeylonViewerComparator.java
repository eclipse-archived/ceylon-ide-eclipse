/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.search;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class CeylonViewerComparator extends ViewerComparator {
    
    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        if (e1 instanceof CeylonSearchMatch.Type &&
            e2 instanceof CeylonSearchMatch.Type) {
            CeylonSearchMatch.Type c1 = 
                    (CeylonSearchMatch.Type) e1;
            CeylonSearchMatch.Type c2 = 
                    (CeylonSearchMatch.Type) e2;
            return c1.compareTo(c2);
        }
        if (e2 instanceof CeylonSearchMatch) {
            e2 = ((CeylonSearchMatch) e2).getElement();
        }
        if (e1 instanceof CeylonSearchMatch) {
            e1 = ((CeylonSearchMatch) e1).getElement();
        }
        if (e2 instanceof CeylonSearchMatch) {
            e2 = ((CeylonSearchMatch) e2).getElement();
        }
        if (e1 instanceof CeylonElement && 
            e2 instanceof CeylonElement) {
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