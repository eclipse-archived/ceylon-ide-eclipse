/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.complete;

import static org.eclipse.ceylon.ide.eclipse.code.complete.CodeCompletions.getTextForDocLink;
import static org.eclipse.ceylon.ide.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static org.eclipse.ceylon.ide.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.ide.common.typechecker.LocalAnalysisResult;
import org.eclipse.ceylon.ide.common.util.escaping_;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Scope;
import org.eclipse.ceylon.model.typechecker.model.Unit;

public class BasicCompletionProposal extends CompletionProposal {
    
    @Deprecated
    static void addImportProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            Declaration dec, Scope scope) {
        result.add(new BasicCompletionProposal(offset, prefix,
                dec.getName(), escaping_.get_().escapeName(dec), dec, cpc));
    }

    @Deprecated
    static void addDocLinkProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            Declaration dec, Scope scope) {
        //for doc links, propose both aliases and unaliased 
        //qualified form we don't need to do this in code 
        //b/c there is no fully-qualified form
        String name = dec.getName();
        Unit unit = cpc.getLastCompilationUnit().getUnit();
        String aliasedName = dec.getName(unit);
        if (!name.equals(aliasedName)) {
            result.add(new BasicCompletionProposal(offset, prefix,
                    aliasedName, aliasedName, dec, cpc));
        }
        result.add(new BasicCompletionProposal(offset, prefix,
                name, getTextForDocLink(cpc, dec), dec, cpc));
    }
    
    private final CeylonParseController cpc;
    private final Declaration declaration;
    
    public BasicCompletionProposal(int offset, String prefix, 
            String desc, String text, Declaration dec, 
            LocalAnalysisResult cpc) {
        super(offset, prefix, getImageForDeclaration(dec), 
                desc, text);
        this.cpc = ((EclipseCompletionContext) cpc).getCpc();
        this.declaration = dec;
    }
    
    public String getAdditionalProposalInfo() {
        return getAdditionalProposalInfo(null);
    }

    public String getAdditionalProposalInfo(IProgressMonitor monitor) {
        return getDocumentationFor(cpc, declaration, monitor);
    }
}