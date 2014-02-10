package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.isModuleDescriptor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.KW_STYLER;
import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.keywords;

import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class KeywordCompletionProposal extends CompletionProposal {
    
    static void addKeywordProposals(CeylonParseController cpc, int offset, 
    		String prefix, List<ICompletionProposal> result, Node node) {
        if (isModuleDescriptor(cpc)) {
            if( prefix.isEmpty() || "import".startsWith(prefix) ) {
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
        else if (!prefix.isEmpty()) {
            for (String keyword: keywords) {
                if (keyword.startsWith(prefix)) {
                    addKeywordProposal(offset, prefix, result, keyword);
                }
            }
        }
    }
    
    KeywordCompletionProposal(int offset, String prefix, String keyword) {
        super(offset, prefix, null, keyword, keyword);
    }

    @Override
    public StyledString getStyledDisplayString() {
        return new StyledString(getDisplayString(), KW_STYLER);
    }
    
    static void addKeywordProposal(int offset, String prefix, 
            List<ICompletionProposal> result, String keyword) {
        result.add(new KeywordCompletionProposal(offset, prefix, keyword));
    }
    

}
