import com.redhat.ceylon.ide.common.util {
    Indents
}
import com.redhat.ceylon.ide.common.correct{
    ImportProposals
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.core.resources {
    IFile
}
import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.text.edits {
    TextEdit, InsertEdit,
    DeleteEdit,
    ReplaceEdit,
    MultiTextEdit
}
import org.eclipse.ltk.core.refactoring {
    TextChange,
    TextFileChange
}
import com.redhat.ceylon.eclipse.util {
    EditorUtil,
    Highlights,
    eclipseIndents
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources {
        IMPORT
    }
}

shared alias EclipseImportProposals => ImportProposals<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange>;

shared object eclipseImportProposals
        satisfies ImportProposals<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange>
        & EclipseDocumentChanges  {
    shared actual Indents<IDocument> indents => eclipseIndents;

    shared actual TextChange createImportChange(IFile file)
        => TextFileChange("Add Import", file);

    shared actual ICompletionProposal newImportProposal(String description, TextChange correctionChange)
        => object extends CorrectionProposal(description, correctionChange, null, \iIMPORT) {
            styledDisplayString
                => Highlights.styleProposal(displayString, true);
        };
}
