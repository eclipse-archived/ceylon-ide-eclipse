package com.redhat.ceylon.eclipse.code.correct;

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.refactor.ConvertToClassRefactoringAction;

class ConvertToClassProposal implements ICompletionProposal {

    private ConvertToClassRefactoringAction action;
    
    public ConvertToClassProposal(CeylonEditor editor) {
        action = new ConvertToClassRefactoringAction(editor);
    }
    
    @Override
    public Point getSelection(IDocument doc) {
    	return null;
    }

    @Override
    public Image getImage() {
    	return action.isShared() ? 
    	        CeylonLabelProvider.CLASS : 
    	        CeylonLabelProvider.LOCAL_CLASS;
    }

    @Override
    public String getDisplayString() {
    	return "Convert '" + action.currentName() + "' to class";
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
        ConvertToClassProposal prop = new ConvertToClassProposal(editor);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }

}