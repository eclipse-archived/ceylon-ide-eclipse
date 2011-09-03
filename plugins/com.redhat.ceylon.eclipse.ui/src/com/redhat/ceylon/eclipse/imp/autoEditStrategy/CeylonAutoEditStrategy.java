package com.redhat.ceylon.eclipse.imp.autoEditStrategy;

import static org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH;
import static org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS;

import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.services.IAutoEditStrategy;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;

public class CeylonAutoEditStrategy implements IAutoEditStrategy {
    
    public void customizeDocumentCommand(IDocument doc, DocumentCommand cmd) {
        if (cmd.doit == false) {
            return;
        }
        else if (cmd.length==0 && cmd.text!=null && 
                isLineEnding(doc, cmd.text)) {
            smartIndentAfterNewline(doc, cmd);
        }
        else if (cmd.text.length()==1) {
            smartIndentOnKeypress(doc, cmd);
        }
    }
    
    private void smartIndentAfterNewline(IDocument d, DocumentCommand c) {
        
        if (c.offset==-1 || d.getLength()==0) {
            return;
        }

        try {
            int start = getStartOfCurrentLine(d, c);
            int end = findEndOfWhiteSpace(d, start, c.offset);
            //if (end > start) {
                indentNewLine(d, c, start, end);
            //}
        } 
        catch (BadLocationException bleid ) {}
    }

    private void smartIndentOnKeypress(IDocument d, DocumentCommand c) {
        try {
            adjustIndentOfCurrentLine(d, c);
        }
        catch (BadLocationException ble) {}
    }

    private void adjustIndentOfCurrentLine(IDocument d, DocumentCommand c)
            throws BadLocationException {
        if (c.text.charAt(0)=='}') {
            int spaces = getIndentSpaces();
            if (endsWithSpaces(d.get(c.offset-spaces, spaces),spaces)) {
                c.offset = c.offset-spaces;
                c.length = spaces;
            }
            else if (d.get(c.offset-1,1).equals("\t")) {
                c.offset = c.offset-1;
                c.length = 1;
            }
        }
    }
    
    private void indentNewLine(IDocument d, DocumentCommand c, int start, int end)
            throws BadLocationException {
        StringBuilder buf= new StringBuilder(c.text);
        char prev = d.get(c.offset-1,1).charAt(0);
        // append to input
        if (end>start) {
            String indent = d.get(start, end-start);
            buf.append(indent);
            if (prev=='{') {
                //increment the indent level
                int spaces = getIndentSpaces();
                if (indent.length()>=spaces && 
                        endsWithSpaces(indent,spaces)) {
                    for (int i=1; i<=spaces; i++) {
                        buf.append(' ');                            
                    }
                }
                else if (indent.length()>=0 &&
                        indent.charAt(indent.length()-1)=='\t') {
                    buf.append('\t');
                }
            }
        }
        else {
            if (prev=='{') {
                //guess an initial indent level
                int spaces = getIndentSpaces();
                if (getIndentWithSpaces()) {
                    for (int i=1; i<=spaces; i++) {
                        buf.append(' ');                            
                    }
                }
                else {
                    buf.append('\t');
                }
            }
        }
        c.text = buf.toString();
    }

    private int getStartOfCurrentLine(IDocument d, DocumentCommand c) 
            throws BadLocationException {
        int p = c.offset == d.getLength() ? c.offset-1 : c.offset;
        return d.getLineInformationOfOffset(p).getOffset();
    }
    
    private boolean endsWithSpaces(String string, int spaces) {
        for (int i=1; i<=spaces; i++) {
            if (string.charAt(string.length()-i)!=' ') {
                return false;
            }
        }
        return true;
    }
 
    private int getIndentSpaces() {
        return Platform.getPreferencesService()
                .getInt("org.eclipse.ui.editors", EDITOR_TAB_WIDTH, 4, null);
    }
    
    private boolean getIndentWithSpaces() {
        return Platform.getPreferencesService()
                .getBoolean("org.eclipse.ui.editors", EDITOR_SPACES_FOR_TABS, false, null);
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
            if (c!=' ' && c!='\t') {
                return offset;
            }
            offset++;
        }
        return end;
    }

    private boolean isLineEnding(IDocument doc, String text) {
        String[] delimiters = doc.getLegalLineDelimiters();
        if (delimiters != null) {
            return TextUtilities.endsWith(delimiters, text)!=-1;
        }
        return false;
    }
}
