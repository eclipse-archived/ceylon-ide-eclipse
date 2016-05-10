import com.redhat.ceylon.eclipse.code.complete {
    RefinementCompletionProposal
}
import com.redhat.ceylon.eclipse.util {
    Highlights
}
import com.redhat.ceylon.ide.common.correct {
    RefineFormalMembersQuickFix
}

import org.eclipse.core.resources {
    IFile,
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
    ICompletionProposal,
    ICompletionProposalExtension6,
    IContextInformation
}
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.ltk.core.refactoring {
    TextChange,
    PerformChangeOperation
}
import org.eclipse.swt.graphics {
    Point,
    Image
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

object eclipseRefineFormalMembersQuickFix 
        satisfies RefineFormalMembersQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
                & EclipseDocumentChanges & EclipseAbstractQuickFix {
    
    getDocChar(IDocument doc, Integer offset) => doc.getChar(offset);
    
    shared actual void newRefineFormalMembersProposal(EclipseQuickFixData data, String desc) {
        value proposal = object extends RefineFormalMembersProposal(data, desc) {
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

abstract class RefineFormalMembersProposal(EclipseQuickFixData data, String desc) 
        satisfies ICompletionProposal
                & ICompletionProposalExtension6 {

    //TODO: list the members that will be refined!
    shared actual String? additionalProposalInfo => null;
    
    shared actual IContextInformation? contextInformation => null;
    
    shared actual String displayString => desc;
    
    shared actual Point? getSelection(IDocument? iDocument) => null;
    
    shared actual Image image => RefinementCompletionProposal.\iFORMAL_REFINEMENT;
    
    shared actual StyledString styledDisplayString { 
        value hint = CorrectionUtil
                .shortcut("com.redhat.ceylon.eclipse.ui.action.refineFormalMembers");
        
        return Highlights.styleProposal(displayString, false)
                .append(hint, StyledString.\iQUALIFIER_STYLER);
    }
    
    
}