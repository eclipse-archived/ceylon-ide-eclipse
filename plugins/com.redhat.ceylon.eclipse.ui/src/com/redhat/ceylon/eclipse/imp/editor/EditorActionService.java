package com.redhat.ceylon.eclipse.imp.editor;

import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.findScope;

import java.lang.reflect.Method;
import java.text.BreakIterator;
import java.text.CharacterIterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.base.EditorServiceBase;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.TextNavigationAction;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

/**
 * Responsible for registering actions that handle 
 * ALT-arrow navigation. This and related classes 
 * are a massive copy/paste job from JDT.
 * 
 * @author gavin
 *
 */
public class EditorActionService extends EditorServiceBase {
    
    @Override
    public AnalysisRequired getAnalysisRequired() {
        return AnalysisRequired.NONE;
    }
    
    @Override
    public void update(IParseController parseController, IProgressMonitor monitor) {}
    
    @Override
    public void setEditor(UniversalEditor editor) {
        super.setEditor(editor);
        createNavigationActions();
    }
    
    static class SelectionListener implements ISelectionChangedListener {
        UniversalEditor editor;
        SelectionListener(UniversalEditor editor) {
            this.editor = editor;
        }
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            final CeylonParseController cpc = (CeylonParseController) editor.getParseController();
            if (cpc.getRootNode()==null) return;
            Node node = findScope(cpc.getRootNode(), (ITextSelection) event.getSelection());
            if (node!=null) {
                editor.setHighlightRange(node.getStartIndex(), 
                        node.getStopIndex()-node.getStartIndex(), false);
            }
            else {
                editor.resetHighlightRange();
            }
        }
    }
    
    ISourceViewer getSourceViewer() {
        try {
            Method m = AbstractTextEditor.class.getDeclaredMethod("getSourceViewer", new Class[0]);
            m.setAccessible(true);
            return (ISourceViewer) m.invoke(getEditor());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    void createNavigationActions() {

        final StyledText textWidget= getSourceViewer().getTextWidget();

        /*IAction action= new SmartLineStartAction(textWidget, false);
        action.setActionDefinitionId(ITextEditorActionDefinitionIds.LINE_START);
        editor.setAction(ITextEditorActionDefinitionIds.LINE_START, action);

        action= new SmartLineStartAction(textWidget, true);
        action.setActionDefinitionId(ITextEditorActionDefinitionIds.SELECT_LINE_START);
        editor.setAction(ITextEditorActionDefinitionIds.SELECT_LINE_START, action);*/

        editor.setAction(ITextEditorActionDefinitionIds.WORD_PREVIOUS, 
                new NavigatePreviousSubWordAction());
        textWidget.setKeyBinding(SWT.CTRL | SWT.ARROW_LEFT, SWT.NULL);

        editor.setAction(ITextEditorActionDefinitionIds.WORD_NEXT, 
                new NavigateNextSubWordAction());
        textWidget.setKeyBinding(SWT.CTRL | SWT.ARROW_RIGHT, SWT.NULL);

        editor.setAction(ITextEditorActionDefinitionIds.SELECT_WORD_PREVIOUS, 
                new SelectPreviousSubWordAction());
        textWidget.setKeyBinding(SWT.CTRL | SWT.SHIFT | SWT.ARROW_LEFT, SWT.NULL);

        editor.setAction(ITextEditorActionDefinitionIds.SELECT_WORD_NEXT, 
                new SelectNextSubWordAction());
        textWidget.setKeyBinding(SWT.CTRL | SWT.SHIFT | SWT.ARROW_RIGHT, SWT.NULL);

        editor.setAction(ITextEditorActionDefinitionIds.DELETE_PREVIOUS_WORD, new DeletePreviousSubWordAction());
        textWidget.setKeyBinding(SWT.CTRL | SWT.BS, SWT.NULL);
        editor.markAsStateDependentAction(ITextEditorActionDefinitionIds.DELETE_PREVIOUS_WORD, true);

        editor.setAction(ITextEditorActionDefinitionIds.DELETE_NEXT_WORD, new DeleteNextSubWordAction());
        textWidget.setKeyBinding(SWT.CTRL | SWT.DEL, SWT.NULL);
        editor.markAsStateDependentAction(ITextEditorActionDefinitionIds.DELETE_NEXT_WORD, true);
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
            if (!getEditor().validateEditorInputState())
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
            setEnabled(getEditor().isEditorInputModifiable());
        }
    }

    protected class DeletePreviousSubWordAction extends PreviousSubWordAction implements IUpdate {
        public DeletePreviousSubWordAction() {
            super(ST.DELETE_WORD_PREVIOUS);
            setActionDefinitionId(ITextEditorActionDefinitionIds.DELETE_PREVIOUS_WORD);
        }
        @Override
        protected void setCaretPosition(int position) {
            if (!getEditor().validateEditorInputState())
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
            setEnabled(getEditor().isEditorInputModifiable());
        }
    }

    private static final int widgetOffset2ModelOffset(ISourceViewer viewer, int widgetOffset) {
        if (viewer instanceof ITextViewerExtension5) {
            ITextViewerExtension5 extension= (ITextViewerExtension5) viewer;
            return extension.widgetOffset2ModelOffset(widgetOffset);
        }
        return widgetOffset + viewer.getVisibleRegion().getOffset();
    }

    private static final int modelOffset2WidgetOffset(ISourceViewer viewer, int modelOffset) {
        if (viewer instanceof ITextViewerExtension5) {
            ITextViewerExtension5 extension= (ITextViewerExtension5) viewer;
            return extension.modelOffset2WidgetOffset(modelOffset);
        }
        return modelOffset - viewer.getVisibleRegion().getOffset();
    }
    
}
