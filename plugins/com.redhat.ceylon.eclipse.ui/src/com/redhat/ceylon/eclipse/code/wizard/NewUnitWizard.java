package com.redhat.ceylon.eclipse.code.wizard;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoLocation;
import static com.redhat.ceylon.eclipse.code.wizard.WizardUtil.runOperation;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.redhat.ceylon.eclipse.code.editor.RecentFilesPopup;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class NewUnitWizard extends Wizard implements INewWizard {
    
    private IStructuredSelection selection;
    private IWorkbench workbench;
    private String unitName = "";

    private NewUnitWithDeclarationWizardPage page;
    private boolean perform;
    
    public NewUnitWizard() {
        setDialogSettings(CeylonPlugin.getInstance().getDialogSettings());
    }
    
    public String getUnitName() {
        return page.getUnitName();
    }
    
    public void setUnitName(String name) {
        unitName = name;
    }
    
    public IPackageFragment getPackageFragment() {
        return page.getPackageFragment();
    }
    
    public IPackageFragmentRoot getSourceFolder() {
        return page.getSourceDir();
    }
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        this.workbench = workbench;
    }
    
    @Override
    public boolean performFinish() {
        if (perform) {
            CreateSourceFileOperation op = 
                    new CreateSourceFileOperation(page.getSourceDir(),
                            page.getPackageFragment(), page.getUnitName(),
                            page.isIncludePreamble(), getDeclarationText());
            if (runOperation(op, getContainer())) {
                IFile file = op.getFile();
                RecentFilesPopup.addToHistory(file);
                BasicNewResourceWizard.selectAndReveal(file, 
                        workbench.getActiveWorkbenchWindow());
                gotoLocation(file.getFullPath(), 0);
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return true;
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
            page.setUnitName(unitName);
        }
        addPage(page);
    }

    public void setPerform(boolean perform) {
        this.perform = perform;
    }
    
}
