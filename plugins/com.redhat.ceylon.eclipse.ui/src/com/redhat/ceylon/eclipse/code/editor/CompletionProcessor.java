package com.redhat.ceylon.eclipse.code.editor;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer;

public class CompletionProcessor implements IContentAssistProcessor {
    private final IContextInformation[] NO_CONTEXTS= new IContextInformation[0];

    private ICompletionProposal[] NO_COMPLETIONS= new ICompletionProposal[0];

    private CeylonContentProposer fContentProposer;

    // private HippieProposalProcessor hippieProcessor= new HippieProposalProcessor();

    public CompletionProcessor() {
        fContentProposer= new CeylonContentProposer();
    }

    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
        try {
        	CeylonParseController pc = ((CeylonEditor)Util.getCurrentEditor()).getParseController();
			return fContentProposer.getContentProposals(pc, offset, viewer);
            // TODO Once we move to 3.2, delegate to the HippieProposalProcessor
            // return hippieProcessor.computeCompletionProposals(viewer, offset);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return NO_COMPLETIONS;
    }

    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
        return NO_CONTEXTS;
    }

    public char[] getCompletionProposalAutoActivationCharacters() {
        return ".abcdefghijklmnopqrstuvwxyz".toCharArray();
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
