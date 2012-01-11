package com.redhat.ceylon.eclipse.imp.wizard;

import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.gotoLocation;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_NEW_FILE;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

public class NewUnitWizard extends Wizard implements INewWizard {
    
    private IStructuredSelection selection;
    private IWorkbench workbench;

    private NewUnitWizardPage page;
    private String defaultUnitName="";
    private String contents="";
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        this.workbench = workbench;
    }
    
    @Override
    public boolean performFinish() {
    	if ("".equals(contents) && page.isDeclaration()) {
    		char initial = page.getUnitName().charAt(0);
			if (Character.isUpperCase(initial)) {
				contents = "class " + page.getUnitName() + "() {}";
			}
			else {
				contents = "void " + page.getUnitName() + "() {}";
			}
    	}
    	else {
    		contents=contents.replace("$unitName", page.getUnitName());
    	}
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
        BasicNewResourceWizard.selectAndReveal(op.getResult(), 
                workbench.getActiveWorkbenchWindow());
        gotoLocation(op.getResult().getFullPath(), 0);
        return true;
    }
    
    @Override
    public void addPages() {
        super.addPages();
        if (page == null) {
            page= new NewUnitWizardPage("New Ceylon Unit",
                    "Create a new Ceylon compilation unit that will contain Ceylon source.",
                    defaultUnitName, CEYLON_NEW_FILE, !"".equals(contents));
            page.init(workbench, selection);
        }
        addPage(page);
    }
    
    public void setDefaultUnitName(String defaultUnitName) {
		this.defaultUnitName = defaultUnitName;
	}
    
    /*public void setSelection(IStructuredSelection selection) {
		this.selection = selection;
	}*/
    
    public void setContents(String contents) {
		this.contents = contents;
	}

	public static void open(final String def, final IFile file,
	        final String unitName) {
	    IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry()
	            .findWizard("com.redhat.ceylon.eclipse.ui.newUnitWizard");
	    if (descriptor!=null) {
	        try {
	            NewUnitWizard wizard = (NewUnitWizard) descriptor.createWizard();
	            wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(file));
	            wizard.setDefaultUnitName(unitName);
	            wizard.setContents(def);
	            WizardDialog wd = new WizardDialog(Display.getCurrent().getActiveShell(), 
	                    wizard);
	            wd.setTitle(wizard.getWindowTitle());
	            wd.open();
	        }
	        catch (CoreException e) {
	            e.printStackTrace();
	        }
	    }
	}
    
}
