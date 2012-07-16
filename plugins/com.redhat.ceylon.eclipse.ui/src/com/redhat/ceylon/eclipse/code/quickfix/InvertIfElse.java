package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CHANGE;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.imp.editor.quickfix.ChangeCorrectionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Block;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BooleanCondition;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ComparisonOp;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Condition;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.EqualOp;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.EqualityOp;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.IfClause;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.IfStatement;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.LargeAsOp;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.LargerOp;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.NotOp;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SmallAsOp;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SmallerOp;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy;
import com.redhat.ceylon.eclipse.code.editor.Util;

class InvertIfElse extends ChangeCorrectionProposal {
    
    final int offset; 
    final IFile file;
    
    InvertIfElse(int offset, IFile file, TextChange change) {
        super("Invert if-else", change, 10, CHANGE);
        this.offset=offset;
        this.file=file;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset);
    }
    
    static void addReverseIfElseProposal(IDocument doc,
    			Collection<ICompletionProposal> proposals, IFile file,
    			Statement statement) {
    	try {
    		if (! (statement instanceof Tree.IfStatement)) {
    			return;
			}
    		IfStatement ifStmt = (IfStatement) statement;
    		if (ifStmt.getElseClause() == null) {
    			return;
    		}
    		IfClause ifClause = ifStmt.getIfClause();
    		Block ifBlock = ifClause.getBlock();
    		Block elseBlock = ifStmt.getElseClause().getBlock();
			String elseStr = getTerm(doc, elseBlock);
    		Condition ifCondition = ifClause.getCondition();
    		String test = null;
    		String term = getTerm(doc, ifCondition);
    		if (term.equals("(true)")) {
    			test = "false";
    		} else if (term.equals("(false)")) {
    			test = "true";
    		} else if (ifCondition instanceof BooleanCondition) {
    			BooleanCondition boolCond = (BooleanCondition) ifCondition;
    			Term bt = boolCond.getExpression().getTerm();
    			if (bt instanceof NotOp) {
    				NotOp notOp = (NotOp) bt;
    				test = getTerm(doc, notOp.getTerm());
    			} else if (bt instanceof EqualityOp) {
    				test = getInvertedEqualityTest(doc, (EqualityOp)bt);
    			} else if (bt instanceof ComparisonOp) {
    				test = getInvertedComparisonTest(doc, (ComparisonOp)bt);
    			} 
    		}
			if (test == null) {
	    		test = "! " + removeEnclosingParenthesis(term);
    		}
			String baseIndent = CeylonQuickFixAssistant.getIndent(statement, doc);
			String indent = CeylonAutoEditStrategy.getDefaultIndent();

			elseStr = addEnclosingBraces(elseStr, baseIndent, indent);
    		test = removeEnclosingParenthesis(test);

			StringBuilder replace = new StringBuilder();
			replace.append("if (").append(test).append(") ")
					.append(elseStr);
					if (isElseOnOwnLine(doc, ifBlock, elseBlock)) {
						replace.append("\n").append(baseIndent);
					} else {
						replace.append(" ");
					}
					replace.append("else ")
					.append(getTerm(doc, ifBlock)).append("\n");

			TextChange change = new DocumentChange("Convert To if-else", doc);
			change.setEdit(new ReplaceEdit(statement.getStartIndex(), statement.getStopIndex() - statement.getStartIndex() + 1, replace.toString()));
			proposals.add(new InvertIfElse(statement.getStartIndex(), file, change));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	private static String getInvertedEqualityTest(IDocument doc, EqualityOp eqyalityOp)
			throws BadLocationException {
		String op;
		if (eqyalityOp instanceof EqualOp) {
			op = " != ";
		} else {
			op = " == ";
		}
		return getTerm(doc, eqyalityOp.getLeftTerm()) + op + getTerm(doc, eqyalityOp.getRightTerm());
	}

	private static String getInvertedComparisonTest(IDocument doc, ComparisonOp compOp)
			throws BadLocationException {
		String op;
		if (compOp instanceof LargerOp) {
			op = " <= ";
		} else if (compOp instanceof LargeAsOp) {
			op = " < ";
		} else if (compOp instanceof SmallerOp) {
			op = " >= ";
		} else if (compOp instanceof SmallAsOp) {
			op = " > ";
		} else {
			throw new RuntimeException("Unknown Comarision op " + compOp);
		}
		return getTerm(doc, compOp.getLeftTerm()) + op + getTerm(doc, compOp.getRightTerm());
	}


	private static boolean isElseOnOwnLine(IDocument doc, Block ifBlock,
			Block elseBlock) throws BadLocationException {
		return doc.getLineOfOffset(ifBlock.getStopIndex()) != doc.getLineOfOffset(elseBlock.getStartIndex());
	}

	private static String addEnclosingBraces(String s, String baseIndent, String indent) {
    	if (s.charAt(0) != '{') {
    		return "{\n" + baseIndent + indent + indent(s, indent) + "\n" + baseIndent + "}";
    	}
		return s;
	}
	
	private static String indent(String s, String indentation) {
		return s.replaceAll("\n(\\s*)", "\n$1" + indentation);
	}

	private static String removeEnclosingParenthesis(String s) {
    	if (s.charAt(0) == '(') {
    		int endIndex = 1;
    		int startIndex = 1;
    		//Make sure we are not in this case ((a) == (b))
    		while ((endIndex = s.indexOf(')', endIndex + 1)) > 0) {
    			if (endIndex == s.length() -1 ) {
    				return s.substring(1, s.length() - 1);
    			}
    			if ((startIndex = s.indexOf('(', startIndex + 1)) >  endIndex) {
    				return s;
    			}
    		}
    	}
    	return s;
    }
    
    private static String getTerm(IDocument doc, Node node) throws BadLocationException {
    	return doc.get(node.getStartIndex(), node.getStopIndex() - node.getStartIndex() + 1);
    }
}