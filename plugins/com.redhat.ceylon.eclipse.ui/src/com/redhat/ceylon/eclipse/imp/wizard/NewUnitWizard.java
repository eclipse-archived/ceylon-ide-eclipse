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
    private NewSubtypeWizardPage pageOne;
    private String defaultUnitName="";
    private String contents="";
    private String title = "New Ceylon Unit";
    private String description = "Create a new Ceylon compilation unit that will contain Ceylon source.";
    
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
        if (contents.contains("$className")) {
            contents=contents.replace("$className", pageOne.getClassName());
        }
        FileCreationOp op = new FileCreationOp(page.getSourceDir(), 
                page.getPackageFragment(), page.getUnitName(), 
                page.isIncludePreamble(), contents, getShell());
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
        if (contents.contains("$className")) {
            if (pageOne==null) {
                pageOne = new NewSubtypeWizardPage(title, 
                        defaultUnitName);
            }
            addPage(pageOne);
        }
        if (page==null) {
            page = new NewUnitWizardPage(title,
                    description, defaultUnitName, 
                    CEYLON_NEW_FILE, !"".equals(contents));
            page.init(workbench, selection);
        }
        addPage(page);
    }
    
    public void setDefaultUnitName(String defaultUnitName) {
        this.defaultUnitName = defaultUnitName;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    /*public void setSelection(IStructuredSelection selection) {
        this.selection = selection;
    }*/
    
    public void setContents(String contents) {
        this.contents = contents;
    }
    
    public static void open(String def, IFile file, String unitName, 
            String title, String description) {
        IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry()
                .findWizard("com.redhat.ceylon.eclipse.ui.newUnitWizard");
        if (descriptor!=null) {
            try {
                NewUnitWizard wizard = (NewUnitWizard) descriptor.createWizard();
                wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(file));
                wizard.setDefaultUnitName(unitName);
                wizard.setContents(def);
                wizard.setTitle(title);
                wizard.setDescription(description);
                WizardDialog wd = new WizardDialog(Display.getCurrent().getActiveShell(), 
                        wizard);
                wd.setTitle(title);
                wd.open();
            }
            catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }
    
}
