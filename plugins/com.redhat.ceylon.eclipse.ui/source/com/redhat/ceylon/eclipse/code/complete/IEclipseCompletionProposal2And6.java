package com.redhat.ceylon.eclipse.code.complete;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;

public interface IEclipseCompletionProposal2And6 extends ICompletionProposal,
        ICompletionProposalExtension2, ICompletionProposalExtension6 {

    void apply(IDocument document);

    void apply(ITextViewer viewer, char trigger, int stateMask, int offset);
}