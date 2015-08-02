package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.refactor.InvertBooleanRefactoring.invertTerm;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getDocument;

import java.util.Collection;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class BinaryOperatorProposals {
    
    static void addParenthesizeBinaryOperatorProposal(
            Collection<ICompletionProposal> proposals, 
            IFile file,
            Tree.BinaryOperatorExpression boe) {
        TextChange change = 
                new TextFileChange("Parenthesize Expression", 
                        file);
        change.setEdit(new MultiTextEdit());
        change.addEdit(new InsertEdit(
                boe.getStartIndex(), "("));
        change.addEdit(new InsertEdit(
                boe.getStopIndex()+1, ")"));
        proposals.add(new CorrectionProposal(
                "Parenthesize " + 
                boe.getMainToken().getText() + 
                " expression", change, null));
    }
    
    static void addSwapBinaryOperandsProposal(
            Collection<ICompletionProposal> proposals, 
            IFile file,
            Tree.BinaryOperatorExpression boe) {
        TextChange change = 
                new TextFileChange("Swap Operands", 
                        file);
        change.setEdit(new MultiTextEdit());
        Tree.Term lt = boe.getLeftTerm();
        Tree.Term rt = boe.getRightTerm();
        if (lt!=null && rt!=null) {
            int lto = lt.getStartIndex();
            int ltl = lt.getStopIndex()-lto+1;
            int rto = rt.getStartIndex();
            int rtl = rt.getStopIndex()-rto+1;
            IDocument document = getDocument(change);
            try {
                change.addEdit(new ReplaceEdit(lto, ltl, 
                        document.get(rto, rtl)));
                change.addEdit(new ReplaceEdit(rto, rtl, 
                        document.get(lto, ltl)));
                proposals.add(new CorrectionProposal(
                        "Swap operands of " + 
                        boe.getMainToken().getText() + 
                        " expression", change, null));
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    static void addReverseOperatorProposal(
            Collection<ICompletionProposal> proposals, 
            IFile file,
            Tree.BinaryOperatorExpression boe) {
        if (boe instanceof Tree.ComparisonOp) {
            TextChange change = 
                    new TextFileChange("Reverse Operator", 
                            file);
            change.setEdit(new MultiTextEdit());
            Tree.Term lt = boe.getLeftTerm();
            Tree.Term rt = boe.getRightTerm();
            if (lt!=null && rt!=null) {
                int lto = lt.getStartIndex();
                int ltl = lt.getStopIndex()-lto+1;
                int rto = rt.getStartIndex();
                int rtl = rt.getStopIndex()-rto+1;
                CommonToken op = 
                        (CommonToken) boe.getMainToken();
                String ot = op.getText();
                String iot = reversed(ot);
                change.addEdit(new ReplaceEdit(
                        op.getStartIndex(), 
                        ot.length(), iot));
                if (boe instanceof Tree.ComparisonOp) {
                    IDocument document = getDocument(change);
                    try {
                        change.addEdit(new ReplaceEdit(
                                lto, ltl, 
                                document.get(rto, rtl)));
                        change.addEdit(new ReplaceEdit(
                                rto, rtl, 
                                document.get(lto, ltl)));
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }
                proposals.add(new CorrectionProposal(
                        "Convert " + ot + " to " + iot, 
                        change, null));
            }
        }
    }
    
    static void addInvertOperatorProposal(
            Collection<ICompletionProposal> proposals, 
            IFile file,
            Tree.BinaryOperatorExpression boe) {
        if (boe instanceof Tree.ComparisonOp || 
            boe instanceof Tree.LogicalOp) {
            TextChange change = 
                    new TextFileChange("Invert Operator", 
                            file);
            change.setEdit(new MultiTextEdit());
            Tree.Term lt = boe.getLeftTerm();
            Tree.Term rt = boe.getRightTerm();
            if (lt!=null && rt!=null) {
                CommonToken op = 
                        (CommonToken) boe.getMainToken();
                String ot = op.getText();
                String iot = inverted(ot);
                change.addEdit(new ReplaceEdit(
                        op.getStartIndex(), 
                        ot.length(), iot));
                change.addEdit(new InsertEdit(
                        boe.getStartIndex(), "!("));
                change.addEdit(new InsertEdit(
                        boe.getStopIndex()+1, ")"));
                if (boe instanceof Tree.LogicalOp) {
                    invertTerm(boe.getLeftTerm(), change);
                    invertTerm(boe.getRightTerm(), change);
                }
             proposals.add(new CorrectionProposal(
                        "Convert " + ot + " to " + iot, 
                        change, null));
            }
        }
    }
    
    private static String inverted(String ot) {
        switch (ot) {
        case ">": return "<=";
        case ">=": return "<";
        case "<": return ">=";
        case "<=": return ">";
        case "||": return "&&";
        case "&&": return "||";
        default: return ot;
        }
    }
    
    private static String reversed(String ot) {
        switch (ot) {
        case ">": return "<";
        case ">=": return "<=";
        case "<": return ">";
        case "<=": return ">=";
        default: return ot;
        }
    }

}
