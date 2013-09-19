package com.redhat.ceylon.eclipse.code.wizard;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.gotoLocation;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_NEW_FILE;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.eclipse.ui.ide.undo.WorkspaceUndoUtil.getUIInfoAdapter;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

public class NewUnitWizard extends Wizard implements INewWizard {
    
    private IStructuredSelection selection;
    private IWorkbench workbench;

    private NewUnitWizardPage page;
    private NewSubtypeWizardPage pageOne;
    private String defaultUnitName="";
    private String contents="";
    private String title = "New Ceylon Source File";
    private String description = "Create a new Ceylon source file.";
    
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
        final CreateCeylonSourceFileOperation op = new CreateCeylonSourceFileOperation(page.getSourceDir(), 
                page.getPackageFragment(), page.getUnitName(), 
                page.isIncludePreamble(), contents, getShell());
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException,
                        InterruptedException {
                    //TODO: should we do this in a WorkspaceModifyOperation?
                    try {
                        IWorkbenchOperationSupport os = getWorkbench().getOperationSupport();
                        op.addContext(os.getUndoContext());
                        os.getOperationHistory().execute(op, monitor, 
                                getUIInfoAdapter(getShell()));
                    } 
                    catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                }
            });
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
    
    public static boolean open(String def, IFile file, String unitName, 
            String title, String description) {
        IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry()
                .findWizard(PLUGIN_ID + ".newUnitWizard");
        if (descriptor==null) {
            return false;
        }
        else {
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
                return wd.open()!=Window.CANCEL;
            }
            catch (CoreException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
    
}
