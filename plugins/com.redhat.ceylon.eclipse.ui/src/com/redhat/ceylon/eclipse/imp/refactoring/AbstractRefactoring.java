package com.redhat.ceylon.eclipse.imp.refactoring;

import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.getTokenIterator;

import java.util.Iterator;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.imp.editor.Util;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

public abstract class AbstractRefactoring extends Refactoring {
    
    IProject project;
    IFile sourceFile;
    Node node;
    Tree.CompilationUnit rootNode;
    CommonTokenStream tokenStream;
   
    /*public AbstractRefactoring(IQuickFixInvocationContext context) {
        sourceFile = context.getModel().getFile();
        project = sourceFile.getProject();
        PhasedUnit pu = CeylonBuilder.getPhasedUnit(sourceFile);
        rootNode = pu.getCompilationUnit();
        tokenStream = pu.getTokenStream();
        node = CeylonSourcePositionLocator.findNode(rootNode, context.getOffset(),
                context.getOffset()+context.getLength());
    }*/
    
    public AbstractRefactoring(ITextEditor editor) {
        project = Util.getProject(editor);
        if (editor instanceof CeylonEditor) {
            CeylonParseController cpc = ((CeylonEditor) editor).getParseController();
            tokenStream = cpc.getTokenStream();
            rootNode = cpc.getRootNode();
            IEditorInput input = editor.getEditorInput();
            if (rootNode!=null && input instanceof IFileEditorInput) {
                sourceFile = Util.getFile(input);
                node = findNode(rootNode, 
                    (ITextSelection) editor.getSelectionProvider().getSelection());
            }
        }
    }
    
    abstract boolean isEnabled();
    
    String guessName() {
        Node identifyingNode = node;
        if (identifyingNode instanceof Tree.Expression) {
            identifyingNode = ((Tree.Expression) identifyingNode).getTerm();
        }
        if (identifyingNode instanceof Tree.InvocationExpression) {
            identifyingNode = ((Tree.InvocationExpression) identifyingNode).getPrimary();
        }
        if (identifyingNode instanceof Tree.StaticMemberOrTypeExpression) {
            String id = ((Tree.StaticMemberOrTypeExpression) identifyingNode).getIdentifier().getText();
            return Character.toLowerCase(id.charAt(0)) + 
                    id.substring(1);
        }
        else {
            return "temp";
        }
    }

    String toString(Tree.Term term) {
        return toString(term, tokenStream);
    }
    
    static String toString(Tree.Term term, CommonTokenStream tokenStream) {
        Integer start = term.getStartIndex();
        int length = term.getStopIndex()-start+1;
        Region region = new Region(start, length);
        StringBuilder exp = new StringBuilder();
        for (Iterator<Token> ti = getTokenIterator(tokenStream, region); 
                ti.hasNext();) {
            exp.append(ti.next().getText());
        }
        return exp.toString();
    }

}
