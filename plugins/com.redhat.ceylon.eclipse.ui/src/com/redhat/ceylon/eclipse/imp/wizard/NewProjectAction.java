package com.redhat.ceylon.eclipse.imp.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.wizards.IWizardDescriptor;

public class NewProjectAction extends ActionDelegate {
    @Override
    public void run(IAction action) {
        super.run(action);
        IWorkbench wb = PlatformUI.getWorkbench();
        IWizardDescriptor descriptor = wb.getNewWizardRegistry()
                .findWizard("com.redhat.ceylon.eclipse.ui.newProjectWizard");
        if (descriptor!=null) {
            try {
                NewProjectWizard wizard = (NewProjectWizard) descriptor.createWizard();
                wizard.init(wb, null);
                WizardDialog wd = new WizardDialog(Display.getCurrent().getActiveShell(), 
                        wizard);
                wd.setTitle(wizard.getWindowTitle());
                wd.open();
                wb.showPerspective("com.redhat.ceylon.eclipse.ui.perspective", 
                        wb.getActiveWorkbenchWindow());
            }
            catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }
}
