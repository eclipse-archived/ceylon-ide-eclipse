package com.redhat.ceylon.eclipse.code.wizard;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.gotoLocation;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.eclipse.ui.ide.undo.WorkspaceUndoUtil.getUIInfoAdapter;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class NewPackageWizard extends Wizard implements INewWizard {

    private IStructuredSelection selection;
    private NewUnitWizardPage page;
    private IWorkbench workbench;
    private boolean created=false;
    
    public IPackageFragment getPackageFragment() {
        return page.getPackageFragment();
    }
    
    public IPackageFragmentRoot getSourceFolder() {
        return page.getSourceDir();
    }
    
    public boolean isCreated() {
        return created;
    }
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        this.workbench=workbench;
    }
    
    @Override
    public boolean performFinish() {
        CeylonPlugin.getInstance().getDialogSettings().put("sharedPackage", page.isShared());

        final CreateCeylonSourceFileOperation op = 
                new CreateCeylonSourceFileOperation("New Ceylon Package",
                        page.getSourceDir(), 
                        page.getPackageFragment(), "package", 
                        page.isIncludePreamble(), 
                        (page.isShared() ? "shared " : "") + 
                                "package " + page.getPackageFragment().getElementName() + ";\n",
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
                        IStatus status = os.getOperationHistory()
                                .execute(op, monitor, getUIInfoAdapter(getShell()));
                        created=status.isOK();
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
            page= new NewPackageWizardPage();
            page.init(workbench, selection);
        }
        addPage(page);
    }
}
