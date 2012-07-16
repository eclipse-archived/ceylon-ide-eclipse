package com.redhat.ceylon.eclipse.code.editor;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;

class GotoMatchingFenceAction extends Action {
    private final CeylonEditor fEditor;

    public GotoMatchingFenceAction(CeylonEditor editor) {
            super("Go to Matching Fence");
            Assert.isNotNull(editor);
            fEditor= editor;
            setEnabled(true);
//          PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.GOTO_MATCHING_BRACKET_ACTION);
    }

    public void run() {
            fEditor.gotoMatchingFence();
    }
}