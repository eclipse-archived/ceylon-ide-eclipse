package com.redhat.ceylon.eclipse.code.wizard;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoLocation;
import static com.redhat.ceylon.eclipse.code.wizard.WizardUtil.runOperation;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class NewUnitWizard extends Wizard implements INewWizard {
    
    private IStructuredSelection selection;
    private IWorkbench workbench;

    private NewUnitWithDeclarationWizardPage page;
    
    public NewUnitWizard() {
        setDialogSettings(CeylonPlugin.getInstance().getDialogSettings());
    }
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        this.workbench = workbench;
    }
    
    @Override
    public boolean performFinish() {
        CreateSourceFileOperation op = 
                new CreateSourceFileOperation(page.getSourceDir(),
                        page.getPackageFragment(), page.getUnitName(),
                        page.isIncludePreamble(), getDeclarationText());
        if (runOperation(op, getContainer())) {
            BasicNewResourceWizard.selectAndReveal(op.getFile(), 
                    workbench.getActiveWorkbenchWindow());
            gotoLocation(op.getFile().getFullPath(), 0);
            return true;
        }
        else {
            return false;
        }
    }

    private String getDeclarationText() {
        if (page.isDeclaration()) {
            char initial = page.getUnitName().charAt(0);
            if (Character.isUpperCase(initial)) {
                return "class " + page.getUnitName() + "() {}";
            }
            else {
                return "void " + page.getUnitName() + "() {}";
            }
        }
        else {
            return "";
        }
    }
    
    @Override
    public void addPages() {
        super.addPages();
        if (page==null) {
            page = new NewUnitWithDeclarationWizardPage();
            page.init(workbench, selection);
        }
        addPage(page);
    }
    
}
