package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;

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

import org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;

class SplitIfStatementProposal {

    static void addSplitIfStatementProposal(
            Collection<ICompletionProposal> proposals, 
            IDocument doc, IFile file, 
            Tree.Statement statement) {
        if (statement instanceof Tree.IfStatement) {
            Tree.IfStatement is = 
                    (Tree.IfStatement) statement;
            Tree.ElseClause elseClause = is.getElseClause();
            if (elseClause==null) {
                Tree.ConditionList cl = 
                        is.getIfClause().getConditionList();
                if (cl!=null) {
                    List<Tree.Condition> conditions = 
                            cl.getConditions();
                    int size = conditions.size();
                    if (size>=2) {
                        Tree.Condition c1 = conditions.get(size-2);
                        Tree.Condition c2 = conditions.get(size-1);
                        if (c1!=null && c2!=null) {
                            TextChange change = 
                                    new TextFileChange(
                                            "Split If Statement", 
                                            file);
                            change.setEdit(new MultiTextEdit());
                            String ws;
                            String indent; 
                            int startLine = is.getToken().getLine();
                            int endLine = is.getEndToken().getLine();
                            if (startLine==endLine) {
                                ws = " ";
                                indent = "";
                            }
                            else {
                                ws = utilJ2C().indents().getDefaultLineDelimiter(doc) + 
                                        utilJ2C().indents().getIndent(is, doc);
                                indent = utilJ2C().indents().getDefaultIndent();
                            }
                            int start = c1.getEndIndex();
                            int stop = c2.getStartIndex();
                            change.addEdit(new ReplaceEdit(start, stop-start, 
                                    ") {" + ws + indent + "if ("));
                            int end = is.getEndIndex();
                            change.addEdit(new InsertEdit(end, ws + "}"));
                            incrementIndent(doc, is, cl, change, indent);
                            proposals.add(new CorrectionProposal(
                                    "Split 'if' statement at condition", 
                                    change, null));
                        }
                    }
                }
            }
            else {
                Tree.Block block = elseClause.getBlock();
                if (block!=null &&
                        block.getToken().getType()
                            == CeylonLexer.IF_CLAUSE) {
                    List<Tree.Statement> statements = 
                            block.getStatements();
                    if (statements.size()==1) {
                        Tree.Statement st = statements.get(0);
                        if (st instanceof Tree.IfStatement) {
                            Tree.IfStatement inner = 
                                    (Tree.IfStatement) st;
                            Tree.ConditionList icl = 
                                    inner.getIfClause()
                                        .getConditionList();
                            TextChange change = 
                                    new TextFileChange(
                                            "Split If Statement", 
                                            file);
                            change.setEdit(new MultiTextEdit());
                            String ws = 
                                    utilJ2C().indents().getDefaultLineDelimiter(doc) + 
                                    utilJ2C().indents().getIndent(is, doc);
                            String indent = utilJ2C().indents().getDefaultIndent();
                            int start = block.getStartIndex();
                            change.addEdit(new InsertEdit(start,  
                                    "{" + ws + indent));
                            int end = is.getEndIndex();
                            change.addEdit(new InsertEdit(end, ws + "}"));
                            incrementIndent(doc, is, icl, change, indent);
                            proposals.add(new CorrectionProposal(
                                    "Split 'if' statement at 'else'", 
                                    change, null));
                        }
                    }
                }
            }
        }
    }

    private static void incrementIndent(IDocument doc, Tree.IfStatement is,
            Tree.ConditionList cl, TextChange change, String indent) {
        if (!indent.isEmpty()) {
            try {
                for (int line = doc.getLineOfOffset(cl.getStopIndex())+1;
                        line <= doc.getLineOfOffset(is.getStopIndex()); 
                        line++) {
                    change.addEdit(new InsertEdit(doc.getLineOffset(line), 
                            indent));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
