import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.ltk.core.refactoring {
    TextChange
}
import com.redhat.ceylon.ide.common.correct {
    ConvertThenElseToIfElse
}
import org.eclipse.jface.text {
    Region,
    IDocument
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}
import org.eclipse.core.resources {
    IProject,
    IFile
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}

object convertThenElseToIfElse
        satisfies ConvertThenElseToIfElse<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
                & EclipseAbstractQuickFix
                & EclipseDocumentChanges {
    
    shared actual void newProposal(EclipseQuickFixData data, String desc, TextChange change, DefaultRegion region) {
        data.proposals.add(CorrectionProposal(desc, change, toRegion(region)));
    }
}
