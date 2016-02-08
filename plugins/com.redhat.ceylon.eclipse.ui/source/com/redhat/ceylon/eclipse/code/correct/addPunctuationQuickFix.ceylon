import com.redhat.ceylon.ide.common.correct {
    AddPunctuationQuickFix
}

import org.eclipse.core.resources {
    IProject,
    IFile
}
import org.eclipse.jface.text {
    IDocument,
    Region
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

object eclipseAddPunctuationQuickFix 
        satisfies AddPunctuationQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
        & EclipseAbstractQuickFix
        & EclipseDocumentChanges {
    
    shared actual void newProposal(EclipseQuickFixData data, Integer offset, 
        Integer length, String desc, TextChange change) {
        
        data.proposals.add(CorrectionProposal(desc, change, Region(offset, length)));
    }

}