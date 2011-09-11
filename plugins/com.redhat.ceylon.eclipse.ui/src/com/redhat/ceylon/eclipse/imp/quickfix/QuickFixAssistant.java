package com.redhat.ceylon.eclipse.imp.quickfix;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.imp.editor.hover.ProblemLocation;
import org.eclipse.imp.editor.quickfix.ChangeCorrectionProposal;
import org.eclipse.imp.services.IQuickFixAssistant;
import org.eclipse.imp.services.IQuickFixInvocationContext;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;

/**
 * Popup quick fixes for problem annotations displayed in editor
 * @author gavin
 */
public class QuickFixAssistant implements IQuickFixAssistant {

    @Override
    public boolean canFix(Annotation annotation) {
        return true;
    }

    @Override
    public boolean canAssist(IQuickFixInvocationContext invocationContext) {
        return false;
    }

    @Override
    public String[] getSupportedMarkerTypes() {
        return new String[] { CeylonBuilder.PROBLEM_MARKER_ID };
    }

    @Override
    public void addProposals(IQuickFixInvocationContext context, ProblemLocation problem,
            Collection<ICompletionProposal> proposals) {
        switch ( problem.getProblemId() ) {
        case 69:
            IFile file = context.getModel().getFile();
            TextFileChange change = new TextFileChange("Fix", file);
            change.setEdit(new MultiTextEdit());
            change.getEdit().addChild(new InsertEdit(problem.getOffset(), "\"Hello world\""));
            proposals.add(new ChangeCorrectionProposal("Insert \"Hello world\"", change, 50, null));
            break;
        }
    }
    
}
