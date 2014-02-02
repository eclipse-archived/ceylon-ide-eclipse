package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberExpression;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.refactor.AbstractRefactoring;
import com.redhat.ceylon.eclipse.util.FindReferenceVisitor;
import com.redhat.ceylon.eclipse.util.FindStatementVisitor;

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
        	FindStatementVisitor fsv = new FindStatementVisitor(node, false);
        	fsv.visit(cu);
        	Tree.Statement s = fsv.getStatement();
        	if (s instanceof Tree.SwitchStatement) {
        		String name = AbstractRefactoring.guessName(node);
        		TextChange change = new TextFileChange("Shadow Reference", file);
        		change.setEdit(new MultiTextEdit());
        		change.addEdit(new ReplaceEdit(s.getStartIndex(), node.getStartIndex()-s.getStartIndex(),
        				"value " + name + " = "));
        		change.addEdit(new InsertEdit(node.getStopIndex()+1, "; switch (" + name));
        		if (node instanceof BaseMemberExpression) {
        			FindReferenceVisitor frv = new FindReferenceVisitor(((BaseMemberExpression) node).getDeclaration());
                    frv.visit(((Tree.SwitchStatement) s).getSwitchCaseList());
                    for (Node n: frv.getNodes()) {
                        Node identifyingNode = getIdentifyingNode(n);
                        Integer start = identifyingNode.getStartIndex();
                        if (start!=node.getStartIndex()) {
                            change.addEdit(new ReplaceEdit(start, 
                                    identifyingNode.getText().length(), name));
                        }
                    }
        		}
        		proposals.add(new ShadowReferenceProposal(s.getStartIndex()+6, name.length(), file, change));
        	}
        }
    }
    
    static void addShadowReferenceProposal(IFile file, Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals, Node node) {
        if (node instanceof Tree.Variable) {
            Tree.Variable var = (Tree.Variable) node;
            int offset = var.getIdentifier().getStartIndex();
            String name = AbstractRefactoring.guessName(var.getSpecifierExpression().getExpression().getTerm());
            TextChange change = new TextFileChange("Shadow Reference", file);
            change.setEdit(new MultiTextEdit());
            change.addEdit(new InsertEdit(offset, name + " = "));
            FindStatementVisitor fsv = new FindStatementVisitor(var, false);
            fsv.visit(cu);
            FindReferenceVisitor frv = new FindReferenceVisitor(var.getDeclarationModel());
            frv.visit(fsv.getStatement());
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
            String name = AbstractRefactoring.guessName(node);
            TextChange change = new TextFileChange("Shadow Reference", file);
//            change.setEdit(new MultiTextEdit());
            Integer offset = node.getStartIndex();
            change.setEdit(new InsertEdit(offset, name + " = "));
            proposals.add(new ShadowReferenceProposal(offset, 1, file, change));
        }
    }
}