import com.redhat.ceylon.ide.common.correct {
    ChangeTypeQuickFix
}
import com.redhat.ceylon.model.typechecker.model {
    Unit
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

object eclipseChangeTypeQuickFix
        satisfies ChangeTypeQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
                & EclipseAbstractQuickFix
                & EclipseDocumentChanges {
    
    shared actual void newProposal(EclipseQuickFixData data, String desc, 
        TextChange change, Integer offset, Integer length, Unit unit) {
        
        data.proposals.add(ChangeTypeProposal(desc, Region(offset, length),
            unit, change));
    }
}
