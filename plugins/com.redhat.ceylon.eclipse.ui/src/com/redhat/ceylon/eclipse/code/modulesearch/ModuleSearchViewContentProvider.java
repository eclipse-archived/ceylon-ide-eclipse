package com.redhat.ceylon.eclipse.code.modulesearch;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.redhat.ceylon.ide.common.modulesearch.ModuleNode;
import com.redhat.ceylon.ide.common.modulesearch.ModuleVersionNode;

public class ModuleSearchViewContentProvider implements ITreeContentProvider {

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof List) {
            return ((List<?>) inputElement).toArray();
        }
        return null;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof ModuleNode) {
            return ((ModuleNode) parentElement).getVersions().toArray();
        }
        return null;
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof ModuleVersionNode) {
            return ((ModuleVersionNode) element).getModule();
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof ModuleNode) {
            return true;
        }
        return false;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // noop
    }

    @Override
    public void dispose() {
        // noop
    }

}