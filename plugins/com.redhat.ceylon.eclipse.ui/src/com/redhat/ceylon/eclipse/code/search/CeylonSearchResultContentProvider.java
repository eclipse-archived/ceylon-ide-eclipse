package com.redhat.ceylon.eclipse.code.search;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.swt.widgets.Table;


class CeylonSearchResultContentProvider implements
        CeylonStructuredContentProvider {
    
    private static final Object[] EMPTY_ARR= new Object[0];
    
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
//        CeylonSearchResult csr = (CeylonSearchResult) input;
//        return csr.getElements();
        if (input instanceof AbstractTextSearchResult) {
            Set<Object> filteredElements = new HashSet<Object>();
            Object[] rawElements = ((AbstractTextSearchResult) input).getElements();
            int limit= page.getElementLimit().intValue();
            for (int i=0; i<rawElements.length; i++) {
                if (page.getDisplayedMatchCount(rawElements[i])>0) {
                    filteredElements.add(rawElements[i]);
                    if (limit!=-1 && limit<filteredElements.size()) {
                        break;
                    }
                }
            }
            return filteredElements.toArray();
        }
        return EMPTY_ARR;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (newInput instanceof CeylonSearchResult) {
            result = (CeylonSearchResult) newInput;
        }
    }
    
    public void elementsChanged(Object[] updatedElements) {
        if (result!=null) {
            int addLimit = getAddLimit();

            Set<Object> updated= new HashSet<Object>();
            Set<Object> added= new HashSet<Object>();
            Set<Object> removed= new HashSet<Object>();

            for (int i=0; i<updatedElements.length; i++) {
                if (page.getDisplayedMatchCount(updatedElements[i])>0) {
                    if (viewer.testFindItem(updatedElements[i])!=null)
                        updated.add(updatedElements[i]);
                    else {
                        if (addLimit>0) {
                            added.add(updatedElements[i]);
                            addLimit--;
                        }
                    }
                } else {
                    removed.add(updatedElements[i]);
                }
            }
        }
    }

    private int getAddLimit() {
        int limit = page.getElementLimit().intValue();
        if (limit!=-1) {
            Table table = (Table) viewer.getControl();
            int itemCount = table.getItemCount();
            if (itemCount>=limit) {
                return 0;
            }
            return limit-itemCount;
        }
        return Integer.MAX_VALUE;
    }

    
    @Override
    public void clear() {
        viewer.refresh();
    }
    
    @Override
    public void setLevel(int grouping) {
        //ignore
    }
    
}