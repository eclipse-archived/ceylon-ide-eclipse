package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getTokenIndexAtCharacter;

import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;

import ceylon.language.StringBuilder;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.util.FindStatementVisitor;

final class TerminateStatementAction extends Action {
	private final CeylonEditor editor;

	TerminateStatementAction(CeylonEditor editor) {
		super(null);
		this.editor = editor;
	}
	
	int count(String s, char c) {
		int count=0;
		for (int i=0; i<s.length(); i++) {
			if (s.charAt(i)==c) count++;
		}
		return count;
	}

	@Override
	public void run() {
		try {
			IDocument doc = editor.getCeylonSourceViewer().getDocument();
			TextChange change = new DocumentChange("Terminate Statement", doc);
			CompilationUnit rootNode = editor.getParseController().getRootNode();
			ITextSelection ts = (ITextSelection) editor.getSelectionProvider().getSelection();
			IRegion li = doc.getLineInformation(ts.getEndLine());
			String lineText = doc.get(li.getOffset(), li.getLength());
			List<CommonToken> tokens = editor.getParseController().getTokens();
			int j=lineText.length()-1;
			for (; j>=0; j--) {
				int ti = getTokenIndexAtCharacter(tokens, li.getOffset()+j);
				if (ti<0) ti=-ti;
				int type = tokens.get(ti).getType();
				if (type!=CeylonLexer.WS &&
				    type!=CeylonLexer.MULTI_COMMENT &&
				    type!=CeylonLexer.LINE_COMMENT) break;
			}
			int endOfCodeInLine = li.getOffset()+j;
			Node node = findNode(rootNode, endOfCodeInLine);
			//TODO: named args, imports
			FindStatementVisitor fcv = new FindStatementVisitor(node, false);
			fcv.visit(rootNode);
			Tree.Statement s = fcv.getStatement();
			StringBuilder terminators = new StringBuilder();
			for (int i=0; i<count(lineText, '(')-count(lineText,')'); i++) {
				terminators.appendCharacter(')');
			}
			for (int i=0; i<count(lineText, '{')-count(lineText,'}'); i++) {
				terminators.appendCharacter('}');
			}
			if (s instanceof Tree.ControlStatement||
				s instanceof Tree.MethodDefinition||
				s instanceof Tree.AttributeGetterDefinition||
				s instanceof Tree.AttributeSetterDefinition||
				s instanceof Tree.ObjectDefinition||
				s instanceof Tree.ClassDefinition||
				s instanceof Tree.InterfaceDefinition) {
				/*if (!text.endsWith("}")) {
					terminators.append("}");
				}*/
			}
			else {
				if (!lineText.endsWith(";")) {
					terminators.appendCharacter(';');
				}
			}
			InsertEdit edit = new InsertEdit(endOfCodeInLine+1, 
					terminators.toString());
			change.setEdit(edit);
			change.perform(new NullProgressMonitor());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}