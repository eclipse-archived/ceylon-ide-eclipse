/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.explorer;

import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.packageview.PackagesMessages;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

class LayoutAction extends Action {

    private boolean fIsFlatLayout;
    private PackageExplorerPart fPackageExplorer;

    public LayoutAction(PackageExplorerPart packageExplorer, boolean flat) {
        super("", AS_RADIO_BUTTON); //$NON-NLS-1$

        fIsFlatLayout= flat;
        fPackageExplorer= packageExplorer;
        if (fIsFlatLayout) {
            setText(PackagesMessages.LayoutActionGroup_flatLayoutAction_label);
            JavaPluginImages.setLocalImageDescriptors(this, "flatLayout.png"); //$NON-NLS-1$
            PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.LAYOUT_FLAT_ACTION);
        } else {
            setText(PackagesMessages.LayoutActionGroup_hierarchicalLayoutAction_label);
            JavaPluginImages.setLocalImageDescriptors(this, "hierarchicalLayout.png"); //$NON-NLS-1$
            PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.LAYOUT_HIERARCHICAL_ACTION);
        }
        setChecked(packageExplorer.isFlatLayout() == fIsFlatLayout);
    }

    /*
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public void run() {
        if (fPackageExplorer.isFlatLayout() != fIsFlatLayout)
            fPackageExplorer.setFlatLayout(fIsFlatLayout);
    }
}