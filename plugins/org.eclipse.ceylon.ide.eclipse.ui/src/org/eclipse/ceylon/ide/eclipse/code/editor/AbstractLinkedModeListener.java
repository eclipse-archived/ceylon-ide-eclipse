package org.eclipse.ceylon.ide.eclipse.code.editor;

import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;

public abstract class AbstractLinkedModeListener 
        implements ILinkedModeListener {
    
    private final CeylonEditor editor;
    private final Object linkedModeOwner;
    
    public AbstractLinkedModeListener(CeylonEditor editor,
            Object linkedModeOwner) {
        this.editor = editor;
        this.linkedModeOwner = linkedModeOwner;
    }
    
    @Override
    public void suspend(LinkedModeModel model) {
        editor.clearLinkedMode();
    }
    
    @Override
    public void resume(LinkedModeModel model, int flags) {
        editor.setLinkedMode(model, linkedModeOwner);
    }
}