package com.redhat.ceylon.eclipse.code.moduledependencies;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class DependencyGraph implements IWorkbenchWindowActionDelegate {

    private IProject fProject;

    public void dispose() {}

    public void init(IWorkbenchWindow window) {}

    public void run(IAction action) {
        if (fProject == null) {
            return;
        }
        try {
            IWorkbenchWindow activeWorkbenchWindow = 
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            DependencyGraphView view = (DependencyGraphView) 
                    activeWorkbenchWindow.getActivePage()
                            .showView(DependencyGraphView.ID);
            view.setProject(fProject);
        }
        catch (PartInitException e) {
            e.printStackTrace();
        }
    }
    
    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection) selection;
            Object first = ss.getFirstElement();

            if (first instanceof IProject) {
                fProject = (IProject) first;
            }
            else if (first instanceof IJavaProject) {
                fProject = ((IJavaProject) first).getProject();
            }
        }
    }
}
