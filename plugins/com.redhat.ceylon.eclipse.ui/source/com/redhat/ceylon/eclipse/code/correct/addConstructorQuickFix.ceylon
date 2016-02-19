import com.redhat.ceylon.ide.common.correct {
    AddConstructorQuickFix
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
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}

object eclipseAddConstructorQuickFix
        satisfies AddConstructorQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
                & EclipseAbstractQuickFix
                & EclipseDocumentChanges {
    
    shared actual void newProposal(EclipseQuickFixData data, String name, 
        TextChange change, DefaultRegion region) {
        
        data.proposals.add(AddConstructorProposal(name, change, toRegion(region)));
    }
}
