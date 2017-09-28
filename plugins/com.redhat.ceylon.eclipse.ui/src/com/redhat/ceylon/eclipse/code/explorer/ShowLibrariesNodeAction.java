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