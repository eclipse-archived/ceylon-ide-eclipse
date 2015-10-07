import com.redhat.ceylon.ide.common.correct {
    ChangeReferenceQuickFix
}
import org.eclipse.core.resources {
    IFile,
    IProject
}
import org.eclipse.jface.text {
    IDocument,
    Region
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}
import org.eclipse.ltk.core.refactoring {
    TextChange
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}

object eclipseChangeReferenceQuickFix
        satisfies ChangeReferenceQuickFix<IFile,IProject,IDocument,InsertEdit,TextEdit,TextChange,EclipseQuickFixData,Region,ICompletionProposal>
                & EclipseAbstractQuickFix
                & EclipseDocumentChanges {
    
    
    shared actual void newChangeReferenceProposal(EclipseQuickFixData data, String desc,
        TextChange change, Region selection) {
        
        data.proposals.add(ChangeReferenceProposal(desc, change, selection));
    }
}
