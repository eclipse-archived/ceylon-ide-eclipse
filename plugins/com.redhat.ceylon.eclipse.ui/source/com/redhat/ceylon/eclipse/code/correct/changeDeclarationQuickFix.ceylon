import com.redhat.ceylon.ide.common.correct {
    ChangeDeclarationQuickFix
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

object eclipseChangeDeclarationQuickFix
        satisfies ChangeDeclarationQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
                & EclipseAbstractQuickFix
                & EclipseDocumentChanges {
    
    shared actual void newProposal(EclipseQuickFixData data, String keyword,
        String desc, Integer position, TextChange change) {
        
        data.proposals.add(CorrectionProposal(desc, change, Region(position, keyword.size)));
    }
    

}