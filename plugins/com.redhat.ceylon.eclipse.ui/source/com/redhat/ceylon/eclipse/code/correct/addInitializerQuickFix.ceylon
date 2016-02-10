import com.redhat.ceylon.ide.common.correct {
    AddInitializerQuickFix
}
import com.redhat.ceylon.model.typechecker.model {
    TypedDeclaration
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
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}

object eclipseAddInitializerQuickFix
        satisfies AddInitializerQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
                & EclipseAbstractQuickFix
                & EclipseDocumentChanges {
    
    shared actual void newProposal(EclipseQuickFixData data, String desc, 
        TypedDeclaration dec, Integer offset, Integer length, TextChange change) {
        
        data.proposals.add(EclipseAddInitializerProposal(desc, dec, offset, length, change));
    }
}

class EclipseAddInitializerProposal(
    String desc,
    TypedDeclaration dec,
    Integer offset,
    Integer length,
    TextChange change
) extends EclipseInitializerProposal(
    desc, change, dec.unit, dec.scope, dec.type, Region(offset, length),
    CeylonResources.\iMINOR_CHANGE, -1) {

}
