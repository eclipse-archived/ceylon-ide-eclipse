package com.redhat.ceylon.eclipse.code.propose;

import static java.lang.Character.isWhitespace;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class CompletionProcessor implements IContentAssistProcessor {
	
	private final IContextInformation[] NO_CONTEXTS= new IContextInformation[0];
	private ICompletionProposal[] NO_COMPLETIONS= new ICompletionProposal[0];

    private CeylonContentProposer contentProposer;
    
    private CeylonEditor editor;
    
    private boolean filter;
    private int lastOffset=-1;
    
    public void sessionStarted() {
    	filter = false;
    	lastOffset=-1;
    }

    // private HippieProposalProcessor hippieProcessor= new HippieProposalProcessor();

    public CompletionProcessor(CeylonEditor editor) {
        contentProposer= new CeylonContentProposer();
        this.editor=editor;
    }

    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
    	try {
			if (lastOffset>=0 && offset>0 && 
					isWhitespace(viewer.getDocument().get(offset-1, 1).charAt(0))) {
				//user typed a whitespace char with an open
				//completions window, so close the window
				return NO_COMPLETIONS;
			}
		} 
    	catch (BadLocationException e1) {
			e1.printStackTrace();
    		return NO_COMPLETIONS;
		}
		if (offset==lastOffset) {
			filter = !filter;
		}
		lastOffset = offset;
    	try {
    		return contentProposer.getContentProposals(editor.getParseController(), 
    				offset, viewer, filter);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return NO_COMPLETIONS;
    	}
    }

    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
        return NO_CONTEXTS;
    }

    public char[] getCompletionProposalAutoActivationCharacters() {
        return ".abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    }

    public char[] getContextInformationAutoActivationCharacters() {
        return null;
    }

    public IContextInformationValidator getContextInformationValidator() {
        return null;
    }

    public String getErrorMessage() {
        return null;
    }
    
}
