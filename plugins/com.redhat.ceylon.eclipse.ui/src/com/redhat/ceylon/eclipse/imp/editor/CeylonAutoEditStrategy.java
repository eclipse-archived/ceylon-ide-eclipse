package com.redhat.ceylon.eclipse.imp.editor;

import static java.lang.Character.isWhitespace;
import static org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH;
import static org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS;

import org.eclipse.core.runtime.Platform;
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
        else if (cmd.length==0 && cmd.text!=null && 
                isLineEnding(doc, cmd.text)) {
            smartIndentAfterNewline(doc, cmd);
        }
        else if (cmd.text.length()==1 || 
                getIndentWithSpaces() && isIndent(cmd.text)) {
            smartIndentOnKeypress(doc, cmd);
        }
    }
    
    public boolean isIndent(String text) {
        if (text.length()==getIndentSpaces()) {
            for (char c: text.toCharArray()) {
                if (c!=' ') return false;
            }
            return true;
        }
        else {
            return false;
        }
    }
    
    private void smartIndentAfterNewline(IDocument d, DocumentCommand c) {
        
        if (c.offset==-1 || d.getLength()==0) {
            return;
        }

        try {
            //if (end > start) {
                indentNewLine(d, c);
            //}
        } 
        catch (BadLocationException bleid ) {
            bleid.printStackTrace();
        }
    }

    private void smartIndentOnKeypress(IDocument d, DocumentCommand c) {

        if (c.offset==-1 || d.getLength()==0) {
            return;
        }
         
        try {
            adjustIndentOfCurrentLine(d, c);
        }
        catch (BadLocationException ble) {
            ble.printStackTrace();
        }
    }

    private void adjustIndentOfCurrentLine(IDocument d, DocumentCommand c)
            throws BadLocationException {

       switch (c.text.charAt(0)) {
           case '}':
               reduceIndentOfCurrentLine(d, c);
               break;
           case '\t':
               fixIndentOfCurrentLine(d, c);
               break;
           default:
               if (isIndent(c.text)) {
                   fixIndentOfCurrentLine(d, c);
               }
       }
    }

    protected void fixIndentOfCurrentLine(IDocument d, DocumentCommand c)
            throws BadLocationException {
        int spaces = getIndentSpaces();
        int start = getStartOfCurrentLine(d, c);
        int endOfWs = findEndOfWhiteSpace(d, c);
        if (c.offset<=endOfWs) {
            if (start==0) {
                c.text="";
            }
            else {
                c.offset = getEndOfPreviousLine(d, c);
                c.text = "";
                indentNewLine(d, c);
                if (d.getChar(endOfWs)=='}') {
                    if (endsWithSpaces(c.text, spaces)) {
                        c.text = c.text.substring(0, c.text.length()-spaces);
                    }
                    else if (c.text.endsWith("\t")) {
                        c.text = c.text.substring(0, c.text.length()-1);
                    }
                }
            }
            c.offset=start;
            c.length=endOfWs-start;
            /*if (c.text.length()==c.length) {
                c.text+='\t';
            }*/
        }
    }

    protected void reduceIndentOfCurrentLine(IDocument d, DocumentCommand c)
            throws BadLocationException {
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

    /*private int getStartOfPreviousLine(IDocument d, DocumentCommand c)
            throws BadLocationException {
        return getStartOfPreviousLine(d, c.offset);
    }*/

    private int getStartOfPreviousLine(IDocument d, int offset) 
            throws BadLocationException {
        return d.getLineOffset(d.getLineOfOffset(offset)-1);
    }
    
    private void indentNewLine(IDocument d, DocumentCommand c)
            throws BadLocationException {
        StringBuilder buf = new StringBuilder(c.text);
        char terminator = getPreviousNonWhitespaceCharacterInLine(d, c.offset-1);
        boolean isContinuation = terminator!=';' && terminator!='}' && terminator!='{' &&
                        terminator!='\n'; //ahem, ugly "null"
        String indent = getIndent(d, c);
        if (!indent.isEmpty()) {
            buf.append(indent);
            if (terminator=='{') {
                //increment the indent level
                incrementIndent(buf, indent);
            }
            else {
                if (isContinuation) {
                    incrementIndent(buf, indent);
                    incrementIndent(buf, indent);
                }
            }
        }
        else {
            if (terminator=='{') {
                initialIndent(buf);
            }
            else if (isContinuation) {
                initialIndent(buf);
                initialIndent(buf);
            }
        }
        c.text = buf.toString();
    }

    private String getIndent(IDocument d, DocumentCommand c) 
            throws BadLocationException {
        int start = getStartOfCurrentLine(d, c);
        int end = getEndOfCurrentLine(d, c);
        while (true) {
            //System.out.println(d.get(start, end-start));
            if (start==0) {
                return "";
            }
            else {
                int startOfPrev = getStartOfPreviousLine(d, start); 
                int endOfPrev = getEndOfPreviousLine(d, start);
                char ch = getLastNonWhitespaceCharacterInLine(d, startOfPrev, endOfPrev);
                if (ch==';' || ch=='{' || ch=='}') break;
                start = startOfPrev;
                end = endOfPrev;
            }
        }
        int endOfWs = firstEndOfWhitespace(d, start, end);
        return d.get(start, endOfWs-start);
    }

    private char getLastNonWhitespaceCharacterInLine(IDocument d, int offset, int end) 
            throws BadLocationException {
        char result = '\n'; //ahem, ugly null!
        for (;offset<end; offset++) {
            char ch = d.getChar(offset);
            if (!isWhitespace(ch)) result=ch;
        }
        return result;
    }
    
    private void incrementIndent(StringBuilder buf, String indent) {
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
    
    private char getPreviousNonWhitespaceCharacterInLine(IDocument d, int offset)
            throws BadLocationException {
        for (;offset>=0; offset--) {
            String ch = d.get(offset,1);
            if (!isWhitespace(ch.charAt(0)) ||
                    isLineEnding(d, ch)) {
                return ch.charAt(0);
            }
        }
        return '\n';
    }

    private static void initialIndent(StringBuilder buf) {
        //guess an initial indent level
        if (getIndentWithSpaces()) {
            int spaces = getIndentSpaces();
            for (int i=1; i<=spaces; i++) {
                buf.append(' ');                            
            }
        }
        else {
            buf.append('\t');
        }
    }

    private int getStartOfCurrentLine(IDocument d, DocumentCommand c) 
            throws BadLocationException {
        int p = c.offset == d.getLength() ? c.offset-1 : c.offset;
        return d.getLineInformationOfOffset(p).getOffset();
    }
    
    private int getEndOfCurrentLine(IDocument d, DocumentCommand c) 
            throws BadLocationException {
        int p = c.offset == d.getLength() ? c.offset-1 : c.offset;
        IRegion lineInfo = d.getLineInformationOfOffset(p);
        return lineInfo.getOffset() + lineInfo.getLength();
    }
    
    private int getEndOfPreviousLine(IDocument d, DocumentCommand c) 
            throws BadLocationException {
        return getEndOfPreviousLine(d, c.offset);
    }

    private int getEndOfPreviousLine(IDocument d, int offset) 
            throws BadLocationException {
        int p = offset == d.getLength() ? offset-1 : offset;
        IRegion lineInfo = d.getLineInformation(d.getLineOfOffset(p)-1);
        return lineInfo.getOffset() + lineInfo.getLength();
    }
    
    private boolean endsWithSpaces(String string, int spaces) {
        if (string.length()<spaces) return false;
        for (int i=1; i<=spaces; i++) {
            if (string.charAt(string.length()-i)!=' ') {
                return false;
            }
        }
        return true;
    }
 
    private static int getIndentSpaces() {
        return Platform.getPreferencesService()
                .getInt("org.eclipse.ui.editors", EDITOR_TAB_WIDTH, 4, null);
    }
    
    private static boolean getIndentWithSpaces() {
        return Platform.getPreferencesService()
                .getBoolean("org.eclipse.ui.editors", EDITOR_SPACES_FOR_TABS, false, null);
    }
    
    public static String getDefaultIndent() {
        StringBuilder result = new StringBuilder();
        initialIndent(result);
        return result.toString();
    }
    
    private int findEndOfWhiteSpace(IDocument d, DocumentCommand c) 
            throws BadLocationException {
        int offset = getStartOfCurrentLine(d, c);
        int end = getEndOfCurrentLine(d, c);
        return firstEndOfWhitespace(d, offset, end);
    }

    /**
     * Returns the first offset greater than <code>offset</code> and smaller than
     * <code>end</code> whose character is not a space or tab character. If no such
     * offset is found, <code>end</code> is returned.
     *
     * @param d the document to search in
     * @param offset the offset at which searching start
     * @param end the offset at which searching stops
     * @return the offset in the specified range whose character is not a space or tab
     * @exception BadLocationException if position is an invalid range in the given document
     */
    private int firstEndOfWhitespace(IDocument d, int offset, int end)
            throws BadLocationException {
        while (offset < end) {
            char ch= d.getChar(offset);
            if (ch!=' ' && ch!='\t') {
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
