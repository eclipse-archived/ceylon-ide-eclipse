package com.redhat.ceylon.eclipse.imp.quickfix;

import java.util.Collection;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.eclipse.imp.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider;

class CreateSubtypeProposal implements ICompletionProposal {

    private CeylonEditor editor;
    
    public CreateSubtypeProposal(CeylonEditor editor) {
        this.editor = editor;
    }
    
    @Override
    public Point getSelection(IDocument doc) {
    	return null;
    }

    @Override
    public Image getImage() {
    	return CeylonLabelProvider.CLASS;
    }

    @Override
    public String getDisplayString() {
    	return "Create subtype in new unit";
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
        CreateSubtypeHandler.createSubtype(editor);
    }
    
    public static void add(Collection<ICompletionProposal> proposals, UniversalEditor editor) {
        if (editor instanceof CeylonEditor) {
            if (CreateSubtypeHandler.canCreateSubtype((CeylonEditor) editor)) {
                proposals.add(new CreateSubtypeProposal((CeylonEditor) editor));
            }
        }
    }

}