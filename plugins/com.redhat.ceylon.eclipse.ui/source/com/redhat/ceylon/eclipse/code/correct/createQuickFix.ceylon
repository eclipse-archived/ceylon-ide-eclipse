import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.ide.common.correct {
    CreateQuickFix,
    CreateParameterQuickFix
}
import com.redhat.ceylon.ide.common.doc {
    Icons
}
import com.redhat.ceylon.model.typechecker.model {
    Unit,
    Type,
    Scope
}

import org.eclipse.core.resources {
    IFile
}
import org.eclipse.jface.text {
    IDocument,
    Region
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

object eclipseCreateQuickFix
        satisfies CreateQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
                & EclipseAbstractQuickFix
                & EclipseDocumentChanges {

    shared actual CreateParameterQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal> createParameterQuickFix
            => eclipseCreateParameterQuickFix;
    
    shared actual void newCreateQuickFix(EclipseQuickFixData data, String desc, Scope scope,
        Unit unit, Type? returnType, Icons image, TextChange change, Integer exitPos, Region selection) {
        
        value icon = switch(image)
        case (Icons.localClass) CeylonResources.\iLOCAL_CLASS
        case (Icons.localMethod) CeylonResources.\iLOCAL_METHOD
        else CeylonResources.\iLOCAL_ATTRIBUTE;
        
        data.proposals.add(CreateProposal(desc, scope, unit, returnType, icon, change, exitPos, selection));
    }
}
