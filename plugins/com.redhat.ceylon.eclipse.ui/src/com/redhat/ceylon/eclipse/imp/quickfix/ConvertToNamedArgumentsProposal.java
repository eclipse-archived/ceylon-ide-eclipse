package com.redhat.ceylon.eclipse.imp.quickfix;

import java.util.Collection;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.imp.refactoring.ConvertToNamedArgumentsRefactoringAction;

class ConvertToNamedArgumentsProposal implements ICompletionProposal {

    private ConvertToNamedArgumentsRefactoringAction action;
    
    public ConvertToNamedArgumentsProposal(UniversalEditor editor) {
        action = new ConvertToNamedArgumentsRefactoringAction(editor);
    }
    
    @Override
    public Point getSelection(IDocument doc) {
    	return null;
    }

    @Override
    public Image getImage() {
    	return CeylonLabelProvider.CORRECTION;
    }

    @Override
    public String getDisplayString() {
    	return "Convert to named argument list";
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
    
    public static void add(Collection<ICompletionProposal> proposals, UniversalEditor editor) {
        ConvertToNamedArgumentsProposal prop = new ConvertToNamedArgumentsProposal(editor);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }

}