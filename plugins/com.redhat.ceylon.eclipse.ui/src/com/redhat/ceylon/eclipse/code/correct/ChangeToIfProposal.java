package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class ChangeToIfProposal {

    static void addChangeToIfProposal(Collection<ICompletionProposal> proposals,
            IDocument doc, IFile file, Tree.CompilationUnit rootNode,
            final Tree.Statement statement) {
        if (statement instanceof Tree.Assertion) {
            Tree.ConditionList conditionList = 
                    ((Tree.Assertion) statement).getConditionList();
            if (conditionList!=null) {
                class FindBodyVisitor extends Visitor {
                    Tree.Body result;
                    @Override
                    public void visit(Tree.Body that) {
                        if (that.getStatements().contains(statement)) {
                            result = that;
                        }
                        else {
                            super.visit(that);
                        }
                    }
                }
                FindBodyVisitor fbv = new FindBodyVisitor();
                fbv.visit(rootNode);
                List<Tree.Statement> statements = fbv.result.getStatements();
                Tree.Statement last = statements.get(statements.size()-1);
                boolean isLast = statement==last;
                TextFileChange change = 
                        new TextFileChange("Change Assert To If", file);
                change.setEdit(new MultiTextEdit());
                String newline = getDefaultLineDelimiter(doc);
                String indent = getIndent(last, doc);
                int begin = statement.getStartIndex();
                int end = conditionList.getStartIndex();
                change.addEdit(new ReplaceEdit(begin, end-begin, "if "));
                change.addEdit(new ReplaceEdit(statement.getEndIndex()-1, 1, 
                        isLast ? " {}" : " {"));
                //TODO: this is wrong, need to look for lines, not statements!
                for (int i=statements.indexOf(statement)+1; i<statements.size(); i++) {
                    change.addEdit(new InsertEdit(statements.get(i).getStartIndex(), 
                            getDefaultIndent()));
                }
                if (!isLast) {
                    change.addEdit(new InsertEdit(last.getEndIndex(), 
                            newline + indent + "}"));
                }
                String elseBlock = newline + indent +
                        "else {" + newline + indent + getDefaultIndent() + 
                        "assert (false);" + newline + indent + "}" ;
                change.addEdit(new InsertEdit(last.getEndIndex(), elseBlock));
                proposals.add(new CorrectionProposal("Change 'assert' to 'if'", change, 
                        new Region(statement.getEndIndex()-3, 0)));
            }
        }
    }

}
