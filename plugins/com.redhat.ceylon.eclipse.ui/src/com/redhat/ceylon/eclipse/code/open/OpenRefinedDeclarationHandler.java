package com.redhat.ceylon.eclipse.code.open;

import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getSelection;
import static com.redhat.ceylon.eclipse.util.Nodes.findDeclaration;
import static com.redhat.ceylon.eclipse.util.Nodes.findNode;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.Navigation;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class OpenRefinedDeclarationHandler extends AbstractHandler {
    
    private Tree.Declaration getSelectedNode(ITextSelection textSel) {
        CeylonEditor editor = (CeylonEditor) getCurrentEditor();
        CeylonParseController pc = editor.getParseController();
        if (pc==null) {
            return null;
        }
        else {
            Tree.CompilationUnit ast = pc.getRootNode();
            if (ast == null) {
                return null;
            }
            else {
                return findDeclaration(ast, 
                        findNode(ast, textSel.getOffset()));
            }
        }
    }
    
    public boolean isEnabled() {
        IEditorPart editor = getCurrentEditor();
        if (super.isEnabled() && editor instanceof CeylonEditor) {
            Tree.Declaration decNode = 
                    getSelectedNode(getSelection((ITextEditor) editor));
            if (decNode==null) {
                return false;
            }
            else {
                Declaration dec = decNode.getDeclarationModel();
                return !dec.getRefinedDeclaration().equals(dec);
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
            Tree.Declaration decNode = 
                    getSelectedNode(getSelection((ITextEditor) ce));
            if (decNode!=null) {
                Declaration refined = 
                        decNode.getDeclarationModel().getRefinedDeclaration();
                Navigation.gotoDeclaration(refined, ce);
            }
        }
        return null;
    }
        
}