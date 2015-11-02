import com.redhat.ceylon.eclipse.ui {
    CeylonResources {
        \iIMPORT
    }
}
import com.redhat.ceylon.eclipse.util {
    Highlights,
    eclipseIndents
}
import com.redhat.ceylon.ide.common.correct {
    ImportProposals
}
import com.redhat.ceylon.ide.common.util {
    Indents
}

import org.eclipse.core.resources {
    IFile
}
import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.ltk.core.refactoring {
    TextChange,
    TextFileChange
}
import org.eclipse.text.edits {
    TextEdit,
    InsertEdit
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
