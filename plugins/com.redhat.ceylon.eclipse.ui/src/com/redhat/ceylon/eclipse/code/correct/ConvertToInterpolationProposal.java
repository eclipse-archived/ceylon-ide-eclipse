package com.redhat.ceylon.eclipse.code.correct;

import java.util.Collection;

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
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class ConvertToInterpolationProposal extends CorrectionProposal {

    private ConvertToInterpolationProposal(String name, Change change) {
        super(name, change, null);
    }

    static void addConvertToInterpolationProposal(Collection<ICompletionProposal> proposals,
            IFile file, Tree.CompilationUnit cu, final Node node, IDocument doc) {
        class ConcatenationVisitor extends Visitor {
            Tree.SumOp result;
            @Override
            public void visit(Tree.SumOp that) {
                if (that.getStartIndex()<=node.getStartIndex() &&
                    that.getEndIndex()>=node.getEndIndex()) {
                    Tree.Term lt = that.getLeftTerm();
                    Tree.Term rt = that.getRightTerm();
                    if ((lt instanceof Tree.StringLiteral ||
                         lt instanceof Tree.StringTemplate) &&
                            rt instanceof Tree.SumOp &&
                            (((Tree.SumOp) rt).getRightTerm() 
                                    instanceof Tree.StringLiteral ||
                            ((Tree.SumOp) rt).getRightTerm() 
                                    instanceof Tree.StringTemplate)) {
                        result = that;
                    }
                    if ((rt instanceof Tree.StringLiteral ||
                         rt instanceof Tree.StringTemplate) &&
                            lt instanceof Tree.SumOp &&
                            (((Tree.SumOp) lt).getLeftTerm() 
                                    instanceof Tree.StringLiteral ||
                            ((Tree.SumOp) lt).getLeftTerm() 
                                    instanceof Tree.StringTemplate)) {
                        result = that;
                    }
                }
                super.visit(that);
            }
        }
        ConcatenationVisitor tv = new ConcatenationVisitor();
        tv.visit(cu);
        Tree.SumOp op = tv.result;
        if (op!=null) {
            TextFileChange change = new TextFileChange("Convert to Interpolation", file);
            change.setEdit(new MultiTextEdit());
            Tree.Term rt = op.getRightTerm();
            Tree.Term lt = op.getLeftTerm();
            if (rt instanceof Tree.StringLiteral ||
                rt instanceof Tree.StringTemplate) {
                change.addEdit(new ReplaceEdit(lt.getEndIndex(), 
                        rt.getStartIndex()-lt.getEndIndex()+1, 
                        "``"));
            }
            else {
                Tree.SumOp rop = (Tree.SumOp) rt;
                change.addEdit(new ReplaceEdit(rop.getLeftTerm().getEndIndex(), 
                        rop.getRightTerm().getStartIndex()-rop.getLeftTerm().getEndIndex()+1, 
                        "``"));
                if (rop.getLeftTerm() instanceof Tree.QualifiedMemberExpression) {
                    Tree.QualifiedMemberExpression rlt = 
                            (Tree.QualifiedMemberExpression) rop.getLeftTerm();
                    if (rlt.getDeclaration().getName().equals("string")) {
                        int from = rlt.getMemberOperator().getStartIndex();
                        int to = rlt.getIdentifier().getStartIndex();
                        change.addEdit(new DeleteEdit(from, to-from));
                    }
                }
            }
            if (lt instanceof Tree.StringLiteral ||
                lt instanceof Tree.StringTemplate) {
                change.addEdit(new ReplaceEdit(lt.getEndIndex()-1, 
                        rt.getStartIndex()-lt.getEndIndex()+1, 
                        "``"));
            }
            else {
                Tree.SumOp lop = (Tree.SumOp) lt;
                change.addEdit(new ReplaceEdit(lop.getLeftTerm().getEndIndex()-1, 
                        lop.getRightTerm().getStartIndex()-lop.getLeftTerm().getEndIndex()+1, 
                        "``"));
                if (lop.getRightTerm() instanceof Tree.QualifiedMemberExpression) {
                    Tree.QualifiedMemberExpression lrt = 
                            (Tree.QualifiedMemberExpression) lop.getRightTerm();
                    if (lrt.getDeclaration().getName().equals("string")) {
                        int from = lrt.getMemberOperator().getStartIndex();
                        int to = lrt.getIdentifier().getEndIndex();
                        change.addEdit(new DeleteEdit(from, to-from));
                    }
                }
            }
            proposals.add(new ConvertToInterpolationProposal("Convert to string interpolation", change));
        }
    }
    
    
}