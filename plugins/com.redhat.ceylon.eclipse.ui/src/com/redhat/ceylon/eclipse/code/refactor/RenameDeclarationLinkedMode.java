package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.LINKED_MODE_RENAME;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;
import static org.eclipse.jface.text.link.ILinkedModeListener.NONE;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.internal.ui.refactoring.RefactoringExecutionHelper;
import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedNamesAssistProposal.DeleteBlockingExitPolicy;
import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.IUndoManagerExtension;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.ui.editors.text.EditorsUI;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public final class RenameDeclarationLinkedMode extends
			AbstractRenameLinkedMode {
    	
	private IUndoableOperation fStartingUndoOperation;
	private final RenameRefactoring refactoring;
	private boolean showPreview = false;
	
	public RenameDeclarationLinkedMode(CeylonEditor editor) {
		super(editor);
		this.refactoring = new RenameRefactoring(editor);
	}
	
	public static boolean useLinkedMode() {
		IPreferenceStore prefStore = EditorsUI.getPreferenceStore();
		return prefStore.getBoolean(LINKED_MODE_RENAME);
	}
	
	@Override
	public void start() {
	    if (!refactoring.isEnabled()) return;
		editor.doSave(new NullProgressMonitor());
		saveEditorState();
		super.start();
	}

	public void done() {
		if (isEnabled()) {
		    try {
		        hideEditorActivity();
		        refactoring.setNewName(getNewName());
		        revertChanges();
		        if (showPreview) {
		            openPreview();
		        }
		        else {
		            new RefactoringExecutionHelper(refactoring,
		                    RefactoringStatus.WARNING,
		                    RefactoringSaveHelper.SAVE_ALL,
		                    editor.getSite().getShell(),
		                    editor.getSite().getWorkbenchWindow())
		                .perform(false, true);
		        }
		    } 
		    catch (Exception e) {
		        e.printStackTrace();
		    }
		    finally {
		        unhideEditorActivity();
		    }
		    super.done();
		}
		else {
		    super.cancel();
		}
	}

    private void saveEditorState() {
        //save where we are before opening linked mode
        IUndoManager undoManager = editor.getCeylonSourceViewer().getUndoManager();
        if (undoManager instanceof IUndoManagerExtension) {
            IUndoManagerExtension undoManagerExtension= (IUndoManagerExtension)undoManager;
            IUndoContext undoContext = undoManagerExtension.getUndoContext();
            IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
            fStartingUndoOperation = operationHistory.getUndoOperation(undoContext);
        }
    }

    private void revertChanges()  {
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
                            if (fStartingUndoOperation != null && 
                                    fStartingUndoOperation.equals(operationHistory.getUndoOperation(undoContext))) {
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

	@Override
	public String getHintTemplate() {
		return "Enter new name for " + refactoring.getCount() + 
		        " occurrences of '" + getName()  + "' {0}";
	}

	@Override
    protected int getIdentifyingOffset() {
    	return getIdentifyingNode(refactoring.getNode()).getStartIndex();
    }
    
	@Override
	protected void addLinkedPositions(IDocument document,
			CompilationUnit rootNode, int adjust,
			LinkedPositionGroup linkedPositionGroup) {
		int i=1;
		for (Node node: refactoring.getNodesToRename(rootNode)) {
			Node identifyingNode = getIdentifyingNode(node);
			try {
				linkedPositionGroup.addPosition(new LinkedPosition(document, 
						identifyingNode.getStartIndex(), 
						identifyingNode.getText().length(), i++));
			} 
			catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
        for (Region region: refactoring.getStringsToReplace(rootNode)) {
            try {
                linkedPositionGroup.addPosition(new LinkedPosition(document, 
                        region.getOffset(), 
                        region.getLength(), i++));
            } 
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
	}

	@Override
	protected String getName() {
		return refactoring.getDeclaration().getName();
	}
	
    void enterDialogMode() {
        refactoring.setNewName(getNewName());
        revertChanges();
        linkedModeModel.exit(NONE);
    }
    
    void openPreview() {
        new RenameRefactoringAction(editor) {
            @Override
            public AbstractRefactoring createRefactoring() {
                return RenameDeclarationLinkedMode.this.refactoring;
            }
            @Override
            public RefactoringWizard createWizard(AbstractRefactoring refactoring) {
                return new RenameWizard((AbstractRefactoring) refactoring) {
                    @Override
                    protected void addUserInputPages() {}
                };
            }
        }.run();
    }

    void openDialog() {
        new RenameRefactoringAction(editor) {
            @Override
            public AbstractRefactoring createRefactoring() {
                return RenameDeclarationLinkedMode.this.refactoring;
            }
        }.run();
    }
    
	@Override
    void addMenuItems(IMenuManager manager) {
	    
	    IAction previewAction = new Action("Preview") {
	        @Override
	        public void run() {
	            enterDialogMode();
	            openPreview();
	        }
	    };
	    previewAction.setAccelerator(SWT.CTRL | SWT.CR);
	    previewAction.setEnabled(true);
	    manager.add(previewAction);

        IAction openDialogAction = new Action("Open Dialog" + '\t' + 
                openDialogKeyBinding) {
            @Override
            public void run() {
                enterDialogMode();
                openDialog();
            }
        };
        manager.add(openDialogAction);
    }
	
//  private Image image= null;
//  private Label label= null;
	
	private void hideEditorActivity() {
//      if (viewer instanceof SourceViewer) {
//      final SourceViewer sourceViewer= (SourceViewer) viewer;
//      Control viewerControl= sourceViewer.getControl();
//      if (viewerControl instanceof Composite) {
//          Composite composite= (Composite) viewerControl;
//          Display display= composite.getDisplay();
//
//          // Flush pending redraw requests:
//          while (! display.isDisposed() && display.readAndDispatch()) {
//          }
//
//          // Copy editor area:
//          GC gc= new GC(composite);
//          Point size;
//          try {
//              size= composite.getSize();
//              image= new Image(gc.getDevice(), size.x, size.y);
//              gc.copyArea(image, 0, 0);
//          } finally {
//              gc.dispose();
//              gc= null;
//          }
//
//          // Persist editor area while executing refactoring:
//          label= new Label(composite, SWT.NONE);
//          label.setImage(image);
//          label.setBounds(0, 0, size.x, size.y);
//          label.moveAbove(null);
//      }
	}
	
	private void unhideEditorActivity() {
//	    if (label != null)
//            label.dispose();
//        if (image != null)
//            image.dispose();
	}
	
	@Override
    DeleteBlockingExitPolicy createExitPolicy(final IDocument document) {
        return new DeleteBlockingExitPolicy(document) {
            @Override
            public ExitFlags doExit(LinkedModeModel model, VerifyEvent event, int offset, int length) {
                showPreview = (event.stateMask & SWT.CTRL) != 0
                                && (event.character == SWT.CR || event.character == SWT.LF);
                return super.doExit(model, event, offset, length);
            }
        };
    }

}
