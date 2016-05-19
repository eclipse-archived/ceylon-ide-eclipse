import com.redhat.ceylon.eclipse.code.complete {
    RefinementCompletionProposal
}
import com.redhat.ceylon.eclipse.util {
    Highlights
}

import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal,
    ICompletionProposalExtension6,
    IContextInformation
}
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.swt.graphics {
    Point,
    Image
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
