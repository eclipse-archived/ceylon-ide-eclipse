package com.redhat.ceylon.eclipse.code.resolve;

import com.redhat.ceylon.common.Backend;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class CeylonJavascriptBackendHyperlinkDetector extends
        CeylonHyperlinkDetector {

    public CeylonJavascriptBackendHyperlinkDetector(CeylonEditor editor,
            CeylonParseController controller) {
        super(editor, controller);
    }

    @Override
    public Backend supportedBackend() {
        return Backend.JavaScript;
    }
}
