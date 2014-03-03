package com.redhat.ceylon.eclipse.code.complete;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class LinkedModeCompletionProposal 
        implements ICompletionProposal {
    
    private final String text;
    private final Image image;
    private final int offset;
    private int position;
    
    public LinkedModeCompletionProposal(int offset, String text, 
            int position) {
        this(offset, text, position, null);
    }
    
    public LinkedModeCompletionProposal(int offset, String text, 
            int position, Image image) {
        this.text=text;
        this.position = position;
        this.image = image;
        this.offset = offset;
    }
    
    @Override
    public Image getImage() {
        return image;
    }
    
    @Override
    public Point getSelection(IDocument document) {
        return new Point(offset + text.length(), 0);
    }
    
    public void apply(IDocument document) {
        try {
            int start = offset;
            int length = 0;
            int count = 0;
            for (int i=offset;
                    i<document.getLength(); 
                    i++) {
                if (Character.isWhitespace(document.getChar(i))) {
                    if (count++==position) {
                        break;
                    }
                    else {
                        start = i+1;
                        length = -1;
                    }
                }
                length++;
            }
            document.replace(start, length, text);
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    public String getDisplayString() {
        return text;
    }
    
    public String getAdditionalProposalInfo() {
        return null;
    }
    
    @Override
    public IContextInformation getContextInformation() {
        return null;
    }
    
}