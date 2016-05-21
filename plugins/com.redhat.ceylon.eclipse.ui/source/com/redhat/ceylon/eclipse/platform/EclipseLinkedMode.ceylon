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
import com.redhat.ceylon.ide.common.platform {
    LinkedMode
}

import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.jface.text.link {
    LinkedModeModel,
    ProposalPosition,
    LinkedPositionGroup,
    LinkedPosition
}

shared class EclipseLinkedMode(EclipseDocument document) extends LinkedMode(document) {
    
    shared LinkedModeModel model = LinkedModeModel();
    
    shared actual void addEditableRegion(Integer start, Integer length,
        Integer exitSeqNumber, Anything proposals) {
        
        if (is ICompletionProposal[] proposals) {
            value linkedPosition = ProposalPosition(document.document, start,
                length, exitSeqNumber, createJavaObjectArray(proposals));
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
