package com.redhat.ceylon.eclipse.code.search;

import org.eclipse.jface.viewers.IStructuredContentProvider;

public interface CeylonStructuredContentProvider extends IStructuredContentProvider {
    public void elementsChanged(Object[] updatedElements);
    public void clear();
    public void setLevel(int grouping);
}
