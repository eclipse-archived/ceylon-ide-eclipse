/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.ide.eclipse.code.correct {
    EclipseDocument
}
import org.eclipse.ceylon.ide.eclipse.code.outline {
    CeylonLabelProvider
}
import org.eclipse.ceylon.ide.eclipse.platform {
    EclipseLinkedMode,
    EclipseProposalsHolder
}
import org.eclipse.ceylon.ide.eclipse.ui {
    CeylonResources,
    CeylonPlugin
}
import org.eclipse.ceylon.ide.eclipse.util {
    Highlights
}
import org.eclipse.ceylon.ide.common.completion {
    ImportedModulePackageProposal,
    ProposalsHolder
}
import org.eclipse.ceylon.ide.common.platform {
    LinkedMode
}
import org.eclipse.ceylon.ide.common.refactoring {
    DefaultRegion
}
import org.eclipse.ceylon.model.typechecker.model {
    Package,
    Declaration,
    ModelUtil,
    TypeDeclaration
}

import java.lang {
    JCharacter=Character,
    overloaded
}

import org.eclipse.jface.text {
    IDocument,
    DocumentEvent,
    ITextViewer,
    BadLocationException
}
import org.eclipse.jface.text.contentassist {
    IContextInformation
}
import org.eclipse.jface.text.link {
    ILinkedModeListener
}
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.swt.graphics {
    Point,
    Image
}

shared class EclipseImportedModulePackageProposal(Integer offset, String prefix, String memberPackageSubname, Boolean withBody,
                String fullPackageName, EclipseCompletionContext ctx, Package candidate)
                extends ImportedModulePackageProposal
                (offset, prefix, memberPackageSubname, withBody, fullPackageName, candidate, ctx)
                satisfies EclipseCompletionProposal{

    shared actual variable String? currentPrefix = prefix;
    shared actual variable Boolean toggleOverwriteInternal = false;
    shared actual Boolean toggleOverwrite => toggleOverwriteInternal;

    shared actual Boolean qualifiedNameIsPath => true;
    
    shared actual Image image => CeylonResources.\iPACKAGE;
    
    shared actual void apply(IDocument doc) => applyInternal(EclipseDocument(doc));
    
    shared actual void newPackageMemberCompletionProposal(ProposalsHolder proposals, Declaration d, DefaultRegion selection, LinkedMode lm) {
        if (is EclipseProposalsHolder proposals) {
            value proposal = object satisfies IEclipseCompletionProposal2And6 {
                function length(IDocument document) {
                    variable value length = 0;
                    variable value i = selection.start;
                    try {
                        while (i<document.length &&
                            (JCharacter.isJavaIdentifierPart(document.getChar(i)) ||
                            document.getChar(i)=='.')) {
                            length++;
                            i++;
                        }
                    }
                    catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                    return length;
                }
                
                shared actual overloaded void apply(IDocument document) {
                    try {
                        document.replace(selection.start,
                            length(document),
                            d.name);
                    }
                    catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                    
                    assert(is EclipseLinkedMode lm);
                    lm.model.exit(ILinkedModeListener.updateCaret);
                }
                
                shared actual overloaded void apply(ITextViewer viewer, Character trigger, Integer stateMask, Integer offset) {
                    apply(viewer.document);
                }
                
                shared actual Point? getSelection(IDocument document) => null;
                
                shared actual Image image => CeylonLabelProvider.getImageForDeclaration(d);
                
                shared actual String displayString => d.name;
                
                shared actual IContextInformation? contextInformation => null;
                
                shared actual String? additionalProposalInfo => null;
                
                shared actual void selected(ITextViewer iTextViewer, Boolean boolean) {}
                
                shared actual void unselected(ITextViewer iTextViewer) {}
                
                shared actual StyledString styledDisplayString {
                    StyledString result = StyledString();
                    Highlights.styleIdentifier(result, prefix,
                        displayString,
                        d is TypeDeclaration 
                        then Highlights.typeStyler 
                        else Highlights.memberStyler,
                        CeylonPlugin.completionFont);
                    return result;
                }
                
                shared actual Boolean validate(IDocument document, Integer currentOffset, DocumentEvent? documentEvent) {
                    value start = selection.start;
                    if (currentOffset<start) {
                        return false;
                    }
                    String prefix;
                    try {
                        prefix = document.get(start, currentOffset-start);
                    }
                    catch (BadLocationException e) {
                        return false;
                    }
                    return ModelUtil.isNameMatching(prefix, d);
                }
                
            };
            
            proposals.add(proposal);
        }
    }
}