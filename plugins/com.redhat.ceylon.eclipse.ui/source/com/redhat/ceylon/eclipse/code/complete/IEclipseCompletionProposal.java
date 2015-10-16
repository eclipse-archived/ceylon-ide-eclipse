package com.redhat.ceylon.eclipse.code.complete;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;

// Needed because Ceylon can't get its head around those two `apply` methods
// coming from different interfaces
public interface IEclipseCompletionProposal extends ICompletionProposal,
        ICompletionProposalExtension2, ICompletionProposalExtension4,
        ICompletionProposalExtension6, ICompletionProposalExtension3 {

    void apply(IDocument document);

    void apply(ITextViewer viewer, char trigger, int stateMask, int offset);
}

interface IEclipseCompletionProposal2And6 extends ICompletionProposal,
        ICompletionProposalExtension2, ICompletionProposalExtension6 {

    void apply(IDocument document);

    void apply(ITextViewer viewer, char trigger, int stateMask, int offset);
}