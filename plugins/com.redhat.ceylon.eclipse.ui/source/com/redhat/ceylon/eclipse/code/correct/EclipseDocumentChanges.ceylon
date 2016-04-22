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

    shared actual void initMultiEditChange(TextChange importChange) {
        importChange.edit = MultiTextEdit();
    }

    shared actual IDocument getDocumentForChange(TextChange change)
            => EditorUtil.getDocument(change);

    shared actual TextEdit newDeleteEdit(Integer start, Integer length)
            => DeleteEdit(start, length);

    shared actual TextEdit newReplaceEdit(Integer start, Integer length, String text)
            => ReplaceEdit(start, length, text);

    shared actual InsertEdit newInsertEdit(Integer position, String text)
            => InsertEdit(position, text);

    shared actual void addEditToChange(TextChange change, TextEdit edit) {
        if (is MultiTextEdit me = change.edit) {
            change.addEdit(edit);
        } else {
            change.edit = edit;            
        }
    }

    shared actual String getInsertedText(TextEdit edit)
            => switch(edit)
               case (is InsertEdit) edit.text
               case (is ReplaceEdit) edit.text
               else "";
    
    shared actual Boolean hasChildren(TextChange change)
            => change.edit.hasChildren();
    
    shared actual String getDocContent(IDocument doc, Integer start, Integer length) 
            => doc.get(start, length);
    
    shared actual Integer getLineOfOffset(IDocument doc, Integer offset)
            => doc.getLineOfOffset(offset);
    
    shared actual Integer getLineStartOffset(IDocument doc, Integer line)
            => doc.getLineInformation(line).offset;
    
    shared actual String getLineContent(IDocument doc, Integer line)
            => let (info = doc.getLineInformation(line))
               doc.get(info.offset, info.length);
}

shared class EclipseDocument(shared IDocument doc) satisfies CommonDocument {
    
    getLineContent(Integer line)
            => let(info = doc.getLineInformation(line))
            doc.get(info.offset, info.length);

    getLineStartOffset(Integer line)
            => doc.getLineInformation(line).offset;
    
    getLineEndOffset(Integer line)
            => doc.getLineInformation(line).offset
             + doc.getLineInformation(line).length;
    
    getLineOfOffset(Integer offset) => doc.getLineOfOffset(offset);
    
    getText(Integer offset, Integer length) => doc.get(offset, length);
    
    getDefaultLineDelimiter() => eclipseIndents.getDefaultLineDelimiter(doc);
    
}
