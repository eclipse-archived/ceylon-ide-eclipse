package com.redhat.ceylon.eclipse.code.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.redhat.ceylon.eclipse.ui.CeylonResources;

public class AddToUnitWizard extends Wizard implements INewWizard {

    private IStructuredSelection selection;
    private IWorkbench workbench;
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        this.workbench = workbench;
    }
    
    @Override
    public boolean performFinish() {
        return true;
    }
    
    @Override
    public void addPages() {
        super.addPages();
        AddToUnitWizardPage page = new AddToUnitWizardPage("Move to Source File", 
                "Select a Ceylon source file.", 
                CeylonResources.CEYLON_NEW_FILE);
        page.init(workbench, selection);
        addPage(page);
    }
    
    public static boolean open(String text, IFile file, String title, String msg) {
//        IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry()
//                .findWizard(PLUGIN_ID + ".addToUnitWizard");
//        if (descriptor==null) {
//            return false;
//        }
//        else {
//            try {
//                NewUnitWizard wizard = (NewUnitWizard) descriptor.createWizard();
                AddToUnitWizard wizard = new AddToUnitWizard();
                wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(file));
                WizardDialog wd = new WizardDialog(Display.getCurrent().getActiveShell(), 
                        wizard);
                wd.setTitle(title);
//                wd.setMessage(msg);
                return wd.open()!=Window.CANCEL;
//            }
//            catch (CoreException e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
    }

}
