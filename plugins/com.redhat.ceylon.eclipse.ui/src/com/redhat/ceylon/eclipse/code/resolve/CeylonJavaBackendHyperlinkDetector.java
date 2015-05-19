package com.redhat.ceylon.eclipse.code.resolve;

import com.redhat.ceylon.common.Backend;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class CeylonJavaBackendHyperlinkDetector extends CeylonHyperlinkDetector {

    public CeylonJavaBackendHyperlinkDetector(CeylonEditor editor,
            CeylonParseController controller) {
        super(editor, controller);
    }

    @Override
    public Backend supportedBackend() {
        return Backend.Java;
    }
}
