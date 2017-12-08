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

import org.eclipse.jdt.internal.ui.packageview.PackagesMessages;
import org.eclipse.jface.action.Action;

class ShowLibrariesNodeAction extends Action {

    private PackageExplorerPart fPackageExplorer;

    public ShowLibrariesNodeAction(PackageExplorerPart packageExplorer) {
        super(PackagesMessages.LayoutActionGroup_show_libraries_in_group, AS_CHECK_BOX);
        fPackageExplorer= packageExplorer;
        setChecked(packageExplorer.isLibrariesNodeShown());
    }

    /*
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public void run() {
        fPackageExplorer.setShowLibrariesNode(isChecked());
    }
}