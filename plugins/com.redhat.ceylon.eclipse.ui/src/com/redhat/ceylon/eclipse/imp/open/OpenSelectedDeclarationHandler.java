package com.redhat.ceylon.eclipse.imp.open;

import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getReferencedNode;
import static com.redhat.ceylon.eclipse.imp.editor.Util.getCurrentEditor;
import static com.redhat.ceylon.eclipse.imp.editor.Util.getSelection;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.gotoLocation;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator;

public class OpenSelectedDeclarationHandler extends AbstractHandler {
    
    private Tree.Declaration getSelectionTarget(ITextSelection textSel) {
        CeylonEditor editor = (CeylonEditor) getCurrentEditor();
        CeylonParseController pc= editor.getParseController();
        Tree.CompilationUnit ast= pc.getRootNode();
        if (ast == null) {
            return null;
        }
        else {
            Object sourceNode= findNode(ast, textSel.getOffset());
            if (sourceNode == null) {
                return null;
            }
            else {
                return getReferencedNode(sourceNode, pc);
            }
        }
    }

    private void go(Tree.Declaration dec) {
        CeylonEditor editor = (CeylonEditor) getCurrentEditor();
        CeylonSourcePositionLocator locator= editor.getParseController().getSourcePositionLocator();
        if (dec != null) {
            gotoLocation(locator.getPath(dec), locator.getStartOffset(dec));
        }
    }
    
    public boolean isEnabled() {
        IEditorPart editor = getCurrentEditor();
        return editor instanceof CeylonEditor &&
                getSelectionTarget(getSelection((ITextEditor) editor))!=null;
    }
    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart editor = getCurrentEditor();
        if (editor instanceof CeylonEditor) {
            go(getSelectionTarget(getSelection((ITextEditor) editor)));
        }
        return null;
    }
        
}