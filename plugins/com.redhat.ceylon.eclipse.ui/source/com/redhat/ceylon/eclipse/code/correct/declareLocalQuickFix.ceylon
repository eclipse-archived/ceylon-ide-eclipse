import com.redhat.ceylon.ide.common.correct {
    DeclareLocalQuickFix
}
import org.eclipse.jface.text {
    IDocument,
    IRegion
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}
import org.eclipse.ltk.core.refactoring {
    TextChange
}
import org.eclipse.jface.text.link {
    LinkedModeModel
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import com.redhat.ceylon.eclipse.code.complete {
    EclipseLinkedModeSupport,
    EclipseCompletionManager
}
import com.redhat.ceylon.ide.common.completion {
    IdeCompletionManager
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import org.eclipse.core.resources {
    IFile,
    IProject
}
//object declareLocalQuickFix
//        satisfies DeclareLocalQuickFix<IFile,IDocument, InsertEdit, TextEdit, TextChange, LinkedModeModel, ICompletionProposal,IProject,EclipseQuickFixData,IRegion>
//                & EclipseDocumentChanges
//                & EclipseLinkedModeSupport {
//    
//    shared actual IdeCompletionManager<out Object,out Object,ICompletionProposal,IDocument> completionManager => EclipseCompletionManager(CeylonEditor());        
//}
