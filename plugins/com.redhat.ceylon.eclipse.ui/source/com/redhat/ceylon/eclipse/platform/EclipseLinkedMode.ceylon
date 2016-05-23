import ceylon.interop.java {
    createJavaObjectArray
}

import com.redhat.ceylon.eclipse.code.correct {
    EclipseDocument
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.util {
    ELinkedMode=LinkedMode,
    EditorUtil
}
import com.redhat.ceylon.ide.common.completion {
    ProposalsHolder
}
import com.redhat.ceylon.ide.common.platform {
    LinkedMode
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
                length, exitSeqNumber, createJavaObjectArray(holder.proposals));
            ELinkedMode.addLinkedPosition(model, linkedPosition);
        }
    }
    
    shared actual void addEditableGroup(Integer[3]+ positions) {
        value group = LinkedPositionGroup();
        
        for (pos in positions) {
            value [offset, length, exitSeq] = pos;
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
