package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.editor.Util.getCurrentEditor;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public abstract class AbstractHandler extends org.eclipse.core.commands.AbstractHandler
        implements IObjectActionDelegate {
    
    //TODO: copy/pasted from AbstractFindAction
    public static Node getSelectedNode(CeylonEditor editor) {
        CeylonParseController cpc = editor.getParseController();
        return cpc==null || cpc.getRootNode()==null ? null : 
            findNode(cpc.getRootNode(), 
                (ITextSelection) editor.getSelectionProvider().getSelection());
    }

    @Override
    public boolean isEnabled() {
        IEditorPart editor = getCurrentEditor();
        if (super.isEnabled() && 
                editor instanceof CeylonEditor &&
                editor.getEditorInput() instanceof IFileEditorInput) {
            return isEnabled((CeylonEditor)editor);
        }
        else {
            return false;
        }
    }

    protected abstract boolean isEnabled(CeylonEditor editor);
    
    @Override
    public void run(IAction action) {
    	try {
			execute(null);
		} 
    	catch (ExecutionException e) {
			e.printStackTrace();
		}
    }
    
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    	action.setEnabled(isEnabled());
    }
    
    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {}

}
