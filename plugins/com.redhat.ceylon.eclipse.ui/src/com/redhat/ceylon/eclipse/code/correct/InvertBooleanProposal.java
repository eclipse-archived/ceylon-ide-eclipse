package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.util.EditorUtil.getCommandBinding;

import java.util.Collection;

import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.refactor.InvertBooleanRefactoringAction;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.util.Highlights;

class InvertBooleanProposal implements ICompletionProposal, ICompletionProposalExtension6 {

    private final InvertBooleanRefactoringAction action;

    public InvertBooleanProposal(CeylonEditor editor) {
        action = new InvertBooleanRefactoringAction(editor);
    }

    @Override
    public Image getImage() {
        return CeylonResources.COMPOSITE_CHANGE;
    }

    @Override
    public String getDisplayString() {
        return "Invert boolean value of '" + action.getValueName() + "'";
    }

    @Override
    public Point getSelection(IDocument doc) {
        return null;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public void apply(IDocument doc) {
        action.run();
    }

    public static void add(Collection<ICompletionProposal> proposals, CeylonEditor editor) {
        InvertBooleanProposal proposal = new InvertBooleanProposal(editor);
        if (proposal.action.isEnabled()) {
            proposals.add(proposal);
        }
    }

    @Override
    public StyledString getStyledDisplayString() {
        TriggerSequence binding = 
                getCommandBinding("com.redhat.ceylon.eclipse.ui.action.invertBoolean");
        String hint = binding==null ? "" : " (" + binding.format() + ")";
        return Highlights.styleProposal(getDisplayString(), false)
                .append(hint, StyledString.QUALIFIER_STYLER);
    }

}