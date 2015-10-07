import com.redhat.ceylon.ide.common.correct {
    CreateQuickFix,
    CreateParameterQuickFix
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
import com.redhat.ceylon.ide.common.doc {
    Icons
}
import com.redhat.ceylon.model.typechecker.model {
    Unit,
    Type,
    Scope
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}

object eclipseCreateQuickFix
        satisfies CreateQuickFix<IFile,IProject,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
                & EclipseAbstractQuickFix
                & EclipseDocumentChanges {

    shared actual CreateParameterQuickFix<IFile,IProject,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal> createParameterQuickFix
            => eclipseCreateParameterQuickFix;
    
    shared actual Integer getLineOfOffset(IDocument doc, Integer offset)
             => doc.getLineOfOffset(offset);
    
    shared actual void newCreateQuickFix(EclipseQuickFixData data, String desc, Scope scope,
        Unit unit, Type? returnType, Icons image, TextChange change, Integer exitPos, Region selection) {
        
        value icon = switch(image)
        case (Icons.localClass) CeylonResources.\iLOCAL_CLASS
        case (Icons.localMethod) CeylonResources.\iLOCAL_METHOD
        else CeylonResources.\iLOCAL_ATTRIBUTE;
        
        data.proposals.add(CreateProposal(desc, scope, unit, returnType, icon, change, exitPos, selection));
    }
    
    
}