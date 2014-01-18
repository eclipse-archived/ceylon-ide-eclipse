package com.redhat.ceylon.eclipse.code.quickfix;

/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import static org.eclipse.jface.text.link.ILinkedModeListener.NONE;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

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
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.IUndoManagerExtension;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;

import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportModule;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.refactor.AbstractRefactoring;
import com.redhat.ceylon.eclipse.code.refactor.AbstractRenameLinkedMode;
import com.redhat.ceylon.eclipse.code.refactor.ChangeVersionRefactoring;
import com.redhat.ceylon.eclipse.code.refactor.ChangeVersionRefactoringAction;
import com.redhat.ceylon.eclipse.code.refactor.ChangeVersionWizard;


class EnterVersionLinkedMode extends AbstractRenameLinkedMode {

	private IUndoableOperation startingUndoOperation;
	private final Tree.ImportPath module;
	private final Tree.QuotedLiteral version;
	private boolean showPreview = false;
	
	private final ChangeVersionRefactoring refactoring;

	private final class LinkedPositionsVisitor 
	        extends Visitor implements NaturalVisitor {
		private final int adjust;
		private final IDocument document;
		private final LinkedPositionGroup linkedPositionGroup;
		int i=1;

		private LinkedPositionsVisitor(int adjust, IDocument document,
				LinkedPositionGroup linkedPositionGroup) {
			this.adjust = adjust;
			this.document = document;
			this.linkedPositionGroup = linkedPositionGroup;
		}

		@Override
		public void visit(ImportModule that) {
		    super.visit(that);
		    addLinkedPosition(document, that.getVersion(), 
		            that.getImportPath());
		}
		
		boolean eq(Tree.ImportPath x, Tree.ImportPath y) {
			List<Identifier> xids = x.getIdentifiers();
			List<Identifier> yids = y.getIdentifiers();
			if (xids.size()!=yids.size()) {
				return false;
			}
			for (int i=0; i<xids.size(); i++) {
				if (!xids.get(0).equals(yids.get(i))) {
					return false;
				}
			}
			return true;
		}
		
		protected void addLinkedPosition(final IDocument document,
		        Tree.QuotedLiteral version, Tree.ImportPath path) {
		    if (version!=null && path!=null && eq(module, path)) {
		        try {
		            int pos = version.getStartIndex()+adjust+1;
					int len = version.getText().length()-2;
					linkedPositionGroup.addPosition(new LinkedPosition(document, 
		                    pos, len, i++));
		        }
		        catch (BadLocationException e) {
		            e.printStackTrace();
		        }
		    }
		}
	}

	public EnterVersionLinkedMode(Tree.QuotedLiteral version, 
			Tree.ImportPath module, CeylonEditor editor) {
		super(editor);
		this.module = module;
		this.version = version;
		this.refactoring = new ChangeVersionRefactoring(editor);
	}

	@Override
	protected String getName() {
		String quoted = version.getText();
		return quoted.substring(1, quoted.length()-1);
	}

	@Override
	public String getHintTemplate() {
		return "Enter new version for " + linkedPositionGroup.getPositions().length + 
		        " occurrences of \"" + getName() + "\" {0}";
	}
	
	@Override
	protected int getIdentifyingOffset() {
		return version.getStartIndex()+1;
	}
	
	@Override
	public void addLinkedPositions(final IDocument document, Tree.CompilationUnit rootNode, 
			final int adjust, final LinkedPositionGroup linkedPositionGroup) {
		rootNode.visit(new LinkedPositionsVisitor(adjust, document, linkedPositionGroup));
	}
	
	public boolean isEnabled() {
		return !getNewName().isEmpty();
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
//		        editor.doSave(new NullProgressMonitor());
//		        hideEditorActivity();
		        refactoring.setNewVersion(getNewName());
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
//		        unhideEditorActivity();
		    }
		    super.done();
		}
		else {
		    super.cancel();
		}
	}

    void enterDialogMode() {
        refactoring.setNewVersion(getNewName());
        revertChanges();
        linkedModeModel.exit(NONE);
    }
    
    private void saveEditorState() {
        //save where we are before opening linked mode
        IUndoManager undoManager = editor.getCeylonSourceViewer().getUndoManager();
        if (undoManager instanceof IUndoManagerExtension) {
            IUndoManagerExtension undoManagerExtension= (IUndoManagerExtension)undoManager;
            IUndoContext undoContext = undoManagerExtension.getUndoContext();
            IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
            startingUndoOperation = operationHistory.getUndoOperation(undoContext);
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

    void openPreview() {
        new ChangeVersionRefactoringAction(editor) {
            @Override
            public AbstractRefactoring createRefactoring() {
                return EnterVersionLinkedMode.this.refactoring;
            }
            @Override
            public RefactoringWizard createWizard(AbstractRefactoring refactoring) {
                return new ChangeVersionWizard((AbstractRefactoring) refactoring) {
                    @Override
                    protected void addUserInputPages() {}
                };
            }
        }.run();
    }

    void openDialog() {
        new ChangeVersionRefactoringAction(editor) {
            @Override
            public AbstractRefactoring createRefactoring() {
                return EnterVersionLinkedMode.this.refactoring;
            }
        }.run();
    }
    
	@Override
	public void addMenuItems(IMenuManager manager) {
	    
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

        IAction openDialogAction = new Action("Open Dialog"/* + '\t' + 
                openDialogKeyBinding*/) {
            @Override
            public void run() {
                enterDialogMode();
                openDialog();
            }
        };
        manager.add(openDialogAction);
    }
	
	@Override
    public DeleteBlockingExitPolicy createExitPolicy(final IDocument document) {
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
