import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.ide.common.correct {
    CreateParameterQuickFix
}
import com.redhat.ceylon.ide.common.doc {
    Icons
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Type
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

object eclipseCreateParameterQuickFix
        satisfies CreateParameterQuickFix<IFile,IProject,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
        & EclipseAbstractQuickFix
        & EclipseDocumentChanges {

    shared actual void newCreateParameterProposal(EclipseQuickFixData data, String desc, Declaration dec,
        Type? type, Region selection, Icons image, TextChange change, Integer exitPos) {
        
        data.proposals.add(CreateParameterProposal(desc, dec, type, selection, CeylonResources.\iADD_CORR, change, exitPos));
    }
}
