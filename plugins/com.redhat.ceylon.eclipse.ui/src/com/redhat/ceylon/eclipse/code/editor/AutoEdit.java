package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.ASTRING_LITERAL;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.AVERBATIM_STRING;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.CHAR_LITERAL;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.EOF;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.LINE_COMMENT;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.MULTI_COMMENT;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_END;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_LITERAL;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_MID;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_START;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.VERBATIM_STRING;
import static com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy.getDefaultIndent;
import static com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy.getIndentSpaces;
import static com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy.getIndentWithSpaces;
import static com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy.getPreferences;
import static com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy.initialIndent;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.CLOSE_ANGLES;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.CLOSE_BACKTICKS;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.CLOSE_BRACES;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.CLOSE_BRACKETS;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.CLOSE_PARENS;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.CLOSE_QUOTES;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getTokenIndexAtCharacter;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getTokenIterator;
import static java.lang.Character.isWhitespace;

import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;

class AutoEdit {
	
	public AutoEdit(IDocument document, List<CommonToken> tokens,
			DocumentCommand command) {
		this.document = document;
		this.tokens = tokens;
		this.command = command;
	}

	private IDocument document;
	private List<CommonToken> tokens;
	private DocumentCommand command;
	
    public void customizeDocumentCommand() {
    	
        //Note that IMP's Correct Indentation sends us a tab
    	//character at the start of each line of selected
    	//text. This is amazingly sucky because it's very
    	//difficult to distinguish Correct Indentation from
    	//an actual typed tab.
    	//Note also that typed tabs are replaced with spaces
    	//before this method is called if the spacesfortabs 
    	//setting is enabled.
        if (command.doit == false) {
            return;
        }
        
        //command.length>0 means we are replacing or deleting text
        else if (command.text!=null && command.length==0) { 
            if (command.text.isEmpty()) {
                //workaround for a really annoying bug where we 
                //get sent "" instead of "\t" or "    " by IMP
                //reconstruct what we would have been sent 
                //without the bug
                if (getIndentWithSpaces()) {
                    int overhang = getPrefix().length() % getIndentSpaces();
                    command.text = getDefaultIndent().substring(overhang);
                }
                else {
                	command.text = "\t";
                }
                smartIndentOnKeypress();
            }
            else if ((command.text.length()==1 || command.text.length()==2) && isLineEnding(command.text)) {
            	//a typed newline
                smartIndentAfterNewline();
            }
            else if (command.text.length()==1 || 
                    //when spacesfortabs is enabled, we get 
                    //sent spaces instead of a tab - the right
                    //number of spaces to take us to the next
                    //tab stop
                    getIndentWithSpaces() && isIndent(getPrefix())) {
            	//anything that might represent a single 
                //keypress or a Correct Indentation
                smartIndentOnKeypress();
            }
        }
        
        closeOpening();
    }

	private static String[][] FENCES = {
			{ "'", "'", CLOSE_QUOTES },
			{ "\"", "\"", CLOSE_QUOTES },
			{ "`", "`", CLOSE_BACKTICKS },
			{ "<", ">", CLOSE_ANGLES },
			{ "(", ")", CLOSE_PARENS },
			{ "[", "]", CLOSE_BRACKETS }};

	public void closeOpening() {
		
		try {
			// TODO: improve this, check the surrounding token type!
			if (command.offset>0 &&
				document.getChar(command.offset - 1) == '\\') {
				return;
			}
		} 
		catch (BadLocationException e) {}

		String current = command.text;
		String opening = null;
		String closing = null;
		
		boolean found=false;
		IPreferenceStore store = getPreferences();
		for (String[] type : FENCES) {
			if (type[0].equals(current) || 
					type[1].equals(current)) {
			    if (store==null || store.getBoolean(type[2])) {
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
					if (skipClosingFence(closing)) {
						command.text = "";
						command.shiftsCaret = false;
						command.caretOffset = command.offset + 1;
						return;
					}
				} 
				catch (BadLocationException e) {}
			}

            if (current.equals(opening) && (!isQuotedOrCommented(command.offset) || 
            		isGraveAccentCharacterInStringLiteral(command.offset, opening) ||
            		isOpeningBracketInAnnotationStringLiteral(command.offset, opening))) {
				//typed character is an opening fence
				if (closeOpeningFence(opening, closing)) {
					//add a closing fence
					command.shiftsCaret = false;
					command.caretOffset = command.offset + 1;
				    if (isGraveAccentCharacterInStringLiteral(command.offset, opening)) {
				        try {
				            if (command.offset>1 &&
				                    document.get(command.offset-1,1).equals("`") &&
				                    !document.get(command.offset-2,1).equals("`")) {
				            	command.text += "``";
				            }
				        } 
				        catch (BadLocationException e) {}
				    }
				    else if (isOpeningBracketInAnnotationStringLiteral(command.offset, opening)) {
                        try {
                            if (command.offset>1 &&
                                    document.get(command.offset-1,1).equals("[") &&
                                    !document.get(command.offset-2,1).equals("]")) {
                                command.text += "]]";
                            }
                        } 
                        catch (BadLocationException e) {}
				    }
				    else if (opening.equals("\"")) {
				        try {
				            if (command.offset<=1 ||
				                    !document.get(command.offset-2,1).equals("\"")) {
                                command.text += closing;
                            }
				            else if (command.offset>1 &&
				                    document.get(command.offset-2,2).equals("\"\"") &&
				                    !(command.offset>2 && document.get(command.offset-3,1).equals("\""))) {
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

    private boolean skipClosingFence(String closing) throws BadLocationException {
        return String.valueOf(document.getChar(command.offset)).equals(closing);
    }

	private boolean closeOpeningFence(String opening, String closing) {
		boolean closeOpening;
		if (opening.equals(closing)) { 
			closeOpening = count(opening)%2==0;
		}
		else { 
			closeOpening = count(opening)>=count(closing);
		}

		if (opening.equals("<")) {
			// only close angle brackets if it's after a UIdentifier
			// if(a< -> don't close
			// if(Some< -> close
			// A< -> close
			int currOfset = command.offset - 1;
			char currChar;
			try {
				while (Character.isAlphabetic(currChar = document.getChar(currOfset))) {
					currOfset--;
				}
				currChar = document.getChar(currOfset + 1);
				if (!Character.isUpperCase(currChar)) {
					closeOpening = false;
				}
			} catch (BadLocationException e) {
			}
		}
		return closeOpening;
	}

	private String getPrefix() {
		try {
			int lineOffset = getStartOfCurrentLine();
			return document.get(lineOffset, command.offset-lineOffset) + command.text;
		} 
		catch (BadLocationException e) {
			return command.text;
		}
	}
    
    public boolean isIndent(String text) {
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
        if (command.offset==-1 || document.getLength()==0) {
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
        return isStringToken(getTokenTypeStrictlyContainingOffset(offset));
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

    private boolean isQuotedOrCommented(int offset) {
    	return isQuoteOrCommentToken(getTokenTypeStrictlyContainingOffset(offset));
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
    
    private boolean isGraveAccentCharacterInStringLiteral(int offset, String fence) {
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

    private boolean isOpeningBracketInAnnotationStringLiteral(int offset, String fence) {
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
    
    private boolean isInUnterminatedMultilineComment(int offset, IDocument d) {
    	CommonToken token = getTokenStrictlyContainingOffset(offset);
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

    private int getTokenTypeStrictlyContainingOffset(int offset) {
    	CommonToken token = getTokenStrictlyContainingOffset(offset);
		return token==null ? -1 : token.getType();
    }
    
    private int getTokenTypeOfCharacterAtOffset(int offset) {
		int tokenIndex = getTokenIndexAtCharacter(tokens, offset);
		if (tokenIndex>=0) {
			CommonToken token = tokens.get(tokenIndex);
			return token.getType();
		}
		return -1;
    }
    
	private CommonToken getTokenStrictlyContainingOffset(int offset) {
        List<CommonToken> tokens = getTokens();
		if (tokens!=null) {
    		if (tokens.size()>1) {
    			if (tokens.get(tokens.size()-1).getStartIndex()==offset) { //at very end of file
    				//check to see if last token is an
    				//unterminated string or comment
    			    //Note: ANTLR sometimes sends me 2 EOFs, 
    			    //      so do this:
    			    CommonToken token = null;
    			    for (int i=1; token==null || token.getType()==EOF; i++) {
    			        token = tokens.get(tokens.size()-i);
    			    }
    				int type = token==null ? -1 : token.getType();
    				if ((type==STRING_LITERAL ||
    						type==STRING_END ||
    						type==ASTRING_LITERAL) && 
    						(!token.getText().endsWith("\"") ||
    						token.getText().length()==1) ||
    						(type==VERBATIM_STRING || type==AVERBATIM_STRING) && 
    						(!token.getText().endsWith("\"\"\"")||
    						token.getText().length()==3) ||
    						(type==MULTI_COMMENT) && 
    						(!token.getText().endsWith("*/")||
    						token.getText().length()==2) ||
    						type==LINE_COMMENT) {
    					return token;
    				}
    			}
    			else {
    				int tokenIndex = getTokenIndexAtCharacter(tokens, offset);
    				if (tokenIndex>=0) {
    					CommonToken token = tokens.get(tokenIndex);
    					if (token.getStartIndex()<offset) {
    						return token;
    					}
    				}
    			}
    		}
        }
		return null;
	}

	private int getStringOrCommentIndent(int offset) {
		CommonToken token = getTokenStrictlyContainingOffset(offset);
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
//			        return start+3;
			    case VERBATIM_STRING: 
			    case AVERBATIM_STRING:
			        return start+3;
			    }
			}
		}
		return -1;
	}
	
    private void adjustIndentOfCurrentLine()
            throws BadLocationException {
        char ch = command.text.charAt(0);
        if (isQuotedOrCommented(command.offset)) {
            if (ch=='\t' || getIndentWithSpaces() && isIndent(getPrefix())) {
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
                //when spacesfortabs is enabled, we get sent spaces instead of a tab
                if (getIndentWithSpaces() && isIndent(getPrefix())) {
                    fixIndentOfCurrentLine();
                }
            }
        }
    }

    private void fixIndentOfStringOrCommentContinuation()
            throws BadLocationException {
        int endOfWs = firstEndOfWhitespace(command.offset, getEndOfCurrentLine());
        if (endOfWs<0) return;
        CommonToken token = getTokenStrictlyContainingOffset(command.offset);
        int pos = command.offset - getStartOfCurrentLine();
        int tokenIndent = token.getCharPositionInLine();
        if (pos>tokenIndent) return;
        StringBuilder indent = new StringBuilder();
        int startOfTokenLine = document.getLineOffset(token.getLine()-1);
        String prefix = document.get(startOfTokenLine+pos, tokenIndent-pos);
        for (int i=0; i<prefix.length(); i++) {
            char ch = prefix.charAt(i);
            indent.append(ch=='\t'?'\t':' ');
        }
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
        indent.append(extraIndent);
        if (indent.length()>0) {
            command.length = endOfWs-command.offset;
            command.text = indent.toString();
        }
    }
    
    private void indentNewLine()
            throws BadLocationException {
        int stringIndent = getStringOrCommentIndent(command.offset);
        int start = getStartOfCurrentLine();
        if (stringIndent>=0) {
            //we're in a string or multiline comment
            StringBuilder sb = new StringBuilder();
            for (int i=0; i<stringIndent; i++) {
                char ws = document.getChar(start+i)=='\t' ? 
                        '\t' : ' ';
                sb.append(ws);
            }
            command.text = command.text + sb.toString();
        }
        else {
            char endOfLastLineChar = getPreviousNonHiddenCharacterInLine(command.offset);
            char startOfNewLineChar = getNextNonHiddenCharacterInNewline(command.offset);
            
            StringBuilder buf = new StringBuilder(command.text);
            IPreferenceStore store = getPreferences();
            boolean closeBrace = store==null || 
                    store.getBoolean(CLOSE_BRACES) && 
                            count("{")>count("}");
            int end = getEndOfCurrentLine();
			appendIndent(command.offset, end, start, command.offset, 
                    startOfNewLineChar, endOfLastLineChar, closeBrace, buf);
            if (buf.length()>2) {
                char ch = buf.charAt(buf.length()-1);
                if (ch=='}'||ch==')') {
                    String hanging = document.get(command.offset, end-command.offset); //stuff after the { on the current line
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
//                    (text.indexOf(' ')>=0 ? text.replaceFirst(" ", "") : text) + 
                    "*/";
        }
    }
    
    int count(String token) {
    	int count = 0;
    	List<CommonToken> tokens = getTokens();
		for (CommonToken tok: tokens) {
			if (tok.getText().equals(token)) {
				count++;
			}
		}
    	return count;
    }

	protected List<CommonToken> getTokens() {
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
        
        boolean correctingIndentation = command.offset==start && 
                                       !command.shiftsCaret;
        boolean opening = command.text.equals("{") || 
                          command.text.equals("(");
        if (command.offset<endOfWs || //we want strictly < since we don't want to prevent the caret advancing when a space is typed
            command.offset==endOfWs && endOfWs==end && opening ||  //this can cause the caret to jump *backwards* when a { or ( is typed!
            correctingIndentation) {
            
            int endOfPrev = getEndOfPreviousLine();
            int startOfPrev = getStartOfPreviousLine();
            char startOfCurrentLineChar = opening ?
                    command.text.charAt(0) : //the typed character is now the first character in the line
                    getNextNonHiddenCharacterInLine(start);
            char endOfLastLineChar = getPreviousNonHiddenCharacterInLine(endOfPrev);
            
            StringBuilder buf = new StringBuilder();
            appendIndent(start, end, startOfPrev, endOfPrev, 
            		startOfCurrentLineChar, endOfLastLineChar,
                    false, buf);
            
            if (opening) {
                buf.append(command.text.charAt(0));
            }
            command.text = buf.toString();
            command.offset=start;
            command.length=endOfWs-start;
        }
    }

    private void appendIndent(int startOfCurrent, int endOfCurrent, 
            int startOfPrev, int endOfPrev,
            char startOfCurrentLineChar, char endOfLastLineChar, 
            boolean closeBraces, StringBuilder buf)
            		throws BadLocationException {
    	CommonToken prevEnding = getPreviousNonHiddenToken(endOfPrev);
    	CommonToken currStarting = getNextNonHiddenToken(startOfCurrent, endOfCurrent);
    	boolean terminatedCleanly = endOfLastLineChar==';' || endOfLastLineChar==',';
    	boolean isContinuation = !terminatedCleanly &&
    	        //note: unfortunately we can't treat a line after a closing paren  
    	        //      as a continuation because it might be an annotation
    	        (isBinaryOperator(prevEnding) || isBinaryOperator(currStarting) ||
    			        isInheritanceClause(currStarting) || 
    			        isOperatorChar(startOfCurrentLineChar)); //to account for a previously line-commented character
        boolean isOpening = endOfLastLineChar=='{' && startOfCurrentLineChar!='}' ||
                endOfLastLineChar=='(' && startOfCurrentLineChar!=')';
        boolean isClosing = startOfCurrentLineChar=='}' && endOfLastLineChar!='{' ||
                startOfCurrentLineChar==')' && endOfLastLineChar!='(';
        appendIndent(isContinuation, isOpening, isClosing,  
                startOfPrev, endOfPrev, closeBraces, buf);
    }
    
    boolean isInheritanceClause(CommonToken t) {
        if (t==null) return false;
        int tt = t.getType(); 
    	return tt==CeylonLexer.EXTENDS||
    			tt==CeylonLexer.CASE_TYPES||
    			tt==CeylonLexer.TYPE_CONSTRAINT||
    			tt==CeylonLexer.SATISFIES;
    }
    
    boolean isOperatorChar(char ch) {
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
    
    boolean isBinaryOperator(CommonToken t) {
        if (t==null) return false;
        int tt = t.getType(); 
    	return tt==CeylonLexer.SPECIFY||
    			tt==CeylonLexer.COMPUTE||
    			tt==CeylonLexer.NOT_EQUAL_OP||
    			tt==CeylonLexer.EQUAL_OP||
    			tt==CeylonLexer.IDENTICAL_OP||
    			tt==CeylonLexer.ADD_SPECIFY||
    			tt==CeylonLexer.SUBTRACT_SPECIFY||
    			tt==CeylonLexer.DIVIDE_SPECIFY||
    			tt==CeylonLexer.MULTIPLY_SPECIFY||
    			tt==CeylonLexer.OR_SPECIFY||
    			tt==CeylonLexer.AND_SPECIFY||
    			tt==CeylonLexer.COMPLEMENT_SPECIFY||
    			tt==CeylonLexer.UNION_SPECIFY||
    			tt==CeylonLexer.INTERSECT_SPECIFY||
    			tt==CeylonLexer.MEMBER_OP||
    			tt==CeylonLexer.SPREAD_OP||
    			tt==CeylonLexer.SAFE_MEMBER_OP||
    			tt==CeylonLexer.SUM_OP||
    			tt==CeylonLexer.COMPLEMENT_OP||
    			tt==CeylonLexer.DIFFERENCE_OP||
    			tt==CeylonLexer.QUOTIENT_OP||
    			tt==CeylonLexer.PRODUCT_OP||
    			tt==CeylonLexer.REMAINDER_OP||
    			tt==CeylonLexer.RANGE_OP||
    			tt==CeylonLexer.SEGMENT_OP||
    			tt==CeylonLexer.ENTRY_OP||
    			tt==CeylonLexer.UNION_OP||
    			tt==CeylonLexer.INTERSECTION_OP||
    			tt==CeylonLexer.AND_OP||
    			tt==CeylonLexer.OR_OP||
    			tt==CeylonLexer.POWER_OP||
    			tt==CeylonLexer.COMPARE_OP||
    			tt==CeylonLexer.LARGE_AS_OP||
    			tt==CeylonLexer.LARGER_OP||
    			tt==CeylonLexer.SMALL_AS_OP||
    			tt==CeylonLexer.SMALLER_OP||
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
        if (endsWithSpaces(document.get(command.offset-spaces, spaces),spaces)) {
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
    
    private void appendIndent(boolean isContinuation, boolean isBeginning,
            boolean isEnding, int start, int end, boolean closeBraces, 
            StringBuilder buf) 
            		throws BadLocationException {
        String indent = getIndent(start, end);
        buf.append(indent);
        if (isBeginning) {
            //increment the indent level
            incrementIndent(buf, indent);
            if (closeBraces) {
            	command.shiftsCaret=false;
            	command.caretOffset=command.offset+buf.length();
            	int line = document.getLineOfOffset(start);
            	buf.append(getLineDelimiter(document, line))
            		.append(indent)
            		.append('}');
            }
        }
        else if (isContinuation) {
            incrementIndent(buf, indent);
            incrementIndent(buf, indent);
        }
        if (isEnding) {
            decrementIndent(buf, indent);
            if (isContinuation) decrementIndent(buf, indent);
        }
    }

    private static String getLineDelimiter(IDocument document, int line) 
            throws BadLocationException {
        String newlineChar = document.getLineDelimiter(line);
        if (newlineChar==null && line>0) {
        	return document.getLineDelimiter(line-1);
        }
        else if (document instanceof IDocumentExtension4) {
            return ((IDocumentExtension4) document).getDefaultLineDelimiter();
        }
        else {
            return System.lineSeparator();
        }
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
            char startingChar = getNextNonHiddenCharacterInLine(start);
            if (startingChar=='}' || startingChar==')') break;
            int prevEnd = end;
            int prevStart = start;
            char prevEndingChar;
//            do {
                prevEnd = getEndOfPreviousLine(prevStart);
                prevStart = getStartOfPreviousLine(prevStart);
                prevEndingChar = getPreviousNonHiddenCharacterInLine(prevEnd);
//            }
//            while (prevEndingChar=='\n' && prevStart>0); //skip blank lines when searching for previous line
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
        return document.get(start, 
                firstEndOfWhitespace(start, end)-start);
    }

    int parenCount(int start, int end) {
        int count=0;
        for (Iterator<CommonToken> it = getTokenIterator(getTokens(), 
                new Region(start, end-start)); it.hasNext();) {
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
    
    private void incrementIndent(StringBuilder buf, String indent) {
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

    private boolean endsWithTab(String indent) {
        return !indent.isEmpty() &&
                indent.charAt(indent.length()-1)=='\t';
    }
    
    private CommonToken getPreviousNonHiddenToken(int offset) {
        int index = getTokenIndexAtCharacter(tokens, offset);
        if (index<0) index=-index;
        for (; index>=0; index--) {
            CommonToken token = getTokens().get(index);
            if (token.getChannel()!=CommonToken.HIDDEN_CHANNEL &&
                    token.getStopIndex()<offset) {
                return token;
            }
        }
        return null;
    }
    
    private CommonToken getNextNonHiddenToken(int offset, int end) {
        int index = getTokenIndexAtCharacter(tokens, offset);
        if (index<0) index=1-index;
        int size = getTokens().size();
        for (; index<size; index++) {
            CommonToken token = getTokens().get(index);
            if (token.getStartIndex()>=end) {
                return null;
            }
            if (token.getChannel()!=CommonToken.HIDDEN_CHANNEL &&
                    token.getStartIndex()>=offset) {
                return token;
            }
        }
        return null;
    }
    
    private char getPreviousNonHiddenCharacterInLine(int offset)
            throws BadLocationException {
        offset--;
        for (;offset>=0; offset--) {
            String ch = document.get(offset,1);
            if (!isWhitespace(ch.charAt(0)) && 
            	!isCommentToken(getTokenTypeOfCharacterAtOffset(offset)) ||
                    isLineEnding(ch)) {
                return ch.charAt(0);
            }
        }
        return '\n';
    }

    private char getNextNonHiddenCharacterInLine(int offset)
            throws BadLocationException {
        for (;offset<document.getLength(); offset++) {
            String ch = document.get(offset,1);
            if (!isWhitespace(ch.charAt(0)) && 
                !isCommentToken(getTokenTypeOfCharacterAtOffset(offset)) ||
                    isLineEnding(ch)) {
                return ch.charAt(0);
            }
        }
        return '\n';
    }
    
    private char getNextNonHiddenCharacterInNewline(int offset)
            throws BadLocationException {
        for (;offset<document.getLength(); offset++) {
            String ch = document.get(offset,1);
            if (!isWhitespace(ch.charAt(0)) && 
                getTokenTypeOfCharacterAtOffset(offset)!=MULTI_COMMENT ||
                    isLineEnding(ch)) {
                return ch.charAt(0);
            }
        }
        return '\n';
    }
    
    private int getStartOfCurrentLine() 
            throws BadLocationException {
        int p = command.offset == document.getLength() ? 
                command.offset-1 : command.offset;
        return document.getLineInformationOfOffset(p).getOffset();
    }
    
    private int getEndOfCurrentLine() 
            throws BadLocationException {
        int p = command.offset == document.getLength() ? 
                command.offset-1 : command.offset;
        IRegion lineInfo = document.getLineInformationOfOffset(p);
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
        while (lineInfo.getLength()==0 || isQuoted(lineInfo.getOffset()));
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
    
    private boolean isLineEnding(String text) {
        String[] delimiters = document.getLegalLineDelimiters();
        if (delimiters != null) {
            return TextUtilities.endsWith(delimiters, text)!=-1;
        }
        return false;
    }
    
}