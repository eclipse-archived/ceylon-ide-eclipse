package com.redhat.ceylon.eclipse.imp.wizard;

import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.gotoLocation;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_NEW_MODULE;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewModuleWizard extends Wizard implements INewWizard {
    
    private final class ModuleCreationOp implements IRunnableWithProgress {
        private IFile result;
        @Override
        public void run(IProgressMonitor monitor) 
                throws InvocationTargetException, InterruptedException {
            FileCreationOp op = new FileCreationOp(page.getSourceDir(), 
                    page.getPackageFragment(), page.getUnitName(), 
                    page.isIncludePreamble(), "doc \"Run the module `" +
                            page.getPackageFragment().getElementName() +
                            "`.\"\nvoid run() {\n    \n}");
            op.run(monitor);
            result = op.getResult();
            new FileCreationOp(page.getSourceDir(), 
                    page.getPackageFragment(), "module", 
                    page.isIncludePreamble(), 
                    "Module module {\n    name='" + 
                            page.getPackageFragment().getElementName() + 
                            "';\n    version='1.0.0';\n}")
                .run(monitor);
            new FileCreationOp(page.getSourceDir(), 
                    page.getPackageFragment(), "package", 
                    page.isIncludePreamble(), 
                    "Package package {\n    name='" + 
                             page.getPackageFragment().getElementName() + 
                             "';\n    shared=" + page.isShared() + ";\n}")
                .run(monitor);
        }
        public IFile getResult() {
            return result;
        }
    }

    private IStructuredSelection selection;
    private NewUnitWizardPage page;
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
    }
    
    @Override
    public boolean performFinish() {
        ModuleCreationOp op = new ModuleCreationOp();
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
            page= new NewUnitWizardPage("New Ceylon Module",
                    "Create a runnable Ceylon module with module and package descriptors.",
                    "run", CEYLON_NEW_MODULE) {
                @Override
                String getCompilationUnitLabel() {
                    return "Runnable compilation unit: ";
                }
                @Override
                String getPackageLabel() {
                    return "Module name: ";
                }
                @Override
        		String getSharedPackageLabel() {
        			return "Create module with shared root package"; // (visible to other modules)
        		}
                @Override
            	void createControls(Composite composite) {
            		Text name = createPackageField(composite);
                    createSharedField(composite);
                    createNameField(composite);
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
