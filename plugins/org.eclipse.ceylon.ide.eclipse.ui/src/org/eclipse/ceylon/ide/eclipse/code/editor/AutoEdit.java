/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.ASTRING_LITERAL;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.AVERBATIM_STRING;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.CHAR_LITERAL;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.LINE_COMMENT;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.MULTI_COMMENT;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_END;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_LITERAL;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_MID;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_START;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.VERBATIM_STRING;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.CLOSE_ANGLES;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.CLOSE_BACKTICKS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.CLOSE_BRACES;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.CLOSE_BRACKETS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.CLOSE_PARENS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.CLOSE_QUOTES;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getTokenIndexAtCharacter;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getTokenIterator;
import static java.lang.Character.isDigit;
import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isLetter;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.isWhitespace;
import static org.antlr.runtime.Token.HIDDEN_CHANNEL;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;

import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

import org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import org.eclipse.ceylon.ide.eclipse.util.Nodes;

class AutoEdit {
    
    public AutoEdit(IDocument document, 
            List<CommonToken> tokens,
            DocumentCommand command) {
        this.document = document;
        this.tokens = tokens;
        this.command = command;
    }

    private IDocument document;
    private List<CommonToken> tokens;
    private DocumentCommand command;
    
    //TODO: when pasting inside an existing string literal, should
    //      we automagically escape unescaped quotes in the pasted
    //      text?
    
    public void customizeDocumentCommand() {
        
        //Note that Correct Indentation sends us a tab
        //character at the start of each line of selected
        //text. This is amazingly sucky because it's very
        //difficult to distinguish Correct Indentation from
        //an actual typed tab.
        //Note also that typed tabs are replaced with spaces
        //before this method is called if the spacesfortabs 
        //setting is enabled.
        
        if (command.text!=null) { 
            //command.length>0 means we are replacing or deleting text
            if (command.length==0) {
                if (command.text.isEmpty()) {
                    //workaround for a really annoying bug where we 
                    //get sent "" instead of "\t" or "    " by IMP
                    //reconstruct what we would have been sent 
                    //without the bug
                    if (getIndentWithSpaces()) {
                        int overhang = 
                                getPrefix().length() % 
                                getIndentSpaces();
                        command.text = 
                                getDefaultIndent()
                                        .substring(overhang);
                    }
                    else {
                        command.text = "\t";
                    }
                    smartIndentOnKeypress();
                }
                else if (isLineEnding(command.text)) {
                    //a typed newline (might have length 1 or 2, 
                    //depending on the platform)
                    smartIndentAfterNewline();
                }
                else if (command.text.length()==1 || 
                        //when spacesfortabs is enabled, we get 
                        //sent spaces instead of a tab - the right
                        //number of spaces to take us to the next
                        //tab stop
                        getIndentWithSpaces() && 
                                isIndent(getPrefix())) {
                    //anything that might represent a single 
                    //keypress or a Correct Indentation
                    smartIndentOnKeypress();
                }
            }

            if (command.text!=null &&
                    command.text.length()==1 &&
                    command.length==0) {
                closeOpening();
            }
        }
    }

    private String getDefaultIndent() {
        return utilJ2C().indents().getDefaultIndent();
    }

    private int getIndentSpaces() {
        return (int) utilJ2C().indents().getIndentSpaces();
    }

    private boolean getIndentWithSpaces() {
        return utilJ2C().indents().getIndentWithSpaces();
    }

    private static String[][] FENCES = {
            { "'", "'", CLOSE_QUOTES },
            { "\"", "\"", CLOSE_QUOTES },
            { "`", "`", CLOSE_BACKTICKS },
            { "<", ">", CLOSE_ANGLES },
            { "(", ")", CLOSE_PARENS },
            { "[", "]", CLOSE_BRACKETS }};

    private void closeOpening() {
        
        if (isCommented(command.offset)) {
            return;
        }
        
        boolean quoted = isQuoted(command.offset);
        String current = command.text;
        
        try {
            // don't close fences after a backslash escape
            // TODO: improve this, check the surrounding 
            //       token type!
            if (command.offset>0 &&
                document.getChar(command.offset-1)=='\\') {
                return;
            }
            // don't close fences before an identifier,
            // literal or opening paren
            if (!quoted &&
                    //there are special rules for < below
                    !current.equals("<")) {
                int curr = command.offset;
                while (curr<document.getLength()) {
                    char ch = document.getChar(curr++);
                    if (isLetter(ch) || 
                        isDigit(ch) ||
                        ch=='(') {
                        return;
                    }
                    if (ch=='"' &&
                        !"\"".equals(current) &&
                        !"`".equals(current)) {
                        return;
                    }
                    if (ch=='\'' &&
                        !"\'".equals(current)) {
                        return;
                    }
                    if (ch!=' ') {
                        break;
                    }
                }
            }
        } 
        catch (BadLocationException e) {}

        String opening = null;
        String closing = null;
        
        boolean found=false;
        IPreferenceStore store = CeylonPlugin.getPreferences();
        for (String[] type: FENCES) {
            if (type[0].equals(current) || 
                    type[1].equals(current)) {
                if (store==null || 
                        store.getBoolean(type[2])) {
                    opening = type[0];
                    closing = type[1];
                    found = true;
                    break;
                }
            }
        }
        
        if (found) {
        
            if (current.equals(closing)) {
                //typed character is a closing fence
                try {
                    // skip one ahead if next char is already a closing fence
                    if (skipClosingFence(closing) &&
                            closeOpeningFence(opening, closing)) {
                        command.text = null;
                        command.shiftsCaret = false;
                        command.caretOffset = command.offset + 1;
                        return;
                    }
                } 
                catch (BadLocationException e) {}
            }

            boolean isInterpolation = 
                    isGraveAccentCharacterInStringLiteral(command.offset, opening);
            boolean isDocLink = 
                    isOpeningBracketInAnnotationStringLiteral(command.offset, opening);
            if (current.equals(opening) && 
                    (isInterpolation || isDocLink || !quoted)) {
                //typed character is an opening fence
                if (isInterpolation || isDocLink ||
                        closeOpeningFence(opening, closing)) {
                    //add a closing fence
                    command.shiftsCaret = false;
                    command.caretOffset = command.offset + 1;
                    if (isInterpolation) {
                        try {
                            if (command.offset>1 &&
                                    document.get(command.offset-1,1)
                                            .equals("`") &&
                                    !document.get(command.offset-2,1)
                                            .equals("`")) {
                                command.text += "``";
                            }
                        } 
                        catch (BadLocationException e) {}
                    }
                    else if (isDocLink) {
                        try {
                            if (command.offset>1 &&
                                    document.get(command.offset-1,1)
                                            .equals("[") &&
                                    !document.get(command.offset-2,1)
                                            .equals("]")) {
                                command.text += "]]";
                            }
                        } 
                        catch (BadLocationException e) {}
                    }
                    else if (opening.equals("\"")) {
                        try {
                            if (command.offset<=1 ||
                                    !document.get(command.offset-1,1)
                                             .equals("\"")) {
                                command.text += closing;
                            }
                            else if (command.offset>1 &&
                                    document.get(command.offset-2,2)
                                            .equals("\"\"") &&
                                    !(command.offset>2 && 
                                            document.get(command.offset-3,1)
                                                    .equals("\""))) {
                                command.text += "\"\"\"";
                            }
                        } 
                        catch (BadLocationException e) {}
                    }
                    else {
                        command.text += closing;
                    }
                }
            }

        }
    }

    private boolean skipClosingFence(String closing) 
            throws BadLocationException {
        char ch = document.getChar(command.offset);
        return String.valueOf(ch).equals(closing);
    }

    private boolean closeOpeningFence(String opening, String closing) {
        if (opening.equals("<")) {
            // only close angle brackets if it's after 
            // an uppercase identifier or open fence
            int currOffset = command.offset;
            try {
                //TODO: eat whitespace
                char ch = document.getChar(currOffset-1);
                if (ch=='{'||ch=='('||ch=='['||ch=='<'||ch==',') {
                    return !isJavaIdentifierPart(document.getChar(currOffset));
                }
                while (isJavaIdentifierPart(ch) &&
                        --currOffset>0) {
                    ch = document.getChar(currOffset-1);
                }
                return currOffset<command.offset &&
                        isUpperCase(document.getChar(currOffset));
            }
            catch (BadLocationException e) {
                return false;
            }
        }
        else {
            if (opening.equals(closing)) { 
                return count(opening)%2==0;
            }
            else { 
                return count(opening)>=count(closing);
            }

        }
    }

    private String getPrefix() {
        try {
            int lineOffset = 
                    getStartOfCurrentLine();
            return document.get(lineOffset, 
                    command.offset-lineOffset) + 
                    command.text;
        } 
        catch (BadLocationException e) {
            return command.text;
        }
    }
    
    private boolean isIndent(String text) {
        if (!text.isEmpty() && 
                text.length() % getIndentSpaces()==0) {
            for (char c: text.toCharArray()) {
                if (c!=' ') return false;
            }
            return true;
        }
        else {
            return false;
        }
    }
    
    private void smartIndentAfterNewline() {
        if (command.offset==-1 || 
                document.getLength()==0) {
            return;
        }

        try {
            //if (end > start) {
                indentNewLine();
            //}
        } 
        catch (BadLocationException bleid ) {
            bleid.printStackTrace();
        }
    }

    private void smartIndentOnKeypress() {
        if (command.offset==-1 || 
            document.getLength()==0) {
            return;
        }
         
        try {
            adjustIndentOfCurrentLine();
        }
        catch (BadLocationException ble) {
            ble.printStackTrace();
        }
    }
    
    private boolean isQuoted(int offset) {
        int tokenType = 
                getTokenTypeStrictlyContainingOffset(offset);
        return isStringToken(tokenType);
    }

    private boolean isStringToken(int type) {
        return type==STRING_LITERAL || 
                type==STRING_MID ||
                type==STRING_START ||
                type==STRING_END ||
                type==VERBATIM_STRING ||
                type==ASTRING_LITERAL ||
                type==AVERBATIM_STRING ||
                type==MULTI_COMMENT ||
                type==CHAR_LITERAL; //doesn't really belong here
    }

    private boolean isCommented(int offset) {
        int tokenType = 
                getTokenTypeStrictlyContainingOffset(offset);
        return isCommentToken(tokenType);
    }

    private boolean isQuotedOrCommented(int offset) {
        int tokenType = 
                getTokenTypeStrictlyContainingOffset(offset);
        return isQuoteOrCommentToken(tokenType);
    }

    private boolean isQuoteOrCommentToken(int type) {
        return type==STRING_LITERAL ||
                type==STRING_MID ||
                type==STRING_START ||
                type==STRING_END ||
                type==VERBATIM_STRING ||
                type==ASTRING_LITERAL ||
                type==AVERBATIM_STRING ||
                type==CHAR_LITERAL || 
                type==LINE_COMMENT ||
                type==MULTI_COMMENT;
    }
    
    private boolean isCommentToken(int type) {
        return type==LINE_COMMENT ||
                type==MULTI_COMMENT;
    }
    
    private boolean isGraveAccentCharacterInStringLiteral(int offset, 
            String fence) {
        if ("`".equals(fence)) {
            int type = getTokenTypeStrictlyContainingOffset(offset);
            return type == STRING_LITERAL ||
                    type == STRING_START ||
                    type == STRING_END ||
                    type == STRING_MID;
        }
        else {
            return false;
        }
    }

    private boolean isOpeningBracketInAnnotationStringLiteral(int offset, 
            String fence) {
        if ("[".equals(fence)) {
            int type = getTokenTypeStrictlyContainingOffset(offset);
            //damn, AutoEdit can now no longer 
            //distinguish annotation strings :(
            return type == ASTRING_LITERAL ||
                    type == AVERBATIM_STRING;
        }
        else {
            return false;
        }
    }
    
    private boolean isInUnterminatedMultilineComment(int offset, 
            IDocument d) {
        CommonToken token = 
                getTokenStrictlyContainingOffset(offset);
        if (token==null) return false;
        try {
            return token.getType()==MULTI_COMMENT && 
                    !token.getText().endsWith("*/") &&
                    d.getLineOfOffset(offset)+1==token.getLine();
        } 
        catch (BadLocationException e) {
            return false;
        }
    }
    
    private String getRelativeIndent(int offset) {
        int indent = getStringOrCommentIndent(offset);
        try {
            IRegion lineInfo = 
                    document.getLineInformationOfOffset(offset);
            StringBuilder result = new StringBuilder();
            int lineOffset = lineInfo.getOffset();
            for (int i = lineOffset; 
                    i<lineOffset+indent; 
                    i++) {
                char ch = document.getChar(i);
                if (ch!=' ' && ch!='\t') {
                    return "";
                }
            }
            for (int i = lineOffset+indent;;) {
                char ch = document.getChar(i++);
                if (ch==' '||ch=='\t') {
                    result.append(ch);
                }
                else {
                    break;
                }
            }
            return result.toString();
        }
        catch (BadLocationException e) {
            e.printStackTrace();
            return "";
        }
    }

    private int getTokenTypeStrictlyContainingOffset(int offset) {
        CommonToken token = 
                getTokenStrictlyContainingOffset(offset);
        return token==null ? -1 : token.getType();
    }
    
    private int getTokenTypeOfCharacterAtOffset(int offset) {
        int tokenIndex = 
                getTokenIndexAtCharacter(tokens, offset);
        if (tokenIndex>=0) {
            CommonToken token = tokens.get(tokenIndex);
            return token.getType();
        }
        return -1;
    }
    
    private CommonToken getTokenStrictlyContainingOffset(int offset) {
        return Nodes.getTokenStrictlyContainingOffset(offset, 
                getTokens());
    }

    private int getStringOrCommentIndent(int offset) {
        CommonToken tokenContainingOffset = 
                getTokenStrictlyContainingOffset(offset);
        CommonToken token = 
                getStartOfStringToken(tokenContainingOffset);
        if (token!=null) {
            int type = token.getType();
            int start = token.getCharPositionInLine();
            if (token.getStartIndex()<offset) {
                switch (type) {
                case STRING_LITERAL:
                case STRING_START:
                case ASTRING_LITERAL:
                    return start+1;
                case STRING_MID:
                case STRING_END:
                case MULTI_COMMENT:
                    return start+1;
                    //uncomment to get a bigger indent
//                    return start+3;
                case VERBATIM_STRING: 
                case AVERBATIM_STRING:
                    return start+3;
                }
            }
        }
        return -1;
    }

    private CommonToken getStartOfStringToken(CommonToken token) {
        if (token==null) {
            return null;
        }
        int type = token.getType();
        if (type==STRING_MID||type==STRING_END) {
            while (type!=STRING_START) {
                int index = token.getTokenIndex();
                if (index==0) {
                    return null;
                }
                token = tokens.get(index-1);
                type = token.getType(); 
            }
        }
        return token;
    }
    
    private void adjustIndentOfCurrentLine()
            throws BadLocationException {
        char ch = command.text.charAt(0);
        if (isQuotedOrCommented(command.offset)) {
            if (ch=='\t' || 
                    getIndentWithSpaces() && 
                    isIndent(getPrefix())) {
                fixIndentOfStringOrCommentContinuation();
            }
        }
        else {
            switch (ch) {
            case '}':
            case ')':
                reduceIndentOfCurrentLine();
                break;
            case '\t':
            case '{':
            case '(':
                fixIndentOfCurrentLine();
            default:
                //when spacesfortabs is enabled, and a tab is
                //pressed, we get sent spaces instead of a tab
                //we need this special case to "jump" the caret
                //to the start of the code in the line
                if (getIndentWithSpaces() && 
                        isIndent(getPrefix())) {
                    fixIndentOfCurrentLine();
                }
            }
            adjustStringOrCommentIndentation();
        }
    }
    
    private void adjustStringOrCommentIndentation() 
            throws BadLocationException {
        CommonToken tok = 
                getTokenStrictlyContainingOffset(getEndOfCurrentLine());
        if (tok!=null) {
            int len = command.length;
            String text = command.text;
            if (isQuoteOrCommentToken(tok.getType()) &&
                    text!=null && text.length()<len) { //reduced indent of a quoted or commented token
                String indent = document.get(command.offset, len);
                int line = document.getLineOfOffset(tok.getStartIndex())+1;
                int lastLine = document.getLineOfOffset(tok.getStopIndex());
                while (line<=lastLine) {
                    int offset = document.getLineOffset(line);
                    if (document.get(offset, len).equals(indent)) {
                        document.replace(offset, len, text);
                    }
                    line++;
                }
            }
        }
    }

    private void fixIndentOfStringOrCommentContinuation()
            throws BadLocationException {
        int endOfWs = 
                firstEndOfWhitespace(command.offset, 
                        getEndOfCurrentLine());
        if (endOfWs<0) return;
        CommonToken tokenContainingOffset = 
                getTokenStrictlyContainingOffset(command.offset);
        CommonToken token = 
                getStartOfStringToken(tokenContainingOffset);
        int pos = command.offset - getStartOfCurrentLine();
        int tokenIndent = token.getCharPositionInLine();
        if (pos>tokenIndent) return;
        StringBuilder indent = new StringBuilder();
        int startOfTokenLine = 
                document.getLineOffset(token.getLine()-1);
        String prefix = 
                document.get(startOfTokenLine+pos, 
                        tokenIndent-pos);
        for (int i=0; i<prefix.length(); i++) {
            char ch = prefix.charAt(i);
            indent.append(ch=='\t'?'\t':' ');
        }
        indent.append(getExtraIndent(token));
        indent.append(getRelativeIndent(command.offset));
        if (indent.length()>0) {
            command.length = endOfWs-command.offset;
            command.text = indent.toString();
        }
    }

    private String getExtraIndent(CommonToken token) {
        String extraIndent = "";
        switch (token.getType()) {
        case MULTI_COMMENT:
            //uncomment to get a bigger indent
//            if (document.getLineOfOffset(command.offset) <
//                document.getLineOfOffset(token.getStopIndex())) {
//                extraIndent="   ";
//            }
//            else {
                extraIndent=" ";
//            }
            break;
        case STRING_MID:
        case STRING_END:
            extraIndent="  ";
            break;
        case STRING_LITERAL:
        case ASTRING_LITERAL:
        case STRING_START:
            extraIndent=" ";
            break;
        case VERBATIM_STRING:
        case AVERBATIM_STRING:
            extraIndent="   ";
            break;
        }
        return extraIndent;
    }
    
    private void indentNewLine()
            throws BadLocationException {
        int stringIndent = 
                getStringOrCommentIndent(command.offset);
        int start = getStartOfCurrentLine();
        if (stringIndent>=0) {
            //we're in a string or multiline comment
            StringBuilder sb = new StringBuilder();
            for (int i=0; i<stringIndent; i++) {
                char ws = document.getChar(start+i)=='\t' ? 
                        '\t' : ' ';
                sb.append(ws);
            }
            command.text = command.text + 
                    sb.toString() + 
                    getRelativeIndent(command.offset);
        }
        else {
            char endOfLastLineChar = 
                    getPreviousNonHiddenCharacterInLine(command.offset);
            char startOfNewLineChar = 
                    getNextNonHiddenCharacterInNewline(command.offset);
            
            StringBuilder buf = 
                    new StringBuilder(command.text);
            IPreferenceStore store = CeylonPlugin.getPreferences();
            boolean closeBrace = store==null || 
                    store.getBoolean(CLOSE_BRACES);
            int end = getEndOfCurrentLine();
            appendIndent(command.offset, end, start, 
                    command.offset, 
                    startOfNewLineChar, endOfLastLineChar, 
                    closeBrace, buf);
            if (buf.length()>2) {
                char ch = buf.charAt(buf.length()-1);
                if (ch=='}'||ch==')') {
                    String hanging = 
                            document.get(command.offset,
                                    end-command.offset); //stuff after the { on the current line
                    buf.insert(command.caretOffset-command.offset, hanging);
                    command.length = hanging.length();
                }
            }
            command.text = buf.toString();
            
        }
        closeUnterminatedMultlineComment();
    }

    private void closeUnterminatedMultlineComment() {
        if (isInUnterminatedMultilineComment(command.offset, document)) {
            command.shiftsCaret=false;
            String text = command.text;
            command.caretOffset=command.offset+text.length();
            command.text = text +
                    text +
                    //uncomment to get a bigger indent
//                    (text.indexOf(' ')>=0 ? 
//                     text.replaceFirst(" ", "") : text) + 
                    "*/";
        }
    }
    
    private int count(String token) {
        int count = 0;
        List<CommonToken> tokens = getTokens();
        for (CommonToken tok: tokens) {
            String text = tok.getText();
            if (text.equals(token)) {
                count++;
            }
            if (text.startsWith(token) &&
                    !text.endsWith(token)) {
                count++;
            }
            if (text.endsWith(token) &&
                    !text.startsWith(token)) {
                count++;
            }
        }
        return count;
    }

    private int count(String token, int startIndex, int stopIndex) {
        int count = 0;
        List<CommonToken> tokens = getTokens();
        for (CommonToken tok: tokens) {
            if (tok.getStartIndex()>=startIndex && 
                tok.getStopIndex()<stopIndex &&
                    tok.getText().equals(token)) {
                count++;
            }
        }
        return count;
    }

    private List<CommonToken> getTokens() {
        return tokens;
    }
    
    private void fixIndentOfCurrentLine()
            throws BadLocationException {
        
        int start = getStartOfCurrentLine();
        int end = getEndOfCurrentLine();
        int endOfWs = firstEndOfWhitespace(start, end);
        
        // we want this to happen in three cases:
        // 1. the user types a tab in the whitespace 
        //    at the start of the line
        // 2. the user types { or ( at the start of
        //    the line
        // 3. Correct Indentation is calling us
        //test for Correct Indentation action
        
        boolean correctingIndentation = 
                command.offset==start && 
                !command.shiftsCaret;
        boolean opening = 
                command.text.equals("{") || 
                command.text.equals("(");
        if (command.offset<endOfWs || //we want strictly < since we don't want to prevent the caret advancing when a space is typed
            command.offset==endOfWs && endOfWs==end && opening ||  //this can cause the caret to jump *backwards* when a { or ( is typed!
            correctingIndentation) {
            
            int endOfPrev = getEndOfPreviousLine();
            int startOfPrev = getStartOfPreviousLine();
            char startOfCurrentLineChar = 
                    opening ?
                    command.text.charAt(0) : //the typed character is now the first character in the line
                    getNextNonHiddenCharacterInLine(start);
            char endOfLastLineChar = 
                    getPreviousNonHiddenCharacterInLine(endOfPrev);
            
            StringBuilder buf = new StringBuilder();
            appendIndent(start, end, 
                    startOfPrev, endOfPrev, 
                    startOfCurrentLineChar, endOfLastLineChar,
                    false, buf);
            
            int len = endOfWs-start;
            String text = buf.toString();
            if (text.length()!=len ||
                    !document.get(start,len).equals(text)) {
                if (opening) {
                    text+=command.text;
                }
                command.text = text;
                command.offset = start;
                command.length = len;
            }
            else if (!opening) {
                command.caretOffset = start+len;
                command.shiftsCaret = false;
                command.text = null;
            }
        }
    }

    private void appendIndent(int startOfCurrent, 
            int endOfCurrent, 
            int startOfPrev, int endOfPrev,
            char startOfCurrentLineChar, 
            char endOfLastLineChar, 
            boolean closeBraces, 
            StringBuilder buf)
                    throws BadLocationException {
        CommonToken prevEnding = 
                getPreviousNonHiddenToken(endOfPrev);
        CommonToken currStarting = 
                getNextNonHiddenToken(startOfCurrent, 
                        endOfCurrent);
        boolean terminatedCleanly = 
                endOfLastLineChar==';' || 
                endOfLastLineChar==',';
        boolean isContinuation = !terminatedCleanly &&
                //note: unfortunately we can't treat a line after a closing paren  
                //      as a continuation because it might be an annotation
                (isBinaryOperator(prevEnding) || 
                 isBinaryOperator(currStarting) ||
                        isInheritanceClause(currStarting) || 
                        isOperatorChar(startOfCurrentLineChar)); //to account for a previously line-commented character
        boolean isClosing =
                startOfCurrentLineChar=='}' /*&& endOfLastLineChar!='{'*/ ||
                startOfCurrentLineChar==')' /*&& endOfLastLineChar!='('*/;
        boolean isOpening = 
                    endOfLastLineChar=='{' /*&& startOfCurrentLineChar!='}'*/ ||
                    endOfLastLineChar=='(' /*&& startOfCurrentLineChar!=')'*/;
        boolean isListContinuation =
                    count("{",startOfPrev,endOfPrev) >
                            count("}",startOfPrev,endOfPrev) ||
                    count("(",startOfPrev,endOfPrev) >
                            count(")",startOfPrev,endOfPrev);
        appendIndent(isContinuation, 
                isOpening, isClosing, 
                isListContinuation, 
                startOfPrev, endOfPrev, 
                closeBraces, buf);
    }
    
    private boolean isInheritanceClause(CommonToken t) {
        if (t==null) return false;
        int tt = t.getType(); 
        return tt==CeylonLexer.EXTENDS ||
                tt==CeylonLexer.CASE_TYPES ||
                tt==CeylonLexer.TYPE_CONSTRAINT ||
                tt==CeylonLexer.SATISFIES;
    }
    
    private boolean isOperatorChar(char ch) {
        return ch=='+'||
                ch=='-'||
                ch=='/'||
                ch=='*'||
                ch=='^'||
                ch=='%'||
                ch=='|'||
                ch=='&'||
                ch=='='||
                ch=='<'||
                ch=='>'||
                ch=='~'||
                ch=='?'||
                ch=='.'||
                ch=='!';
    }
    
    private boolean isBinaryOperator(CommonToken t) {
        if (t==null) return false;
        int tt = t.getType();
        //partial fix for #1253
        /*if (tt==CeylonLexer.ELSE_CLAUSE ||
            tt==CeylonLexer.THEN_CLAUSE) {
            CommonToken nextToken = 
                    getNextNonHiddenToken(t.getStopIndex(), 
                            Integer.MAX_VALUE);
            return nextToken!=null && 
                    nextToken.getType()!=CeylonLexer.LBRACE &&
                    nextToken.getType()!=CeylonLexer.IF_CLAUSE;
        }*/
        return tt==CeylonLexer.SPECIFY ||
                tt==CeylonLexer.COMPUTE ||
                tt==CeylonLexer.NOT_EQUAL_OP ||
                tt==CeylonLexer.EQUAL_OP ||
                tt==CeylonLexer.IDENTICAL_OP ||
                tt==CeylonLexer.ADD_SPECIFY ||
                tt==CeylonLexer.SUBTRACT_SPECIFY ||
                tt==CeylonLexer.DIVIDE_SPECIFY ||
                tt==CeylonLexer.MULTIPLY_SPECIFY ||
                tt==CeylonLexer.OR_SPECIFY ||
                tt==CeylonLexer.AND_SPECIFY ||
                tt==CeylonLexer.COMPLEMENT_SPECIFY ||
                tt==CeylonLexer.UNION_SPECIFY ||
                tt==CeylonLexer.INTERSECT_SPECIFY ||
                tt==CeylonLexer.MEMBER_OP ||
                tt==CeylonLexer.SPREAD_OP ||
                tt==CeylonLexer.SAFE_MEMBER_OP ||
                tt==CeylonLexer.SUM_OP ||
                tt==CeylonLexer.COMPLEMENT_OP ||
                tt==CeylonLexer.DIFFERENCE_OP ||
                tt==CeylonLexer.QUOTIENT_OP ||
                tt==CeylonLexer.PRODUCT_OP ||
                tt==CeylonLexer.REMAINDER_OP ||
                tt==CeylonLexer.RANGE_OP ||
                tt==CeylonLexer.SEGMENT_OP ||
                tt==CeylonLexer.ENTRY_OP ||
                tt==CeylonLexer.UNION_OP ||
                tt==CeylonLexer.INTERSECTION_OP ||
                tt==CeylonLexer.AND_OP ||
                tt==CeylonLexer.OR_OP ||
                tt==CeylonLexer.POWER_OP ||
                tt==CeylonLexer.COMPARE_OP ||
                tt==CeylonLexer.LARGE_AS_OP ||
                tt==CeylonLexer.LARGER_OP ||
                tt==CeylonLexer.SMALL_AS_OP ||
                tt==CeylonLexer.SMALLER_OP ||
                tt==CeylonLexer.SCALE_OP;
    }

//    private void reduceIndent(DocumentCommand command) {
//        int spaces = getIndentSpaces();
//        if (endsWithSpaces(command.text, spaces)) {
//            command.text = command.text.substring(0, command.text.length()-spaces);
//        }
//        else if (command.text.endsWith("\t")) {
//            command.text = command.text.substring(0, command.text.length()-1);
//        }
//    }

    private void reduceIndentOfCurrentLine()
            throws BadLocationException {
        int spaces = getIndentSpaces();
        String text = 
                document.get(command.offset-spaces, spaces);
        if (endsWithSpaces(text,spaces)) {
            command.offset = command.offset-spaces;
            command.length = spaces;
        }
        else if (document.get(command.offset-1,1).equals("\t")) {
            command.offset = command.offset-1;
            command.length = 1;
        }
    }

    private void decrementIndent(StringBuilder buf, String indent)
            throws BadLocationException {
        int spaces = getIndentSpaces();
        if (endsWithSpaces(indent,spaces)) {
            buf.setLength(buf.length()-spaces);
        }
        else if (endsWithTab(indent)) {
            buf.setLength(buf.length()-1);
        }
    }

    /*private int getStartOfNextLine(IDocument d, int offset) 
            throws BadLocationException {
        return d.getLineOffset(d.getLineOfOffset(offset)+1);
    }*/
    
    private void appendIndent(boolean isContinuation, 
            boolean isOpening, boolean isClosing, 
            boolean isListContinuation, 
            int start, int end, 
            boolean closeBraces, 
            StringBuilder buf) 
                    throws BadLocationException {
        int line = document.getLineOfOffset(start);
        String indent = getIndent(start, end);
        String delim = getLineDelimiter(document, line);
        buf.append(indent);
        if (isOpening||isListContinuation) {
            if (isClosing) {
                if (closeBraces && isOpening) {
                    //increment the indent level
                    incrementIndent(buf, indent);
                    //move the closing brace to next line
                    command.shiftsCaret = false;
                    command.caretOffset =
                            command.offset+buf.length();
                    buf.append(delim)
                       .append(indent);
                }
//                else if (closeBraces && //hack just to distinguish a newline from a correct indentation! 
//                        isListContinuation) {
//                    //just increment the indent level
//                    incrementIndent(buf, indent);
//                }
            }
            else {
                //increment the indent level
                incrementIndent(buf, indent);
                if (closeBraces && 
                        count("{") > count("}")) {
                    //close the opening brace
                    command.shiftsCaret = false;
                    command.caretOffset =
                            command.offset+buf.length();
                    buf.append(delim)
                       .append(indent)
                       .append('}');
                }
            }
        }
        else if (isContinuation) {
            incrementIndent(buf, indent);
            incrementIndent(buf, indent);
        }
        else if (isClosing) {
            decrementIndent(buf, indent);
        }
    }

    private static String getLineDelimiter(IDocument document, int line) 
            throws BadLocationException {
        String newlineChar = 
                document.getLineDelimiter(line);
        if (newlineChar==null && line>0) {
            return document.getLineDelimiter(line-1);
        }
        else {
            return getDefaultLineDelimiter(document);
        }
    }

    private static String getDefaultLineDelimiter(IDocument document) {
        return utilJ2C().indents().getDefaultLineDelimiter(document);
    }

    private String getIndent(int start, int end) 
            throws BadLocationException {
        if (start<0||end<0) return "";
        int nestingLevel = 0;
        while (start>0) {
            nestingLevel += parenCount(start, end);
            //We're searching for an earlier line whose 
            //immediately preceding line ends cleanly 
            //with a {, }, or ; or which itelf starts 
            //with a }. We will use that to infer the 
            //indent for the current line
            char startingChar = 
                    getNextNonHiddenCharacterInLine(start);
            if (startingChar=='}' || startingChar==')') break;
            int prevEnd = end;
            int prevStart = start;
            prevEnd = getEndOfPreviousLine(prevStart);
            prevStart = getStartOfPreviousLine(prevStart);
            char prevEndingChar = 
                    getPreviousNonHiddenCharacterInLine(prevEnd);
            if (prevEndingChar==';' || 
                prevEndingChar==',' && nestingLevel>=0 || 
                prevEndingChar=='{' || 
                prevEndingChar=='}' ||
                prevEndingChar=='(' && nestingLevel>=0)
                //note assymmetry between } and ) here, 
                //due to stuff like "class X()\nextends Y()"
                //and X f()\n=> X()
                break;
            end = prevEnd;
            start = prevStart;
        }
        while (isQuoted(start)) {
            end = getEndOfPreviousLine(start);
            start = getStartOfPreviousLine(start);
        }
        int len = firstEndOfWhitespace(start, end) - start;
        return document.get(start, len);
    }

    private int parenCount(int start, int end) {
        int count=0;
        for (Iterator<CommonToken> it = 
                getTokenIterator(getTokens(), 
                new Region(start, end-start)); 
                it.hasNext();) {
            int type = it.next().getType();
            if (type==CeylonLexer.RPAREN) {
                count--;
            }
            else if (type==CeylonLexer.LPAREN) {
                count++;
            }
        }
        return count;
    }
    
    private void incrementIndent(StringBuilder buf, 
            String indent) {
        int spaces = getIndentSpaces();
        if (endsWithSpaces(indent,spaces)) {
            for (int i=1; i<=spaces; i++) {
                buf.append(' ');                            
            }
        }
        else if (endsWithTab(indent)) {
            buf.append('\t');
        }
        else {
            initialIndent(buf);
        }
    }

    private void initialIndent(StringBuilder buf) {
        utilJ2C().indents().initialIndent(buf);
    }

    private boolean endsWithTab(String indent) {
        return !indent.isEmpty() &&
                indent.charAt(indent.length()-1)=='\t';
    }
    
    private CommonToken getPreviousNonHiddenToken(int offset) {
        int index = 
                getTokenIndexAtCharacter(tokens, offset);
        if (index<0) index=-index;
        for (; index>=0; index--) {
            CommonToken token = getTokens().get(index);
            if (token.getChannel()!=HIDDEN_CHANNEL &&
                    token.getStopIndex()<offset) {
                return token;
            }
        }
        return null;
    }
    
    private CommonToken getNextNonHiddenToken(int offset, int end) {
        int index = 
                getTokenIndexAtCharacter(tokens, offset);
        if (index<0) index=1-index;
        int size = getTokens().size();
        for (; index<size; index++) {
            CommonToken token = getTokens().get(index);
            if (token.getStartIndex()>=end) {
                return null;
            }
            if (token.getChannel()!=HIDDEN_CHANNEL &&
                    token.getStartIndex()>=offset) {
                return token;
            }
        }
        return null;
    }
    
    /**
     * Is the given offset in the document a line ending?
     */
    private boolean isLineEnding(int offset) {
        int documentLength = document.getLength();
        String[] delimiters = 
                document.getLegalLineDelimiters();
        for (String delimiter: delimiters) {
            int length = delimiter.length();
            if (offset+length <= documentLength) {
                try {
                    String string = 
                            document.get(offset,length);
                    if (string.equals(delimiter)) {
                        return true;
                    }
                }
                catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
    
    private char getPreviousNonHiddenCharacterInLine(int offset)
            throws BadLocationException {
        offset--;
        for (; offset>=0; offset--) {
            char ch = document.getChar(offset);
            int tt = getTokenTypeOfCharacterAtOffset(offset);
            if (!isWhitespace(ch) && !isCommentToken(tt) ||
                    isLineEnding(offset)) {
                return ch;
            }
        }
        return '\n'; //lame null
    }

    private char getNextNonHiddenCharacterInLine(int offset)
            throws BadLocationException {
        for (; offset<document.getLength(); offset++) {
            char ch = document.getChar(offset);
            int tt = getTokenTypeOfCharacterAtOffset(offset);
            if (!isWhitespace(ch) && !isCommentToken(tt) || 
                    isLineEnding(offset)) {
                return ch;
            }
        }
        return '\n'; //lame null
    }
    
    private char getNextNonHiddenCharacterInNewline(int offset)
            throws BadLocationException {
        for (; offset<document.getLength(); offset++) {
            char ch = document.getChar(offset);
            try {
                if (document.get(offset,2).equals("//")) {
                    break; 
                }
            }
            catch (BadLocationException ble) {}
            int tt = getTokenTypeOfCharacterAtOffset(offset);
            if (!isWhitespace(ch) && tt!=MULTI_COMMENT ||
                    isLineEnding(offset)) {
                return ch;
            }
        }
        return '\n'; //lame null
    }
    
    private int getStartOfCurrentLine() 
            throws BadLocationException {
        int p = command.offset == document.getLength() ? 
                command.offset-1 : command.offset;
        IRegion lineInfo = 
                document.getLineInformationOfOffset(p);
        return lineInfo.getOffset();
    }
    
    private int getEndOfCurrentLine() 
            throws BadLocationException {
        int p = command.offset == document.getLength() ? 
                command.offset-1 : command.offset;
        IRegion lineInfo = 
                document.getLineInformationOfOffset(p);
        return lineInfo.getOffset() + lineInfo.getLength();
    }
    
    private int getStartOfPreviousLine()
            throws BadLocationException {
        return getStartOfPreviousLine(command.offset);
    }

    private int getStartOfPreviousLine(int offset) 
            throws BadLocationException {
        int line = document.getLineOfOffset(offset);
        IRegion lineInfo;
        do {
            if (line==0) return 0;
            lineInfo = document.getLineInformation(--line);
        }
        while (lineInfo.getLength()==0 || 
                isQuoted(lineInfo.getOffset()));
        return lineInfo.getOffset();
    }
    
    private int getEndOfPreviousLine() 
            throws BadLocationException {
        return getEndOfPreviousLine(command.offset);
    }

    private int getEndOfPreviousLine(int offset) 
            throws BadLocationException {
        if (offset == document.getLength() && offset>0) {
            offset--;
        }
        int line = document.getLineOfOffset(offset);
        IRegion lineInfo;
        do {
            if (line==0) return 0;
            lineInfo = document.getLineInformation(--line);
        }
        while (lineInfo.getLength()==0);
        return lineInfo.getOffset() + 
                lineInfo.getLength();
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
 
    /**
     * Returns the first offset greater than <code>offset</code> 
     * and smaller than <code>end</code> whose character is 
     * not a space or tab character. If no such offset is 
     * found, <code>end</code> is returned.
     *
     * @param d the document to search in
     * @param offset the offset at which searching start
     * @param end the offset at which searching stops
     * @return the offset in the specified range whose 
     *         character is not a space or tab
     * @exception BadLocationException if position is an 
     *            invalid range in the given document
     */
    private int firstEndOfWhitespace(int offset, int end)
            throws BadLocationException {
        while (offset < end) {
            char ch= document.getChar(offset);
            if (ch!=' ' && ch!='\t') {
                return offset;
            }
            offset++;
        }
        return end;
    }
    
    /**
     * Is the given character sequence a line-ending 
     * character sequence for this document/platform?
     */
    private boolean isLineEnding(String text) {
        String[] delimiters = 
                document.getLegalLineDelimiters();
        for (String delim: delimiters) {
            if (delim.equals(text)) {
                return true;
            }
        }
        return false;
    }
}