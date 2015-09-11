package com.redhat.ceylon.eclipse.code.correct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Term;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class ConvertToInterpolationProposal extends CorrectionProposal {

    private ConvertToInterpolationProposal(String name, Change change) {
        super(name, change, null);
    }
    
    private static List<Tree.Term> flatten(Tree.SumOp sum) {
        Tree.Term lt = sum.getLeftTerm();
        Tree.Term rt = sum.getRightTerm();
        List<Tree.Term> result;
        if (lt instanceof Tree.SumOp) {
            result = flatten((Tree.SumOp) lt);
            result.add(rt);
        }
        else {
            result = new ArrayList<Tree.Term>();
            result.add(lt);
            result.add(rt);
        }
        return result;
    }

    private static boolean isConcatenation(Tree.SumOp sum) {
        boolean expectingLiteral = true;
        for (Tree.Term term: flatten(sum)) {
            if (expectingLiteral) {
                if (!(term instanceof Tree.StringLiteral ||
                      term instanceof Tree.StringTemplate)) {
                    return false;
                }
                expectingLiteral = false;
            }
            else {
                expectingLiteral = true;
            }
        }
        return !expectingLiteral;
    }
    
    static class ConcatenationVisitor extends Visitor {
        Node node;
        Tree.SumOp result;
        ConcatenationVisitor(Node node) {
            this.node = node;
        }
        @Override
        public void visit(Tree.SumOp that) {
            if (that.getStartIndex()<=node.getStartIndex() &&
                that.getEndIndex()>=node.getEndIndex()) {
                if (isConcatenation(that)) {
                    result = that;
                    return;
                }
            }
            super.visit(that);
        }
    }
    
    static void addConvertToInterpolationProposal(
            Collection<ICompletionProposal> proposals,
            IFile file, Tree.CompilationUnit cu, Node node, 
            IDocument doc) {
        ConcatenationVisitor tv = 
                new ConcatenationVisitor(node);
        tv.visit(cu);
        Tree.SumOp sum = tv.result;
        if (sum!=null) {
            TextFileChange change = 
                    new TextFileChange(
                            "Convert to Interpolation", 
                            file);
            change.setEdit(new MultiTextEdit());
            List<Term> terms = flatten(sum);
            for (int i=0; i<terms.size(); i++) {
                Tree.Term term = terms.get(i);
                if (i>0) {
                    Tree.Term previous = terms.get(i-1);
                    int from = previous.getEndIndex();
                    int to = term.getStartIndex();
                    change.addEdit(new DeleteEdit(from, to-from));
                }
                if (i%2==0) {
                    if (i>0) {
                        change.addEdit(new ReplaceEdit(term.getStartIndex(), 1, "``"));
                    }
                    if (i<terms.size()-1) {
                        change.addEdit(new ReplaceEdit(term.getEndIndex()-1, 1, "``"));
                    }
                }
                else {
                    if (term instanceof Tree.QualifiedMemberExpression) {
                        Tree.QualifiedMemberExpression lrt = 
                                (Tree.QualifiedMemberExpression) term;
                        if (lrt.getDeclaration().getName().equals("string")) {
                            int from = lrt.getMemberOperator().getStartIndex();
                            int to = lrt.getIdentifier().getEndIndex();
                            change.addEdit(new DeleteEdit(from, to-from));
                        }
                    }
                }
            }
            proposals.add(new ConvertToInterpolationProposal(
                    "Convert to string interpolation", change));
        }
    }
    
    
}