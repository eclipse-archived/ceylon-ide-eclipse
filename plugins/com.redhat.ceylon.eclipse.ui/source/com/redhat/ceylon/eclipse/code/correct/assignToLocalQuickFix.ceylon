import com.redhat.ceylon.eclipse.code.outline {
    CeylonLabelProvider
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.ide.common.correct {
    AssignToLocalQuickFix,
    AssignToLocalProposal
}
import com.redhat.ceylon.model.typechecker.model {
    Unit,
    Type
}

import org.eclipse.core.resources {
    IProject,
    IFile
}
import org.eclipse.jface.text {
    Region,
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.jface.text.link {
    LinkedModeModel
}
import org.eclipse.ltk.core.refactoring {
    TextChange
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}
import java.util {
    Collections
}
import com.redhat.ceylon.eclipse.code.correct {
    LinkedModeCompletionProposal {
        NullProposal
    }
}
import org.eclipse.jface.viewers {
    StyledString
}

object eclipseAssignToLocalQuickFix
        satisfies AssignToLocalQuickFix<IFile,IProject,EclipseQuickFixData>
                & EclipseAbstractQuickFix
                & EclipseDocumentChanges {
    
    shared actual void newProposal(EclipseQuickFixData data, String desc) {
        data.proposals.add(EclipseAssignToLocalProposal(data, desc));
    }
}

class EclipseAssignToLocalProposal(EclipseQuickFixData data, String desc)
        extends EclipseLocalProposal(data, desc)
        satisfies AssignToLocalProposal<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal,LinkedModeModel> {

    shared actual ICompletionProposal[] toNameProposals(String[] names, 
        Integer offset, Unit unit, Integer seq) => [
            NullProposal(Collections.emptyList<ICompletionProposal>()),
            *names.map(
                (n) => LinkedModeCompletionProposal(n, offset, seq)
            ).sequence()
        ];
    
    shared actual ICompletionProposal[] toProposals(<String|Type>[] types, 
        Integer offset, Unit unit) {
        
        return types.map((t) {
            if (is String t) {
                return LinkedModeCompletionProposal(t, offset, t, 0,
                    CeylonLabelProvider.getDecoratedImage(CeylonResources.\iCEYLON_LITERAL, 0, false));
            }
            return LinkedModeCompletionProposal(t, unit, offset, 0);
        }).sequence();
    }
    
    styledDisplayString => 
        let(hint = CorrectionUtil.shortcut("com.redhat.ceylon.eclipse.ui.action.assignToLocal"))
        StyledString(displayString).append(hint, StyledString.\iQUALIFIER_STYLER);
    
}
