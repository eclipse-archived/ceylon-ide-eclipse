package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CORRECTION;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.imp.editor.quickfix.ChangeCorrectionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Expression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ThenOp;
import com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy;
import com.redhat.ceylon.eclipse.code.editor.Util;

class ConvertThenElseToIfElse extends ChangeCorrectionProposal {
    
    final int offset; 
    final IFile file;
    
    ConvertThenElseToIfElse(Declaration dec, int offset, IFile file, TextChange change) {
        super("Convert expression to If/Else", change, 10, CORRECTION);
        this.offset=offset;
        this.file=file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset);
    }
    
    static void addConvertToGetterProposal(IDocument doc,
    			Collection<ICompletionProposal> proposals, IFile file,
    			Tree.Return decNode) {
    	Expression returnExp = decNode.getExpression();
        if (returnExp == null || returnExp.getChildren().isEmpty()) {
        	return; //No match
        }
        Node node = returnExp.getChildren().get(0);
        if (! (node instanceof Tree.DefaultOp)) {
        	return;
        }
		Tree.DefaultOp defaultOp = (Tree.DefaultOp) node;
		Term elseTerm = defaultOp.getRightTerm();
		Tree.ThenOp thenOp = (ThenOp) defaultOp.getLeftTerm();
		Term thenTerm = thenOp.getRightTerm();
		Term ifExpression = thenOp.getLeftTerm();
		
		
		TextChange change = new DocumentChange("Convert To Getter", doc);
		change.setEdit(new MultiTextEdit());
		
		String baseIndent = CeylonQuickFixAssistant.getIndent(decNode, doc);
		String indent = CeylonAutoEditStrategy.getDefaultIndent();
		try {
			StringBuilder replace = new StringBuilder();
			replace.append("if (").append(removeEnclosingParentesis(getTerm(doc, ifExpression))).append(") {\n")
					.append(baseIndent).append(indent).append("return ").append(getTerm(doc, thenTerm)).append(";\n")
					.append(baseIndent).append("}\n")
					.append(baseIndent).append("else {\n")
					.append(baseIndent).append(indent).append("return ").append(getTerm(doc, elseTerm)).append(";\n")
					.append(baseIndent).append("}");
			change.addEdit(new ReplaceEdit(decNode.getStartIndex(), decNode.getStopIndex() - decNode.getStartIndex() + 1, replace.toString()));
			proposals.add(new ConvertThenElseToIfElse(decNode.getDeclaration(), decNode.getStartIndex(), file, change));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
    }
    
    private static String removeEnclosingParentesis(String s) {
    	if (s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')') {
    		return s.substring(1, s.length() - 1);
    	}
    	return s;
    }
    
    private static String getTerm(IDocument doc, Term term) throws BadLocationException {
    	return doc.get(term.getStartIndex(), term.getStopIndex() - term.getStartIndex() + 1);
    }
    
   
}