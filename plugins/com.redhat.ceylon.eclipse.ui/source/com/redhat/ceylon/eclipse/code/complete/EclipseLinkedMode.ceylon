import ceylon.interop.java {
    createJavaObjectArray
}

import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.util {
    LinkedMode,
    EditorUtil
}
import com.redhat.ceylon.ide.common.completion {
    LinkedModeSupport
}

import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.jface.text.link {
    LinkedModeModel,
    ProposalPosition
}

interface EclipseLinkedModeSupport satisfies LinkedModeSupport<LinkedModeModel, IDocument, ICompletionProposal> {
    shared actual void addEditableRegion(LinkedModeModel lm, IDocument doc, Integer start, Integer len, Integer exitSeqNumber,
        ICompletionProposal[] proposals) {
        
        value linkedPosition = ProposalPosition(doc, start, len, exitSeqNumber, createJavaObjectArray(proposals));
        LinkedMode.addLinkedPosition(lm, linkedPosition);
    }

    shared actual void installLinkedMode(IDocument doc, LinkedModeModel lm, Object owner, Integer exitSeqNumber, Integer exitPosition) {
        assert(is CeylonEditor ceylonEditor = EditorUtil.currentEditor);
        LinkedMode.installLinkedMode(ceylonEditor, doc, lm, owner, LinkedMode.NullExitPolicy(), exitSeqNumber, exitPosition);
    }
    
    shared actual LinkedModeModel newLinkedMode() => LinkedModeModel();
}