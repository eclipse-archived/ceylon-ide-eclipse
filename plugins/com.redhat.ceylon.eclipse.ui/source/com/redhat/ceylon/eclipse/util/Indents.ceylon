import com.redhat.ceylon.compiler.typechecker.tree {
    Node
}
import com.redhat.ceylon.ide.common.util {
    Indents
}
import org.eclipse.jface.text {
    IDocument,
    BadLocationException,
    IDocumentExtension4
}
import org.eclipse.ui.editors.text {
    EditorsUI
}
import org.eclipse.jface.preference {
    IPreferenceStore
}
import org.eclipse.ui.texteditor {
    AbstractDecoratedTextEditorPreferenceConstants {
        EDITOR_TAB_WIDTH,
        EDITOR_SPACES_FOR_TABS
    }
}

shared object indents satisfies Indents<IDocument> {

    shared actual String getLine(Node node, IDocument doc) {
        try {
            value region = doc.getLineInformation(node.token.line-1);
            return doc.get(region.offset, region.length);
        } catch(BadLocationException ble) {
            return "";
        }
    }

    shared actual Integer indentSpaces
        => let(IPreferenceStore? store = EditorsUI.preferenceStore)
            (store?.getInt(\iEDITOR_TAB_WIDTH) else 4);

    shared actual Boolean indentWithSpaces
        => let(IPreferenceStore? store = EditorsUI.preferenceStore)
            (store?.getBoolean(\iEDITOR_SPACES_FOR_TABS) else false);

    shared actual String getDefaultLineDelimiter(IDocument? document)
        => if (is IDocumentExtension4 document)
            then document.defaultLineDelimiter
            else operatingSystem.newline;
}
