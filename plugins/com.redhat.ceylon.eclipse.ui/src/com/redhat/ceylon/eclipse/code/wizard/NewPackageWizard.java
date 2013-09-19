package com.redhat.ceylon.eclipse.code.wizard;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.gotoLocation;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_NEW_PACKAGE;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.eclipse.ui.ide.undo.WorkspaceUndoUtil.getUIInfoAdapter;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;
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
        final CreateCeylonSourceFileOperation op = new CreateCeylonSourceFileOperation("New Ceylon Package",
                page.getSourceDir(), 
                page.getPackageFragment(), "package", 
                page.isIncludePreamble(), 
                (page.isShared() ? "shared " : "") + "package " + page.getPackageFragment().getElementName() + ";\n",
                getShell());
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException,
                        InterruptedException {
                    //TODO: should we do this in a WorkspaceModifyOperation?
                    try {
                        IWorkbenchOperationSupport os = getWorkbench().getOperationSupport();
                        op.addContext(os.getUndoContext());
                        os.getOperationHistory()
                                .execute(op, monitor, getUIInfoAdapter(getShell()));
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
