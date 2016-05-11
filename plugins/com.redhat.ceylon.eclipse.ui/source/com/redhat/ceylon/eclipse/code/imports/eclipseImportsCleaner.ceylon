import ceylon.interop.java {
    JavaList
}

import com.redhat.ceylon.eclipse.code.correct {
    EclipseDocument
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.code.parse {
    CeylonParseController
}
import com.redhat.ceylon.eclipse.util {
    EditorUtil
}
import com.redhat.ceylon.ide.common.imports {
    AbstractImportsCleaner
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.window {
    Window
}

object eclipseImportsCleaner satisfies AbstractImportsCleaner {
    
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
         
         value commonDoc = EclipseDocument(doc);
         cleanImports(cpc.typecheckedRootNode, commonDoc);
    }
}
