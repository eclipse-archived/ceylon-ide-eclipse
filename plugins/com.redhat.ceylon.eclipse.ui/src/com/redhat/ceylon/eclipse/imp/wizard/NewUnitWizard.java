package com.redhat.ceylon.eclipse.imp.wizard;

import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.gotoLocation;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_NEW_FILE;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewUnitWizard extends Wizard implements INewWizard {
    
    private IStructuredSelection selection;
    private NewUnitWizardPage page;
    private String defaultUnitName="";
    private String contents="";
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
    }
    
    @Override
    public boolean performFinish() {
        FileCreationOp op = new FileCreationOp(page.getSourceDir(), 
                page.getPackageFragment(), page.getUnitName(), 
                page.isIncludePreamble(), contents);
        try {
            getContainer().run(true, true, op);
        } 
        catch (InvocationTargetException e) {
            e.printStackTrace();
            return false;
        } 
        catch (InterruptedException e) {
            return false;
        }
        gotoLocation(op.getResult().getFullPath(), 0);
        return true;
    }
    
    @Override
    public void addPages() {
        super.addPages();
        if (page == null) {
            page= new NewUnitWizardPage("New Ceylon Unit",
                    "Create a new Ceylon compilation unit that will contain Ceylon source.",
                    defaultUnitName, CEYLON_NEW_FILE);
            page.init(selection);
        }
        addPage(page);
    }
    
    public void setDefaultUnitName(String defaultUnitName) {
		this.defaultUnitName = defaultUnitName;
	}
    
    public void setSelection(IStructuredSelection selection) {
		this.selection = selection;
	}
    
    public void setContents(String contents) {
		this.contents = contents;
	}
    
}
