package com.redhat.ceylon.eclipse.code.editor;

import org.eclipse.jface.text.IEditingSupport;
import org.eclipse.jface.text.IEditingSupportRegistry;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;

public final class ProposalLinkedModeListener implements
    ILinkedModeListener {
    private final CeylonEditor editor;
    private IEditingSupport editingSupport;
    
    public ProposalLinkedModeListener(CeylonEditor editor, 
            IEditingSupport editingSupport) {
        this.editor = editor;
        this.editingSupport = editingSupport;
    }
    
    @Override
    public void left(LinkedModeModel model, int flags) {
        editor.clearLinkedMode();
        //linkedModeModel.exit(ILinkedModeListener.NONE);
        CeylonSourceViewer viewer= editor.getCeylonSourceViewer();
        if (viewer instanceof IEditingSupportRegistry) {
            ((IEditingSupportRegistry) viewer).unregister(editingSupport);
        }
        editor.getSite().getPage().activate(editor);
        if ((flags&EXTERNAL_MODIFICATION)==0 && viewer!=null) {
            viewer.invalidateTextPresentation();
        }
    }
    
    @Override
    public void suspend(LinkedModeModel model) {
        editor.clearLinkedMode();
    }
    
    @Override
    public void resume(LinkedModeModel model, int flags) {
        editor.setLinkedMode(model, this);
    }
}