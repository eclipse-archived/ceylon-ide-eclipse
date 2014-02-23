package com.redhat.ceylon.eclipse.code.wizard;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.FILE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class UnitSelectionDialog extends ElementListSelectionDialog {

    IPackageFragmentRoot sourceDir;
    
    public UnitSelectionDialog(Shell parent, //IProject project, 
            final IPackageFragmentRoot sourceDir) {
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
                return ((IFile) element).getFullPath()
                        .makeRelativeTo(sourceDir.getPath())
                        .toPortableString();
            }
            @Override
            public Image getImage(Object element) {
                return FILE;
            }
        });
        this.sourceDir = sourceDir;
    }
    
    @Override
    public int open() {
        final List<IFile> elements = new ArrayList<IFile>();
        try {
            ((IFolder) sourceDir.getCorrespondingResource()).accept(new IResourceVisitor() {
                @Override
                public boolean visit(IResource resource) throws CoreException {
                    if (resource instanceof IFile) {
                        String ext = resource.getFileExtension();
                        if (ext!=null && ext.equals("ceylon")) {
                            elements.add((IFile) resource);
                        }
                        return false;
                    }
                    else {
                        return true;
                    }
                }
            }, IResource.DEPTH_INFINITE, false);
        }
        catch (Exception e) {
            e.printStackTrace();
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
    
}
