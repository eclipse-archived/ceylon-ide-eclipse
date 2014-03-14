package com.redhat.ceylon.eclipse.code.wizard;

import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.eclipse.ui.ide.undo.WorkspaceUndoUtil.getUIInfoAdapter;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;
import org.eclipse.ui.wizards.IWizardDescriptor;

class WizardUtil {

    static boolean runOperation(IUndoableOperation op, 
            IWizardContainer container) {
        
        class RunOperation implements IRunnableWithProgress {
            
            private final Shell shell;
            private final IUndoableOperation op;
            private boolean created;
    
            protected RunOperation(IUndoableOperation op, Shell shell) {
                this.op = op;
                this.shell = shell;
            }
    
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException,
                    InterruptedException {
                //TODO: should we do this in a WorkspaceModifyOperation?
                try {
                    IWorkbenchOperationSupport os = getWorkbench().getOperationSupport();
                    op.addContext(os.getUndoContext());
                    IStatus status = os.getOperationHistory()
                            .execute(op, monitor, getUIInfoAdapter(shell));
                    created=status.isOK();
                } 
                catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        
        try {
            RunOperation runnable = new RunOperation(op, container.getShell());
            container.run(true, true, runnable);
            return runnable.created;
        } 
        catch (InvocationTargetException e) {
            e.printStackTrace();
            return false;
        } 
        catch (InterruptedException e) {
            return false;
        }
    }

    static void startWizard(IWorkbench wb, IWizardDescriptor descriptor)
            throws CoreException {
        ISelection selection = wb.getActiveWorkbenchWindow()
                .getSelectionService().getSelection();
        if (!(selection instanceof IStructuredSelection)) {
            selection = null;
        }
        IWorkbenchWizard wizard = descriptor.createWizard();
        wizard.init(wb, (IStructuredSelection) selection);
        WizardDialog wd = new WizardDialog(Display.getCurrent().getActiveShell(), 
                wizard);
        wd.setTitle(wizard.getWindowTitle());
        wd.open();
    }

}
