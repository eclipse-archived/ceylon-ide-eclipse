package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.util.Nodes.getTokenIndexAtCharacter;

import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.CommonTokenStream;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.ICharacterPairMatcherExtension;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.util.NewlineFixingStringStream;
import com.redhat.ceylon.eclipse.util.Nodes;

public class CeylonCharacterPairMatcher 
        implements ICharacterPairMatcher, 
                   ICharacterPairMatcherExtension {
    
    int anchor = -1;
    
    @Override
    public void dispose() {}

    @Override
    public void clear() {
        anchor = -1;
    }
    
    private int matching(int type) {
        switch (type) {
        case CeylonLexer.LPAREN:
            return CeylonLexer.RPAREN;
        case CeylonLexer.LBRACE:
            return CeylonLexer.RBRACE;
        case CeylonLexer.LBRACKET:
            return CeylonLexer.RBRACKET;
        case CeylonLexer.RPAREN:
            return CeylonLexer.LPAREN;
        case CeylonLexer.RBRACE:
            return CeylonLexer.LBRACE;
        case CeylonLexer.RBRACKET:
            return CeylonLexer.LBRACKET;
        }
        return -1;
    }

    @Override
    public IRegion match(IDocument document, int offset) {
        List<CommonToken> tokens = getTokens(document);
        int index = getTokenIndexAtCharacter(tokens, offset);
        if (index<0) index=-index;
        CommonToken token = tokens.get(index);
        if (index>0 &&
                matching(token.getType())<0 && 
                offset==token.getStartIndex()) {
            index--;
            token = tokens.get(index);
        }
        return getRegion(tokens, index, token);
    }

    private List<CommonToken> getTokens(IDocument document) {
        ANTLRStringStream stream = 
                new NewlineFixingStringStream(document.get());
        CeylonLexer lexer = new CeylonLexer(stream);
        CommonTokenStream ts = new CommonTokenStream(lexer);
        ts.fill();
        return (List) ts.getTokens();
    }

    private IRegion getRegion(List<CommonToken> tokens, int index,
            CommonToken token) {
        int type = token.getType();
        int matching = matching(type);
        if (isOpening(type)) {
            anchor = LEFT; 
            int count = 1;
            for (int i=index+1; i<tokens.size(); i++) {
                CommonToken t = tokens.get(i);
                int tt = t.getType();
                if (tt==type) {
                    count++;
                }
                if (tt==matching) {
                    count--;
                    if (count==0) {
                        return new Region(token.getStartIndex(), 
                                t.getStopIndex()-token.getStartIndex()+1);
                    }
                }
            }
        }
        if (isClosing(type)) {
            anchor = RIGHT;
            int count = 1;
            for (int i=index-1; i>=0; i--) {
                CommonToken t = tokens.get(i);
                int tt = t.getType();
                if (tt==type) {
                    count++;
                }
                if (tt==matching) {
                    count--;
                    if (count==0) {
                        return new Region(t.getStartIndex(), 
                                token.getStopIndex()-t.getStartIndex()+1);
                    }
                }
            }
        }
        return null;
    }

    private boolean isClosing(int type) {
        return type==CeylonLexer.RPAREN ||
                type==CeylonLexer.RBRACE ||
                type==CeylonLexer.RBRACKET;
    }

    private boolean isOpening(int type) {
        return type==CeylonLexer.LPAREN ||
                type==CeylonLexer.LBRACE ||
                type==CeylonLexer.LBRACKET;
    }

    @Override
    public int getAnchor() {
        return anchor;
    }

    @Override
    public IRegion match(IDocument document, int offset, int length) {
        return match(document,offset);
    }

    @Override
    public IRegion findEnclosingPeerCharacters(IDocument document, int offset,
            int length) {
        List<CommonToken> tokens = getTokens(document);
        int index = Nodes.getTokenIndexAtCharacter(tokens, offset);
        if (index<0) index=-index;
        int count = 1;
        for (int i=index; i>=0; i--) {
            CommonToken t = tokens.get(i);
            int tt = t.getType();
            if (isOpening(tt)) {
                count--;
                if (count==0) {
                    return getRegion(tokens, i, t);
                }
            }
            if (isClosing(tt)) {
                count++;
            }
        }
        return null;
    }

    @Override
    public boolean isMatchedChar(char ch) {
        return ch=='('||ch=='{'||ch=='['||ch==')'||ch=='}'||ch==']';
    }

    @Override
    public boolean isMatchedChar(char ch, IDocument document, int offset) {
        return isMatchedChar(ch);
    }

    @Override
    public boolean isRecomputationOfEnclosingPairRequired(IDocument document,
            IRegion currentSelection, IRegion previousSelection) {
        return currentSelection.getOffset()!=previousSelection.getOffset();
    }

}
