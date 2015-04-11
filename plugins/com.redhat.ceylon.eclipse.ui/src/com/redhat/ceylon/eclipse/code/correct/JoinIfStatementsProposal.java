package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;

class JoinIfStatementsProposal {

    static void addJoinIfStatementsProposal(
            Collection<ICompletionProposal> proposals, 
            IDocument doc, IFile file, 
            Tree.Statement statement) {
        //TODO: look for containing if statement?
        if (statement instanceof Tree.IfStatement) {
            Tree.IfStatement outer = 
                    (Tree.IfStatement) statement;
            if (outer.getElseClause()==null) {
                Tree.Block block = 
                        outer.getIfClause().getBlock();
                if (block!=null) {
                    List<Tree.Statement> statements = 
                            block.getStatements();
                    if (statements.size()==1) {
                        Tree.Statement st = statements.get(0);
                        if (st instanceof Tree.IfStatement) {
                            Tree.IfStatement inner = 
                                    (Tree.IfStatement) st;
                            Tree.ConditionList ocl = 
                                    outer.getIfClause()
                                        .getConditionList();
                            Tree.ConditionList icl = 
                                    inner.getIfClause()
                                        .getConditionList();
                            if (ocl!=null && icl!=null) {
                                TextChange change = 
                                        new TextFileChange("Join If Statements", 
                                                file);
                                change.setEdit(new MultiTextEdit());
                                change.addEdit(new ReplaceEdit(ocl.getStopIndex(), 
                                        icl.getStartIndex()+1-ocl.getStopIndex(), 
                                        ", "));
                                decrementIndent(doc, inner, icl, change,
                                        getIndent(inner, doc),
                                        getIndent(outer, doc));
                                change.addEdit(new DeleteEdit(inner.getStopIndex()+1, 
                                        outer.getStopIndex()-inner.getStopIndex()));
                                proposals.add(new CorrectionProposal("Join 'if' statements", 
                                        change, null));
                            }
                        }
                    }
                }
            }
        }
    }

    private static void decrementIndent(IDocument doc, Tree.IfStatement is,
            Tree.ConditionList cl, TextChange change, String indent,
            String outerIndent) {
        String defaultIndent = getDefaultIndent();
        try {
            for (int line = doc.getLineOfOffset(cl.getStopIndex())+1;
                    line < doc.getLineOfOffset(is.getStopIndex()); 
                    line++) {
                IRegion lineInformation = doc.getLineInformation(line);
                String lineText = doc.get(lineInformation.getOffset(), 
                        lineInformation.getLength());
                if (lineText.startsWith(indent+defaultIndent)) {
                    change.addEdit(new DeleteEdit(
                            lineInformation.getOffset() + indent.length(), 
                            defaultIndent.length()));
                }
            }
            int line = doc.getLineOfOffset(is.getStopIndex());
            IRegion lineInformation = doc.getLineInformation(line);
            String lineText = doc.get(lineInformation.getOffset(), 
                    lineInformation.getLength());
            if (lineText.startsWith(indent)) {
                change.addEdit(new ReplaceEdit(
                        lineInformation.getOffset(), indent.length(), 
                        defaultIndent));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
