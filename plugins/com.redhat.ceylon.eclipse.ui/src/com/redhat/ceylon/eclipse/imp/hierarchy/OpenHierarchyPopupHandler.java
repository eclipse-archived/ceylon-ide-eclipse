package com.redhat.ceylon.eclipse.imp.hierarchy;

import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getReferencedDeclaration;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.findNode;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.imp.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.imp.editor.Util;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

public class OpenHierarchyPopupHandler extends AbstractHandler {
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart editor = Util.getCurrentEditor();
        Declaration declaration;
        if (editor instanceof CeylonEditor) {
            declaration = getReferencedDeclaration(getSelectedNode((CeylonEditor) editor));
        }
        else {
            return null;
        }
        HierarchyPopup hp  = new HierarchyPopup(declaration, 
                editor.getEditorSite().getShell());
        hp.open();
        return null;
    }
    
    //TODO: this is a copy/paste from AbstractFindAction
    private static Node getSelectedNode(CeylonEditor editor) {
        CeylonParseController cpc = editor.getParseController();
        return cpc.getRootNode()==null ? null : 
            findNode(cpc.getRootNode(), 
                (ITextSelection) editor.getSelectionProvider().getSelection());
    }

}
