package com.redhat.ceylon.eclipse.code.navigator;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputFolder;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.redhat.ceylon.eclipse.core.builder.CeylonNature;

public class OutputRepositoryFolderFilter extends ViewerFilter {

    public OutputRepositoryFolderFilter() {
        super();
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof IFolder) {
            IFolder folder = (IFolder)element;
            IProject proj = folder.getProject();
            return !CeylonNature.isEnabled(proj) || 
                    !folder.equals(getCeylonModulesOutputFolder(proj));
        }
        return true;
    }
}
