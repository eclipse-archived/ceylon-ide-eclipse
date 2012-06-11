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

class RefineFormalMembersProposal implements ICompletionProposal {

    private CeylonEditor editor;
    
    public RefineFormalMembersProposal(CeylonEditor editor) {
        this.editor = editor;
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
            RefineFormalMembersHandler.refineFormalMembers(editor);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    public static void add(Collection<ICompletionProposal> proposals, UniversalEditor editor) {
        if (editor instanceof CeylonEditor) {
            if (RefineFormalMembersHandler.canRefine((CeylonEditor) editor)) {
                proposals.add(new RefineFormalMembersProposal((CeylonEditor) editor));
            }
        }
    }

}