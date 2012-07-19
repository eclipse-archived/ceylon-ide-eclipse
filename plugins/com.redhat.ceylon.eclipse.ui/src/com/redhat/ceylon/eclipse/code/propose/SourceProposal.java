package com.redhat.ceylon.eclipse.code.propose;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class SourceProposal implements ICompletionProposal, ICompletionProposalExtension4 {
    /**
     * The text shown to the user in the popup view
     */
    private final String fProposal;

    /**
     * The new text being added/substituted if the user accepts this proposal
     */
    private final String fNewText;

    /**
     * The prefix being completed.
     */
    private final String fPrefix;

    /**
     * The range of text being replaced.
     */
    private final Region fRange;

    /**
     * The offset at which the insertion point should be placed after completing
     * using this proposal
     */
    private final int fCursorLoc;

    /**
     * Additional information displayed in the pop-up view to the right of the
     * main proposal list view when this proposal is selected.
     */
    private final String fAdditionalInfo;

    /**
     * Create a new completion proposal.
     * @param newText the actual replacement text for this proposal
     * @param prefix the prefix being completed
     * @param offset the starting character offset of the text to be replaced
     */
    public SourceProposal(String newText, String prefix, int offset) {
        this(newText, newText, prefix, offset);
    }

    /**
     * Create a new completion proposal.
     * @param proposal the text to be shown in the popup view listing the proposals
     * @param newText the actual replacement text for this proposal
     * @param prefix the prefix being completed
     * @param offset the starting character offset of the text to be replaced
     */
    public SourceProposal(String proposal, String newText, String prefix, int offset) {
        this(proposal, newText, prefix, offset, offset + newText.length() - prefix.length());
    }

    /**
     * Create a new completion proposal.
     * @param proposal the text to be shown in the popup view listing the proposals
     * @param newText the actual replacement text for this proposal
     * @param prefix the prefix being completed
     * @param offset the starting character offset of the text to be replaced
     * @param cursorLoc the point at which to place the cursor after the replacement
     */
    public SourceProposal(String proposal, String newText, String prefix, int offset, int cursorLoc) {
        this(proposal, newText, prefix, new Region(offset, 0), cursorLoc);
    }

    /**
     * Create a new completion proposal.
     * @param proposal the text to be shown in the popup view listing the proposals
     * @param newText the actual replacement text for this proposal
     * @param prefix the prefix being completed
     * @param region the region of text to be replaced
     * @param cursorLoc the point at which to place the cursor after the replacement
     */
    public SourceProposal(String proposal, String newText, String prefix, Region region, String addlInfo) {
        this(proposal, newText, prefix, region, region.getOffset() + newText.length() - prefix.length(), addlInfo);
    }

    /**
     * Create a new completion proposal.
     * @param proposal the text to be shown in the popup view listing the proposals
     * @param newText the actual replacement text for this proposal
     * @param prefix the prefix being completed
     * @param region the region of text to be replaced
     * @param cursorLoc the point at which to place the cursor after the replacement
     */
    public SourceProposal(String proposal, String newText, String prefix, Region region, int cursorLoc) {
        this(proposal, newText, prefix, region, cursorLoc, null);
    }

    /**
     * Create a new completion proposal.
     * @param proposal the text to be shown in the popup view listing the proposals
     * @param newText the actual replacement text for this proposal
     * @param prefix the prefix being completed
     * @param region the region of text to be replaced
     * @param cursorLoc the point at which to place the cursor after the replacement
     * @param addlInfo the text to display in the pop-up view on the right when this
     * proposal is selected
     */
    public SourceProposal(String proposal, String newText, String prefix, Region region, int cursorLoc, String addlInfo) {
        fProposal= proposal;
        fNewText= newText;
        fPrefix= prefix;
        fRange= region;
        fCursorLoc= cursorLoc;
        fAdditionalInfo= addlInfo;
    }

    public void apply(IDocument document) {
        try {
            document.replace(fRange.getOffset(), fRange.getLength(), fNewText.substring(fPrefix.length()));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public Point getSelection(IDocument document) {
        return new Point(fCursorLoc, 0);
    }

    public String getDisplayString() {
        return fProposal;
    }

    public String getAdditionalProposalInfo() {
        return fAdditionalInfo;
    }

    public Image getImage() {
        return null;
    }

    public IContextInformation getContextInformation() {
        return null;
    }
    
    @Override
    public boolean isAutoInsertable() {
    	return true;
    }
}
