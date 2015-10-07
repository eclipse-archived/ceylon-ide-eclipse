import com.redhat.ceylon.ide.common.correct {
    RefineFormalMembersQuickFix
}

import org.eclipse.core.resources {
    IFile,
    IProject,
    ResourcesPlugin {
        workspace
    }
}
import org.eclipse.core.runtime {
    NullProgressMonitor
}
import org.eclipse.jface.text {
    IDocument,
    Region
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.ltk.core.refactoring {
    TextChange,
    PerformChangeOperation
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

object eclipseRefineFormalMembersQuickFix 
        satisfies RefineFormalMembersQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
                & EclipseDocumentChanges & EclipseAbstractQuickFix {
    
    shared actual Character getDocChar(IDocument doc, Integer offset) => doc.getChar(offset);
    
    shared actual void newRefineFormalMembersProposal(EclipseQuickFixData data, String desc) {
        value proposal = object extends RefineFormalMembersProposal(data.node, data.rootNode, desc) {
            shared actual void apply(IDocument document) {
                if (exists change = refineFormalMembers(data, document, data.editor.selection.offset)) {
                    change.initializeValidationData(null);
                    workspace.run(PerformChangeOperation(change), NullProgressMonitor());
                }
            }
        };
        data.proposals.add(proposal);
    }
}
