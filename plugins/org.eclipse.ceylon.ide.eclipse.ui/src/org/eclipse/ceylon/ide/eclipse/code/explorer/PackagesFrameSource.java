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

import org.eclipse.ui.views.framelist.TreeFrame;
import org.eclipse.ui.views.framelist.TreeViewerFrameSource;

class PackagesFrameSource extends TreeViewerFrameSource {
    private PackageExplorerPart fPackagesExplorer;

    PackagesFrameSource(PackageExplorerPart explorer) {
        super(explorer.getTreeViewer());
        fPackagesExplorer= explorer;
    }

    @Override
    protected TreeFrame createFrame(Object input) {
        TreeFrame frame = super.createFrame(input);
        frame.setName(fPackagesExplorer.getFrameName(input));
        frame.setToolTipText(fPackagesExplorer.getToolTipText(input));
        return frame;
    }
}