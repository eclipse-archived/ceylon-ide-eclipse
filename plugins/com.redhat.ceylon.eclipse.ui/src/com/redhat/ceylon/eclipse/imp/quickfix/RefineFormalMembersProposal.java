package com.redhat.ceylon.eclipse.imp.quickfix;

import java.util.Collection;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.eclipse.imp.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.imp.proposals.CeylonContentProposer;
import com.redhat.ceylon.eclipse.imp.refine.RefineFormalMembersHandler;

class RefineFormalMembersProposal implements ICompletionProposal {

    private RefineFormalMembersHandler action;
    
    public RefineFormalMembersProposal(CeylonEditor editor) {
        action = new RefineFormalMembersHandler(editor);
    }
    
    @Override
    public Point getSelection(IDocument doc) {
    	return null;
    }

    @Override
    public Image getImage() {
    	return CeylonContentProposer.FORMAL_REFINEMENT;
    }

    @Override
    public String getDisplayString() {
    	return "Refine formal members";
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
        try {
            action.execute(null);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    boolean isEnabled() {
        return action.isEnabled();
    }
    
    public static void add(Collection<ICompletionProposal> proposals, UniversalEditor editor) {
        if (editor instanceof CeylonEditor) {
            RefineFormalMembersProposal prop = new RefineFormalMembersProposal((CeylonEditor)editor);
            if (prop.isEnabled()) {
                proposals.add(prop);
            }
        }
    }

}