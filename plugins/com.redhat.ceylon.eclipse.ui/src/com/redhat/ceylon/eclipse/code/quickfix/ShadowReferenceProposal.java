package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findStatement;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.code.refactor.AbstractRefactoring.guessName;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberExpression;
import com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.util.FindReferenceVisitor;

class ShadowReferenceProposal extends ChangeCorrectionProposal {
    
    final IFile file;
    final int offset;
    final int length;
    
    ShadowReferenceProposal(int offset, int length, IFile file, 
            TextChange change) {
        super("Shadow reference inside control structure", change);
        this.file=file;
        this.offset=offset;
        this.length=length;
    }
    
    @Override
    public void apply(IDocument document) {
        super.apply(document);
        Util.gotoLocation(file, offset, length);
    }
    
    static void addShadowSwitchReferenceProposal(IFile file, Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals, Node node) {
        if (node instanceof Tree.Term) {
        	Tree.Statement statement = findStatement(cu, node);
        	if (statement instanceof Tree.SwitchStatement) {
        		String name = guessName(node);
        		TextChange change = new TextFileChange("Shadow Reference", file);
        		change.setEdit(new MultiTextEdit());
        		Integer offset = statement.getStartIndex();
				change.addEdit(new ReplaceEdit(offset, 
        				node.getStartIndex()-offset,
        				"value " + name + " = "));
				change.addEdit(new InsertEdit(node.getStopIndex()+1, 
        				";" + System.lineSeparator() + 
        				getStatementIndent(statement) +
        				"switch (" + name));
        		if (node instanceof BaseMemberExpression) {
        			Declaration d = ((BaseMemberExpression) node).getDeclaration();
        			if (d!=null) {
        				FindReferenceVisitor frv = new FindReferenceVisitor(d);
        				frv.visit(((Tree.SwitchStatement) statement).getSwitchCaseList());
        				for (Node n: frv.getNodes()) {
        					Node identifyingNode = getIdentifyingNode(n);
        					Integer start = identifyingNode.getStartIndex();
        					if (start!=node.getStartIndex()) {
        						change.addEdit(new ReplaceEdit(start, 
        								identifyingNode.getText().length(), name));
        					}
        				}
        			}
        		}
        		proposals.add(new ShadowReferenceProposal(offset+6, name.length(), file, change));
        	}
        }
    }

    //TODO: this is rubbish, we need a much better way to get indents
    //      when we don't have an IDocument
	private static String getStatementIndent(Tree.Statement statement) {
	    int pos = statement.getToken().getCharPositionInLine();
	    String di = CeylonAutoEditStrategy.getDefaultIndent();
	    StringBuffer indent = new StringBuffer();
	    for (int i=0; i<pos/di.length(); i++) {
	    	indent.append(di);
	    }
	    return indent.toString();
    }
    
    static void addShadowReferenceProposal(IFile file, Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals, Node node) {
        if (node instanceof Tree.Variable) {
            Tree.Variable var = (Tree.Variable) node;
            int offset = var.getIdentifier().getStartIndex();
            String name = guessName(var.getSpecifierExpression().getExpression().getTerm());
            TextChange change = new TextFileChange("Shadow Reference", file);
            change.setEdit(new MultiTextEdit());
            change.addEdit(new InsertEdit(offset, name + " = "));
            Tree.Statement statement = findStatement(cu, node);
            FindReferenceVisitor frv = new FindReferenceVisitor(var.getDeclarationModel());
            frv.visit(statement);
            for (Node n: frv.getNodes()) {
                Node identifyingNode = getIdentifyingNode(n);
                Integer start = identifyingNode.getStartIndex();
                if (start!=offset) {
                    change.addEdit(new ReplaceEdit(start, 
                            identifyingNode.getText().length(), name));
                }
            }
            proposals.add(new ShadowReferenceProposal(offset, 1, file, change));
        }
        else if (node instanceof Tree.Term) {
            String name = guessName(node);
            TextChange change = new TextFileChange("Shadow Reference", file);
//            change.setEdit(new MultiTextEdit());
            Integer offset = node.getStartIndex();
            change.setEdit(new InsertEdit(offset, name + " = "));
            proposals.add(new ShadowReferenceProposal(offset, 1, file, change));
        }
    }
}