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

import antlr.Token;

import com.redhat.ceylon.compiler.typechecker.tree.CustomTree;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AssignOp;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.InitializerExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Return;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SpecifierExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SpecifierOrInitializerExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ThenOp;
import com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy;
import com.redhat.ceylon.eclipse.code.editor.Util;

class ConvertThenElseToIfElse extends ChangeCorrectionProposal {
    
    final int offset; 
    final IFile file;
    
    ConvertThenElseToIfElse(int offset, IFile file, TextChange change) {
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
    			Node node) {
    	try {
    		String action;
    		String prefix = null;
    		Node operation;
    		if (node instanceof Tree.Return) {
    			Tree.Return returnOp = (Return) node;
    			action = "return ";
    			if (returnOp.getExpression() == null) {
    				return;
    			}
    			operation = returnOp.getExpression().getTerm();
    			
    		} else if (node instanceof Tree.AssignOp) {
    			Tree.AssignOp assignOp = (AssignOp) node;
    			action = getTerm(doc, assignOp.getLeftTerm()) + " := ";
    			operation = assignOp.getRightTerm();
    		} else if (node instanceof CustomTree.AttributeDeclaration) {
    			CustomTree.AttributeDeclaration attrDecl = (CustomTree.AttributeDeclaration) node;
    			String identifier = getTerm(doc, attrDecl.getIdentifier());
				String annotations = "";
				if (!attrDecl.getAnnotationList().getAnnotations().isEmpty()) {
					annotations = getTerm(doc, attrDecl.getAnnotationList()) + " ";
				}
				prefix = annotations + getTerm(doc, attrDecl.getType()) + " " + identifier + ";";
    			action = identifier + " " + getToken(attrDecl.getSpecifierOrInitializerExpression()) + " ";
    			operation = attrDecl.getSpecifierOrInitializerExpression().getExpression().getTerm();
    		} else {
    			return;
    		}
    		
    		String ifExpression;
    		String elseTerm;
    		String thenTerm;
			
			if (operation instanceof Tree.DefaultOp) {
    			Tree.DefaultOp defaultOp = (Tree.DefaultOp) operation;
    			if (defaultOp.getLeftTerm() instanceof Tree.ThenOp) {
    				Tree.ThenOp thenOp = (ThenOp) defaultOp.getLeftTerm();
    				thenTerm = getTerm(doc, thenOp.getRightTerm());
    				ifExpression = getTerm(doc, thenOp.getLeftTerm());
    			} else {
    				thenTerm = getTerm(doc, defaultOp.getLeftTerm());
    				ifExpression = "exists " + thenTerm;
    			}
    			elseTerm = getTerm(doc, defaultOp.getRightTerm());
    		} else if (operation instanceof Tree.ThenOp) {
    			Tree.ThenOp thenOp = (ThenOp) operation;
    			thenTerm = getTerm(doc, thenOp.getRightTerm());
   			 	ifExpression = getTerm(doc, thenOp.getLeftTerm());
   			 	elseTerm = "null";
    		} else {
    			return;
    		}

			// return test then x else y;
			// return test then x;
			// return obj else default;
			// Object o = test then x else y;
			// Object o = test else default;
			// Object o = x else default;

			String baseIndent = CeylonQuickFixAssistant.getIndent(node, doc);
			String test;
			test = removeEnclosingParentesis(ifExpression);
			String then = thenTerm;
			String else_ = elseTerm;
			StringBuilder replace = buildIfElse(baseIndent, prefix, test, action, then,else_);

			TextChange change = new DocumentChange("Convert To Getter", doc);
			change.setEdit(new MultiTextEdit());
			int length;
			int stopIndex;
			if (doc.getChar(node.getStopIndex()) == ';') {
				stopIndex = node.getStopIndex();
			} else { //Workaround since the AssignOp length does not include the semi-colon 
				stopIndex = node.getStopIndex() + 1;
				while (stopIndex < doc.getLength() && (doc.getChar(stopIndex)) != ';') {
					if (! isWhitespace(doc.getChar(stopIndex))) {
						return; //
					}
					stopIndex++;
				}
			}
			length = stopIndex - node.getStartIndex() + 1;
			change.addEdit(new ReplaceEdit(node.getStartIndex(), length, replace.toString()));
			proposals.add(new ConvertThenElseToIfElse(node.getStartIndex(), file, change));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
    }

	private static String getToken(
			SpecifierOrInitializerExpression token) {
		if (token instanceof SpecifierExpression) {
			return "=";
		} else {
			return ":=";
		}
	}

	private static StringBuilder buildIfElse(String baseIndent, String prefix, String test,
			String action, String then, String else_) {
		String indent = CeylonAutoEditStrategy.getDefaultIndent();
		StringBuilder replace = new StringBuilder();
		replace.append(prefix != null ? prefix + "\n" + baseIndent : "")
				.append("if (").append(test).append(") {\n")
				.append(baseIndent).append(indent).append(action).append(then).append(";\n")
				.append(baseIndent).append("}\n")
				.append(baseIndent).append("else {\n")
				.append(baseIndent).append(indent).append(action).append(else_).append(";\n")
				.append(baseIndent).append("}");
		return replace;
	}
    
    private static String removeEnclosingParentesis(String s) {
    	if (s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')') {
    		return s.substring(1, s.length() - 1);
    	}
    	return s;
    }
    
    private static String getTerm(IDocument doc, Node node) throws BadLocationException {
    	return doc.get(node.getStartIndex(), node.getStopIndex() - node.getStartIndex() + 1);
    }
    
    private static boolean isWhitespace(char c) {
    	return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
    }
      
   
}