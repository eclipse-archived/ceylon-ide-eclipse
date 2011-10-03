package com.redhat.ceylon.eclipse.imp.hierarchy;

import org.eclipse.imp.services.ILabelProvider;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider;

public class HierarchyPopup extends PopupDialog {

    final private Declaration declaration;
    
    public HierarchyPopup(Declaration declaration, Shell parent) {
        super(parent, SWT.RESIZE, true, true, false, true, true,
                "Hierarchy of '" + declaration.getName() + "'", null);
        this.declaration = declaration;
    }
    
    @Override
    protected void adjustBounds() {
        Rectangle bounds = getShell().getBounds();
        int h = bounds.height;
        if (h>400) {
            bounds.height=400;
            bounds.y = bounds.y + (h-400)/3;
            getShell().setBounds(bounds);
        }
    }
    
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        Tree tree = new Tree(composite, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData gd= new GridData(GridData.FILL_BOTH);
        //gd.heightHint= tree.getItemHeight() * 12;
        tree.setLayoutData(gd);
        TreeViewer treeViewer = new TreeViewer(tree);
        treeViewer.setContentProvider(new ITreeContentProvider() {
            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
            @Override
            public void dispose() {}
            @Override
            public boolean hasChildren(Object element) {
                return getChildren(element).length>0;
            }
            @Override
            public Object getParent(Object element) {
                return null;
            }
            @Override
            public Object[] getElements(Object inputElement) {
                //return new Object[] { inputElement };
                return getChildren(inputElement);
            }
            @Override
            public Object[] getChildren(Object parentElement) {
                if (parentElement instanceof TypeDeclaration) {
                    return ((TypeDeclaration) parentElement).getKnownSubtypes().toArray();
                }
                else if (parentElement instanceof TypedDeclaration) {
                    Declaration refinedDeclaration = ((TypedDeclaration) parentElement).getRefinedDeclaration();
                    if (refinedDeclaration!=parentElement) {
                        return new Object[] { refinedDeclaration };
                    }
                }
                else if (parentElement instanceof HierarchyPopup) {
                    return new Object[] { declaration };
                }
                return new Object[0];
            }
        });
        treeViewer.setLabelProvider(new ILabelProvider() {
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
                Declaration d = (Declaration) element;
                if (d.isClassOrInterfaceMember()) {
                    return d.getName() + " in " + 
                        ((ClassOrInterface) d.getContainer()).getName();
                }
                return d.getName();
            }            
            @Override
            public Image getImage(Object element) {
                return CeylonLabelProvider.getImage((Declaration) element);
            }
        });
        treeViewer.setInput(this);
        treeViewer.expandAll();
        return composite;
    }
    
}
