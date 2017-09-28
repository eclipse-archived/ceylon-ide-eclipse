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