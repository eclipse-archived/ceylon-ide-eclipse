/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.ide.eclipse.code.hover {
    DocumentationHover
}
import org.eclipse.ceylon.ide.eclipse.ui {
    CeylonPlugin
}
import org.eclipse.ceylon.ide.eclipse.util {
    Highlights
}
import org.eclipse.ceylon.ide.common.completion {
    ProposalKind,
    keyword
}
import org.eclipse.ceylon.ide.common.doc {
    Icons
}
import org.eclipse.ceylon.ide.common.platform {
    TextChange
}
import org.eclipse.ceylon.model.typechecker.model {
    Declaration
}

import java.lang {
    JString=String
}

import org.eclipse.core.runtime {
    IProgressMonitor
}
import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.swt.graphics {
    Point
}
shared object proposalFactory {
    
    alias Builder => ICompletionProposal(EclipseCompletionContext, Integer, 
        String, CompletionProposal.ImageRetriever, String, String,
        ProposalKind, TextChange?, Declaration?, Point?);
    
    shared ICompletionProposal create(EclipseCompletionContext ctx, Integer offset, 
        String prefix, Icons|Declaration icon, String description, String text,
        ProposalKind kind, TextChange? additionalChange, Point? region) {
        
        Builder builder = switch (kind)
        case (keyword) keywordProposal
        else basicProposal;
        
        CompletionProposal.ImageRetriever imageRetriever;
        Declaration? decl;
        if (is Icons icon) {
            imageRetriever = CompletionProposal.IconImageRetriever(icon);
            decl = null;
        } else {
            imageRetriever = CompletionProposal.DeclarationImageRetriever(icon);
            decl = icon;
        }

        return builder(ctx, offset, prefix, imageRetriever, description, text, kind, 
            additionalChange, decl, region);
    }
    
    ICompletionProposal basicProposal(EclipseCompletionContext ctx, Integer _offset, 
        String _prefix, CompletionProposal.ImageRetriever icon, String description, String _text,
        ProposalKind kind, TextChange? additionalChange, Declaration? decl,
        Point? selection) {
        
        return object extends CompletionProposal(_offset, _prefix, icon, description, _text) {
            shared actual String? additionalProposalInfo {
                if (exists res = getAdditionalProposalInfo(null)) {
                    return res.string;
                }
                return null;
            }
            
            // We need to return a JString because the formal type is Object,
            // otherwise the compiler will generate an incorrect c.l.String
            shared actual JString? getAdditionalProposalInfo(IProgressMonitor? pm) {
                return if (exists decl)
                then JString(DocumentationHover.getDocumentationFor(ctx.cpc, decl, pm))
                else null;
            }
            
            shared actual Point? getSelection(IDocument document)
                    => selection else super.getSelection(document);
        };
    }
    
    ICompletionProposal keywordProposal(EclipseCompletionContext ctx, Integer _offset, 
        String _prefix, CompletionProposal.ImageRetriever icon, String description, String _text,
        ProposalKind kind, TextChange? additionalChange, Declaration? decl,
        Point? selection) {
        
        return object extends CompletionProposal(_offset, _prefix, icon, description, _text) {
            
            styledDisplayString =>
                StyledString(displayString, Highlights.FontStyler(
                    CeylonPlugin.completionFont, Highlights.kwStyler));

            shared actual Point? getSelection(IDocument document)
                    => selection;
        };
    }
}