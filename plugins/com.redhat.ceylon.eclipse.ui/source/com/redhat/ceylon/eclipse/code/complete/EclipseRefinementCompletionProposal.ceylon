import com.redhat.ceylon.ide.common.completion {
    RefinementCompletionProposal
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.jface.text.link {
    LinkedModeModel
}
import com.redhat.ceylon.eclipse.code.complete {
    ERefinementCompletionProposal=RefinementCompletionProposal
}
import com.redhat.ceylon.eclipse.code.parse {
    CeylonParseController
}
import org.eclipse.ltk.core.refactoring {
    TextChange
}
import org.eclipse.swt.graphics {
    Point,
    Image
}
import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}
import org.eclipse.core.resources {
    IProject,
    IFile
}
import com.redhat.ceylon.eclipse.code.correct {
    EclipseDocumentChanges,
    eclipseImportProposals
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Scope,
    Reference
}
import com.redhat.ceylon.ide.common.correct {
    ImportProposals
}
import com.redhat.ceylon.eclipse.code.outline {
    CeylonLabelProvider
}
class EclipseRefinementCompletionProposal(Integer _offset, String prefix, Reference pr, String desc, 
        String text, CeylonParseController cpc, Declaration declaration, Scope scope,
        Boolean fullType, Boolean explicitReturnType)
        extends RefinementCompletionProposal<CeylonParseController, IProject, ICompletionProposal, IFile,
                IDocument, InsertEdit, TextEdit, TextChange, Point, LinkedModeModel>
                (_offset, prefix, pr, desc, text, cpc, declaration, scope, fullType, explicitReturnType)
        satisfies EclipseDocumentChanges & EclipseCompletionProposal {

    shared actual variable String? currentPrefix = prefix;
    
    shared actual Image image => CeylonLabelProvider.getRefinementIcon(declaration);
    
    shared actual ImportProposals<IFile,ICompletionProposal,IDocument,InsertEdit,TextEdit,TextChange> importProposals
            => eclipseImportProposals;
    
    shared actual ICompletionProposal newNestedCompletionProposal(Declaration dec, Integer loc)
            => ERefinementCompletionProposal.NestedCompletionProposal(dec, loc, cpc.lastCompilationUnit.unit);
    
    shared actual ICompletionProposal newNestedLiteralCompletionProposal(String val, Integer loc)
            => ERefinementCompletionProposal.NestedLiteralCompletionProposal(val, loc);

    shared actual variable Boolean toggleOverwriteInternal = false;
    
    shared actual Boolean toggleOverwrite => toggleOverwriteInternal;
    
    
}