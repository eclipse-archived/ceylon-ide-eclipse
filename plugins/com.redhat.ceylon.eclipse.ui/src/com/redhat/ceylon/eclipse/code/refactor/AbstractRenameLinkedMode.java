package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.addLinkedPosition;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.installLinkedMode;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.unregisterEditingSupport;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static org.eclipse.jface.text.link.LinkedPositionGroup.NO_STOP;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

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
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IEditingSupport;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.IUndoManagerExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.complete.LinkedModeCompletionProposal;
import com.redhat.ceylon.eclipse.code.editor.AbstractLinkedModeListener;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.FocusEditingSupport;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;

public abstract class AbstractRenameLinkedMode {

    protected boolean showPreview = false;
    private IUndoableOperation startingUndoOperation;
    
    void addTypeProposals(IDocument document,
            List<ProducedType> supertypes, 
            int offset, int length) {
        Unit unit = editor.getParseController().getRootNode().getUnit();
        ICompletionProposal[] proposals = 
                new ICompletionProposal[supertypes.size()];
        for (int i=0; i<supertypes.size(); i++) {
            ProducedType type = supertypes.get(i);
            String typeName = type.getProducedTypeName(unit);
            proposals[i] = new LinkedModeCompletionProposal(offset, typeName, 0,
                    getImageForDeclaration(type.getDeclaration()));
        }
        ProposalPosition linkedPosition = 
                new ProposalPosition(document, offset, length, 2, proposals);
        try {
            addLinkedPosition(linkedModeModel, linkedPosition);
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    protected final CeylonEditor editor;
    
    private RenameInformationPopup infoPopup;

    private Point originalSelection;
    private String originalName;

    protected LinkedPosition namePosition;
    protected LinkedModeModel linkedModeModel;
    protected LinkedPositionGroup linkedPositionGroup;
    
    protected String openDialogKeyBinding= "";
    
    public AbstractRenameLinkedMode(CeylonEditor editor) {
        this.editor = editor;
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
            createLinkedModeModel(document, adjust);
            addAdditionalLinkedPositionGroups(document);
            enterLinkedMode(document, offset, adjust);
            openPopup();
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void createLinkedModeModel(IDocument document,
            int adjust) 
                    throws BadLocationException {
        linkedModeModel = new LinkedModeModel();
        
        linkedPositionGroup = new LinkedPositionGroup();
        namePosition = new LinkedPosition(document, getIdentifyingOffset(), 
                originalName.length(), 0);
        linkedPositionGroup.addPosition(namePosition);
        
        addLinkedPositions(document, editor.getParseController().getRootNode(), 
                adjust, linkedPositionGroup);

        linkedModeModel.addGroup(linkedPositionGroup);
    }

    private void openPopup() {
        // Must cache here, since editor context is not available in menu from popup shell:
        openDialogKeyBinding = getOpenDialogBinding();
        infoPopup = new RenameInformationPopup(editor, this);
        infoPopup.open();
    }

    private void enterLinkedMode(final IDocument document, 
            int offset, int adjust) 
                    throws BadLocationException {
        final IEditingSupport editingSupport = new FocusEditingSupport(editor) {
            public boolean ownsFocusShell() {
                if (infoPopup == null) {
                    return false;
                }
                if (infoPopup.ownsFocusShell()) {
                    return true;
                }
                return super.ownsFocusShell();
            }
        };
        installLinkedMode(editor, linkedModeModel, this, 
                NO_STOP, getExitPosition(offset, adjust), 
                editingSupport, 
                new DeleteBlockingExitPolicy(document) {
                    @Override
                    public ExitFlags doExit(LinkedModeModel model, 
                            VerifyEvent event, int offset, int length) {
                        showPreview = (event.stateMask & SWT.CTRL) != 0
                                && (event.character == SWT.CR || event.character == SWT.LF);
                        return super.doExit(model, event, offset, length);
                    }
                }, 
                new AbstractLinkedModeListener(editor, this) {
                    @Override
                    public void left(LinkedModeModel model, int flags) {
                        editor.clearLinkedMode();
                        if (infoPopup != null) {
                            infoPopup.close();
                            infoPopup=null;
                        }                
                        editor.getSite().getPage().activate(editor);
                        if ((flags&UPDATE_CARET)!=0) {
                            done();
                        }
                        else {
                            if ((flags&EXTERNAL_MODIFICATION)==0) {
                                editor.getCeylonSourceViewer().invalidateTextPresentation();
                            }
                            cancel();
                        }
                        unregisterEditingSupport(editor, editingSupport);
                    }
                });
        
        //NOTE: I hate this behavior in the Java editor!
        //viewer.setSelectedRange(originalSelection.x+adjust, originalSelection.y+adjust); // by default, full word is selected; restore original selection
    }

    protected void addAdditionalLinkedPositionGroups(IDocument document) {}

    protected int getExitPosition(int selectionOffset, int adjust) {
        return selectionOffset+adjust;
    }

    String getOpenDialogBinding() {
        return null;
    }

    protected abstract void addLinkedPositions(IDocument document, 
            Tree.CompilationUnit rootNode, int adjust, 
            LinkedPositionGroup linkedPositionGroup);

    protected void cancel() {}
    
    protected void done() {
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