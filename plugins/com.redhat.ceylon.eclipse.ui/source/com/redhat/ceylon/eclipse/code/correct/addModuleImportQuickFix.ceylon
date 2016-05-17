import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.eclipse.util {
    Highlights
}
import com.redhat.ceylon.ide.common.correct {
    addModuleImportQuickFix,
    QuickFixData
}
import com.redhat.ceylon.model.typechecker.model {
    Unit
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

class EclipseAddModuleImportProposal(String desc, QuickFixData data, Unit unit,
    String name, String version)
        satisfies ICompletionProposal & ICompletionProposalExtension6 {
    
    shared actual String? additionalProposalInfo => null;
    
    shared actual void apply(IDocument doc) {
        addModuleImportQuickFix.applyChanges(data, unit, name, version);
    }
    
    shared actual IContextInformation? contextInformation => null;
    
    shared actual String displayString => desc;
    
    shared actual Point? getSelection(IDocument doc) => null;
    
    shared actual Image image => CeylonResources.\iIMPORT;
    
    shared actual StyledString styledDisplayString 
            => Highlights.styleProposal(displayString, true);
}
