import com.redhat.ceylon.ide.common.correct {
    AddSpreadToVariadicParameterQuickFix
}
import com.redhat.ceylon.model.typechecker.model {
    TypedDeclaration
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
import org.eclipse.ltk.core.refactoring {
    TextChange
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

object eclipseAddSpreadToVariadicParameterQuickFix
        satisfies AddSpreadToVariadicParameterQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
        & EclipseAbstractQuickFix
        & EclipseDocumentChanges {
    
    shared actual void newProposal(EclipseQuickFixData data, String desc, 
        TypedDeclaration parameter, Integer offset, TextChange change) {
        
        value proposal = AddSpreadToVariadicParameterProposal(parameter, desc, 
            offset, change);
        
        if (!data.proposals.contains(proposal)) {
            data.proposals.add(proposal);
        }
    }
    

}