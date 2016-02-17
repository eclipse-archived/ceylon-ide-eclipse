import com.redhat.ceylon.eclipse.code.imports {
    eclipseModuleImportUtils
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.eclipse.util {
    Highlights
}
import com.redhat.ceylon.ide.common.correct {
    AddModuleImportQuickFix
}
import com.redhat.ceylon.model.typechecker.model {
    Unit
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
    ICompletionProposal,
    ICompletionProposalExtension6,
    IContextInformation
}
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.ltk.core.refactoring {
    TextChange
}
import org.eclipse.swt.graphics {
    Point,
    Image
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

object eclipseAddModuleImportQuickFix
        satisfies AddModuleImportQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
                & EclipseAbstractQuickFix
                & EclipseDocumentChanges {

    importUtil => eclipseModuleImportUtils;
    
    shared actual void newProposal(EclipseQuickFixData data, String desc, 
        Unit unit, String name, String version) {
        
        data.proposals.add(EclipseAddModuleImportProposal(desc, data.project,
            unit, name, version));
    }
}

class EclipseAddModuleImportProposal(String desc, IProject project, Unit unit,
    String name, String version)
        satisfies ICompletionProposal & ICompletionProposalExtension6 {
    
    shared actual String? additionalProposalInfo => null;
    
    shared actual void apply(IDocument doc) {
        eclipseAddModuleImportQuickFix.applyChanges(project, unit, name, version);
    }
    
    shared actual IContextInformation? contextInformation => null;
    
    shared actual String displayString => desc;
    
    shared actual Point? getSelection(IDocument doc) => null;
    
    shared actual Image image => CeylonResources.\iIMPORT;
    
    shared actual StyledString styledDisplayString 
            => Highlights.styleProposal(displayString, true);
}
