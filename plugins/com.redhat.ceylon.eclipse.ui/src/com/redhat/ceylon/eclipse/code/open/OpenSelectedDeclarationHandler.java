package com.redhat.ceylon.eclipse.code.open;

import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getSelection;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.gotoNode;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedModel;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedNode;
import static com.redhat.ceylon.eclipse.code.resolve.JavaHyperlinkDetector.gotoJavaNode;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class OpenSelectedDeclarationHandler extends AbstractHandler {
    
    private Node getSelectedNode(ITextSelection textSel) {
        CeylonEditor editor = (CeylonEditor) getCurrentEditor();
        CeylonParseController pc= editor.getParseController();
        if (pc==null) {
            return null;
        }
        else {
            Tree.CompilationUnit ast= pc.getRootNode();
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
        return super.isEnabled() && editor instanceof CeylonEditor &&
                getReferencedModel(getSelectedNode(getSelection((ITextEditor) editor)))!=null;
    }
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart editor = getCurrentEditor();
        if (editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor) editor;
            Node node = getSelectedNode(getSelection((ITextEditor)ce));
            Referenceable r = getReferencedModel(node);
            if (r!=null) {
                CeylonParseController cpc = ce.getParseController();
                Node refNode = getReferencedNode(r, cpc);
                IProject project = cpc.getProject();
                if (refNode!=null) {
                    gotoNode(refNode, project, cpc.getTypeChecker());
                }
                else if (r instanceof Declaration) {
                    gotoJavaNode(((Declaration)r), node, project);
                }
            }
        }
        return null;
    }
        
}