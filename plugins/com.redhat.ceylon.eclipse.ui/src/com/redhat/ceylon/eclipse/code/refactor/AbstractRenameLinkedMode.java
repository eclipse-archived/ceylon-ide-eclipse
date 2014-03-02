package com.redhat.ceylon.eclipse.code.refactor;

import static org.eclipse.jface.text.link.LinkedPositionGroup.NO_STOP;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedNamesAssistProposal.DeleteBlockingExitPolicy;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IEditingSupport;
import org.eclipse.jface.text.IEditingSupportRegistry;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.IUndoManagerExtension;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewer;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;

public abstract class AbstractRenameLinkedMode {

    protected boolean showPreview = false;
    private IUndoableOperation startingUndoOperation;
    
    protected final class PreviewingDeleteBlockingExitPolicy extends
            DeleteBlockingExitPolicy {
        protected PreviewingDeleteBlockingExitPolicy(IDocument document) {
            super(document);
        }

        @Override
        public ExitFlags doExit(LinkedModeModel model, 
                VerifyEvent event, int offset, int length) {
            showPreview = (event.stateMask & SWT.CTRL) != 0
                    && (event.character == SWT.CR || event.character == SWT.LF);
            return super.doExit(model, event, offset, length);
        }
    }

    private final class LinkedModeListener implements
            ILinkedModeListener {
        @Override
        public void left(LinkedModeModel model, int flags) {
            if ((flags&UPDATE_CARET)!=0) {
                done();
            }
            else {
                if ((flags&EXTERNAL_MODIFICATION)==0) {
                    editor.getCeylonSourceViewer().invalidateTextPresentation();
                }
                cancel();
            }
        }

        @Override
        public void suspend(LinkedModeModel model) {
            editor.clearLinkedMode();
        }

        @Override
        public void resume(LinkedModeModel model, int flags) {
            editor.setLinkedMode(linkedModeModel, AbstractRenameLinkedMode.this);
        }
    }

    private final class FocusEditingSupport implements IEditingSupport {
        public boolean ownsFocusShell() {
            if (infoPopup == null) {
                return false;
            }
            if (infoPopup.ownsFocusShell()) {
                return true;
            }

            Shell editorShell= editor.getSite().getShell();
            Shell activeShell= editorShell.getDisplay().getActiveShell();
            if (editorShell == activeShell) {
                return true;
            }
            return false;
        }

        public boolean isOriginator(DocumentEvent event, IRegion subjectRegion) {
            return false; //leave on external modification outside positions
        }
    }

    protected final CeylonEditor editor;
    
    private RenameInformationPopup infoPopup;

    private Point originalSelection;
    private String originalName;

    protected LinkedPosition namePosition;
    protected LinkedModeModel linkedModeModel;
    protected LinkedPositionGroup linkedPositionGroup;
    private final FocusEditingSupport focusEditingSupport;
    
    protected String openDialogKeyBinding= "";
    
    public AbstractRenameLinkedMode(CeylonEditor editor) {
        this.editor = editor;
        focusEditingSupport = new FocusEditingSupport();
    }
    
    protected abstract String getName();
    
    protected int init(IDocument document) {
        return 0;
    }
    
    protected abstract int getIdentifyingOffset();
    
    public void start() {
        ISourceViewer viewer = editor.getCeylonSourceViewer();
        final IDocument document = viewer.getDocument();
        originalSelection = viewer.getSelectedRange();
        int offset = originalSelection.x;
        final int adjust = init(document);        
        originalName = getName();
                
        
        try {
            
            linkedPositionGroup = new LinkedPositionGroup();
            namePosition = new LinkedPosition(document, getIdentifyingOffset(), 
                    originalName.length(), 0);
            linkedPositionGroup.addPosition(namePosition);
            
            addLinkedPositions(document, editor.getParseController().getRootNode(), 
                    adjust, linkedPositionGroup);

            linkedModeModel = new LinkedModeModel();
            linkedModeModel.addGroup(linkedPositionGroup);
            linkedModeModel.forceInstall();
            linkedModeModel.addLinkingListener(new LinkedModeListener());
            editor.setLinkedMode(linkedModeModel, this);
            
            LinkedModeUI ui= new EditorLinkedModeUI(linkedModeModel, viewer);
            ui.setExitPosition(viewer, getExitPosition(offset, adjust), 0, NO_STOP);
            ui.setExitPolicy(new PreviewingDeleteBlockingExitPolicy(document));
            ui.enter();
            
            //NOTE: I hate this behavior in the Java editor!
//            viewer.setSelectedRange(originalSelection.x+adjust, originalSelection.y+adjust); // by default, full word is selected; restore original selection
            
            if (viewer instanceof IEditingSupportRegistry) {
                ((IEditingSupportRegistry) viewer).register(focusEditingSupport);
            }

            // Must cache here, since editor context is not available in menu from popup shell:
            openDialogKeyBinding = getOpenDialogBinding();
            infoPopup = new RenameInformationPopup(editor, this);
            infoPopup.open();

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    protected int getExitPosition(int selectionOffset, final int adjust) {
        return selectionOffset+adjust;
    }

    String getOpenDialogBinding() {
        return null;
    }

    protected abstract void addLinkedPositions(IDocument document, 
            Tree.CompilationUnit rootNode, int adjust, 
            LinkedPositionGroup linkedPositionGroup);

    public void cancel() {
        linkedModeLeft();
    }
    
    public void done() {
        linkedModeLeft();
        editor.doSave(new NullProgressMonitor());
    }

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

    private void linkedModeLeft() {
        CeylonSourceViewer viewer = editor.getCeylonSourceViewer();
        editor.clearLinkedMode();

//        if (linkedModeModel != null) {
//            linkedModeModel.exit(ILinkedModeListener.NONE);
//            linkedModeModel = null;
//        }
                
        if (infoPopup != null) {
            infoPopup.close();
            infoPopup=null;
        }
        
        if (viewer instanceof IEditingSupportRegistry) {
            ((IEditingSupportRegistry) viewer).unregister(focusEditingSupport);
        }
        
        editor.getSite().getPage().activate(editor);
    }

    public boolean isCaretInLinkedPosition() {
        return getCurrentLinkedPosition() != null;
    }

    public LinkedPosition getCurrentLinkedPosition() {
        Point selection = editor.getCeylonSourceViewer().getSelectedRange();
        int start = selection.x;
        int end = start + selection.y;
        LinkedPosition[] positions = linkedPositionGroup.getPositions();
        for (int i=0; i<positions.length; i++) {
            LinkedPosition position = positions[i];
            if (position.includes(start) && position.includes(end)) {
                return position;
            }
        }
        return null;
    }
    
    protected String getOriginalName() {
        return originalName;
    }
    
    protected String getNewName() {
        try {
            return namePosition.getContent();
        }
        catch (BadLocationException e) {
            return originalName;
        }
    }

    public boolean isEnabled() {
        String newName = getNewName();
        return !originalName.equals(newName) &&
                newName.matches("^\\w(\\w|\\d)*$") &&
                !CeylonTokenColorer.keywords.contains(newName);
    }
    
    public boolean isOriginalName() {
        return originalName.equals(getNewName());
    }

    public abstract String getHintTemplate();

    void addMenuItems(IMenuManager manager) {
        
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
        IUndoManager undoManager = editor.getCeylonSourceViewer().getUndoManager();
        if (undoManager instanceof IUndoManagerExtension) {
            IUndoManagerExtension undoManagerExtension= (IUndoManagerExtension)undoManager;
            IUndoContext undoContext = undoManagerExtension.getUndoContext();
            IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
            startingUndoOperation = operationHistory.getUndoOperation(undoContext);
        }
    }

    protected void revertChanges()  {
        //undo the change made in the current editor
        //note: I would prefer to do it this way 
        //      but that's not the way JDT does it
//        DocumentChange change = new DocumentChange("Reverting Inline Rename", 
//                namePosition.getDocument());
//        change.setEdit(new MultiTextEdit());
//        for (LinkedPosition lp: linkedPositionGroup.getPositions()) {
//            change.addEdit(new ReplaceEdit(lp.getOffset(), 
//                    lp.getLength(), 
//                    getOriginalName()));
//        }
//        try {
//            change.perform(new NullProgressMonitor());
//        } 
//        catch (CoreException e) {
//            e.printStackTrace();
//        }
        try {
            editor.getSite().getWorkbenchWindow().run(false, true, new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor) 
                        throws InvocationTargetException, InterruptedException {
                    IUndoManager undoManager = editor.getCeylonSourceViewer().getUndoManager();
                    if (undoManager instanceof IUndoManagerExtension) {
                        IUndoContext undoContext = ((IUndoManagerExtension) undoManager).getUndoContext();
                        IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
                        while (undoManager.undoable()) {
                            if (startingUndoOperation != null && 
                                    startingUndoOperation.equals(operationHistory.getUndoOperation(undoContext))) {
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
    }

}