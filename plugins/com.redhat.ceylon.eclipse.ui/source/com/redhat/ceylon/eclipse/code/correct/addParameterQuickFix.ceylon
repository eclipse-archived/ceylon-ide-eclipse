import com.redhat.ceylon.eclipse.code.complete {
    EclipseLinkedModeSupport
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.ide.common.correct {
    AddParameterQuickFix
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Type
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

object eclipseAddParameterQuickFix
        satisfies AddParameterQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
                & EclipseAbstractQuickFix
                & EclipseLinkedModeSupport
                & EclipseDocumentChanges {
    
    shared actual void newProposal(EclipseQuickFixData data, String desc, 
        Declaration dec, Type? type, Integer offset, Integer length, 
        TextChange change, Integer exitPos) {
        
        data.proposals.add(EclipseAddParameterProposal(desc, dec, type, offset, 
            length, change, exitPos));
    }
}

class EclipseAddParameterProposal(String desc, Declaration dec,
	        Type? type, Integer offset, Integer len, TextChange change, Integer exitPos)
        extends EclipseInitializerProposal(desc, change, dec.unit, dec.scope, 
	            type, Region(offset, len), CeylonResources.\iADD_CORR, exitPos) {
    
}