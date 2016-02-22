import com.redhat.ceylon.ide.common.correct {
    MiscQuickFixes
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

object eclipseMiscQuickFix
        satisfies MiscQuickFixes<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
                & EclipseAbstractQuickFix
                & EclipseDocumentChanges {
    
    convertToBlockQuickFix => eclipseConvertToBlockQuickFix;
    convertToSpecifierQuickFix => eclipseConvertToSpecifierQuickFix;
    convertToGetterQuickFix => eclipseConvertToGetterQuickFix;
}