package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.AddParameterListProposal.addParameterListProposal;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getFile;
import static com.redhat.ceylon.eclipse.util.Nodes.findDeclarationWithBody;
import static com.redhat.ceylon.eclipse.util.Nodes.findNode;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class AddParameterListHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) 
            throws ExecutionException {
        IEditorPart editor = getCurrentEditor();
        if (editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor) editor;
            Tree.CompilationUnit rootNode = 
                    ce.getParseController().getRootNode();
            if (rootNode!=null) {
                IRegion selection = ce.getSelection();
                int start = selection.getOffset();
                int end = start + selection.getLength();
                Node node = findDeclarationWithBody(rootNode, 
                        findNode(rootNode, ce.getParseController().getTokens(), start, end));
                List<ICompletionProposal> list = 
                        new ArrayList<ICompletionProposal>();
                addParameterListProposal(getFile(editor), list, node, rootNode, true);
                if (!list.isEmpty()) {
                    IDocument doc = ce.getCeylonSourceViewer().getDocument();
                    ICompletionProposal proposal = list.get(0);
                    proposal.apply(doc);
                    Point point = proposal.getSelection(doc);
                    ce.getSelectionProvider().setSelection(new TextSelection(point.x, point.y));
                }
            }
        }
        return null;
    }

}
