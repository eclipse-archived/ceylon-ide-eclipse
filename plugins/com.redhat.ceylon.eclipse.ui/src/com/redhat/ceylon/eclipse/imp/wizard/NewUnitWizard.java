package com.redhat.ceylon.eclipse.imp.wizard;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewUnitWizard extends Wizard implements INewWizard {
    
    IStructuredSelection selection;
    IWorkbench workbench;
    NewUnitWizardPage page;
    
    public NewUnitWizard() {}
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        this.workbench = workbench;
    }
    
    @Override
    public boolean performFinish() {
        //ResourcesPlugin.getWorkspace()
        return true;
    }
    
    @Override
    public void addPages() {
        super.addPages();
        if (page == null) {
            page= new NewUnitWizardPage();
            page.init(selection);
        }
        addPage(page);
    }

}
