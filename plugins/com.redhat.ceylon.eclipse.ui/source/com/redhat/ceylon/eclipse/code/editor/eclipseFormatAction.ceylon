import com.redhat.ceylon.eclipse.code.correct {
    EclipseDocumentChanges
}
import com.redhat.ceylon.eclipse.util {
    eclipseIndents
}
import com.redhat.ceylon.ide.common.editor {
    AbstractFormatAction
}
import com.redhat.ceylon.ide.common.util {
    Indents
}

import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.ltk.core.refactoring {
    TextChange,
    DocumentChange
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

object eclipseFormatAction
        satisfies AbstractFormatAction<IDocument,InsertEdit,TextEdit,TextChange>
                & EclipseDocumentChanges {
    
    shared actual Indents<IDocument> indents
            => eclipseIndents;
    
    shared actual TextChange newTextChange(String desc, IDocument doc)
            => DocumentChange(desc, doc);
}
