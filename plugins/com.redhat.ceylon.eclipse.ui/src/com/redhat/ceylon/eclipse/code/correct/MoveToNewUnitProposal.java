package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.move.MoveUtil.canMoveDeclaration;
import static com.redhat.ceylon.eclipse.code.move.MoveUtil.moveDeclaration;

import java.util.Collection;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;

class MoveToNewUnitProposal implements ICompletionProposal {

    private CeylonEditor editor;
    
    public MoveToNewUnitProposal(CeylonEditor editor) {
        this.editor = editor;
    }
    
    @Override
    public Point getSelection(IDocument doc) {
        return null;
    }

    @Override
    public Image getImage() {
        return CeylonLabelProvider.MOVE;
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
            moveDeclaration(editor);
        } 
        catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    static void add(Collection<ICompletionProposal> proposals, CeylonEditor editor) {
        if (canMoveDeclaration(editor)) {
            proposals.add(new MoveToNewUnitProposal(editor));
        }
    }

}