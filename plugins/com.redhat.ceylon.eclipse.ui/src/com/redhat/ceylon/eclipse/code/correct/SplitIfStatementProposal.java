package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

class SplitIfStatementProposal {

    static void addSplitIfStatementProposal(
            Collection<ICompletionProposal> proposals, IDocument doc,
            IFile file, Tree.Statement statement) {
        if (statement instanceof Tree.IfStatement) {
            Tree.IfStatement is = (Tree.IfStatement) statement;
            if (is.getElseClause()==null) {
                Tree.ConditionList cl = is.getIfClause().getConditionList();
                if (cl!=null) {
                    List<Tree.Condition> conditions = cl.getConditions();
                    if (conditions.size()>=2) {
                        Tree.Condition c1 = conditions.get(conditions.size()-2);
                        Tree.Condition c2 = conditions.get(conditions.size()-1);
                        if (c1!=null && c2!=null) {
                            TextChange change = new TextFileChange("Split If Statement", file);
                            change.setEdit(new MultiTextEdit());
                            String ws;
                            String indent; 
                            if (is.getToken().getLine()==is.getEndToken().getLine()) {
                                ws = " ";
                                indent = "";
                            }
                            else {
                                ws = getDefaultLineDelimiter(doc) + getIndent(is, doc);
                                indent = getDefaultIndent();
                            }
                            change.addEdit(new ReplaceEdit(c1.getStopIndex()+1, 
                                    c2.getStartIndex()-c1.getStopIndex()-1, 
                                    ") {" + ws + indent + "if ("));
                            change.addEdit(new InsertEdit(is.getStopIndex()+1, ws + "}"));
                            if (!indent.isEmpty()) {
                                try {
                                    for (int line = doc.getLineOfOffset(cl.getStopIndex())+1;
                                            line <= doc.getLineOfOffset(is.getStopIndex()); 
                                            line++) {
                                        change.addEdit(new InsertEdit(doc.getLineOffset(line), indent));
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            proposals.add(new CorrectionProposal("Split if statement", change, null));
                        }
                    }
                }
            }
        }
    }

}
