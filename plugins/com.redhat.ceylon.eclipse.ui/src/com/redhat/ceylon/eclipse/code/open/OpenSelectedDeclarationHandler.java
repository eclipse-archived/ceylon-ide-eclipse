package com.redhat.ceylon.eclipse.code.open;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoDeclaration;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getSelection;
import static com.redhat.ceylon.eclipse.util.Nodes.findNode;
import static com.redhat.ceylon.eclipse.util.Nodes.getReferencedModel;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class OpenSelectedDeclarationHandler extends AbstractHandler {
    
    private Node getSelectedNode(
            ITextSelection textSel, CeylonEditor editor) {
        CeylonParseController controller = 
                editor.getParseController();
        if (controller==null) {
            return null;
        }
        else {
            Tree.CompilationUnit rootNode = 
                    controller.getRootNode();
            if (rootNode == null) {
                return null;
            }
            else {
                return findNode(rootNode,
                        controller.getTokens(),
                        textSel.getOffset(),
                        textSel.getOffset() + 
                        textSel.getLength());
            }
        }
    }
    
    public boolean isEnabled() {
        IEditorPart editor = getCurrentEditor();
        if (super.isEnabled() 
                && editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor) editor;
            Node selectedNode = 
                    getSelectedNode(getSelection(ce), ce);
            return getReferencedModel(selectedNode)!=null;
        }
        else {
            return false;
        }
                
    }
    
    @Override
    public Object execute(ExecutionEvent event) 
            throws ExecutionException {
        IEditorPart editor = getCurrentEditor();
        if (editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor) editor;
            Node selectedNode = 
                    getSelectedNode(getSelection(ce), ce);
            Referenceable ref = 
                    getReferencedModel(selectedNode);
            if (ref!=null) {
                gotoDeclaration(ref);
            }
        }
        return null;
    }
        
}