package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.ui.CeylonResources.CHANGE;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.ide.common.util.escaping_;

@Deprecated
class RenameDescriptorProposal {
    
    static void addRenameDescriptorProposal(Tree.CompilationUnit cu,
            IQuickAssistInvocationContext context, ProblemLocation problem,
            Collection<ICompletionProposal> proposals, IFile file) {
        String pn = escaping_.get_().escapePackageName(cu.getUnit().getPackage());
        //TODO: DocumentChange doesn't work for Problems View
        TextFileChange change = new TextFileChange("Rename", file);
//        DocumentChange change = new DocumentChange("Rename", context.getSourceViewer().getDocument());
        change.setEdit(new ReplaceEdit(problem.getOffset(), problem.getLength(), pn));
        proposals.add(new CorrectionProposal("Rename to '" + pn + "'", change, null, CHANGE) {
            @Override
            public StyledString getStyledDisplayString() {
                return Highlights.styleProposal(getDisplayString(), true);
            }
        });
    }
    
}