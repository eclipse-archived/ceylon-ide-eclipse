/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.select;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.PACKAGE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class PackageSelectionDialog extends ElementListSelectionDialog {

    private final IPackageFragmentRoot sourceDir;
    
    private PackageSelectionDialog(Shell parent, //IProject project, 
            IPackageFragmentRoot sourceDir) {
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
                String name = ((IPackageFragment) element).getElementName();
                if (name.isEmpty()) {
                    return "(default package)";
                }
                else {
                    return name;
                }
            }
            @Override
            public Image getImage(Object element) {
                return PACKAGE;
            }
        });
        this.sourceDir = sourceDir;
    }
    
    @Override
    public int open() {
        List<IPackageFragment> elements = 
                new ArrayList<IPackageFragment>();
        try {
            addChildren(elements, sourceDir.getChildren());
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
    
    private void addChildren(List<IPackageFragment> elements, IJavaElement[] children)
            throws JavaModelException {
        for (IJavaElement je: children) {
            if (je instanceof IPackageFragment) {
                IPackageFragment pf = (IPackageFragment) je;
                elements.add(pf);
                addChildren(elements, pf.getChildren());
            }
        }
    }
    
    public static IPackageFragment selectPackage(Shell shell, 
            IPackageFragmentRoot sourceDir) {
        PackageSelectionDialog dialog = 
                new PackageSelectionDialog(shell, sourceDir);
        dialog.setMultipleSelection(false);
        dialog.setTitle("Package Selection");
        dialog.setMessage("Select a package:");
        if (dialog.open() == Window.OK) {
            return (IPackageFragment) dialog.getFirstResult();
        }
        else {
            return null;
        }
    }
    
}
