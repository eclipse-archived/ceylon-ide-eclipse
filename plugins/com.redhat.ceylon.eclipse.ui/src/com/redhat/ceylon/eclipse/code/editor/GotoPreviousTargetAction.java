package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.IEditorActionDefinitionIds.GOTO_PREVIOUS_TARGET;

class GotoPreviousTargetAction extends TargetNavigationAction {
    public GotoPreviousTargetAction() {
        this(null);
    }

    public GotoPreviousTargetAction(CeylonEditor editor) {
        super(editor, "Go to Previous Navigation Target", GOTO_PREVIOUS_TARGET);
    }

    @Override
    protected Object getNavTarget(Object o, Object astRoot) {
        return fNavTargetFinder.getPreviousTarget(o, astRoot);
    }
}