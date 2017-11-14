/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import static org.eclipse.ceylon.ide.eclipse.util.Highlights.getColoring;
import static org.eclipse.ceylon.ide.eclipse.util.Highlights.getInterpolationColoring;
import static org.eclipse.ceylon.ide.eclipse.util.Highlights.getMemberColoring;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getTokenIndexAtCharacter;

import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

import org.eclipse.ceylon.compiler.typechecker.parser.CeylonInterpolatingLexer;
import org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer;
import org.eclipse.ceylon.compiler.typechecker.parser.CeylonParser;
import org.eclipse.ceylon.compiler.typechecker.util.NewlineFixingStringStream;

class PresentationDamageRepairer implements IPresentationDamager, 
        IPresentationRepairer {
    
    private volatile List<? extends Token> tokens;
    private final CeylonEditor editor;
    private IDocument document;
    
    PresentationDamageRepairer(ISourceViewer sourceViewer, 
            CeylonEditor editor) {
        this.editor = editor;
    }
    
    public IRegion getDamageRegion(ITypedRegion partition, 
            DocumentEvent event, 
            boolean documentPartitioningChanged) {

        if (tokens==null) {
            //parse and color the whole document the first time!
            return partition;
        }
                
        if (editor!=null && editor.isInLinkedMode()) {
            Position linkedPosition =
                    getLinkedPosition(event.getOffset(), 
                            event.getLength());
            if (linkedPosition == null) {
                return partition;
            }
            else {
                return new Region(linkedPosition.getOffset(), 
                        linkedPosition.getLength());
            }
        }

        if (noTextChange(event)) {
            //it was a change to annotations - don't reparse
            return new Region(event.getOffset(), 
                    event.getLength());
        }
        
        Region tokenRegion = 
                getContainingTokenRegion(event);
        if (tokenRegion == null) {
            //the change is totally within a token,
            //and doesn't break it, return the
            //token extent
            return partition;
        }
        else {
            return tokenRegion;
        }
    }
    
    private Region getContainingTokenRegion(DocumentEvent event) {
        int tokenIndex = 
                getTokenIndexAtCharacter((List)tokens, event.getOffset()-1);
        if (tokenIndex<0) tokenIndex=-tokenIndex;
        CommonToken t = (CommonToken) tokens.get(tokenIndex);
        if (isWithinExistingToken(event, t) 
                && isWithinTokenChange(event, t)) {
            //the edit just changes the text inside
            //a token, leaving the rest of the
            //document structure unchanged
            return new Region(event.getOffset(), 
                    event.getText().length());
        }
        return null;
    }

    public boolean isWithinExistingToken(DocumentEvent event, 
            CommonToken t) {
        int eventStart = event.getOffset();
        int eventStop = event.getOffset()+event.getLength();
        int tokenStart = t.getStartIndex();
        int tokenStop = t.getStopIndex()+1;
        switch (t.getType()) {
        case CeylonLexer.MULTI_COMMENT:
            return tokenStart<=eventStart-2 && 
                    tokenStop>=eventStop+2;
        case CeylonLexer.VERBATIM_STRING:
        case CeylonLexer.AVERBATIM_STRING:
            return tokenStart<=eventStart-3 && 
                    tokenStop>=eventStop+3;
        case CeylonLexer.CHAR_LITERAL:
        case CeylonLexer.STRING_LITERAL:
        case CeylonLexer.ASTRING_LITERAL:
        case CeylonLexer.STRING_START:
        case CeylonLexer.STRING_MID:
        case CeylonLexer.STRING_END:
            return tokenStart<=event.getOffset()-1 && 
                    tokenStop>=eventStop+1;
        case CeylonLexer.LINE_COMMENT:
            return tokenStart<=eventStart-2 && 
                    tokenStop>=eventStop+1; //account for case where we delete the newline
        default:
            return tokenStart<=eventStart && 
                    tokenStop>=eventStop;
        }
    }

    public boolean isWithinTokenChange(DocumentEvent event,
            CommonToken t) {
        switch (t.getType()) {
        case CeylonLexer.WS:
            for (char c: event.getText().toCharArray()) {
                if (!Character.isWhitespace(c)) {
                    return false;
                }
            }
            break;
        case CeylonLexer.UIDENTIFIER:
        case CeylonLexer.LIDENTIFIER:
            for (char c: event.getText().toCharArray()) {
                if (!Character.isJavaIdentifierPart(c)) {
                    return false;
                }
            }
            break;
        case CeylonLexer.STRING_LITERAL:
        case CeylonLexer.ASTRING_LITERAL:
        case CeylonLexer.VERBATIM_STRING:
        case CeylonLexer.AVERBATIM_STRING:
        case CeylonLexer.STRING_START:
        case CeylonLexer.STRING_MID:
        case CeylonLexer.STRING_END:
            for (char c: event.getText().toCharArray()) {
                if (c=='"'||c=='`') {
                    return false;
                }
            }
            break;
        case CeylonLexer.CHAR_LITERAL:
            for (char c: event.getText().toCharArray()) {
                if (c=='\'') {
                    return false;
                }
            }
            break;
        case CeylonLexer.MULTI_COMMENT:
            for (char c: event.getText().toCharArray()) {
                if (c=='/'||c=='*') {
                    return false;
                }
            }
            break;
        case CeylonLexer.LINE_COMMENT:
            for (char c: event.getText().toCharArray()) {
                if (c=='\n'||c=='\f'||c=='\r') {
                    return false;
                }
            }
            break;
        default:
            return false;
        }
        return true;
    }
    
    public void createPresentation(TextPresentation presentation, 
            ITypedRegion damage) {
        ANTLRStringStream input = 
                new NewlineFixingStringStream(document.get());
        CeylonLexer lexer = 
                new CeylonLexer(input);
        CommonTokenStream tokenStream = 
                new CommonTokenStream(
                        new CeylonInterpolatingLexer(lexer));
        
        CeylonParser parser = 
                new CeylonParser(tokenStream);
        try {
            parser.compilationUnit();
        }
        catch (RecognitionException e) {
            throw new RuntimeException(e);
        }
        
        //it sounds strange, but it's better to parse
        //and cache here than in getDamageRegion(),
        //because these methods get called in strange
        //orders
        tokens = tokenStream.getTokens();
        
        highlightTokens(presentation, damage);
    }

    private void highlightTokens(TextPresentation presentation,
            ITypedRegion damage) {
        //int prevStartOffset= -1;
        //int prevEndOffset= -1;
        boolean inMetaLiteral=false;
        int inInterpolated=0;
        boolean afterMemberOp = false;
        //start iterating tokens
        Iterator<? extends Token> iter = tokens.iterator();
        if (iter!=null) {
            while (iter.hasNext()) {
                CommonToken token = (CommonToken) iter.next();
                int tt = token.getType();
                if (tt==CeylonLexer.EOF) {
                    break;
                }
                switch (tt) {
                case CeylonParser.BACKTICK:
                    inMetaLiteral = !inMetaLiteral;
                    break;
                case CeylonParser.STRING_START:
                    inInterpolated++;
                    break;
                case CeylonParser.STRING_END:
                    inInterpolated--;
                    break;
                }
                
                int startOffset = token.getStartIndex();
                int endOffset = token.getStopIndex()+1;
                if (endOffset<damage.getOffset()) continue;
                if (startOffset>damage.getOffset()+damage.getLength()) break;
                
                int realStartOffset = startOffset;
                int realEndOffset = endOffset;
                if (tt==CeylonParser.STRING_START ||
                    tt==CeylonParser.STRING_MID) {
                    endOffset-=2;
                }
                if (tt==CeylonParser.STRING_MID ||
                    tt==CeylonParser.STRING_END) {
                    boolean isBacktick = false;
                    try {
                        isBacktick = document.getChar(startOffset)=='`';
                    } catch (BadLocationException e) {}
                    startOffset+=isBacktick?2:1; 
                }
                /*if (startOffset <= prevEndOffset && 
                        endOffset >= prevStartOffset) {
                    //this case occurs when applying a
                    //quick fix, and causes an error
                    //from SWT if we let it through
                    continue;
                }*/
                if (tt==CeylonParser.STRING_MID ||
                    tt==CeylonParser.STRING_END) {
                    changeTokenPresentation(presentation,
                            getInterpolationColoring(),
                            realStartOffset,startOffset,
                            inInterpolated>1 ? SWT.ITALIC : SWT.NORMAL);
                }
                changeTokenPresentation(presentation, 
                        afterMemberOp && tt==CeylonLexer.LIDENTIFIER ?
                                getMemberColoring() : getColoring(token), 
                        startOffset, endOffset,
                        inMetaLiteral || inInterpolated>1 ||
                            inInterpolated>0
                                && tt!=CeylonParser.STRING_START
                                && tt!=CeylonParser.STRING_MID
                                && tt!=CeylonParser.STRING_END? 
                                    SWT.ITALIC : SWT.NORMAL);
                if (tt==CeylonParser.STRING_MID ||
                    tt==CeylonParser.STRING_START) {
                    changeTokenPresentation(presentation, 
                            getInterpolationColoring(),
                            endOffset,realEndOffset,
                            inInterpolated>1 ? SWT.ITALIC : SWT.NORMAL);
                }
                //prevStartOffset= startOffset;
                //prevEndOffset= endOffset;
                afterMemberOp = tt==CeylonLexer.MEMBER_OP ||
                                tt==CeylonLexer.SAFE_MEMBER_OP||
                                tt==CeylonLexer.SPREAD_OP;
            }
        }
    }
    
    private void changeTokenPresentation(TextPresentation presentation, 
            TextAttribute attribute, int startOffset, int endOffset,
            int extraStyle) {
        
        Color foreground = attribute==null ? 
                null : attribute.getForeground();
        Color background = attribute==null ? 
                null : attribute.getBackground();
        int fontStyle = attribute==null ? 
                extraStyle : attribute.getStyle()|extraStyle;
        StyleRange styleRange = new StyleRange(startOffset, 
                endOffset-startOffset,
                foreground, background, fontStyle);
        
        presentation.addStyleRange(styleRange);
        
    }

    private Position getLinkedPosition(int offset, int length) {
        LinkedModeModel linkedMode = editor.getLinkedMode();
        if (linkedMode.anyPositionContains(offset) ||
                linkedMode.anyPositionContains(offset+length)) {
            try {
                for (Position p: 
                    document.getPositions(linkedMode.toString())) {
                    if (!p.isDeleted()) {
                        if (p.includes(offset) && 
                                p.includes(offset+length)) {
                            return p;
                        }
                    }
                }
            }
            catch (BadPositionCategoryException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private boolean noTextChange(DocumentEvent event) {
        try {
            return document.get(event.getOffset(), event.getLength())
                    .equals(event.getText());
        } 
        catch (BadLocationException e) {
            return false;
        }
    }
    
    public void setDocument(IDocument document) {
        this.document = document;
    }
    
}