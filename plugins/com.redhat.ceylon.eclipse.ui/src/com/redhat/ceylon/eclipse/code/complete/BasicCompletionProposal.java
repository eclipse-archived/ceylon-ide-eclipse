package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getTextForDocLink;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.ide.common.util.Escaping.escapeName;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

class BasicCompletionProposal extends CompletionProposal {
    
    @Deprecated
    static void addImportProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            Declaration dec, Scope scope) {
        result.add(new BasicCompletionProposal(offset, prefix,
                dec.getName(), escapeName(dec), dec, cpc));
    }

    @Deprecated
    static void addDocLinkProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            Declaration dec, Scope scope) {
        //for doc links, propose both aliases and unaliased qualified form
        //we don't need to do this in code b/c there is no fully-qualified form
        String name = dec.getName();
        String aliasedName = dec.getName(cpc.getLastCompilationUnit().getUnit());
        if (!name.equals(aliasedName)) {
            result.add(new BasicCompletionProposal(offset, prefix,
                    aliasedName, aliasedName, dec, cpc));
        }
        result.add(new BasicCompletionProposal(offset, prefix,
                name, getTextForDocLink(cpc, dec), dec, cpc));
    }
    
    private final CeylonParseController cpc;
    private final Declaration declaration;
    
    BasicCompletionProposal(int offset, String prefix, 
            String desc, String text, Declaration dec, 
            CeylonParseController cpc) {
        super(offset, prefix, getImageForDeclaration(dec), 
                desc, text);
        this.cpc = cpc;
        this.declaration = dec;
    }
    
    public String getAdditionalProposalInfo() {
        return getAdditionalProposalInfo(null);
    }

    public String getAdditionalProposalInfo(IProgressMonitor monitor) {
        return getDocumentationFor(cpc, declaration, monitor);
    }
}