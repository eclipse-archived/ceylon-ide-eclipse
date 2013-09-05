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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedNamesAssistProposal.DeleteBlockingExitPolicy;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IEditingSupport;
import org.eclipse.jface.text.IEditingSupportRegistry;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Identifier;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class EnterAliasLinkedMode {

    private class FocusEditingSupport implements IEditingSupport {
        public boolean ownsFocusShell() {
            if (fInfoPopup == null)
                return false;
            if (fInfoPopup.ownsFocusShell()) {
                return true;
            }

            Shell editorShell= fEditor.getSite().getShell();
            Shell activeShell= editorShell.getDisplay().getActiveShell();
            if (editorShell == activeShell)
                return true;
            return false;
        }

        public boolean isOriginator(DocumentEvent event, IRegion subjectRegion) {
            return false; //leave on external modification outside positions
        }
    }

    private class EditorSynchronizer implements ILinkedModeListener {
        public void left(LinkedModeModel model, int flags) {
            linkedModeLeft();
            if ( (flags & ILinkedModeListener.UPDATE_CARET) != 0) {
//                doRename(fShowPreview);
                fEditor.doSave(new NullProgressMonitor());
            }
        }

        public void resume(LinkedModeModel model, int flags) {
        }

        public void suspend(LinkedModeModel model) {
        }
    }

    private class ExitPolicy extends DeleteBlockingExitPolicy {
        public ExitPolicy(IDocument document) {
            super(document);
        }

        @Override
        public ExitFlags doExit(LinkedModeModel model, VerifyEvent event, int offset, int length) {
//            fShowPreview= (event.stateMask & SWT.CTRL) != 0
//                            && (event.character == SWT.CR || event.character == SWT.LF);
            return super.doExit(model, event, offset, length);
        }
    }

    private final CeylonEditor fEditor;
    private final Tree.ImportMemberOrType node;
    Declaration dec;

    private RenameInformationPopup fInfoPopup;

    private Point fOriginalSelection;
    private String fOriginalName;

    private LinkedPosition fNamePosition;
    private LinkedModeModel fLinkedModeModel;
    private LinkedPositionGroup fLinkedPositionGroup;
    private final FocusEditingSupport fFocusEditingSupport;

    /**
     * The operation on top of the undo stack when the rename is {@link #start()}ed, or
     * <code>null</code> if rename has not been started or the undo stack was empty.
     * 
     * @since 3.5
     */
//    private IUndoableOperation fStartingUndoOperation;


    public EnterAliasLinkedMode(Tree.ImportMemberOrType element, 
            Declaration dec, CeylonEditor editor) {
        fEditor= editor;
        node= element;
        fFocusEditingSupport= new FocusEditingSupport();
        this.dec = dec;
    }
    
    public void start() {
        ISourceViewer viewer= fEditor.getCeylonSourceViewer();
        final IDocument document= viewer.getDocument();
        fOriginalSelection= viewer.getSelectedRange();
        int offset= fOriginalSelection.x;
        int start = node.getStartIndex();
        String alias;
        final int adjust;
        if (node.getAlias()==null) {
            alias = dec.getName();
            //TODO: is this really the right way to insert the text?
            try {
                document.set(document.get(0,start) + alias + "=" + 
                        document.get(start, document.getLength()-start));
                adjust = alias.length()+1;
            }
            catch (BadLocationException e) {
                e.printStackTrace();
                return;
            }
        }
        else {
            alias = node.getAlias().getIdentifier().getText();
            adjust = 0;
        }
        fOriginalName = alias;
                
        
        try {

            fLinkedPositionGroup= new LinkedPositionGroup();

//            if (viewer instanceof ITextViewerExtension6) {
//                IUndoManager undoManager= ((ITextViewerExtension6)viewer).getUndoManager();
//                if (undoManager instanceof IUndoManagerExtension) {
//                    IUndoManagerExtension undoManagerExtension= (IUndoManagerExtension)undoManager;
//                    IUndoContext undoContext= undoManagerExtension.getUndoContext();
//                    IOperationHistory operationHistory= OperationHistoryFactory.getOperationHistory();
//                    fStartingUndoOperation= operationHistory.getUndoOperation(undoContext);
//                }
//            }
            
            fNamePosition = new LinkedPosition(document, start, alias.length(), 0);
            fLinkedPositionGroup.addPosition(fNamePosition);
            
            fEditor.getParseController().getRootNode().visit(new Visitor() {
                int i=1;
                @Override
                public void visit(BaseMemberOrTypeExpression that) {
                    super.visit(that);
                    addLinkedPosition(document, that.getIdentifier(), 
                            that.getDeclaration());
                }
                @Override
                public void visit(BaseType that) {
                    super.visit(that);
                    addLinkedPosition(document, that.getIdentifier(), 
                            that.getDeclarationModel());
                }
                protected void addLinkedPosition(final IDocument document,
                        Identifier id, Declaration d) {
                    if (id!=null && d!=null && dec.equals(d)) {
                        try {
                            fLinkedPositionGroup.addPosition(new LinkedPosition(document, 
                                    id.getStartIndex()+adjust, 
                                    id.getText().length(), i++));
                        }
                        catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            fLinkedModeModel= new LinkedModeModel();
            fLinkedModeModel.addGroup(fLinkedPositionGroup);
            fLinkedModeModel.forceInstall();
            //TODO: suspend occurrence marking!!!!!!!
//            fLinkedModeModel.addLinkingListener(new EditorHighlightingSynchronizer(fEditor));
            fLinkedModeModel.addLinkingListener(new EditorSynchronizer());

            LinkedModeUI ui= new EditorLinkedModeUI(fLinkedModeModel, viewer);
            ui.setExitPosition(viewer, offset, 0, Integer.MAX_VALUE);
            ui.setExitPolicy(new ExitPolicy(document));
            ui.enter();

//            viewer.setSelectedRange(fOriginalSelection.x, fOriginalSelection.y); // by default, full word is selected; restore original selection
            
            if (viewer instanceof IEditingSupportRegistry) {
                IEditingSupportRegistry registry= (IEditingSupportRegistry) viewer;
                registry.register(fFocusEditingSupport);
            }

            openSecondaryPopup();

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /*void doRename(boolean showPreview) {
        cancel();

        Image image= null;
        Label label= null;

        fShowPreview|= showPreview;
        try {
            ISourceViewer viewer= fEditor.getCeylonSourceViewer();
            if (viewer instanceof SourceViewer) {
                SourceViewer sourceViewer= (SourceViewer) viewer;
                Control viewerControl= sourceViewer.getControl();
                if (viewerControl instanceof Composite) {
                    Composite composite= (Composite) viewerControl;
                    Display display= composite.getDisplay();

                    // Flush pending redraw requests:
                    while (! display.isDisposed() && display.readAndDispatch()) {
                    }

                    // Copy editor area:
                    GC gc= new GC(composite);
                    Point size;
                    try {
                        size= composite.getSize();
                        image= new Image(gc.getDevice(), size.x, size.y);
                        gc.copyArea(image, 0, 0);
                    } finally {
                        gc.dispose();
                        gc= null;
                    }

                    // Persist editor area while executing refactoring:
                    label= new Label(composite, SWT.NONE);
                    label.setImage(image);
                    label.setBounds(0, 0, size.x, size.y);
                    label.moveAbove(null);
                }
            }

//            String newName= fNamePosition.getContent();
//            if (fOriginalName.equals(newName))
//                return;
//            RenameSupport renameSupport= undoAndCreateRenameSupport(newName);
//            if (renameSupport == null)
//                return;
//
//            Shell shell= fEditor.getSite().getShell();
//            boolean executed;
//            if (fShowPreview) { // could have been updated by undoAndCreateRenameSupport(..)
//                executed= renameSupport.openDialog(shell, true);
//            } else {
//                renameSupport.perform(shell, fEditor.getSite().getWorkbenchWindow());
//                executed= true;
//            }
//            if (executed) {
//                restoreFullSelection();
//            }
//            JavaModelUtil.reconcile(getCompilationUnit());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (label != null)
                label.dispose();
            if (image != null)
                image.dispose();
        }
    }*/

    public void cancel() {
        if (fLinkedModeModel != null) {
            fLinkedModeModel.exit(ILinkedModeListener.NONE);
        }
        linkedModeLeft();
    }
    
    public void done() {
        if (fLinkedModeModel != null) {
            fLinkedModeModel.exit(ILinkedModeListener.NONE);
        }
        linkedModeLeft();
        fEditor.doSave(new NullProgressMonitor());
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
        if (fInfoPopup != null) {
            fInfoPopup.close();
        }

        ISourceViewer viewer= fEditor.getCeylonSourceViewer();
        if (viewer instanceof IEditingSupportRegistry) {
            IEditingSupportRegistry registry= (IEditingSupportRegistry) viewer;
            registry.unregister(fFocusEditingSupport);
        }
        
        activateEditor();
    }

    private void activateEditor() {
        fEditor.getSite().getPage().activate(fEditor);
    }

    private void openSecondaryPopup() {
        fInfoPopup= new RenameInformationPopup(fEditor, this);
        fInfoPopup.open();
    }

    public boolean isCaretInLinkedPosition() {
        return getCurrentLinkedPosition() != null;
    }

    public LinkedPosition getCurrentLinkedPosition() {
        Point selection= fEditor.getCeylonSourceViewer().getSelectedRange();
        int start= selection.x;
        int end= start + selection.y;
        LinkedPosition[] positions= fLinkedPositionGroup.getPositions();
        for (int i= 0; i < positions.length; i++) {
            LinkedPosition position= positions[i];
            if (position.includes(start) && position.includes(end))
                return position;
        }
        return null;
    }

    public boolean isEnabled() {
        try {
            String newName= fNamePosition.getContent();
            if (fOriginalName.equals(newName))
                return false;
            //TODO: check that it is a valid identifier!
            return true;//JavaConventionsUtil.validateIdentifier(newName, fJavaElement).isOK();
        } catch (BadLocationException e) {
            return false;
        }

    }

    public boolean isOriginalName() {
        try {
            String newName= fNamePosition.getContent();
            return fOriginalName.equals(newName);
        } catch (BadLocationException e) {
            return false;
        }
    }

}
