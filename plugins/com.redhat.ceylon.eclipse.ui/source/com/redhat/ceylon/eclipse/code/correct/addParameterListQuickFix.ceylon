import com.redhat.ceylon.ide.common.correct {
    AddParameterListQuickFix
}

import org.eclipse.core.resources {
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

object eclipseAddParameterListQuickFix
        satisfies AddParameterListQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
        & EclipseAbstractQuickFix
        & EclipseDocumentChanges {
    
    shared actual void newProposal(EclipseQuickFixData data,
        Integer start, String desc, TextChange change) {
        
        data.proposals.add(AddParameterListProposal(start, desc, change));
    }
    
}