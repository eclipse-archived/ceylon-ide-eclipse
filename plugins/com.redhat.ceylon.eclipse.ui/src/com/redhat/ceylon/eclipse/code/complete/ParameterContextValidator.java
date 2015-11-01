package com.redhat.ceylon.eclipse.code.complete;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;

import com.redhat.ceylon.eclipse.code.complete.InvocationCompletionProposal.ParameterContextInformation;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewer;

class ParameterContextValidator 
        implements IContextInformationValidator, IContextInformationPresenter {
    
    private int position;
    private IContextInformation information;
    private int currentParameter;
    private CeylonEditor editor;
    
    ParameterContextValidator(CeylonEditor editor) {
        this.editor = editor;
    }

    @Override
    public boolean updatePresentation(int brokenPosition, 
            TextPresentation presentation) {
        
        String s = information.getInformationDisplayString();
        presentation.clear();
        
        if (this.position==-1) {
            presentation.addStyleRange(new StyleRange(0, s.length(), 
                    null, null, SWT.BOLD));
            addItalics(presentation, s);
            return true;
        }
        
        int currentParameter = -1;
        CeylonSourceViewer viewer = editor.getCeylonSourceViewer();
        int position = viewer.getSelectedRange().x;
        IDocument doc = viewer.getDocument();
        try {
            boolean namedInvocation = doc.getChar(this.position)=='{';
            if (!namedInvocation) Assert.isTrue(doc.getChar(this.position)=='(');
//            int paren = doc.get(this.position, position-this.position)
//                    .indexOf(namedInvocation?'{':'(');
//            if (paren<0) { //TODO: is this really useful?
//                this.position = doc.get(0, position).lastIndexOf('(');
//            }
            currentParameter = getCharCount(doc, 
                    this.position+1, position, 
                    namedInvocation?";":",", "", true);
        } 
        catch (BadLocationException x) {
            return false;
        }

        if (currentParameter != -1) {
            if (this.currentParameter == currentParameter) {
                return false;
            }
        }

        presentation.clear();
        this.currentParameter = currentParameter;

        int[] commas = computeCommaPositions(s);

        if (commas.length - 2 < currentParameter) {
            presentation.addStyleRange(new StyleRange(0, s.length(), 
                    null, null, SWT.NORMAL));
            addItalics(presentation, s);
            return true;
        }

        int start = commas[currentParameter] + 1;
        int end = commas[currentParameter + 1];
        if (start > 0) {
            presentation.addStyleRange(new StyleRange(0, start, 
                    null, null, SWT.NORMAL));
        }
        if (end > start) {
            presentation.addStyleRange(new StyleRange(start, end - start, 
                    null, null, SWT.BOLD));
        }
        if (end < s.length()) {
            presentation.addStyleRange(new StyleRange(end, s.length() - end, 
                    null, null, SWT.NORMAL));
        }
        
        addItalics(presentation, s);
        
        return true;
    }

    private void addItalics(TextPresentation presentation, String s) {
        Matcher m2 = p2.matcher(s);
        while (m2.find()) {
            presentation.mergeStyleRange(new StyleRange(m2.start(), m2.end()-m2.start(), 
                    null, null, SWT.ITALIC));
        }
//      Matcher m1 = p1.matcher(s);
//      while (m1.find()) {
//          presentation.mergeStyleRange(new StyleRange(m1.start(), m1.end()-m1.start()+1, 
//                  typeColor, null));
//      }
    }

//    final Pattern p1 = Pattern.compile("\\b\\p{javaUpperCase}\\w*\\b");
    final Pattern p2 = Pattern.compile("\\b\\p{javaLowerCase}\\w*\\b");
//    final Color typeColor = color(getCurrentTheme().getColorRegistry(), TYPES);

    @Override
    public void install(IContextInformation info, ITextViewer viewer, 
            int documentPosition) {
        if (info instanceof InvocationCompletionProposal.ParameterContextInformation) {
            ParameterContextInformation pci = 
                    (InvocationCompletionProposal.ParameterContextInformation) info;
            this.position = pci.getArgumentListOffset();
        }
        else {
            this.position = -1;
        }
        Assert.isTrue(viewer==editor.getCeylonSourceViewer());
        this.information = info;
        this.currentParameter= -1;
    }
    
    @Override
    public boolean isContextInformationValid(int brokenPosition) {
        if (editor.isInLinkedMode()) {
            Object linkedModeOwner = editor.getLinkedModeOwner();
            if (linkedModeOwner instanceof InvocationCompletionProposal ||
                linkedModeOwner instanceof RefinementCompletionProposal ||
                linkedModeOwner instanceof com.redhat.ceylon.ide.common.completion.InvocationCompletionProposal ||
                linkedModeOwner instanceof com.redhat.ceylon.ide.common.completion.RefinementCompletionProposal) {
                return true;
            }
        }
        try {
            CeylonSourceViewer viewer = editor.getCeylonSourceViewer();
            int position = viewer.getSelectedRange().x;
            if (position < this.position) {
                return false;
            }
            
            IDocument document = viewer.getDocument();
            IRegion line = 
                    document.getLineInformationOfOffset(this.position);
            
            if (position < line.getOffset() || 
                    position >= document.getLength()) {
                return false;
            }
//            System.out.println(document.get(this.position, position-this.position));
            int semiCount = getCharCount(document, this.position, position, ";", "", true);
            int fenceCount = getCharCount(document, this.position, position, "{(", "})", false);
            return semiCount==0 && fenceCount>0;

        } 
        catch (BadLocationException x) {
            return false;
        }
    }
    
    /*@Override
    public boolean isContextInformationValid(int offset) {
        IContextInformation[] infos= computeContextInformation(viewer, offset);
        if (infos != null && infos.length > 0) {
            for (int i= 0; i < infos.length; i++)
                if (information.equals(infos[i]))
                    return true;
        }
        return false;
    }*/
    
    private static final int NONE = 0;
    private static final int BRACKET = 1;
    private static final int BRACE = 2;
    private static final int PAREN = 3;
    private static final int ANGLE = 4;

    private static int getCharCount(IDocument document, 
            final int start, final int end, 
            String increments, String decrements, 
            boolean considerNesting) 
                    throws BadLocationException {

        Assert.isTrue((increments.length() != 0 || decrements.length() != 0) 
                && !increments.equals(decrements));

        int nestingMode = NONE;
        int nestingLevel = 0;

        int charCount = 0;
        int offset = start;
        char prev = ' ';
        while (offset < end) {
            char curr = document.getChar(offset++);
            switch (curr) {
                case '/':
                    if (offset < end) {
                        char next = document.getChar(offset);
                        if (next == '*') {
                            // a comment starts, advance to the comment end
                            offset= getCommentEnd(document, offset + 1, end);
                        }
                        else if (next == '/') {
                            // '//'-comment: nothing to do anymore on this line
                            int nextLine= document.getLineOfOffset(offset) + 1;
                            if (nextLine == document.getNumberOfLines()) {
                                offset= end;
                            }
                            else {
                                offset= document.getLineOffset(nextLine);
                            }
                        }
                    }
                    break;
                case '*':
                    if (offset < end) {
                        char next= document.getChar(offset);
                        if (next == '/') {
                            // we have been in a comment: forget what we read before
                            charCount= 0;
                            ++ offset;
                        }
                    }
                    break;
                case '"':
                case '\'':
                    offset= getStringEnd(document, offset, end, curr);
                    break;
                case '[':
                    if (considerNesting) {
                        if (nestingMode == BRACKET || nestingMode == NONE) {
                            nestingMode= BRACKET;
                            nestingLevel++;
                        }
                        break;
                    }
                    //$FALL-THROUGH$
                case ']':
                    if (considerNesting) {
                        if (nestingMode == BRACKET) {
                            if (--nestingLevel == 0) {
                                nestingMode= NONE;
                            }
                        }
                        break;
                    }
                    //$FALL-THROUGH$
                case '(':
                    if (considerNesting) {
                        if (nestingMode == ANGLE) {
                            // generics heuristic failed
                            nestingMode=PAREN;
                            nestingLevel= 1;
                        }
                        if (nestingMode == PAREN || nestingMode == NONE) {
                            nestingMode= PAREN;
                            nestingLevel++;
                        }
                        break;
                    }
                    //$FALL-THROUGH$
                case ')':
                    if (considerNesting) {
                        if (nestingMode == PAREN) {
                            if (--nestingLevel == 0) {
                                nestingMode= NONE;
                            }
                        }
                        break;
                    }
                    //$FALL-THROUGH$
                case '{':
                    if (considerNesting) {
                        if (nestingMode == ANGLE) {
                            // generics heuristic failed
                            nestingMode=BRACE;
                            nestingLevel= 1;
                        }
                        if (nestingMode == BRACE || nestingMode == NONE) {
                            nestingMode= BRACE;
                            nestingLevel++;
                        }
                        break;
                    }
                    //$FALL-THROUGH$
                case '}':
                    if (considerNesting) {
                        if (nestingMode == BRACE) {
                            if (--nestingLevel == 0) {
                                nestingMode= NONE;
                            }
                        }
                        break;
                    }
                    //$FALL-THROUGH$
                case '<':
                    if (considerNesting) {
                        if (nestingMode == ANGLE || nestingMode == NONE 
                                /*&& checkGenericsHeuristic(document, offset - 1, start - 1)*/) {
                            nestingMode= ANGLE;
                            nestingLevel++;
                        }
                        break;
                    }
                    //$FALL-THROUGH$
                case '>':
                    if (considerNesting 
                            && prev != '=') { //check that it's not a fat arrow
                        if (nestingMode == ANGLE) {
                            if (--nestingLevel == 0) {
                                nestingMode= NONE;
                            }
                        }
                        break;
                    }
                    //$FALL-THROUGH$
                default:
                    if (nestingLevel==0) {
                        if (increments.indexOf(curr) >= 0) {
                            ++ charCount;
                        }
                        if (decrements.indexOf(curr) >= 0) {
                            -- charCount;
                        }
                    }
            }
        }

        return charCount;
    }

    static int findCharCount(int count, IDocument document, 
            final int start, final int end, 
            String increments, String decrements, 
            boolean considerNesting) 
                    throws BadLocationException {

        Assert.isTrue((increments.length() != 0 || decrements.length() != 0) 
                && !increments.equals(decrements));

        final int NONE= 0;
        final int BRACKET= 1;
        final int BRACE= 2;
        final int PAREN= 3;
        final int ANGLE= 4;

        int nestingMode= NONE;
        int nestingLevel= 0;

        int charCount= 0;
        int offset= start;
        boolean lastWasEquals = false;
        while (offset < end) {
            if (nestingLevel == 0) {
                if (count==charCount) {
                    return offset-1;
                }
            }
            char curr= document.getChar(offset++);
            switch (curr) {
                case '/':
                    if (offset < end) {
                        char next= document.getChar(offset);
                        if (next == '*') {
                            // a comment starts, advance to the comment end
                            offset= getCommentEnd(document, offset + 1, end);
                        }
                        else if (next == '/') {
                            // '//'-comment: nothing to do anymore on this line
                            int nextLine= document.getLineOfOffset(offset) + 1;
                            if (nextLine == document.getNumberOfLines()) {
                                offset= end;
                            }
                            else {
                                offset= document.getLineOffset(nextLine);
                            }
                        }
                    }
                    break;
                case '*':
                    if (offset < end) {
                        char next= document.getChar(offset);
                        if (next == '/') {
                            // we have been in a comment: forget what we read before
                            charCount= 0;
                            ++ offset;
                        }
                    }
                    break;
                case '"':
                case '\'':
                    offset= getStringEnd(document, offset, end, curr);
                    break;
                case '[':
                    if (considerNesting) {
                        if (nestingMode == BRACKET || nestingMode == NONE) {
                            nestingMode= BRACKET;
                            nestingLevel++;
                        }
                        break;
                    }
                    //$FALL-THROUGH$
                case ']':
                    if (considerNesting) {
                        if (nestingMode == BRACKET)
                            if (--nestingLevel == 0) {
                                nestingMode= NONE;
                            }
                        break;
                    }
                    //$FALL-THROUGH$
                case '(':
                    if (considerNesting) {
                        if (nestingMode == ANGLE) {
                            // generics heuristic failed
                            nestingMode=PAREN;
                            nestingLevel= 1;
                        }
                        if (nestingMode == PAREN || nestingMode == NONE) {
                            nestingMode= PAREN;
                            nestingLevel++;
                        }
                        break;
                    }
                    //$FALL-THROUGH$
                case ')':
                    if (considerNesting) {
                        if (nestingMode == 0) {
                            return offset-1;
                        }
                        if (nestingMode == PAREN) {
                            if (--nestingLevel == 0) {
                                nestingMode= NONE;
                            }
                        }
                        break;
                    }
                    //$FALL-THROUGH$
                case '{':
                    if (considerNesting) {
                        if (nestingMode == ANGLE) {
                            // generics heuristic failed
                            nestingMode=BRACE;
                            nestingLevel= 1;
                        }
                        if (nestingMode == BRACE || nestingMode == NONE) {
                            nestingMode= BRACE;
                            nestingLevel++;
                        }
                        break;
                    }
                    //$FALL-THROUGH$
                case '}':
                    if (considerNesting) {
                        if (nestingMode == 0) {
                            return offset-1;
                        }
                        if (nestingMode == BRACE) {
                            if (--nestingLevel == 0) {
                                nestingMode= NONE;
                            }
                        }
                        break;
                    }
                    //$FALL-THROUGH$
                case '<':
                    if (considerNesting) {
                        if (nestingMode == ANGLE || nestingMode == NONE /*&& checkGenericsHeuristic(document, offset - 1, start - 1)*/) {
                            nestingMode= ANGLE;
                            nestingLevel++;
                        }
                        break;
                    }
                    //$FALL-THROUGH$
                case '>':
                    if (!lastWasEquals) {
                        if (nestingMode == 0) {
                            return offset-1;
                        }
                        if (considerNesting) {
                            if (nestingMode == ANGLE) {
                                if (--nestingLevel == 0) {
                                    nestingMode= NONE;
                                }
                            }
                            break;
                        }
                    }
                    //$FALL-THROUGH$
                default:
                    if (nestingLevel == 0) {
                        if (increments.indexOf(curr) >= 0) {
                            ++ charCount;
                        }
                        if (decrements.indexOf(curr) >= 0) {
                            -- charCount;
                        }
                    }
            }
            lastWasEquals = curr=='=';
        }

        return -1;
    }
    
    private static int[] computeCommaPositions(String code) {
        final int length= code.length();
        int pos = 0;
        int angleLevel = 0;
        List<Integer> positions= new ArrayList<Integer>();
        positions.add(new Integer(-1));
        char prev = ' ';
        while (pos < length && pos != -1) {
            char ch = code.charAt(pos);
            switch (ch) {
                case ',':
                case ';':
                    if (angleLevel == 0) {
                        positions.add(new Integer(pos));
                    }
                    break;
                case '<':
                case '(':
                case '{':
                case '[':
                    angleLevel++;
                    break;
                case '>':
                    if (prev=='=') break;
                case ')':
                case '}':
                case ']':
                    angleLevel--;
                    break;
//                case '[':
//                    pos= code.indexOf(']', pos);
//                    break;
                default:
                    break;
            }
            if (pos != -1) {
                pos++;
            }
        }
        positions.add(new Integer(length));

        int[] fields= new int[positions.size()];
        for (int i= 0; i < fields.length; i++) {
            fields[i]= positions.get(i).intValue();
        }
        return fields;
    }

    private static int getCommentEnd(IDocument d, int pos, int end) 
            throws BadLocationException {
        while (pos < end) {
            char curr= d.getChar(pos);
            pos++;
            if (curr == '*') {
                if (pos < end && d.getChar(pos) == '/') {
                    return pos + 1;
                }
            }
        }
        return end;
    }

    private static int getStringEnd(IDocument d, int pos, int end, char ch) 
            throws BadLocationException {
        while (pos < end) {
            char curr= d.getChar(pos);
            pos++;
            if (curr == '\\') {
                // ignore escaped characters
                pos++;
            }
            else if (curr == ch) {
                return pos;
            }
        }
        return end;
    }
    
}