/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.explorer;

import org.eclipse.jdt.internal.ui.packageview.ClassPathContainer;
import org.eclipse.jdt.internal.ui.packageview.PackagesMessages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Goto to the referenced required project
 */
class GotoRequiredProjectAction extends Action {

    private PackageExplorerPart fPackageExplorer;

    GotoRequiredProjectAction(PackageExplorerPart part) {
        super(PackagesMessages.GotoRequiredProjectAction_label);
        setDescription(PackagesMessages.GotoRequiredProjectAction_description);
        setToolTipText(PackagesMessages.GotoRequiredProjectAction_tooltip);
        fPackageExplorer= part;
    }

    @Override
    public void run() {
        IStructuredSelection selection= (IStructuredSelection)fPackageExplorer.getSite().getSelectionProvider().getSelection();
        Object element= selection.getFirstElement();
        if (element instanceof ClassPathContainer.RequiredProjectWrapper) {
            ClassPathContainer.RequiredProjectWrapper wrapper= (ClassPathContainer.RequiredProjectWrapper) element;
            fPackageExplorer.tryToReveal(wrapper.getProject());
        }
    }
}