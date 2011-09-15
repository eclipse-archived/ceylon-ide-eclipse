package com.redhat.ceylon.eclipse.imp.search;

import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getReferencedDeclaration;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.widgets.Shell;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.Util;

abstract class FindAction extends Action {
    
    private final UniversalEditor editor;

    FindAction(String text, UniversalEditor editor) {
        super(text);
        this.editor = editor;
    }
    
    @Override
    public void run() {
        CeylonParseController cpc = (CeylonParseController) editor.getParseController();
        Node node = CeylonSourcePositionLocator.findNode(cpc.getRootNode(), 
                editor.getSelection().x, editor.getSelection().x+editor.getSelection().y);
        if (node!=null) {
            Declaration dec = getReferencedDeclaration(node);
            if (dec!=null) {
                NewSearchUI.runQueryInBackground(createSearchQuery(dec, 
                        Util.getProject(editor.getEditorInput())));
                return;
            }
        }
        Shell shell= CeylonPlugin.getInstance().getWorkbench()
                .getActiveWorkbenchWindow().getShell();
        MessageDialog.openWarning(shell, "Ceylon Find Error", 
                "No declaration name selected");
    }

    public abstract FindSearchQuery createSearchQuery(Declaration declaration, IProject project);
    
}
