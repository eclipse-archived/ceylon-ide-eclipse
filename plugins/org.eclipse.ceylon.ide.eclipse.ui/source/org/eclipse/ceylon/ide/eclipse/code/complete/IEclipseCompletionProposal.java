/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.complete;

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