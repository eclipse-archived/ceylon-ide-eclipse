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
import org.eclipse.ceylon.ide.eclipse.code.editor {
    CeylonEditor
}
import org.eclipse.ceylon.ide.eclipse.util {
    ELinkedMode=LinkedMode,
    EditorUtil
}
import org.eclipse.ceylon.ide.common.completion {
    ProposalsHolder
}
import org.eclipse.ceylon.ide.common.platform {
    LinkedMode
}

import java.lang {
    ObjectArray
}

import org.eclipse.jface.text.link {
    LinkedModeModel,
    ProposalPosition,
    LinkedPositionGroup,
    LinkedPosition
}

shared class EclipseLinkedMode(document, model = LinkedModeModel()) extends LinkedMode(document) {
    EclipseDocument document;
    
    shared default LinkedModeModel model;
    
    shared actual void addEditableRegion(Integer start, Integer length,
        Integer exitSeqNumber, ProposalsHolder holder) {
        
        if (is EclipseProposalsHolder holder) {
            value linkedPosition = ProposalPosition(document.document, start,
                length, exitSeqNumber, ObjectArray.with(holder.proposals));
            ELinkedMode.addLinkedPosition(model, linkedPosition);
        }
    }
    
    shared actual void addEditableGroup(Integer[3]+ positions) {
        value group = LinkedPositionGroup();
        
        for (pos in positions) {
            let ([offset, length, exitSeq] = pos);
            group.addPosition(LinkedPosition(document.document, offset, length, exitSeq));
        }
        
        model.addGroup(group);
    }
    
    shared actual void install(Object owner, Integer exitSeqNumber, Integer exitPosition) {
        if (is CeylonEditor ceylonEditor = EditorUtil.currentEditor) {
            ELinkedMode.installLinkedMode(
                ceylonEditor, document.document, model,
                owner, ELinkedMode.NullExitPolicy(), exitSeqNumber,
                exitPosition
            );
        }
    }
}
