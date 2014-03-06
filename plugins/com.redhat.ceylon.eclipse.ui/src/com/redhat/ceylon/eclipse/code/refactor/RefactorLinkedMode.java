package com.redhat.ceylon.eclipse.code.refactor;

import static org.eclipse.jface.text.link.ILinkedModeListener.NONE;
import static org.eclipse.jface.text.link.LinkedPositionGroup.NO_STOP;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.keys.IBindingService;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public abstract class RefactorLinkedMode extends AbstractLinkedMode {
    
    private String initialName;

    protected String getOriginalName() {
        return initialName;
    }
    
    protected final String openDialogKeyBinding;
    
    public RefactorLinkedMode(CeylonEditor editor) {
        super(editor);
        openDialogKeyBinding = getOpenDialogBinding(getActionName());
    }
    
    protected abstract String getActionName();
    
    protected abstract String getNewNameFromNamePosition();
    
    protected abstract String getName();
    protected abstract void setName(String name);
    
    protected abstract boolean canStart();
    
    protected int performInitialChange(IDocument document) {
        return 0;
    }
    
    protected abstract void setupLinkedPositions(IDocument document, int adjust) 
            throws BadLocationException;

    public final void start() {
        if (canStart()) {
            editor.saveWithoutActions();
            saveEditorState();
            ISourceViewer viewer = editor.getCeylonSourceViewer();
            IDocument document = viewer.getDocument();
            int offset = originalSelection.x;
            initialName = getName();
            int adjust = performInitialChange(document);        
            try {
                setupLinkedPositions(document, adjust);
                enterLinkedMode(document, NO_STOP, 
                        getExitPosition(offset, adjust));
            }
            catch (BadLocationException e) {
                e.printStackTrace();
                return;
            }
            openPopup();
        }
    }

    @Override
    protected final Action createOpenDialogAction() {
        return new Action("Open Dialog..." + '\t' + 
                openDialogKeyBinding) {
            @Override
            public void run() {
                enterDialogMode();
                openDialog();
            }
        };
    }
    
    @Override
    protected final Action createPreviewAction() {
        return new Action("Preview...") {
            @Override
            public void run() {
                enterDialogMode();
                openPreview();
            }
        };
    }
    
    protected abstract void openPreview();
    protected abstract void openDialog();
    
    public final void enterDialogMode() {
        setName(getNewNameFromNamePosition());
        revertChanges();
        linkedModeModel.exit(NONE);
    }
    

    /**
     * WARNING: only works in workbench window context!
     */
    private static String getOpenDialogBinding(String actionName) {
        if (actionName==null) {
            return "";
        }
        else {
            IBindingService bindingService= (IBindingService) getWorkbench()
                    .getAdapter(IBindingService.class);
            if (bindingService == null) {
                return "";
            }
            else {
                String binding= bindingService.getBestActiveBindingFormattedFor(actionName);
                return binding == null ? "" : binding;
            }
        }
    }
    
}