package com.redhat.ceylon.eclipse.code.navigator;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;

public class OutputRepositoryFolderFilter extends ViewerFilter {

    public OutputRepositoryFolderFilter() {
        super();
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof IFolder) {
            IFolder folder= (IFolder)element;
            IProject proj= folder.getProject();
            try {
                if (!proj.hasNature(CeylonNature.NATURE_ID)) {
                    return true;
                }
                return ! folder.equals(CeylonBuilder.getCeylonModulesOutputFolder(proj));
            } catch (CoreException ex) {
                return true;
            }
        }
        return true;
    }
}
