/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.explorer;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.dialogs.PackageSelectionDialog;
import org.eclipse.jdt.internal.ui.packageview.PackagesMessages;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.progress.IProgressService;

class GotoPackageAction extends Action {

    private PackageExplorerPart fPackageExplorer;

    GotoPackageAction(PackageExplorerPart part) {
        super(PackagesMessages.GotoPackage_action_label);
        setDescription(PackagesMessages.GotoPackage_action_description);
        fPackageExplorer= part;
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.GOTO_PACKAGE_ACTION);
    }

    @Override
    public void run() {
        Shell shell= JavaPlugin.getActiveWorkbenchShell();
        SelectionDialog dialog= createAllPackagesDialog(shell);
        dialog.setTitle(getDialogTitle());
        dialog.setMessage(PackagesMessages.GotoPackage_dialog_message);
        dialog.open();
        Object[] res= dialog.getResult();
        if (res != null && res.length == 1)
            gotoPackage((IPackageFragment)res[0]);
    }

    private SelectionDialog createAllPackagesDialog(Shell shell) {
        IProgressService progressService= PlatformUI.getWorkbench().getProgressService();
        IJavaSearchScope scope= SearchEngine.createWorkspaceScope();
        int flag= PackageSelectionDialog.F_HIDE_EMPTY_INNER;
        PackageSelectionDialog dialog= new PackageSelectionDialog(shell, progressService, flag, scope);
        dialog.setFilter(""); //$NON-NLS-1$
        dialog.setIgnoreCase(false);
        dialog.setMultipleSelection(false);
        return dialog;
    }

    private void gotoPackage(IPackageFragment p) {
        fPackageExplorer.selectReveal(new StructuredSelection(p));
        if (!p.equals(getSelectedElement())) {
            MessageDialog.openInformation(fPackageExplorer.getSite().getShell(),
                getDialogTitle(),
                Messages.format(PackagesMessages.PackageExplorer_element_not_present, JavaElementLabels.getElementLabel(p, JavaElementLabels.ALL_DEFAULT)));
        }
    }

    private Object getSelectedElement() {
        return ((IStructuredSelection)fPackageExplorer.getSite().getSelectionProvider().getSelection()).getFirstElement();
    }

    private String getDialogTitle() {
        return PackagesMessages.GotoPackage_dialog_title;
    }

}