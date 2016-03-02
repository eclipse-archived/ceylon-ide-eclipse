import com.redhat.ceylon.ide.common.correct {
    AssertExistsDeclarationQuickFix
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
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

object assertExistsDeclarationQuickFix
        satisfies AssertExistsDeclarationQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
                & EclipseAbstractQuickFix
                & EclipseDocumentChanges {
    
    newProposal(EclipseQuickFixData data, String desc, TextChange change, DefaultRegion region) 
            => data.proposals.add(CorrectionProposal(desc, change, toRegion(region)));
}