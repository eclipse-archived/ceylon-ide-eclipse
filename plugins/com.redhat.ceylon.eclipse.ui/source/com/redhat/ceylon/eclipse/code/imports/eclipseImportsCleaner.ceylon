import ceylon.interop.java {
    JavaList
}

import org.eclipse.ceylon.ide.eclipse.code.correct {
    EclipseDocument
}
import org.eclipse.ceylon.ide.eclipse.code.editor {
    CeylonEditor
}
import org.eclipse.ceylon.ide.eclipse.code.parse {
    CeylonParseController
}
import org.eclipse.ceylon.ide.eclipse.util {
    EditorUtil
}
import org.eclipse.ceylon.ide.common.imports {
    AbstractImportsCleaner
}
import org.eclipse.ceylon.model.typechecker.model {
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
