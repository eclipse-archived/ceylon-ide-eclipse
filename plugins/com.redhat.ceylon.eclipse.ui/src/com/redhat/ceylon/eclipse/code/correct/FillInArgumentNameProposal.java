package com.redhat.ceylon.eclipse.code.correct;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;

class FillInArgumentNameProposal extends CorrectionProposal {

    public FillInArgumentNameProposal(String name, Change change) {
        super("Fill in argument name '" + name + "'", change);
    }

    static void addFillInArgumentNameProposal(Collection<ICompletionProposal> proposals, 
            IDocument doc, IFile file, Tree.SpecifiedArgument sa) {
        Tree.Identifier id = sa.getIdentifier();
        if (id.getToken()==null) {
            TextChange change = new TextFileChange("Convert To Block", file);
            change.setEdit(new MultiTextEdit());
            Tree.Expression e = sa.getSpecifierExpression().getExpression();
            if (e!=null) {
                final String name = id.getText();
                if (e.getTerm() instanceof Tree.FunctionArgument) {
                    Tree.FunctionArgument fa = (Tree.FunctionArgument) e.getTerm();
                    if (!fa.getParameterLists().isEmpty()) {
                        int startIndex = fa.getParameterLists().get(0).getStartIndex();
                        change.addEdit(new InsertEdit(startIndex, name));
                        try {
                            if (doc.getChar(sa.getStopIndex())==';') {
                                change.addEdit(new DeleteEdit(sa.getStopIndex(), 1));
                            }
                        }
                        catch (Exception ex) {}
                    }
                }
                else {
                    change.addEdit(new InsertEdit(sa.getStartIndex(), name + " = "));
                }
                if (change.getEdit().hasChildren()) {
                    proposals.add(new FillInArgumentNameProposal(name, change));
                }
            }
        }
    }

}
