package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.styleProposal;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;


public class CompletionProposal implements ICompletionProposal, 
        /*ICompletionProposalExtension,*/ ICompletionProposalExtension4, 
        ICompletionProposalExtension6 {
    
    protected final String text;
    private final Image image;
    protected final String prefix;
    private final String description;
    protected int offset;
    
    public CompletionProposal(int offset, String prefix, Image image,
            String desc, String text) {
        this.text = text;
        this.image = image;
        this.offset = offset;
        this.prefix = prefix;
        this.description = desc;
        Assert.isNotNull(description);
    }
    
    @Override
    public Image getImage() {
        return image;
    }
    
    @Override
    public Point getSelection(IDocument document) {
        return new Point(offset + text.length() - prefix.length(), 0);
    }
    
    public void apply(IDocument document) {
        try {
            document.replace(offset-prefix.length(), 
                    prefix.length(), text);
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    public String getDisplayString() {
        return description;
    }

    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public boolean isAutoInsertable() {
        return true;
    }

    @Override
    public StyledString getStyledDisplayString() {
        StyledString result = new StyledString();
        styleProposal(result, getDisplayString());
        return result;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }
    
}