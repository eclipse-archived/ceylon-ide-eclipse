package com.redhat.ceylon.eclipse.code.resolve;

import com.redhat.ceylon.common.Backend;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class CeylonNativeHeaderHyperlinkDetector extends
        CeylonHyperlinkDetector {

    public CeylonNativeHeaderHyperlinkDetector(CeylonEditor editor,
            CeylonParseController controller) {
        super(editor, controller);
    }

    @Override
    public Backend supportedBackend() {
        return Backend.None;
    }
}
