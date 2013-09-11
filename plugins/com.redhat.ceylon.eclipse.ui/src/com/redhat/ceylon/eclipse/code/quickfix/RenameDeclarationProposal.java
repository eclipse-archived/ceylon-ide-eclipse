package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.RENAME;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.internal.ui.refactoring.RefactoringExecutionHelper;
import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewerExtension6;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.IUndoManagerExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.refactor.RenameRefactoring;

class RenameDeclarationProposal implements ICompletionProposal,
		ICompletionProposalExtension6 {

    private final class RenameDeclarationLinkedMode extends
			AbstractRenameLinkedMode {
    	
        private IUndoableOperation fStartingUndoOperation;
    	
		private RenameDeclarationLinkedMode(Node element, Declaration dec,
				CeylonEditor editor) {
			super(element, dec, editor);
		}
		
		@Override
		public void start() {
			final ISourceViewer viewer= editor.getCeylonSourceViewer();
	        editor.doSave(new NullProgressMonitor());
	        
			//save where we are before opening linked mode
			if (viewer instanceof ITextViewerExtension6) {
				IUndoManager undoManager= ((ITextViewerExtension6)viewer).getUndoManager();
				if (undoManager instanceof IUndoManagerExtension) {
					IUndoManagerExtension undoManagerExtension= (IUndoManagerExtension)undoManager;
					IUndoContext undoContext= undoManagerExtension.getUndoContext();
					IOperationHistory operationHistory= OperationHistoryFactory.getOperationHistory();
					fStartingUndoOperation= operationHistory.getUndoOperation(undoContext);
				}
			}
			
			super.start();
		}

		public void done() {
			final ISourceViewer viewer= editor.getCeylonSourceViewer();
			if (!isEnabled()) return;
//			Image image= null;
//			Label label= null;
			
			try {
//				if (viewer instanceof SourceViewer) {
//					final SourceViewer sourceViewer= (SourceViewer) viewer;
//					Control viewerControl= sourceViewer.getControl();
//					if (viewerControl instanceof Composite) {
//						Composite composite= (Composite) viewerControl;
//						Display display= composite.getDisplay();
//
//						// Flush pending redraw requests:
//						while (! display.isDisposed() && display.readAndDispatch()) {
//						}
//
//						// Copy editor area:
//						GC gc= new GC(composite);
//						Point size;
//						try {
//							size= composite.getSize();
//							image= new Image(gc.getDevice(), size.x, size.y);
//							gc.copyArea(image, 0, 0);
//						} finally {
//							gc.dispose();
//							gc= null;
//						}
//
//						// Persist editor area while executing refactoring:
//						label= new Label(composite, SWT.NONE);
//						label.setImage(image);
//						label.setBounds(0, 0, size.x, size.y);
//						label.moveAbove(null);
//					}
				
				refactoring.setNewName(getNewName());
				
				//undo the change made in the current editor
				editor.getSite().getWorkbenchWindow().run(false, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						if (viewer instanceof ITextViewerExtension6) {
							org.eclipse.jface.text.IUndoManager undoManager= ((ITextViewerExtension6)viewer).getUndoManager();
							if (undoManager instanceof IUndoManagerExtension) {
								IUndoManagerExtension undoManagerExtension= (IUndoManagerExtension)undoManager;
								IUndoContext undoContext= undoManagerExtension.getUndoContext();
								IOperationHistory operationHistory= OperationHistoryFactory.getOperationHistory();
								while (undoManager.undoable()) {
									if (fStartingUndoOperation != null && 
											fStartingUndoOperation.equals(operationHistory.getUndoOperation(undoContext)))
										return;
									undoManager.undo();
								}
							}
						}
					}
				});
					
				RefactoringExecutionHelper helper= new RefactoringExecutionHelper(refactoring,
						RefactoringStatus.WARNING,
					    RefactoringSaveHelper.SAVE_ALL,
					    editor.getSite().getShell(),
					    editor.getSite().getWorkbenchWindow());
				helper.perform(false, true);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			super.done();
		}

		@Override
		protected String getHintTemplate() {
			return "Enter new name for '" + dec.getName() + "' {0}";
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
		}
		
	}

	Node node;
    Declaration dec;
    CeylonEditor editor;
    IFile file;
	private RenameRefactoring refactoring;
    
    RenameDeclarationProposal(IFile file, Node node, 
            Declaration dec, CeylonEditor editor, 
            RenameRefactoring rr) {
        this.node = node;
        this.dec = dec;
        this.file = file;
        this.editor = editor;
        this.refactoring = rr;
    }
    
    @Override
    public Point getSelection(IDocument doc) {
    	return null;
    }

    @Override
    public Image getImage() {
    	return RENAME;
    }

    @Override
    public String getDisplayString() {
    	return "Rename '" + dec.getName() + "'";
    }

    @Override
    public IContextInformation getContextInformation() {
    	return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
    	return null;
    }

    @Override
    public void apply(IDocument doc) {
    	new RenameDeclarationLinkedMode(node, dec, editor).start();
    }
    
    @Override
	public StyledString getStyledDisplayString() {
		return ChangeCorrectionProposal.style(getDisplayString());
	}

	public static void add(Collection<ICompletionProposal> proposals,
			IFile file, CeylonEditor editor) {
		RenameRefactoring rr = new RenameRefactoring(editor);
		if (rr.isEnabled()) {
			proposals.add(new RenameDeclarationProposal(file, 
					rr.getNode(), rr.getDeclaration(), editor,
					rr));
		}
	}

}