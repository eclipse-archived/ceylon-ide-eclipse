package com.redhat.ceylon.eclipse.code.move;

import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.eclipse.ui.ide.undo.WorkspaceUndoUtil.getUIInfoAdapter;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

final class TextChangeOperation extends AbstractOperation {
    
    private final TextChange tc;
    private Change undo;
    private Change redo;

    TextChangeOperation(TextChange tc) {
        super(tc.getName());
        this.tc = tc;
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info)
            throws ExecutionException {
        try {
            redo = undo.perform(monitor);
            return Status.OK_STATUS;
        }
        catch (CoreException e) {
            throw new ExecutionException(e.getMessage());
        }
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info)
            throws ExecutionException {
        try {
            redo.perform(monitor);
        } 
        catch (CoreException e) {
            throw new ExecutionException(e.getMessage());
        }
        return Status.OK_STATUS;
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info)
            throws ExecutionException {
        try {
            undo = tc.perform(monitor);
            return Status.OK_STATUS;
        } 
        catch (CoreException e) {
            throw new ExecutionException(e.getMessage());
        }
    }

    void runOperation(CeylonEditor editor)
            throws ExecutionException {
        IWorkbenchOperationSupport os = getWorkbench().getOperationSupport();
        addContext(os.getUndoContext());
        os.getOperationHistory()
                .execute(this, new NullProgressMonitor(), 
                        getUIInfoAdapter(editor.getSite().getShell()));
    }
}