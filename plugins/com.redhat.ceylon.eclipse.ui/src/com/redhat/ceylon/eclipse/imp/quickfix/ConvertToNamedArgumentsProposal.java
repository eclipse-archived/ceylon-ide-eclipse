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
import com.redhat.ceylon.eclipse.imp.refactoring.ConvertToNamedArgumentsHandler;

class ConvertToNamedArgumentsProposal implements ICompletionProposal {

    private ConvertToNamedArgumentsHandler action;
    
    public ConvertToNamedArgumentsProposal(CeylonEditor editor) {
        action = new ConvertToNamedArgumentsHandler(editor);
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
        ConvertToNamedArgumentsProposal prop = new ConvertToNamedArgumentsProposal((CeylonEditor)editor);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }

}