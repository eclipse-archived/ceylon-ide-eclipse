package com.redhat.ceylon.eclipse.code.quickfix;

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.refactor.RenameRefactoringAction;

class RenameDeclarationProposal implements ICompletionProposal,
		ICompletionProposalExtension6 {

    private RenameRefactoringAction action;
    
    public RenameDeclarationProposal(CeylonEditor editor) {
        action = new RenameRefactoringAction(editor);
    }
    
    @Override
    public Point getSelection(IDocument doc) {
    	return null;
    }

    @Override
    public Image getImage() {
    	return CeylonLabelProvider.RENAME;
    }

    @Override
    public String getDisplayString() {
    	return "Rename '" + action.currentName() + "'";
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
    
    boolean isEnabled() {
        return action.isEnabled();
    }
    
    public static void add(Collection<ICompletionProposal> proposals, CeylonEditor editor) {
        RenameDeclarationProposal prop = new RenameDeclarationProposal(editor);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }

	@Override
	public StyledString getStyledDisplayString() {
		return ChangeCorrectionProposal.style(getDisplayString());
	}

}