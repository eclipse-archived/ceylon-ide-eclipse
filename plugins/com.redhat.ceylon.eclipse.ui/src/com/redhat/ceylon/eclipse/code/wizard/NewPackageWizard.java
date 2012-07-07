package com.redhat.ceylon.eclipse.code.wizard;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.gotoLocation;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_NEW_PACKAGE;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

public class NewPackageWizard extends Wizard implements INewWizard {

    private IStructuredSelection selection;
    private NewUnitWizardPage page;
    private IWorkbench workbench;
    
    public IPackageFragment getPackageFragment() {
        return page.getPackageFragment();
    }
    
    public IPackageFragmentRoot getSourceFolder() {
        return page.getSourceDir();
    }
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        this.workbench=workbench;
    }
    
    @Override
    public boolean performFinish() {
        FileCreationOp op = new FileCreationOp(page.getSourceDir(), 
                page.getPackageFragment(), "package", 
                page.isIncludePreamble(), 
                "Package package {\n    name='" + 
                         page.getPackageFragment().getElementName() + 
                         "';\n    shared=" + page.isShared() + ";\n}",
                         getShell());
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
            page= new NewUnitWizardPage("New Ceylon Package",
                    "Create a Ceylon package with a package descriptor.",
                    "package", CEYLON_NEW_PACKAGE, true) {
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
                @Override
                boolean isComplete() {
                    return super.isComplete() && 
                            !getPackageFragment().isDefaultPackage();
                }
                @Override
                boolean packageNameIsLegal(String packageName) {
                    return !packageName.isEmpty() && 
                            super.packageNameIsLegal(packageName);
                }
                @Override
                boolean unitIsNameLegal(String unitName) {
                    return true;
                }
                @Override
                String[] getFileNames() {
                    return new String[] { "package" };
                }
            };
            page.init(workbench, selection);
        }
        addPage(page);
    }
}
