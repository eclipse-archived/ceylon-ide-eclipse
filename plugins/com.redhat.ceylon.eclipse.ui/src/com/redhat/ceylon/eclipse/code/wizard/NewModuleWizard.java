package com.redhat.ceylon.eclipse.code.wizard;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.gotoLocation;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_NEW_MODULE;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

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
                            "`.\"\nvoid run() {\n    \n}", getShell());
            op.run(monitor);
            result = op.getResult();

            new FileCreationOp(page.getSourceDir(), 
                    page.getPackageFragment(), "module", 
                    page.isIncludePreamble(), 
                    "module " + page.getPackageFragment().getElementName() + " '" + version + "' {} \n", 
                    getShell())
                .run(monitor);

            new FileCreationOp(page.getSourceDir(), 
                    page.getPackageFragment(), "package", 
                    page.isIncludePreamble(), 
                    (page.isShared() ? "shared " : "") + "package " + page.getPackageFragment().getElementName() + ";\n",
                    getShell())
                .run(monitor);
        }
        public IFile getResult() {
            return result;
        }
    }

    private IStructuredSelection selection;
    private NewUnitWizardPage page;
    private IWorkbench workbench;
    private String version="1.0.0";
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        this.workbench = workbench;
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
        BasicNewResourceWizard.selectAndReveal(op.getResult(), 
                workbench.getActiveWorkbenchWindow());
        gotoLocation(op.getResult().getFullPath(), 0);
        return true;
    }
    
    @Override
    public void addPages() {
        super.addPages();
        if (page == null) {
            page= new NewUnitWizardPage("New Ceylon Module",
                    "Create a runnable Ceylon module with module and package descriptors.",
                    "run", CEYLON_NEW_MODULE, true) {
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
                    createVersionField(composite);
                    createSharedField(composite);
                    createNameField(composite);
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
                String getIllegalPackageNameMessage() {
                    return "Please enter a legal module name.";
                }
                @Override
                String[] getFileNames() {
                    return new String[] { "module", "package", getUnitName() };
                }
                void createVersionField(Composite composite) {
                    Label versionLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
                    versionLabel.setText("Module version:");
                    GridData lgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
                    lgd.horizontalSpan = 1;
                    versionLabel.setLayoutData(lgd);

                    final Text versionName = new Text(composite, SWT.SINGLE | SWT.BORDER);
                    GridData ngd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
                    ngd.horizontalSpan = 2;
                    ngd.grabExcessHorizontalSpace = true;
                    versionName.setLayoutData(ngd);
                    versionName.setText(version);
                    versionName.addModifyListener(new ModifyListener() {
                        @Override
                        public void modifyText(ModifyEvent e) {
                        	version = versionName.getText();
                            setPageComplete(isComplete());
                        }
                    });
                    new Label(composite, SWT.NONE);
                }                
            };
            page.init(workbench, selection);
        }
        addPage(page);
    }
}
