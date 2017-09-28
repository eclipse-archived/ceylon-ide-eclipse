package org.eclipse.ceylon.ide.eclipse.code.wizard;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
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
                .findWizard(PLUGIN_ID + ".newProjectWizard");
        if (descriptor!=null) {
            try {
                WizardUtil.startWizard(wb, descriptor);
                wb.showPerspective(PLUGIN_ID + ".perspective", 
                        wb.getActiveWorkbenchWindow());
            }
            catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }
}
