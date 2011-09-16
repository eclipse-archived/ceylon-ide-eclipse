package com.redhat.ceylon.eclipse.imp.search;

import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getReferencedDeclaration;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.findNode;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.util.Util;

abstract class AbstractFindAction extends Action {
    
    private final UniversalEditor editor;
    private Declaration declaration;
    
    AbstractFindAction(String text, IEditorPart editor) {
        super(text);
        if (editor instanceof UniversalEditor) {
            this.editor = (UniversalEditor) editor;
            declaration = getReferencedDeclaration(getSelectedNode());
            setEnabled(isValidSelection(declaration));
        }
        else {
            this.editor = null;
            setEnabled(false);
        }
    }
    
    @Override
    public void run() {
        if (isValidSelection(declaration)) {
            NewSearchUI.runQueryInBackground(createSearchQuery(declaration, 
                    Util.getProject(editor.getEditorInput())));
        }
        else {
            MessageDialog.openWarning(editor.getEditorSite().getShell(), 
                    "Ceylon Find Error", "No declaration name selected");
        }
    }
    
    private Node getSelectedNode() {
        CeylonParseController cpc = (CeylonParseController) editor.getParseController();
        if (cpc.getRootNode()==null) {
            return null;
        }
        else {
            return findNode(cpc.getRootNode(), editor.getSelection().x, 
                editor.getSelection().x+editor.getSelection().y);
        }
    }

    abstract boolean isValidSelection(Declaration selectedDeclaration);

    public abstract FindSearchQuery createSearchQuery(Declaration declaration, IProject project);
    
}
