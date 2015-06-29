package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isModuleDescriptor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getDecoratedImage;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_LITERAL;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.CASE;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.CATCH;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.EXPRESSION;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.EXTENDS;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.META;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.Escaping;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.eclipse.util.Highlights.FontStyler;
import com.redhat.ceylon.eclipse.util.OccurrenceLocation;

public class KeywordCompletionProposal extends CompletionProposal {
    
    public static final Set<String> expressionKeywords = 
            new LinkedHashSet<String>(Arrays.asList(
                    "object", "value", "void", "function", 
                    "this", "outer", "super", 
                    "of", "in", "else", "for", "if", "is", 
                    "exists", "nonempty", "then", "let"));
    
    public static final Set<String> postfixKeywords = 
            new LinkedHashSet<String>(Arrays.asList(
                    "of", "in", "else", "exists", "nonempty", "then"));
    
    public static final Set<String> conditionKeywords = 
            new LinkedHashSet<String>(Arrays.asList("assert", "let",
                    "while", "for", "if", "switch", "case", "catch"));
    
    static void addKeywordProposals(CeylonParseController cpc, int offset, 
            String prefix, List<ICompletionProposal> result, Node node,
            OccurrenceLocation ol, boolean postfix, int previousTokenType) {
        if (isModuleDescriptor(cpc) && 
                ol!=META && (ol==null||!ol.reference)) {
            //outside of backtick quotes, the only keyword allowed
            //in a module descriptor is "import"
            if ("import".startsWith(prefix)) {
                addKeywordProposal(offset, prefix, result, "import");
            }
        }
        else if (!prefix.isEmpty() && ol!=CATCH && ol!=CASE) {
            //TODO: this filters out satisfies/extends in an object named arg
            Set<String> keywords;
            if (ol==EXPRESSION) {
                keywords = postfix ? postfixKeywords : expressionKeywords;
            }
            else {
                keywords = Escaping.KEYWORDS;
            }
            for (String keyword: keywords) {
                if (keyword.startsWith(prefix)) {
                    addKeywordProposal(offset, prefix, result, keyword);
                }
            }
        }
        else if (ol==CASE && previousTokenType==CeylonLexer.LPAREN) {
            addKeywordProposal(offset, prefix, result, "is"); 
        }
        else if (!prefix.isEmpty() && ol==CASE) {
            if ("case".startsWith(prefix)) {
                addKeywordProposal(offset, prefix, result, "case");
            }
        }
        else if (ol==null && node instanceof Tree.ConditionList && 
                previousTokenType==CeylonLexer.LPAREN) {
            addKeywordProposal(offset, prefix, result, "exists");
            addKeywordProposal(offset, prefix, result, "nonempty");
        }
        else if (ol==EXTENDS) {
            addKeywordProposal(offset, prefix, result, "package");
            addKeywordProposal(offset, prefix, result, "super");
        }
    }
    
    KeywordCompletionProposal(int offset, String prefix, String keyword) {
        super(offset, prefix, null, keyword, 
                conditionKeywords.contains(keyword) ? keyword+" ()" : keyword);
    }
    
    @Override
    public Point getSelection(IDocument document) {
        int close = text.indexOf(')');
        if (close>0) {
            return new Point(offset + close - prefix.length(), 0);
        }
        else {
            return super.getSelection(document);
        }
    }
    
    @Override
    public int length(IDocument document) {
        return prefix.length();
    }
    
    @Override
    public Image getImage() {
        return getDecoratedImage(CEYLON_LITERAL, 0, false);
    }

    @Override
    public StyledString getStyledDisplayString() {
        return new StyledString(getDisplayString(), 
                new FontStyler(CeylonPlugin.getCompletionFont(), 
                        Highlights.KW_STYLER));
    }
    
    static void addKeywordProposal(int offset, String prefix, 
            List<ICompletionProposal> result, String keyword) {
        result.add(new KeywordCompletionProposal(offset, prefix, keyword));
    }
    

}
