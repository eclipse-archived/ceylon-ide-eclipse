import com.redhat.ceylon.ide.common.correct {
    AddInitializerQuickFix
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

object eclipseAddInitializerQuickFix
        satisfies AddInitializerQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
                & EclipseAbstractQuickFix
                & EclipseDocumentChanges {
    
    shared actual void newProposal(EclipseQuickFixData data, String desc, 
        TypedDeclaration dec, Integer offset, Integer length, TextChange change) {
        
        data.proposals.add(AddInitializerProposal(desc, dec, offset, length, change));
    }
}
