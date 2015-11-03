package com.redhat.ceylon.eclipse.util;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IEditingSupport;
import org.eclipse.jface.text.IEditingSupportRegistry;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.redhat.ceylon.eclipse.code.editor.AbstractLinkedModeListener;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewer;
import com.redhat.ceylon.eclipse.code.editor.FocusEditingSupport;

public class LinkedMode {
    
    public static class NullExitPolicy implements IExitPolicy {
        @Override
        public ExitFlags doExit(LinkedModeModel model, VerifyEvent event,
                int offset, int length) {
            return null;
        }
    }
    
    public static void installLinkedMode(CeylonEditor editor,
            LinkedModeModel linkedModeModel, Object linkedModeOwner,
            int exitSequenceNumber, int exitPosition,
            IEditingSupport editingSupport, IExitPolicy exitPolicy,
            AbstractLinkedModeListener linkedModelListener) 
                    throws BadLocationException {
        linkedModeModel.forceInstall();
        linkedModeModel.addLinkingListener(linkedModelListener);
        LinkedMode.registerEditingSupport(editor, editingSupport);
        editor.setLinkedMode(linkedModeModel, linkedModeOwner);
        CeylonSourceViewer viewer = editor.getCeylonSourceViewer();
        EditorLinkedModeUI ui = new EditorLinkedModeUI(linkedModeModel, viewer);
        if (exitPosition>=0 && exitSequenceNumber>=0) {
            ui.setExitPosition(viewer, exitPosition, 0, exitSequenceNumber);
        }
        ui.setExitPolicy(exitPolicy);
        ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
        ui.setDoContextInfo(true);
        ui.enableColoredLabels(true);
        ui.enter();
    }

    public static void installLinkedMode(final CeylonEditor editor, 
            IDocument document, LinkedModeModel linkedModeModel, 
            Object linkedModeOwner, IExitPolicy exitPolicy,
            int exitSequenceNumber, int exitPosition)
                    throws BadLocationException {
        final IEditingSupport editingSupport = new FocusEditingSupport(editor);
        installLinkedMode(editor, linkedModeModel, linkedModeOwner,
                exitSequenceNumber, exitPosition, editingSupport, exitPolicy, 
                new AbstractLinkedModeListener(editor, 
                        linkedModeOwner) {
                    @Override
                    public void left(LinkedModeModel model, int flags) {
                        editor.clearLinkedMode();
                        //linkedModeModel.exit(ILinkedModeListener.NONE);
                        unregisterEditingSupport(editor, editingSupport);
                        editor.getSite().getPage().activate(editor);
                        if ((flags&EXTERNAL_MODIFICATION)==0) {
                            CeylonSourceViewer viewer = 
                            		editor.getCeylonSourceViewer();
                            if (viewer!=null) {
                                viewer.invalidateTextPresentation();
                            }
                        }
                    }
                });
    }

    public static void unregisterEditingSupport(CeylonEditor editor,
            IEditingSupport editingSupport) {
        CeylonSourceViewer viewer = editor.getCeylonSourceViewer();
        if (viewer!=null) {
            ((IEditingSupportRegistry) viewer).unregister(editingSupport);
        }
    }

    public static void registerEditingSupport(CeylonEditor editor,
            IEditingSupport editingSupport) {
        CeylonSourceViewer viewer = editor.getCeylonSourceViewer();
        if (viewer!=null) {
            ((IEditingSupportRegistry) viewer).register(editingSupport);
        }
    }

    public static void addLinkedPosition(final LinkedModeModel linkedModeModel,
            ProposalPosition linkedPosition) 
                    throws BadLocationException {
        LinkedPositionGroup linkedPositionGroup = new LinkedPositionGroup();
        linkedPositionGroup.addPosition(linkedPosition);
        linkedModeModel.addGroup(linkedPositionGroup);
    }

}
