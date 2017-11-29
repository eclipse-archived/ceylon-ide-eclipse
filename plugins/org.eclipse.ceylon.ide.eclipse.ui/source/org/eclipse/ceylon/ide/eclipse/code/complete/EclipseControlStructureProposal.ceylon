/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.compiler.typechecker.tree {
    Node
}
import org.eclipse.ceylon.ide.eclipse.code.correct {
    EclipseDocument
}
import org.eclipse.ceylon.ide.eclipse.ui {
    CeylonResources
}
import org.eclipse.ceylon.ide.common.completion {
    ControlStructureProposal
}
import org.eclipse.ceylon.ide.common.typechecker {
    LocalAnalysisResult
}
import org.eclipse.ceylon.model.typechecker.model {
    Declaration
}

import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.swt.graphics {
    Image
}

shared class EclipseControlStructureProposal(Integer offset, String prefix, String desc,
    String text, Declaration declaration, LocalAnalysisResult cpc, Node? node)
        extends ControlStructureProposal
        (offset, prefix, desc, text, node, declaration, cpc)
                satisfies EclipseCompletionProposal {
            
    shared actual variable String? currentPrefix = prefix;
    
    shared actual Image image => CeylonResources.minorChange;
    
    shared actual variable Boolean toggleOverwriteInternal = false;
    
    shared actual Boolean toggleOverwrite => toggleOverwriteInternal;
    
    shared actual void apply(IDocument doc) => applyInternal(EclipseDocument(doc));
}
