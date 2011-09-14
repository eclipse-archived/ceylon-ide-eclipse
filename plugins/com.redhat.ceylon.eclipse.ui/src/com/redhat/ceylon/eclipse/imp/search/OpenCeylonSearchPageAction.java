package com.redhat.ceylon.eclipse.imp.search;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class OpenCeylonSearchPageAction implements IWorkbenchWindowActionDelegate {

    private IWorkbenchWindow window;

    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    public void run(IAction action) {
        if (window == null || window.getActivePage() == null) {
            Shell shell= CeylonPlugin.getInstance().getWorkbench()
                    .getActiveWorkbenchWindow().getShell();
            MessageDialog.openError(shell, "Ceylon Search Error", "No active window");
        }
        else {
            NewSearchUI.openSearchDialog(window, "org.redhat.ceylon.ui.searchPage");
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
        // do nothing since the action isn't selection dependent.
    }

    public void dispose() {
        window = null;
    }
        
}