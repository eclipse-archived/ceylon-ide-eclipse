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
    Tree
}
import org.eclipse.ceylon.ide.eclipse.code.editor {
    CeylonEditor
}
import org.eclipse.ceylon.ide.eclipse.ui {
    CeylonResources
}
import org.eclipse.ceylon.ide.common.correct {
    convertToClassQuickFix
}

import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal,
    IContextInformation
}
import org.eclipse.swt.graphics {
    Point,
    Image
}


class EclipseConvertToClassProposal(String desc, CeylonEditor editor,
    Tree.ObjectDefinition declaration)
            satisfies ICompletionProposal {
    
    displayString => desc;
    
    shared actual void apply(IDocument doc) {
        value lm = AbstractLinkedModeAdapter {
            hintTemplate = "Enter name for new class {0}";
            ceylonEditor = editor;
            document = EclipseDocument(doc);
        };
        convertToClassQuickFix.applyChanges {
            doc = EclipseDocument(doc);
            node = declaration;
            mode = lm.linkedMode;
        };
        lm.openPopup();
    }
        
    shared actual String? additionalProposalInfo => null;        
    shared actual IContextInformation? contextInformation => null;
    shared actual Point? getSelection(IDocument doc) => null;
    
    shared actual Image image 
            => if (declaration.declarationModel.shared)
            then CeylonResources.\iCLASS
            else CeylonResources.\iLOCAL_CLASS;
}
