import com.redhat.ceylon.eclipse.util {
    EditorUtil,
    eclipseIndents
}
import com.redhat.ceylon.ide.common.correct {
    DocumentChanges
}
import com.redhat.ceylon.ide.common.platform {
    CommonDocument
}

import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.ltk.core.refactoring {
    TextChange
}
import org.eclipse.text.edits {
    TextEdit,
    InsertEdit,
    DeleteEdit,
    ReplaceEdit,
    MultiTextEdit
}

shared interface EclipseDocumentChanges
        satisfies DocumentChanges<IDocument, InsertEdit, TextEdit, TextChange> {

    initMultiEditChange(TextChange importChange) 
            => importChange.edit = MultiTextEdit();

    getDocumentForChange(TextChange change)
            => EditorUtil.getDocument(change);

    newDeleteEdit(Integer start, Integer length)
            => DeleteEdit(start, length);

    newReplaceEdit(Integer start, Integer length, String text)
            => ReplaceEdit(start, length, text);

    newInsertEdit(Integer position, String text)
            => InsertEdit(position, text);

    shared actual void addEditToChange(TextChange change, TextEdit edit) {
        if (is MultiTextEdit me = change.edit) {
            change.addEdit(edit);
        } else {
            change.edit = edit;            
        }
    }

    getInsertedText(TextEdit edit)
            => switch(edit)
               case (is InsertEdit) edit.text
               case (is ReplaceEdit) edit.text
               else "";
    
    hasChildren(TextChange change)
            => change.edit.hasChildren();
    
    getDocContent(IDocument doc, Integer start, Integer length) 
            => doc.get(start, length);
    
    getLineOfOffset(IDocument doc, Integer offset)
            => doc.getLineOfOffset(offset);
    
    getLineStartOffset(IDocument doc, Integer line)
            => doc.getLineInformation(line).offset;
    
    getLineContent(IDocument doc, Integer line)
            => let (info = doc.getLineInformation(line))
               doc.get(info.offset, info.length);
}


