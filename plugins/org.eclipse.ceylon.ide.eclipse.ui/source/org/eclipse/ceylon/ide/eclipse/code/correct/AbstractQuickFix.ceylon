/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.ide.eclipse.code.refactor {
    AbstractLinkedMode
}
import org.eclipse.ceylon.ide.eclipse.ui {
    CeylonResources
}
import org.eclipse.ceylon.ide.eclipse.util {
    Highlights
}
import org.eclipse.ceylon.ide.common.correct {
    AbstractLocalProposal
}
import org.eclipse.ceylon.model.typechecker.model {
    Type
}

import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal,
    ICompletionProposalExtension6,
    IContextInformation
}
import org.eclipse.jface.text.link {
    LinkedPosition
}
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.swt.graphics {
    Point
}
import org.eclipse.ceylon.ide.eclipse.platform {
    EclipseLinkedMode
}

abstract class EclipseLocalProposal(EclipseQuickFixData data, shared actual String displayString)
        extends AbstractLinkedMode(data.editor)
        satisfies AbstractLocalProposal
                & ICompletionProposal
                & ICompletionProposalExtension6 {
    
    shared actual variable Integer currentOffset = -1;
    
    shared actual variable Integer exitPos = 0;
    
    shared actual variable {String*} names = empty;
    
    shared actual variable Integer offset = 0;
    
    shared actual variable Type? type = null;
    
    hintTemplate => "Enter type and name for new local {0}";
    
    shared actual void updatePopupLocation() {
        LinkedPosition? pos = currentLinkedPosition;
        value popup = infoPopup;
        if (!exists pos) {
            popup.setHintTemplate(hintTemplate);
        } else if (pos.sequenceNumber == 1) {
            popup.setHintTemplate("Enter type for new local {0}");
        } else {
            popup.setHintTemplate("Enter name for new local {0}");
        }
    }
    
    shared actual void apply(IDocument doc) {
        currentOffset = data.editor.selection.offset;
        
        value change = performInitialChange(data, currentOffset);
        
        if (exists change) {
            change.apply();
            value unit = data.editor.parseController.lastCompilationUnit.unit;
            assert(is EclipseDocument edoc = data.document);
            value lm = EclipseLinkedMode(edoc, linkedModeModel);
            addLinkedPositions(lm, unit);
            enterLinkedMode(doc, 2, exitPosition);
            openPopup();
        }
    }
    
    Integer exitPosition => exitPos + initialName.size + 9;
    
    shared actual default StyledString styledDisplayString
            => Highlights.styleProposal(displayString, false, true);
    
    shared actual Point? getSelection(IDocument? doc) => null;
    
    shared actual String? additionalProposalInfo => null;
    
    image => CeylonResources.\iMINOR_CHANGE;
    
    shared actual IContextInformation? contextInformation => null;
}
