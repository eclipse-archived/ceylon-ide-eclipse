package com.redhat.ceylon.eclipse.imp.search;

import org.eclipse.jface.viewers.IStructuredContentProvider;

public interface CeylonStructuredContentProvider extends IStructuredContentProvider {
    public void elementsChanged(Object[] updatedElements);
    public void clear();
}
