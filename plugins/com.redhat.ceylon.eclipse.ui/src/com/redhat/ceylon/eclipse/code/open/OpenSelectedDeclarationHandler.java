package com.redhat.ceylon.eclipse.code.open;

import static com.redhat.ceylon.eclipse.code.editor.Util.getCurrentEditor;
import static com.redhat.ceylon.eclipse.code.editor.Util.getSelection;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.gotoNode;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedDeclarationOrPackage;
import static com.redhat.ceylon.eclipse.code.resolve.CeylonReferenceResolver.getReferencedNode;
import static com.redhat.ceylon.eclipse.code.resolve.JavaHyperlinkDetector.gotoJavaNode;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
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
        		getReferencedDeclarationOrPackage(getSelectedNode(getSelection((ITextEditor) editor)))!=null;
    }
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart editor = getCurrentEditor();
        if (editor instanceof CeylonEditor) {
            Node node = getSelectedNode(getSelection((ITextEditor) editor));
			Declaration dec = getReferencedDeclarationOrPackage(node);
			if (dec!=null) {
			    CeylonParseController cpc = ((CeylonEditor) editor).getParseController();
				Node refNode = getReferencedNode(dec, cpc);
			    if (refNode!=null) {
			    	gotoNode(refNode, cpc.getProject(), cpc.getTypeChecker());
			    }
			    else {
			    	gotoJavaNode(dec, node, cpc);
			    }
			}
        }
        return null;
    }
        
}