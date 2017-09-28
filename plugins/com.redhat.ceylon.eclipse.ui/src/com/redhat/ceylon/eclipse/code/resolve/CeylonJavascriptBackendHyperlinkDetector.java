package org.eclipse.ceylon.ide.eclipse.code.resolve;

import org.eclipse.ceylon.common.Backend;
import org.eclipse.ceylon.common.Backends;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;

public class CeylonJavascriptBackendHyperlinkDetector extends
        CeylonHyperlinkDetector {

    public CeylonJavascriptBackendHyperlinkDetector(CeylonEditor editor,
            CeylonParseController controller) {
        super(editor, controller);
    }

    @Override
    public Backends supportedBackends() {
        return Backend.JavaScript.asSet();
    }
}
