package com.redhat.ceylon.eclipse.imp.proposals;

import org.eclipse.imp.editor.SourceProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

class Proposal extends SourceProposal {
    
    private final String text;
    private final Image image;
    private final boolean selectParams;
    private final int offset;
    private final String prefix;
    
    Proposal(int offset, String prefix, Image image, String doc, 
            String desc, String text, boolean selectParams) {
        super(desc, text, "", 
                new Region(offset-prefix.length(), prefix.length()), 
                offset + text.length(), doc);
        this.text=text;
        this.image = image;
        this.selectParams = selectParams;
        this.offset = offset;
        this.prefix = prefix;
    }
    
    @Override
    public Image getImage() {
        return image;
    }
    @Override
    public Point getSelection(IDocument document) {
        /*if (text.endsWith("= ")) {
                return new Point(offset-prefix.length()+text.length(), 0);
            }
        else*/ 
        if (selectParams) {
            int locOfTypeArgs = text.indexOf('<');
            int loc = locOfTypeArgs;
            if (loc<0) loc = text.indexOf('(');
            if (loc<0) loc = text.indexOf('=')+1;
            int start;
            int length;
            if (loc<=0 || locOfTypeArgs<0 &&
                    (text.contains("()") || text.contains("{}"))) {
                start = text.endsWith("{}") ? text.length()-1 : text.length();
                length = 0;
            }
            else {
                int endOfTypeArgs = text.indexOf('>'); 
                int end = text.indexOf(',');
                if (end<0) end = text.indexOf(';');
                if (end<0) end = text.length()-1;
                if (endOfTypeArgs>0) end = end < endOfTypeArgs ? end : endOfTypeArgs;
                start = loc+1;
                length = end-loc-1;
            }
            return new Point(offset-prefix.length() + start, length);
        }
        else {
            int loc = text.indexOf("bottom;");
            int length;
            int start;
            if (loc<0) {
                start = offset + text.length()-prefix.length();
                if (text.endsWith("{}")) start--;
                length = 0;
            }
            else {
                start = offset + loc-prefix.length();
                length = 6;
            }
            return new Point(start, length);
        }
    }
}