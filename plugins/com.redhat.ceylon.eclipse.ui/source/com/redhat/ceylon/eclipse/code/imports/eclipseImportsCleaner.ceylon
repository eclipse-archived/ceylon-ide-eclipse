import com.redhat.ceylon.ide.common.imports {
    AbstractImportsCleaner
}
import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}
import org.eclipse.ltk.core.refactoring {
    TextChange,
    DocumentChange
}
import com.redhat.ceylon.eclipse.code.correct {
    EclipseDocumentChanges
}
import com.redhat.ceylon.ide.common.util {
    Indents
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}
import com.redhat.ceylon.eclipse.util {
    eclipseIndents,
    EditorUtil
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import ceylon.interop.java {
    JavaList
}
import org.eclipse.jface.window {
    Window
}
import com.redhat.ceylon.eclipse.code.parse {
    CeylonParseController
}

object eclipseImportsCleaner
        satisfies AbstractImportsCleaner<IDocument, InsertEdit, TextEdit, TextChange>
                & EclipseDocumentChanges {
    
    shared actual Indents<IDocument> indents
            => eclipseIndents;
    
    shared actual Declaration? select(List<Declaration> proposals) {
        assert(is CeylonEditor editor = EditorUtil.currentEditor);
        value shell = editor.site.shell;
        value fid = ImportSelectionDialog(shell, JavaList(proposals));
        
        if (fid.open() == Window.\iOK) {
            assert(is Declaration res = fid.firstResult);
            return res;
        }
        
        return null;
    }
    
    shared void cleanEditorImports(CeylonParseController cpc, IDocument doc) {
         if (!CleanImportsHandler.isEnabled(cpc)) {
             return;
         }
         
         value change = DocumentChange("Organize Imports", doc);
         cleanImports(cpc.typecheckedRootNode, doc, change);
         EditorUtil.performChange(change);
    }
}