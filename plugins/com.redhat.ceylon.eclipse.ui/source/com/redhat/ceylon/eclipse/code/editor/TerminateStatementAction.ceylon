import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.eclipse.code.correct {
    EclipseDocumentChanges
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
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
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

class EclipseTerminateStatementAction(CeylonEditor editor)
        extends Action(null)
        satisfies AbstractTerminateStatementAction<IDocument,InsertEdit,TextEdit,TextChange>
                & EclipseDocumentChanges {
    
    value doc => editor.ceylonSourceViewer.document;
    
    shared actual void run() {
        value ts = EditorUtil.getSelection(editor);
        String before = editor.selectionText;

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
        EditorUtil.performChange(change);
    }
    
    shared actual [DefaultRegion, String] getLineInfo(IDocument doc, Integer line)
            => let(li = doc.getLineInformation(line)) 
                [DefaultRegion(li.offset, li.length),
                    doc.get(li.offset, li.length)];
    
    shared actual TextChange newChange(String desc, IDocument doc)
            => DocumentChange(desc, doc);
    
    shared actual [Tree.CompilationUnit, List<CommonToken>] parse(IDocument doc) {
        value cpc = CeylonParseController();
        cpc.initialize(editor.parseController.path,
            editor.parseController.project, null);
        cpc.parseAndTypecheck(doc,
            0, // don't wait for the source model since we don't even need it.
            NullProgressMonitor(), null);
        return [cpc.parsedRootNode, cpc.tokens];
    }
    
    shared actual Character getChar(IDocument doc, Integer offset)
            => doc.getChar(offset);
    
    
}
