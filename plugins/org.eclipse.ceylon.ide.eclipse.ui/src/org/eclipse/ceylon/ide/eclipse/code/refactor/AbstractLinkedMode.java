package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.core.commands.operations.OperationHistoryFactory.getOperationHistory;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedNamesAssistProposal.DeleteBlockingExitPolicy;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IEditingSupport;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.IUndoManagerExtension;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;

import org.eclipse.ceylon.ide.eclipse.code.editor.AbstractLinkedModeListener;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.editor.FocusEditingSupport;
import org.eclipse.ceylon.ide.eclipse.util.LinkedMode;

public abstract class AbstractLinkedMode {

    protected final CeylonEditor editor;
    private Point originalSelection;
    protected final LinkedModeModel linkedModeModel;
    
    private boolean showPreview = false;
    private IUndoableOperation startingUndoOperation;
    private RefactorInformationPopup infoPopup;
    private final boolean wasDirty;

    protected AbstractLinkedMode(CeylonEditor ceylonEditor) {
        editor = ceylonEditor;
        linkedModeModel = new LinkedModeModel();
        wasDirty = ceylonEditor.isDirty();
    }

    synchronized Point getOriginalSelection() {
        if (originalSelection == null) {
            originalSelection = editor.getCeylonSourceViewer()
                    .getSelectedRange();
        }
        return originalSelection;
    }

    
    protected RefactorInformationPopup getInfoPopup() {
        return infoPopup;
    }

    protected void openPopup() {
        // Must cache here, since editor context is not 
        // available in menu from popup shell:
        infoPopup = new RefactorInformationPopup(editor, this);
        infoPopup.open();
    }

    protected boolean isShowPreview() {
        return showPreview;
    }
    
    protected void enterLinkedMode(final IDocument document, 
            int exitSequenceNumber, int exitPosition)
            throws BadLocationException {
        final IEditingSupport editingSupport = 
                new FocusEditingSupport(editor) {
            public boolean ownsFocusShell() {
                return infoPopup != null && 
                            infoPopup.ownsFocusShell() || 
                        super.ownsFocusShell();
            }
        };
        LinkedMode.installLinkedMode(editor, 
                linkedModeModel, this, 
                exitSequenceNumber, exitPosition, 
                editingSupport, createExitPolicy(document), 
                new AbstractLinkedModeListener(editor, this) {
                    @Override
                    public void left(LinkedModeModel model, int flags) {
                        editor.clearLinkedMode();
                        if (infoPopup != null) {
                            infoPopup.close();
                            infoPopup=null;
                        }                
                        editor.getSite()
                            .getPage()
                            .activate(editor);
                        if ((flags&UPDATE_CARET)!=0) {
                            done();
                        }
                        else {
                            if ((flags&EXTERNAL_MODIFICATION)==0) {
                                editor.getCeylonSourceViewer()
                                    .invalidateTextPresentation();
                            }
                            cancel();
                        }
                        LinkedMode.unregisterEditingSupport(
                                editor, editingSupport);
                    }
                });
        
        //NOTE: I hate this behavior in the Java editor!
        //viewer.setSelectedRange(originalSelection.x+adjust, originalSelection.y+adjust); // by default, full word is selected; restore original selection
    }

    DeleteBlockingExitPolicy createExitPolicy(
            final IDocument document) {
        return new DeleteBlockingExitPolicy(document) {
            @Override
            public ExitFlags doExit(LinkedModeModel model, 
                    VerifyEvent event, int offset, int length) {
                showPreview = (event.stateMask & SWT.CTRL) != 0
                        && (event.character == SWT.CR || 
                            event.character == SWT.LF);
                return super.doExit(model, event, offset, length);
            }
        };
    }
    
    protected int getExitPosition(int selectionOffset, int adjust) {
        return selectionOffset+adjust;
    }
    
    protected void cancel() {}

    protected void done() {
        if (!wasDirty || forceSave()) {
            editor.saveWithoutActions();
        }
    }
    
    protected boolean forceSave() {
        return false;
    }

    boolean isCaretInLinkedPosition() {
        return getCurrentLinkedPosition() != null;
    }

    protected LinkedPosition getCurrentLinkedPosition() {
        Point selection = 
                editor.getCeylonSourceViewer()
                    .getSelectedRange();
        Position pos = new Position(selection.x, selection.y);
        LinkedPositionGroup group = 
                linkedModeModel.getGroupForPosition(pos);
        if (group!=null) {
            LinkedPosition[] positions = group
                    .getPositions();
            for (LinkedPosition position: positions) {
                if (position.includes(selection.x) && 
                    position.includes(selection.x+selection.y)) {
                    return position;
                }
            }
        }
        return null;
    }

    protected abstract String getHintTemplate();

    protected void addAdditionalMenuItems(IMenuManager manager) {
        IAction previewAction = createPreviewAction();
        if (previewAction!=null) {
            previewAction.setAccelerator(SWT.CTRL | SWT.CR);
            previewAction.setEnabled(true);
            manager.add(previewAction);
        }
    
        IAction openDialogAction = createOpenDialogAction();
        if (openDialogAction!=null) {
            manager.add(openDialogAction);
        }
    }

    protected Action createOpenDialogAction() { return null; }

    protected Action createPreviewAction() { return null; }

    protected void saveEditorState() {
        //save where we are before opening linked mode
        IUndoManager undoManager = 
                editor.getCeylonSourceViewer()
                    .getUndoManager();
        if (undoManager instanceof IUndoManagerExtension) {
            IUndoManagerExtension undoManagerExtension = 
                    (IUndoManagerExtension) undoManager;
            IUndoContext undoContext = 
                    undoManagerExtension.getUndoContext();
            IOperationHistory oh = getOperationHistory();
            startingUndoOperation = 
                    oh.getUndoOperation(undoContext);
        }
    }

    protected void revertChanges() {
        //go back to where we were before opening linked mode
        try {
            editor.getSite()
                .getWorkbenchWindow()
                .run(false, true, 
                    new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor) 
                        throws InvocationTargetException, 
                               InterruptedException {
                    IUndoManager undoManager = 
                            editor.getCeylonSourceViewer()
                                .getUndoManager();
                    if (undoManager instanceof IUndoManagerExtension) {
                        IUndoManagerExtension ext = 
                                (IUndoManagerExtension) 
                                    undoManager;
                        IUndoContext undoContext = 
                                ext.getUndoContext();
                        IOperationHistory oh = 
                                getOperationHistory();
                        while (undoManager.undoable()) {
                            if (startingUndoOperation!=null && 
                                    startingUndoOperation.equals(
                                            oh.getUndoOperation(
                                                    undoContext))) {
                                return;
                            }
                            undoManager.undo();
                        }
                    }
                }
            });
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        
        //make sure the AST is up dated before running the refactoring
        editor.getParseController()
            .parseAndTypecheck(
                    editor.getCeylonSourceViewer().getDocument(), 
                    10, new NullProgressMonitor(), null);
        
    }
    
    protected void updatePopupLocation() {}
    
    /*private void restoreFullSelection() {
        if (fOriginalSelection.y != 0) {
            int originalOffset= fOriginalSelection.x;
            LinkedPosition[] positions= fLinkedPositionGroup.getPositions();
            for (int i= 0; i < positions.length; i++) {
                LinkedPosition position= positions[i];
                if (! position.isDeleted() && position.includes(originalOffset)) {
                    fEditor.getCeylonSourceViewer().setSelectedRange(position.offset, position.length);
                    return;
                }
            }
        }
    }*/

}