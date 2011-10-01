package com.redhat.ceylon.eclipse.imp.editor;

import java.lang.reflect.Field;
import java.text.BreakIterator;
import java.text.CharacterIterator;

import org.eclipse.imp.editor.GenerateActionGroup;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.ui.DefaultPartListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.TextNavigationAction;

import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

public class CeylonEditor extends UniversalEditor {
    static Field refreshContributionsField;
    static Field generateActionGroupField;
    static {
        try {
            refreshContributionsField = UniversalEditor.class.getDeclaredField("fRefreshContributions");
            refreshContributionsField.setAccessible(true);
            generateActionGroupField = UniversalEditor.class.getDeclaredField("fGenerateActionGroup");
            generateActionGroupField.setAccessible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        getSite().getPage().hideActionSet(IMP_CODING_ACTION_SET);
        //getSite().getPage().hideActionSet(IMP_OPEN_ACTION_SET);
        try {
            getSite().getPage().removePartListener((DefaultPartListener) refreshContributionsField.get(this));
            generateActionGroupField.set(this, new CeylonGenerateActionGroup(this));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    class CeylonGenerateActionGroup extends GenerateActionGroup {
        public CeylonGenerateActionGroup(UniversalEditor editor) {
            super(editor, "");
        }
        @Override
        public void fillContextMenu(IMenuManager menu) {}
    }
    
    private SourceArchiveDocumentProvider sourceArchiveDocumentProvider;
    
    @Override
    public IDocumentProvider getDocumentProvider() {
        if (SourceArchiveDocumentProvider.canHandle(getEditorInput())) {
            if (sourceArchiveDocumentProvider == null) {
                sourceArchiveDocumentProvider= new SourceArchiveDocumentProvider();
            }
            return sourceArchiveDocumentProvider;
        }
        return super.getDocumentProvider();
    }

    public CeylonParseController getParseController() {
        return (CeylonParseController) super.getParseController();
    }
    
    @Override
    protected void createNavigationActions() {
        super.createNavigationActions();
        
        final StyledText textWidget= getSourceViewer().getTextWidget();

        /*IAction action= new SmartLineStartAction(textWidget, false);
        action.setActionDefinitionId(ITextEditorActionDefinitionIds.LINE_START);
        editor.setAction(ITextEditorActionDefinitionIds.LINE_START, action);

        action= new SmartLineStartAction(textWidget, true);
        action.setActionDefinitionId(ITextEditorActionDefinitionIds.SELECT_LINE_START);
        editor.setAction(ITextEditorActionDefinitionIds.SELECT_LINE_START, action);*/

        setAction(ITextEditorActionDefinitionIds.WORD_PREVIOUS, 
                new NavigatePreviousSubWordAction());
        textWidget.setKeyBinding(SWT.CTRL | SWT.ARROW_LEFT, SWT.NULL);

        setAction(ITextEditorActionDefinitionIds.WORD_NEXT, 
                new NavigateNextSubWordAction());
        textWidget.setKeyBinding(SWT.CTRL | SWT.ARROW_RIGHT, SWT.NULL);

        setAction(ITextEditorActionDefinitionIds.SELECT_WORD_PREVIOUS, 
                new SelectPreviousSubWordAction());
        textWidget.setKeyBinding(SWT.CTRL | SWT.SHIFT | SWT.ARROW_LEFT, SWT.NULL);

        setAction(ITextEditorActionDefinitionIds.SELECT_WORD_NEXT, 
                new SelectNextSubWordAction());
        textWidget.setKeyBinding(SWT.CTRL | SWT.SHIFT | SWT.ARROW_RIGHT, SWT.NULL);

        setAction(ITextEditorActionDefinitionIds.DELETE_PREVIOUS_WORD, new DeletePreviousSubWordAction());
        textWidget.setKeyBinding(SWT.CTRL | SWT.BS, SWT.NULL);
        markAsStateDependentAction(ITextEditorActionDefinitionIds.DELETE_PREVIOUS_WORD, true);

        setAction(ITextEditorActionDefinitionIds.DELETE_NEXT_WORD, new DeleteNextSubWordAction());
        textWidget.setKeyBinding(SWT.CTRL | SWT.DEL, SWT.NULL);
        markAsStateDependentAction(ITextEditorActionDefinitionIds.DELETE_NEXT_WORD, true);
    }
    
    protected class NavigateNextSubWordAction extends NextSubWordAction {
        public NavigateNextSubWordAction() {
            super(ST.WORD_NEXT);
            setActionDefinitionId(ITextEditorActionDefinitionIds.WORD_NEXT);
        }
        @Override
        protected void setCaretPosition(final int position) {
            getTextWidget().setCaretOffset(modelOffset2WidgetOffset(getSourceViewer(), position));
        }
    }

    protected class NavigatePreviousSubWordAction extends PreviousSubWordAction {
        public NavigatePreviousSubWordAction() {
            super(ST.WORD_PREVIOUS);
            setActionDefinitionId(ITextEditorActionDefinitionIds.WORD_PREVIOUS);
        }
        @Override
        protected void setCaretPosition(final int position) {
            getTextWidget().setCaretOffset(modelOffset2WidgetOffset(getSourceViewer(), position));
        }
    }

    protected abstract class NextSubWordAction extends TextNavigationAction {

        protected CeylonWordIterator fIterator= new CeylonWordIterator();

        /**
         * Creates a new next sub-word action.
         *
         * @param code Action code for the default operation. Must be an action code from @see org.eclipse.swt.custom.ST.
         */
        protected NextSubWordAction(int code) {
            super(getSourceViewer().getTextWidget(), code);
        }

        /*
         * @see org.eclipse.jface.action.IAction#run()
         */
        @Override
        public void run() {
            // Check whether we are in a java code partition and the preference is enabled

            final ISourceViewer viewer= getSourceViewer();
            final IDocument document= viewer.getDocument();
            try {
                fIterator.setText((CharacterIterator)new DocumentCharacterIterator(document));
                int position= widgetOffset2ModelOffset(viewer, viewer.getTextWidget().getCaretOffset());
                if (position == -1)
                    return;

                int next= findNextPosition(position);
                if (next != BreakIterator.DONE) {
                    setCaretPosition(next);
                    getTextWidget().showSelection();
                    fireSelectionChanged();
                }
            } catch (BadLocationException x) {
                // ignore
            }
        }
        
        /**
         * Finds the next position after the given position.
         *
         * @param position the current position
         * @return the next position
         */
        protected int findNextPosition(int position) {
            ISourceViewer viewer= getSourceViewer();
            int widget= -1;
            int next= position;
            while (next != BreakIterator.DONE && widget == -1) { // XXX: optimize
                next= fIterator.following(next);
                if (next != BreakIterator.DONE)
                    widget= modelOffset2WidgetOffset(viewer, next);
            }

            IDocument document= viewer.getDocument();
            LinkedModeModel model= LinkedModeModel.getModel(document, position);
            if (model != null) {
                LinkedPosition linkedPosition= model.findPosition(new LinkedPosition(document, position, 0));
                if (linkedPosition != null) {
                    int linkedPositionEnd= linkedPosition.getOffset() + linkedPosition.getLength();
                    if (position != linkedPositionEnd && linkedPositionEnd < next)
                        next= linkedPositionEnd;
                } else {
                    LinkedPosition nextLinkedPosition= model.findPosition(new LinkedPosition(document, next, 0));
                    if (nextLinkedPosition != null) {
                        int nextLinkedPositionOffset= nextLinkedPosition.getOffset();
                        if (position != nextLinkedPositionOffset && nextLinkedPositionOffset < next)
                            next= nextLinkedPositionOffset;
                    }
                }
            }

            return next;
        }

        /**
         * Sets the caret position to the sub-word boundary given with <code>position</code>.
         *
         * @param position Position where the action should move the caret
         */
        protected abstract void setCaretPosition(int position);
    }

    protected abstract class PreviousSubWordAction extends TextNavigationAction {

        protected CeylonWordIterator fIterator= new CeylonWordIterator();

        /**
         * Creates a new previous sub-word action.
         *
         * @param code Action code for the default operation. Must be an action code from @see org.eclipse.swt.custom.ST.
         */
        protected PreviousSubWordAction(final int code) {
            super(getSourceViewer().getTextWidget(), code);
        }

        @Override
        public void run() {
            // Check whether we are in a java code partition and the preference is enabled

            final ISourceViewer viewer= getSourceViewer();
            final IDocument document= viewer.getDocument();
            try {
                fIterator.setText((CharacterIterator)new DocumentCharacterIterator(document));
                int position= widgetOffset2ModelOffset(viewer, viewer.getTextWidget().getCaretOffset());
                if (position == -1)
                    return;

                int previous= findPreviousPosition(position);
                if (previous != BreakIterator.DONE) {
                    setCaretPosition(previous);
                    getTextWidget().showSelection();
                    fireSelectionChanged();
                }
            } catch (BadLocationException x) {
                // ignore - getLineOfOffset failed
            }

        }

        /**
         * Finds the previous position before the given position.
         *
         * @param position the current position
         * @return the previous position
         */
        protected int findPreviousPosition(int position) {
            ISourceViewer viewer= getSourceViewer();
            int widget= -1;
            int previous= position;
            while (previous != BreakIterator.DONE && widget == -1) { // XXX: optimize
                previous= fIterator.preceding(previous);
                if (previous != BreakIterator.DONE)
                    widget= modelOffset2WidgetOffset(viewer, previous);
            }

            IDocument document= viewer.getDocument();
            LinkedModeModel model= LinkedModeModel.getModel(document, position);
            if (model != null) {
                LinkedPosition linkedPosition= model.findPosition(new LinkedPosition(document, position, 0));
                if (linkedPosition != null) {
                    int linkedPositionOffset= linkedPosition.getOffset();
                    if (position != linkedPositionOffset && previous < linkedPositionOffset)
                        previous= linkedPositionOffset;
                } else {
                    LinkedPosition previousLinkedPosition= model.findPosition(new LinkedPosition(document, previous, 0));
                    if (previousLinkedPosition != null) {
                        int previousLinkedPositionEnd= previousLinkedPosition.getOffset() + previousLinkedPosition.getLength();
                        if (position != previousLinkedPositionEnd && previous < previousLinkedPositionEnd)
                            previous= previousLinkedPositionEnd;
                    }
                }
            }

            return previous;
        }

        /**
         * Sets the caret position to the sub-word boundary given with <code>position</code>.
         *
         * @param position Position where the action should move the caret
         */
        protected abstract void setCaretPosition(int position);
    }
    
    protected class SelectNextSubWordAction extends NextSubWordAction {
        public SelectNextSubWordAction() {
            super(ST.SELECT_WORD_NEXT);
            setActionDefinitionId(ITextEditorActionDefinitionIds.SELECT_WORD_NEXT);
        }
        @Override
        protected void setCaretPosition(final int position) {
            final ISourceViewer viewer= getSourceViewer();

            final StyledText text= viewer.getTextWidget();
            if (text != null && !text.isDisposed()) {

                final Point selection= text.getSelection();
                final int caret= text.getCaretOffset();
                final int offset= modelOffset2WidgetOffset(viewer, position);

                if (caret == selection.x)
                    text.setSelectionRange(selection.y, offset - selection.y);
                else
                    text.setSelectionRange(selection.x, offset - selection.x);
            }
        }
    }
    
    protected class SelectPreviousSubWordAction extends PreviousSubWordAction {
        public SelectPreviousSubWordAction() {
            super(ST.SELECT_WORD_PREVIOUS);
            setActionDefinitionId(ITextEditorActionDefinitionIds.SELECT_WORD_PREVIOUS);
        }
        @Override
        protected void setCaretPosition(final int position) {
            final ISourceViewer viewer= getSourceViewer();

            final StyledText text= viewer.getTextWidget();
            if (text != null && !text.isDisposed()) {

                final Point selection= text.getSelection();
                final int caret= text.getCaretOffset();
                final int offset= modelOffset2WidgetOffset(viewer, position);

                if (caret == selection.x)
                    text.setSelectionRange(selection.y, offset - selection.y);
                else
                    text.setSelectionRange(selection.x, offset - selection.x);
            }
        }
    }
    
    protected class DeleteNextSubWordAction extends NextSubWordAction implements IUpdate {
        public DeleteNextSubWordAction() {
            super(ST.DELETE_WORD_NEXT);
            setActionDefinitionId(ITextEditorActionDefinitionIds.DELETE_NEXT_WORD);
        }
        @Override
        protected void setCaretPosition(final int position) {
            if (!validateEditorInputState())
                return;

            final ISourceViewer viewer= getSourceViewer();
            StyledText text= viewer.getTextWidget();
            Point selection= viewer.getSelectedRange();
            final int caret, length;
            if (selection.y != 0) {
                caret= selection.x;
                length= selection.y;
            } else {
                caret= widgetOffset2ModelOffset(viewer, text.getCaretOffset());
                length= position - caret;
            }

            try {
                viewer.getDocument().replace(caret, length, ""); //$NON-NLS-1$
            } catch (BadLocationException exception) {
                // Should not happen
            }
        }
        public void update() {
            setEnabled(isEditorInputModifiable());
        }
    }

    protected class DeletePreviousSubWordAction extends PreviousSubWordAction implements IUpdate {
        public DeletePreviousSubWordAction() {
            super(ST.DELETE_WORD_PREVIOUS);
            setActionDefinitionId(ITextEditorActionDefinitionIds.DELETE_PREVIOUS_WORD);
        }
        @Override
        protected void setCaretPosition(int position) {
            if (!validateEditorInputState())
                return;

            final int length;
            final ISourceViewer viewer= getSourceViewer();
            StyledText text= viewer.getTextWidget();
            Point selection= viewer.getSelectedRange();
            if (selection.y != 0) {
                position= selection.x;
                length= selection.y;
            } else {
                length= widgetOffset2ModelOffset(viewer, text.getCaretOffset()) - position;
            }

            try {
                viewer.getDocument().replace(position, length, ""); //$NON-NLS-1$
            } catch (BadLocationException exception) {
                // Should not happen
            }
        }
        public void update() {
            setEnabled(isEditorInputModifiable());
        }
    }

}
