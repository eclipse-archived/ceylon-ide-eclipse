package com.redhat.ceylon.eclipse.imp.wizard;

import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.gotoLocation;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_NEW_PACKAGE;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewPackageWizard extends Wizard implements INewWizard {

    private IStructuredSelection selection;
    private NewUnitWizardPage page;
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
    }
    
    @Override
    public boolean performFinish() {
    	FileCreationOp op = new FileCreationOp(page.getSourceDir(), 
                page.getPackageFragment(), "package", 
                page.isIncludePreamble(), 
                "Package package {\n    name='" + 
                         page.getPackageFragment().getElementName() + 
                         "';\n    shared=" + page.isShared() + ";\n}");
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
            page= new NewUnitWizardPage("New Ceylon Package",
                    "Create a Ceylon package with a package descriptor.",
                    "package", CEYLON_NEW_PACKAGE) {
                @Override
                String getPackageLabel() {
                    return "Package name: ";
                }
                @Override
            	void createControls(Composite composite) {
            		Text name = createPackageField(composite);
                    createSharedField(composite);
                    createSeparator(composite);
            		createFolderField(composite);
            		name.forceFocus();
            	}
            };
            page.init(selection);
        }
        addPage(page);
    }
}
