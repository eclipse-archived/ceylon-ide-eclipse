package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import org.eclipse.jface.viewers.IStructuredContentProvider;

public interface CeylonStructuredContentProvider extends IStructuredContentProvider {
    abstract public void elementsChanged(Object[] updatedElements);
}
