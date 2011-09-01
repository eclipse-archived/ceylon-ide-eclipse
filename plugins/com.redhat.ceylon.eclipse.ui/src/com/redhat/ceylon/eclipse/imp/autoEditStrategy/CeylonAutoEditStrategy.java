package com.redhat.ceylon.eclipse.imp.autoEditStrategy;

import org.eclipse.imp.services.IAutoEditStrategy;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;

public class CeylonAutoEditStrategy implements IAutoEditStrategy {
    
    public void customizeDocumentCommand(IDocument doc, DocumentCommand cmd) {
        if (cmd.doit == false) {
            return;
        }
        else if (cmd.length == 0 && cmd.text != null && 
                isLineDelimiter(doc, cmd.text)) {
            smartIndentAfterNewline(doc, cmd);
        }
        else if (cmd.text.length() == 1) {
            smartIndentOnKeypress(doc, cmd);
        }
    }
    
    private void smartIndentAfterNewline(IDocument d, DocumentCommand c) {
        if (c.offset == -1 || d.getLength() == 0)
            return;

        try {
            // find start of line
            int p= (c.offset == d.getLength() ? c.offset  - 1 : c.offset);
            IRegion info= d.getLineInformationOfOffset(p);
            int start= info.getOffset();

            // find white spaces
            int end= findEndOfWhiteSpace(d, start, c.offset);

            StringBuffer buf= new StringBuffer(c.text);
            if (end > start) {
                // append to input
                String indent = d.get(start, end - start);
                buf.append(indent);
                String prev = d.get(c.offset-1,1);
                if ("{".equals(prev)) {
                    if (indent.endsWith("    ")) {
                        buf.append("    ");
                    }
                    else if (indent.endsWith("\t")) {
                        buf.append("\t");
                    }
                }
            }
            
            c.text = buf.toString();

        } 
        catch (BadLocationException bleid ) {
            // stop work
        }
    }
    
    private void smartIndentOnKeypress(IDocument d, DocumentCommand c) {
        try {
            if (c.text.equals("}")) {
                if (d.get(c.offset-4,4).equals("    ")) {
                    c.offset = c.offset-4;
                    c.length = 4;
                }
                else if (d.get(c.offset-1,1).equals("\t")) {
                    c.offset = c.offset-1;
                    c.length = 1;
                }
            }
        }
        catch (BadLocationException ble) {
            
        }
    }
    
    /**
     * Returns the first offset greater than <code>offset</code> and smaller than
     * <code>end</code> whose character is not a space or tab character. If no such
     * offset is found, <code>end</code> is returned.
     *
     * @param document the document to search in
     * @param offset the offset at which searching start
     * @param end the offset at which searching stops
     * @return the offset in the specified range whose character is not a space or tab
     * @exception BadLocationException if position is an invalid range in the given document
     */
    protected int findEndOfWhiteSpace(IDocument document, int offset, int end) throws BadLocationException {
        while (offset < end) {
            char c= document.getChar(offset);
            if (c != ' ' && c != '\t') {
                return offset;
            }
            offset++;
        }
        return end;
    }

    private boolean isLineDelimiter(IDocument doc, String text) {
        String[] delimiters = doc.getLegalLineDelimiters();
        if (delimiters != null) {
            return TextUtilities.endsWith(delimiters, text) != -1;
        }
        return false;
    }
}
