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

import org.eclipse.jdt.internal.ui.packageview.PackagesMessages;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionGroup;

class LayoutActionGroup extends ActionGroup {

    public static final String VIEWMENU_LAYOUT_GROUP= "layout"; //$NON-NLS-1$

    private IAction fFlatLayoutAction;
    private IAction fHierarchicalLayoutAction;
    private IAction fShowLibrariesNode;

    LayoutActionGroup(PackageExplorerPart packageExplorer) {
        fFlatLayoutAction= new LayoutAction(packageExplorer, true);
        fHierarchicalLayoutAction= new LayoutAction(packageExplorer, false);
        fShowLibrariesNode= new ShowLibrariesNodeAction(packageExplorer);
    }

    /* (non-Javadoc)
     * @see ActionGroup#fillActionBars(IActionBars)
     */
    @Override
    public void fillActionBars(IActionBars actionBars) {
        super.fillActionBars(actionBars);
        contributeToViewMenu(actionBars.getMenuManager());
    }

    private void contributeToViewMenu(IMenuManager viewMenu) {
        viewMenu.add(new Separator(VIEWMENU_LAYOUT_GROUP));

        // Create layout sub menu

        IMenuManager layoutSubMenu= new MenuManager(PackagesMessages.LayoutActionGroup_label);
        layoutSubMenu.add(fFlatLayoutAction);
        layoutSubMenu.add(fHierarchicalLayoutAction);

        viewMenu.add(layoutSubMenu);
        viewMenu.add(fShowLibrariesNode);
    }
}