import com.redhat.ceylon.eclipse.code.correct {
    EclipseDocumentChanges,
    eclipseImportProposals
}
import com.redhat.ceylon.eclipse.code.outline {
    CeylonLabelProvider
}
import com.redhat.ceylon.eclipse.code.parse {
    CeylonParseController
}
import com.redhat.ceylon.eclipse.code.preferences {
    CeylonPreferenceInitializer
}
import com.redhat.ceylon.eclipse.util {
    EditorUtil
}
import com.redhat.ceylon.ide.common.completion {
    InvocationCompletionProposal
}
import com.redhat.ceylon.ide.common.correct {
    ImportProposals
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Reference,
    Scope
}

import org.eclipse.core.resources {
    IProject,
    IFile
}
import org.eclipse.core.runtime {
    NullProgressMonitor
}
import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.jface.text.link {
    LinkedModeModel
}
import org.eclipse.ltk.core.refactoring {
    TextChange,
    DocumentChange
}
import org.eclipse.swt.graphics {
    Point,
    Image
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

class EclipseInvocationCompletionProposal(Integer offset, String prefix, 
            String description, String text, Declaration dec,
            Reference? producedReference, Scope scope, 
            CeylonParseController cpc, Boolean includeDefaulted,
            Boolean positionalInvocation, Boolean namedInvocation, 
            Boolean qualified, Declaration? qualifyingValue,
            EclipseCompletionManager completionManager)
        extends InvocationCompletionProposal<CeylonParseController, IProject, ICompletionProposal, IFile,
                IDocument, InsertEdit, TextEdit, TextChange, Point, LinkedModeModel>
                (offset, prefix, description, text, dec, producedReference, scope, cpc.lastCompilationUnit,
    includeDefaulted, positionalInvocation, namedInvocation, qualified, qualifyingValue, completionManager)
        satisfies EclipseDocumentChanges & EclipseCompletionProposal {
    
    shared actual variable String? currentPrefix = prefix;
    shared actual variable Integer length = prefix.size;
    shared actual variable Boolean toggleOverwriteInternal = false;

    shared actual ImportProposals<IFile,ICompletionProposal,IDocument,InsertEdit,TextEdit,TextChange> importProposals
            => eclipseImportProposals;

    shared actual void apply(IDocument doc) {
        value change = DocumentChange("Complete Invocation", doc);
        createChange(change, doc).perform(NullProgressMonitor());
        
        if (EditorUtil.preferences.getBoolean(CeylonPreferenceInitializer.\iLINKED_MODE_ARGUMENTS)) {
            activeLinkedMode(doc);
        }
    }
    
    shared actual Image image => CeylonLabelProvider.getImageForDeclaration(dec);
    
    shared actual Boolean toggleOverwrite => toggleOverwriteInternal;
}