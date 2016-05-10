import com.redhat.ceylon.ide.common.correct {
    AddNamedArgumentQuickFix
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
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

object eclipseAddNamedArgumentQuickFix
        satisfies AddNamedArgumentQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
                & EclipseAbstractQuickFix
                & EclipseDocumentChanges {
    
    shared actual void newProposal(EclipseQuickFixData data, String desc, 
        TextChange change, DefaultRegion region) {
        
        data.proposals.add(CorrectionProposal(desc, change, toRegion(region)));
    }
}
