import com.redhat.ceylon.eclipse.code.correct {
    LinkedModeCompletionProposal {
        NullProposal
    }
}
import com.redhat.ceylon.eclipse.code.outline {
    CeylonLabelProvider
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.ide.common.correct {
    AssignToLocalProposal
}
import com.redhat.ceylon.model.typechecker.model {
    Unit,
    Type
}

import java.util {
    Collections
}

import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.jface.viewers {
    StyledString
}

class EclipseAssignToLocalProposal(EclipseQuickFixData data, String desc)
        extends EclipseLocalProposal(data, desc)
        satisfies AssignToLocalProposal<ICompletionProposal> {

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
