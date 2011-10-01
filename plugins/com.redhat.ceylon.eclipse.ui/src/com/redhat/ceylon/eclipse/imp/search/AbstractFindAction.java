package com.redhat.ceylon.eclipse.imp.search;

import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getReferencedDeclaration;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.findNode;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.imp.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.imp.editor.Util;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

abstract class AbstractFindAction extends Action {
    
    private final IEditorPart editor;
    protected Declaration declaration;
    protected IProject project;
    
    AbstractFindAction(String text, IEditorPart editor) {
        super(text);
        this.editor = editor;
        project = editor==null ? null : Util.getProject(editor);
        if (editor instanceof CeylonEditor) {
            declaration = getReferencedDeclaration(getSelectedNode((CeylonEditor) editor));
            setEnabled(isValidSelection());
        }
        else {
            setEnabled(false);
        }
    }
    
    @Override
    public void run() {
        if (isValidSelection()) {
            NewSearchUI.runQueryInBackground(createSearchQuery());
        }
        else {
            MessageDialog.openWarning(editor.getEditorSite().getShell(), 
                    "Ceylon Find Error", 
                    "No appropriate declaration name selected");
        }
    }
    
    private static Node getSelectedNode(CeylonEditor editor) {
        CeylonParseController cpc = editor.getParseController();
        return cpc.getRootNode()==null ? null : 
            findNode(cpc.getRootNode(), 
                (ITextSelection) editor.getSelectionProvider().getSelection());
    }

    abstract boolean isValidSelection();

    public abstract FindSearchQuery createSearchQuery();
    
}
