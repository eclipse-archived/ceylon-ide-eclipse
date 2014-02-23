package com.redhat.ceylon.eclipse.code.wizard;

import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_NEW_FILE;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class SelectUnitWizard extends Wizard implements INewWizard {

    private IStructuredSelection selection;
    private SelectUnitWizardPage page;
    private String title;
    private String description;
    
    public SelectUnitWizard(String title, String description) {
        this.title = title;
        this.description = description;
    }
    
    public IPackageFragmentRoot getSourceDir() {
        return page.getSourceDir();
    }
    
    public IFile getFile() {
        return page.getUnit();
    }
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
    }
    
    @Override
    public boolean performFinish() {
        return true;
    }
    
    @Override
    public void addPages() {
        super.addPages();
        page = new SelectUnitWizardPage(title, description, CEYLON_NEW_FILE);
        page.init(selection);
        addPage(page);
    }

    public boolean open(IFile file) {
        init(PlatformUI.getWorkbench(), new StructuredSelection(file));
        Shell shell = Display.getCurrent().getActiveShell();
        WizardDialog wd = new WizardDialog(shell, this);
        wd.setTitle(title);
        return wd.open() != Window.CANCEL;
    }

}
