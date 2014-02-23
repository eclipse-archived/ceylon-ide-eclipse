package com.redhat.ceylon.eclipse.code.select;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.PROJECT;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class ProjectSelectionDialog extends ElementListSelectionDialog {

    public ProjectSelectionDialog(Shell parent) {
        super(parent, new ILabelProvider() {
            @Override
            public void removeListener(ILabelProviderListener listener) {}
            @Override
            public boolean isLabelProperty(Object element, String property) {
                return false;
            }
            @Override
            public void dispose() {}
            @Override
            public void addListener(ILabelProviderListener listener) {}
            @Override
            public String getText(Object element) {
                return ((IJavaProject) element).getElementName();
            }
            @Override
            public Image getImage(Object element) {
                return PROJECT;
            }
        });
    }
    
    @Override
    public int open() {
        List<IJavaProject> elements = new ArrayList<IJavaProject>();
        try {
            addChildren(elements, JavaCore.create(ResourcesPlugin.getWorkspace().getRoot())
                    .getJavaProjects());
        }
        catch (JavaModelException jme) {
            jme.printStackTrace();
        }
        /*Collections.sort(elements, new Comparator<IPackageFragment>() {
            @Override
            public int compare(IPackageFragment pf1, IPackageFragment pf2) {
                return pf1.getElementName().compareTo(pf2.getElementName());
            }
        });*/
        setElements(elements.toArray());
        return super.open();
    }

    public void addChildren(List<IJavaProject> elements, IJavaProject[] children)
            throws JavaModelException {
        for (IJavaProject jp: children) {
            elements.add(jp);
        }
    }
    
}
