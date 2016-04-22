import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.eclipse.code.correct {
    EclipseDocument
}
import com.redhat.ceylon.eclipse.code.parse {
    CeylonParseController
}
import com.redhat.ceylon.eclipse.util {
    EditorUtil
}
import com.redhat.ceylon.ide.common.editor {
    AbstractTerminateStatementAction
}

import java.util {
    List
}

import org.antlr.runtime {
    CommonToken
}
import org.eclipse.core.runtime {
    NullProgressMonitor
}
import org.eclipse.jface.action {
    Action
}
import com.redhat.ceylon.ide.common.platform {
    TextChange
}
import com.redhat.ceylon.eclipse.platform {
    EclipseTextChange
}

class EclipseTerminateStatementAction(CeylonEditor editor)
        extends Action(null)
        satisfies AbstractTerminateStatementAction<EclipseDocument> {
    
    shared actual void run() {
        value ts = EditorUtil.getSelection(editor);
        String before = editor.selectionText;
        value doc = EclipseDocument(editor.ceylonSourceViewer.document);

        terminateStatement(doc, ts.endLine);
        
        if (editor.selectionText != before) {
            //if the caret was at the end of the line, 
            //and a semi was added, it winds up selected
            //so move the caret after the semi
            value selection = editor.selection;
            Integer start = selection.offset + 1;
            editor.ceylonSourceViewer.setSelectedRange(start, 0);
        }
        
        editor.scheduleParsing();
    }
    
    shared actual void applyChange(TextChange change) {
        if (is EclipseTextChange change) {
            EditorUtil.performChange(change.nativeChange);
        }
    }
    
    shared actual [Tree.CompilationUnit, List<CommonToken>] parse(EclipseDocument doc) {
        value cpc = CeylonParseController();
        cpc.initialize(editor.parseController.path,
            editor.parseController.project, null);
        cpc.parseAndTypecheck(doc.doc,
            0, // don't wait for the source model since we don't even need it.
            NullProgressMonitor(), null);
        return [cpc.parsedRootNode, cpc.tokens];
    }
}
