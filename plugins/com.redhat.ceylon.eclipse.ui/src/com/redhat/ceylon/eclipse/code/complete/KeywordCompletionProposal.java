package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isModuleDescriptor;
import static com.redhat.ceylon.eclipse.code.complete.OccurrenceLocation.CASE;
import static com.redhat.ceylon.eclipse.code.complete.OccurrenceLocation.CATCH;
import static com.redhat.ceylon.eclipse.code.complete.OccurrenceLocation.EXPRESSION;
import static com.redhat.ceylon.eclipse.code.complete.OccurrenceLocation.META;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getDecoratedImage;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_LITERAL;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.Escaping;
import com.redhat.ceylon.eclipse.util.Highlights;

public class KeywordCompletionProposal extends CompletionProposal {
    
    public static final Set<String> expressionKeywords = 
            new LinkedHashSet<String>(Arrays.asList(
                    "object", "value", "void", "function", 
                    "this", "outer", "super", 
                    "of", "in", "else", "for", "if", "is", 
                    "exists", "nonempty", "then", "let"));
    
    public static final Set<String> conditionKeywords = 
            new LinkedHashSet<String>(Arrays.asList("assert", "let",
                    "while", "for", "if", "switch", "case", "catch"));
    
    static void addKeywordProposals(CeylonParseController cpc, int offset, 
            String prefix, List<ICompletionProposal> result, Node node,
            OccurrenceLocation ol) {
        if (isModuleDescriptor(cpc) && 
                ol!=META && (ol==null||!ol.reference)) {
            if (prefix.isEmpty() || "import".startsWith(prefix)) {
                if (node instanceof Tree.CompilationUnit) {
                    List<Tree.ModuleDescriptor> moduleDescriptors = 
                            cpc.getRootNode().getModuleDescriptors();
                    if (!moduleDescriptors.isEmpty()) {
                        Tree.ModuleDescriptor moduleDescriptor = moduleDescriptors.get(0);
                        if (moduleDescriptor.getImportModuleList() != null && 
                            moduleDescriptor.getImportModuleList().getStartIndex() < offset ) {
                            addKeywordProposal(offset, prefix, result, "import");
                        }
                    }
                }
                else if (node instanceof Tree.ImportModuleList || 
                        node instanceof Tree.BaseMemberExpression) {
                    addKeywordProposal(offset, prefix, result, "import");
                }
            }
        }
        else if (!prefix.isEmpty() && ol!=CATCH && ol!=CASE) {
            //TODO: this filters out satisfies/extends in an object named arg
            for (String keyword: ol==EXPRESSION ? expressionKeywords : Escaping.KEYWORDS) {
                if (keyword.startsWith(prefix)) {
                    addKeywordProposal(offset, prefix, result, keyword);
                }
            }
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
        return new StyledString(getDisplayString(), Highlights.KW_STYLER);
    }
    
    static void addKeywordProposal(int offset, String prefix, 
            List<ICompletionProposal> result, String keyword) {
        result.add(new KeywordCompletionProposal(offset, prefix, keyword));
    }
    

}
