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
import com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.imp.refactoring.MoveDeclarationHandler;

class MoveDeclarationProposal implements ICompletionProposal {

    private CeylonEditor editor;
    
    public MoveDeclarationProposal(CeylonEditor editor) {
        this.editor = editor;
    }
    
    @Override
    public Point getSelection(IDocument doc) {
    	return null;
    }

    @Override
    public Image getImage() {
    	return CeylonLabelProvider.CHANGE;
    }

    @Override
    public String getDisplayString() {
    	return "Move declaration to new unit";
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
            MoveDeclarationHandler.moveDeclaration(editor);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    public static void add(Collection<ICompletionProposal> proposals, UniversalEditor editor) {
        if (editor instanceof CeylonEditor) {
            if (MoveDeclarationHandler.canMoveDeclaration((CeylonEditor) editor)) {
                proposals.add(new MoveDeclarationProposal((CeylonEditor) editor));
            }
        }
    }

}