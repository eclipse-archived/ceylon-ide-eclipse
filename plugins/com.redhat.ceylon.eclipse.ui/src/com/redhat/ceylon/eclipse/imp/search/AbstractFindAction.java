package com.redhat.ceylon.eclipse.imp.search;

import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getReferencedDeclaration;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.findNode;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.widgets.Shell;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.Util;

abstract class AbstractFindAction extends Action {
    
    private final UniversalEditor editor;
    private Declaration declaration;
    
    AbstractFindAction(String text, UniversalEditor editor) {
        super(text);
        this.editor = editor;
        if (editor==null) {
            setEnabled(false);
        }
        else {
            declaration = getReferencedDeclaration(getSelectedNode());
            setEnabled(isValidSelection(declaration));
        }
    }
    
    @Override
    public void run() {
        if (isValidSelection(declaration)) {
            NewSearchUI.runQueryInBackground(createSearchQuery(declaration, 
                    Util.getProject(editor.getEditorInput())));
            return;
        }
        Shell shell= CeylonPlugin.getInstance().getWorkbench()
                .getActiveWorkbenchWindow().getShell();
        MessageDialog.openWarning(shell, "Ceylon Find Error", 
                "No declaration name selected");
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
