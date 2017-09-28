package org.eclipse.ceylon.ide.eclipse.code.open;

import static org.eclipse.ceylon.ide.eclipse.code.editor.Navigation.gotoDeclaration;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getSelection;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.findNode;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getReferencedDeclaration;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;

import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Referenceable;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;

public class OpenRefinedDeclarationHandler extends AbstractHandler {
    
    private Node getSelectedNode(ITextSelection textSel) {
        CeylonEditor editor = (CeylonEditor) getCurrentEditor();
        CeylonParseController pc = editor.getParseController();
        if (pc==null) {
            return null;
        }
        else {
            Tree.CompilationUnit ast = pc.getLastCompilationUnit();
            if (ast == null) {
                return null;
            }
            else {
                return findNode(ast, textSel.getOffset());
            }
        }
    }
    
    public boolean isEnabled() {
        IEditorPart editor = getCurrentEditor();
        if (super.isEnabled() && editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor) editor;
            Node selectedNode = getSelectedNode(getSelection(ce));
            Referenceable dec = getReferencedDeclaration(selectedNode);
            if (dec instanceof Declaration) {
                Declaration refinedDeclaration = 
                        ((Declaration) dec).getRefinedDeclaration();
                return !dec.equals(refinedDeclaration);
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart editor = getCurrentEditor();
        if (editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor) editor;
            Node selectedNode = getSelectedNode(getSelection(ce));
            Referenceable dec = getReferencedDeclaration(selectedNode);
            if (dec instanceof Declaration) {
                Declaration refinedDeclaration = 
                        ((Declaration) dec).getRefinedDeclaration();
                gotoDeclaration(refinedDeclaration);
            }
        }
        return null;
    }
        
}