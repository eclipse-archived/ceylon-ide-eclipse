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
import com.redhat.ceylon.ide.common.completion {
    ProposalsHolder
}
import com.redhat.ceylon.eclipse.platform {
    EclipseProposalsHolder
}

class EclipseAssignToLocalProposal(EclipseQuickFixData data, String desc)
        extends EclipseLocalProposal(data, desc)
        satisfies AssignToLocalProposal {

    shared actual void toNameProposals(String[] names, ProposalsHolder proposals, 
        Integer offset, Unit unit, Integer seq) {
        
        if (is EclipseProposalsHolder proposals) {
            proposals.add(NullProposal(Collections.emptyList<ICompletionProposal>()));
            names.each((n) => proposals.add(LinkedModeCompletionProposal(n, offset, seq)));
        }
    }
    
    shared actual void toProposals(<String|Type>[] types, ProposalsHolder proposals,
        Integer offset, Unit unit) {
        
        assert(is EclipseProposalsHolder proposals);
        
        types.each((t) {
            if (is String t) {
                proposals.add(LinkedModeCompletionProposal(t, offset, t, 0,
                    CeylonLabelProvider.getDecoratedImage(CeylonResources.\iCEYLON_LITERAL, 0, false)));
            } else {
                proposals.add(LinkedModeCompletionProposal(t, unit, offset, 0));
            }
        });
    }
    
    styledDisplayString => 
        let(hint = CorrectionUtil.shortcut("com.redhat.ceylon.eclipse.ui.action.assignToLocal"))
        StyledString(displayString).append(hint, StyledString.\iQUALIFIER_STYLER);
    
}
