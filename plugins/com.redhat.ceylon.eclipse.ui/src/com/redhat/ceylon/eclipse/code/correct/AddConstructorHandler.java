package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.correctJ2C;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.util.Nodes.findDeclarationWithBody;
import static com.redhat.ceylon.eclipse.util.Nodes.findNode;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.ide.common.correct.QuickFixData;
import com.redhat.ceylon.ide.common.correct.addConstructorQuickFix_;

public class AddConstructorHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) 
            throws ExecutionException {
        IEditorPart editor = getCurrentEditor();
        if (editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor) editor;
            Tree.CompilationUnit rootNode = 
                    ce.getParseController()
                      .getTypecheckedRootNode();
            if (rootNode!=null) {
                IRegion selection = ce.getSelection();
                int start = selection.getOffset();
                int end = start + selection.getLength();
                List<CommonToken> tokens = 
                        ce.getParseController()
                          .getTokens();
                Node node = 
                        findDeclarationWithBody(rootNode, 
                                findNode(rootNode, tokens, 
                                        start, end));
                List<ICompletionProposal> list = 
                        new ArrayList<ICompletionProposal>();
                IDocument doc = 
                        ce.getCeylonSourceViewer()
                          .getDocument();
                IProject project = EditorUtil.getProject(ce.getEditorInput());
                QuickFixData data = correctJ2C().newData(rootNode, node, list,
                        ce, project, doc);
                
                addConstructorQuickFix_.get_().addConstructorProposal(data);

                if (!list.isEmpty()) {
                    ICompletionProposal proposal = list.get(0);
                    proposal.apply(doc);
                    Point point = proposal.getSelection(doc);
                    ce.getSelectionProvider()
                      .setSelection(new TextSelection(point.x, point.y));
                }
            }
        }
        return null;
    }

}
