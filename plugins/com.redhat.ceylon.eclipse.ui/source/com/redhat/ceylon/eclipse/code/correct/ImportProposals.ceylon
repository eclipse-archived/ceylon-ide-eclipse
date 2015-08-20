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
    eclipseIndents=indents
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources {
        IMPORT
    }
}

shared object importProposals satisfies ImportProposals<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange> {
    shared actual Indents<IDocument> indents => eclipseIndents;

    shared actual [TextFileChange, IDocument] getTextChangeAndDocument(IFile file) {
        TextFileChange importChange =
                TextFileChange("Add Import", file);
        importChange.edit = MultiTextEdit();

        IDocument doc = EditorUtil.getDocument(importChange);
        return [importChange, doc];
    }

    shared actual ICompletionProposal newImportProposal(String description, TextChange correctionChange)
        => object extends CorrectionProposal(description, correctionChange, null, \iIMPORT) {
            styledDisplayString
                => Highlights.styleProposal(displayString, true);
        };

    shared actual TextEdit newDeleteEdit(Integer start, Integer stop)
        => DeleteEdit(start, stop);

    shared actual TextEdit newReplaceEdit(Integer start, Integer stop, String text)
        => ReplaceEdit(start, stop, text);

    shared actual InsertEdit newInsertEdit(Integer position, String text)
        => InsertEdit(position, text);

    shared actual void addEditToChange(TextChange change, TextEdit edit)
        => change.addEdit(edit);

    shared actual String getInsertedText(InsertEdit edit)
        => edit.text;
}
