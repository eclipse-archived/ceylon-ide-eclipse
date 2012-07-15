package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.IEditorActionDefinitionIds.GOTO_NEXT_TARGET;

class GotoNextTargetAction extends TargetNavigationAction {
    public GotoNextTargetAction() {
        this(null);
    }

    public GotoNextTargetAction(CeylonEditor editor) {
        super(editor, "Go to Next Navigation Target", GOTO_NEXT_TARGET);
    }

    @Override
    protected Object getNavTarget(Object o, Object astRoot) {
        return fNavTargetFinder.getNextTarget(o, astRoot);
    }
}