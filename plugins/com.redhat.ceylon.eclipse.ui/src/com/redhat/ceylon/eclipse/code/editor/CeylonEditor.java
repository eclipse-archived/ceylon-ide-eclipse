/*******************************************************************************
* Copyright (c) 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/

package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.CORRECT_INDENTATION;
import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.FORMAT;
import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.GOTO_MATCHING_FENCE;
import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.GOTO_NEXT_TARGET;
import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.GOTO_PREVIOUS_TARGET;
import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.SELECT_ENCLOSING;
import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.SHOW_OUTLINE;
import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.TOGGLE_COMMENT;
import static com.redhat.ceylon.eclipse.code.editor.EditorInputUtils.getFile;
import static com.redhat.ceylon.eclipse.code.editor.EditorInputUtils.getPath;
import static com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage.TYPE_ANALYSIS;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static java.util.ResourceBundle.getBundle;
import static org.eclipse.core.resources.IResourceChangeEvent.POST_BUILD;
import static org.eclipse.core.resources.IncrementalProjectBuilder.CLEAN_BUILD;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static org.eclipse.jface.text.IDocument.DEFAULT_CONTENT_TYPE;
import static org.eclipse.ui.texteditor.ITextEditorActionConstants.GROUP_RULERS;
import static org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS;
import static org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds.DELETE_NEXT_WORD;
import static org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds.DELETE_PREVIOUS_WORD;
import static org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds.SELECT_WORD_NEXT;
import static org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds.SELECT_WORD_PREVIOUS;
import static org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds.WORD_NEXT;
import static org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds.WORD_PREVIOUS;
import static org.eclipse.ui.texteditor.spelling.SpellingService.PREFERENCE_SPELLING_ENABLED;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.debug.ui.actions.ToggleBreakpointAction;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.jdt.internal.ui.viewsupport.IProblemChangedListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IEditorStatusLine;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.texteditor.TextNavigationAction;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.redhat.ceylon.eclipse.code.hover.HoverHelpController;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.outline.CeylonOutlineBuilder;
import com.redhat.ceylon.eclipse.code.outline.CeylonOutlinePage;
import com.redhat.ceylon.eclipse.code.parse.CeylonLanguageSyntaxProperties;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.CeylonParserScheduler;
import com.redhat.ceylon.eclipse.code.parse.IAnnotationTypeInfo;
import com.redhat.ceylon.eclipse.code.parse.MessageHandler;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

/**
 * An Eclipse editor, which is not enhanced using API; rather, we publish extension
 * points for outline, content assist, hover help, etc.
 * 
 * @author Chris Laffra
 * @author Robert M. Fuhrer
 */
public class CeylonEditor extends TextEditor {
	
    private static final String SHOW_CEYLON_HIERARCHY = "com.redhat.ceylon.eclipse.ui.action.hierarchy";
	public static final String MESSAGE_BUNDLE= "org.eclipse.imp.editor.messages";
    public static final String PARSE_ANNOTATION_TYPE= "org.eclipse.imp.editor.parseAnnotation";

    private static final int REPARSE_SCHEDULE_DELAY= 200;

    /**
     * Annotation ID for a parser annotation w/ severity = error. Must match the ID of the
     * corresponding annotationTypes extension in the plugin.xml.
     */
    public static final String PARSE_ANNOTATION_TYPE_ERROR= "org.eclipse.imp.editor.parseAnnotation.error";

    /**
     * Annotation ID for a parser annotation w/ severity = warning. Must match the ID of the
     * corresponding annotationTypes extension in the plugin.xml.
     */
    public static final String PARSE_ANNOTATION_TYPE_WARNING= "org.eclipse.imp.editor.parseAnnotation.warning";

    /**
     * Annotation ID for a parser annotation w/ severity = info. Must match the ID of the
     * corresponding annotationTypes extension in the plugin.xml.
     */
    public static final String PARSE_ANNOTATION_TYPE_INFO= "org.eclipse.imp.editor.parseAnnotation.info";

    /** Preference key for matching brackets */
    protected final static String MATCHING_BRACKETS= "matchingBrackets";

    /** Preference key for matching brackets color */
    protected final static String MATCHING_BRACKETS_COLOR= "matchingBracketsColor";

    private CeylonParserScheduler parserScheduler;
    private IDocumentProvider zipDocProvider;
    private ProblemMarkerManager problemMarkerManager;
    private ICharacterPairMatcher bracketMatcher;
    //private SubActionBars fActionBars;
    //private DefaultPartListener fRefreshContributions;
    private ToggleBreakpointAction toggleBreakpointAction;
    private IAction enableDisableBreakpointAction;
    private IResourceChangeListener buildListener;
    private IResourceChangeListener moveListener;
    private IDocumentListener documentListener;
    private FoldingActionGroup foldingActionGroup;
    private SourceArchiveDocumentProvider sourceArchiveDocumentProvider;
    private ToggleBreakpointAdapter toggleBreakpointTarget;
    private CeylonOutlinePage outlinePage;
    
    //public static ResourceBundle fgBundleForConstructedKeys= getBundle(MESSAGE_BUNDLE);
    
    //public static final String IMP_CODING_ACTION_SET = RuntimePlugin.IMP_RUNTIME + ".codingActionSet";
    //public static final String IMP_OPEN_ACTION_SET = RuntimePlugin.IMP_RUNTIME + ".openActionSet";

    public CeylonEditor() {
        // SMS 4 Apr 2007
        // Do not set preference store with store obtained from plugin; one is
        // already defined for the parent text editor and populated with relevant
        // preferences
        // setPreferenceStore(CeylonPlugin.getInstance().getPreferenceStore());
        setSourceViewerConfiguration(createSourceViewerConfiguration());
        configureInsertMode(SMART_INSERT, true);
        setInsertMode(SMART_INSERT);
        problemMarkerManager= new ProblemMarkerManager();
	}

    /**
     * Sub-classes may override this method to extend the behavior provided by IMP's
     * standard StructuredSourceViewerConfiguration.
     * @return the StructuredSourceViewerConfiguration to use with this editor
     */
    protected CeylonSourceViewerConfiguration createSourceViewerConfiguration() {
    	return new CeylonSourceViewerConfiguration(getPreferenceStore(), this);
    }

    public IPreferenceStore getPrefStore() {
    	return super.getPreferenceStore();
    }

    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class required) {
        if (IContentOutlinePage.class.equals(required)) {
            return getOutlinePage();
        }
        if (IToggleBreakpointsTarget.class.equals(required)) {
            return getToggleBreakpointAdapter();
        }
        /*if (IContextProvider.class.equals(required)) {
            return IMPHelp.getHelpContextProvider(this, fLanguageServiceManager, IMP_EDITOR_CONTEXT);
        }*/
        return super.getAdapter(required);
    }

	public Object getToggleBreakpointAdapter() {
		if (toggleBreakpointTarget == null) {
		    toggleBreakpointTarget = new ToggleBreakpointAdapter();
		}
		return toggleBreakpointTarget;
	}

	public Object getOutlinePage() {
		if (outlinePage == null) {
		    outlinePage = new CeylonOutlinePage(getParseController(),
		            new CeylonOutlineBuilder());
		    parserScheduler.addModelListener(outlinePage);
		    //myOutlinePage.update(parseController);
		 }
		 return outlinePage;
	}

    protected void createActions() {
        super.createActions();

        final ResourceBundle bundle= getBundle(MESSAGE_BUNDLE);
        
        Action action= new ContentAssistAction(bundle, "ContentAssistProposal.", this);
        action.setActionDefinitionId(CONTENT_ASSIST_PROPOSALS);
        setAction("ContentAssistProposal", action);
        markAsStateDependentAction("ContentAssistProposal", true);

        toggleBreakpointAction= new ToggleBreakpointAction(this, 
        		getDocumentProvider().getDocument(getEditorInput()), 
        		getVerticalRuler());
        setAction("ToggleBreakpoint", action);
        
        enableDisableBreakpointAction= new RulerEnableDisableBreakpointAction(this, 
        		getVerticalRuler());
        setAction("ToggleBreakpoint", action);

        action= new TextOperationAction(bundle, "Format.", this, 
        		CeylonSourceViewer.FORMAT);
        action.setActionDefinitionId(FORMAT);
        setAction("Format", action);
        markAsStateDependentAction("Format", true);
        markAsSelectionDependentAction("Format", true);
//      PlatformUI.getWorkbench().getHelpSystem().setHelp(action, IJavaHelpContextIds.FORMAT_ACTION);

        action= new TextOperationAction(bundle, "ShowOutline.", this, 
        		CeylonSourceViewer.SHOW_OUTLINE, true /* runsOnReadOnly */);
        action.setActionDefinitionId(SHOW_OUTLINE);
        setAction(SHOW_OUTLINE, action);
//      PlatformUI.getWorkbench().getHelpSystem().setHelp(action, IJavaHelpContextIds.SHOW_OUTLINE_ACTION);

        action= new TextOperationAction(bundle, "ToggleComment.", this, 
        		CeylonSourceViewer.TOGGLE_COMMENT);
        action.setActionDefinitionId(TOGGLE_COMMENT);
        setAction(TOGGLE_COMMENT, action);
//      PlatformUI.getWorkbench().getHelpSystem().setHelp(action, IJavaHelpContextIds.TOGGLE_COMMENT_ACTION);

        action= new TextOperationAction(bundle, "CorrectIndentation.", this, 
        		CeylonSourceViewer.CORRECT_INDENTATION);
        action.setActionDefinitionId(CORRECT_INDENTATION);
        setAction(CORRECT_INDENTATION, action);

        action= new GotoMatchingFenceAction(this);
        action.setActionDefinitionId(GOTO_MATCHING_FENCE);
        setAction(GOTO_MATCHING_FENCE, action);

        action= new GotoPreviousTargetAction(this);
        action.setActionDefinitionId(GOTO_PREVIOUS_TARGET);
        setAction(GOTO_PREVIOUS_TARGET, action);

        action= new GotoNextTargetAction(this);
        action.setActionDefinitionId(GOTO_NEXT_TARGET);
        setAction(GOTO_NEXT_TARGET, action);

        action= new SelectEnclosingAction(this);
        action.setActionDefinitionId(SELECT_ENCLOSING);
        setAction(SELECT_ENCLOSING, action);

    	action= new TextOperationAction(bundle, "ShowHierarchy.", this, 
    			CeylonSourceViewer.SHOW_HIERARCHY, true);
        action.setActionDefinitionId(SHOW_CEYLON_HIERARCHY);
        setAction(SHOW_CEYLON_HIERARCHY, action);

        foldingActionGroup= new FoldingActionGroup(this, this.getSourceViewer());
        
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

        setAction(WORD_PREVIOUS, new NavigatePreviousSubWordAction());
        textWidget.setKeyBinding(SWT.CTRL | SWT.ARROW_LEFT, SWT.NULL);

        setAction(WORD_NEXT, new NavigateNextSubWordAction());
        textWidget.setKeyBinding(SWT.CTRL | SWT.ARROW_RIGHT, SWT.NULL);

        setAction(SELECT_WORD_PREVIOUS, new SelectPreviousSubWordAction());
        textWidget.setKeyBinding(SWT.CTRL | SWT.SHIFT | SWT.ARROW_LEFT, SWT.NULL);

        setAction(SELECT_WORD_NEXT, new SelectNextSubWordAction());
        textWidget.setKeyBinding(SWT.CTRL | SWT.SHIFT | SWT.ARROW_RIGHT, SWT.NULL);

        setAction(DELETE_PREVIOUS_WORD, new DeletePreviousSubWordAction());
        textWidget.setKeyBinding(SWT.CTRL | SWT.BS, SWT.NULL);
        markAsStateDependentAction(DELETE_PREVIOUS_WORD, true);

        setAction(DELETE_NEXT_WORD, new DeleteNextSubWordAction());
        textWidget.setKeyBinding(SWT.CTRL | SWT.DEL, SWT.NULL);
        markAsStateDependentAction(DELETE_NEXT_WORD, true);
    }

    protected class NavigateNextSubWordAction 
    extends NextSubWordAction {
    	public NavigateNextSubWordAction() {
    		super(ST.WORD_NEXT);
    		setActionDefinitionId(WORD_NEXT);
    	}
    	@Override
    	protected void setCaretPosition(final int position) {
    		getTextWidget().setCaretOffset(modelOffset2WidgetOffset(getSourceViewer(), position));
    	}
    }

    protected class NavigatePreviousSubWordAction 
    extends PreviousSubWordAction {
    	public NavigatePreviousSubWordAction() {
    		super(ST.WORD_PREVIOUS);
    		setActionDefinitionId(WORD_PREVIOUS);
    	}
    	@Override
    	protected void setCaretPosition(final int position) {
    		getTextWidget().setCaretOffset(modelOffset2WidgetOffset(getSourceViewer(), position));
    	}
    }

    protected abstract class NextSubWordAction 
    extends TextNavigationAction {

    	protected CeylonWordIterator fIterator= new CeylonWordIterator();

    	/**
    	 * Creates a new next sub-word action.
    	 *
    	 * @param code Action code for the default operation. Must be an action code from @see org.eclipse.swt.custom.ST.
    	 */
    	protected NextSubWordAction(int code) {
    		super(getSourceViewer().getTextWidget(), code);
    	}

    	@Override
    	public void run() {
    		// Check whether we are in a java code partition and the preference is enabled

    		final ISourceViewer viewer= getSourceViewer();
    		final IDocument document= viewer.getDocument();
    		try {
    			fIterator.setText((CharacterIterator)new DocumentCharacterIterator(document));
    			int position= widgetOffset2ModelOffset(viewer, 
    					viewer.getTextWidget().getCaretOffset());
    			if (position == -1)
    				return;

    			int next= findNextPosition(position);
    			if (next != BreakIterator.DONE) {
    				setCaretPosition(next);
    				getTextWidget().showSelection();
    				fireSelectionChanged();
    			}
    		} 
    		catch (BadLocationException x) {
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
    	 * Sets the caret position to the sub-word boundary given with 
    	 * <code>position</code>.
    	 *
    	 * @param position Position where the action should move the caret
    	 */
    	protected abstract void setCaretPosition(int position);
    }

    protected abstract class PreviousSubWordAction 
    extends TextNavigationAction {

    	protected CeylonWordIterator fIterator= new CeylonWordIterator();

    	/**
    	 * Creates a new previous sub-word action.
    	 *
    	 * @param code Action code for the default operation. Must be an 
    	 * action code from @see org.eclipse.swt.custom.ST.
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
    			int position= widgetOffset2ModelOffset(viewer, 
    					viewer.getTextWidget().getCaretOffset());
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

protected class SelectNextSubWordAction 
extends NextSubWordAction {
	public SelectNextSubWordAction() {
		super(ST.SELECT_WORD_NEXT);
		setActionDefinitionId(SELECT_WORD_NEXT);
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

protected class SelectPreviousSubWordAction 
extends PreviousSubWordAction {
	public SelectPreviousSubWordAction() {
		super(ST.SELECT_WORD_PREVIOUS);
		setActionDefinitionId(SELECT_WORD_PREVIOUS);
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

protected class DeleteNextSubWordAction 
extends NextSubWordAction implements IUpdate {
	public DeleteNextSubWordAction() {
		super(ST.DELETE_WORD_NEXT);
		setActionDefinitionId(DELETE_NEXT_WORD);
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
			viewer.getDocument().replace(caret, length, "");
		} catch (BadLocationException exception) {
			// Should not happen
		}
	}
	public void update() {
		setEnabled(isEditorInputModifiable());
	}
}

protected class DeletePreviousSubWordAction 
extends PreviousSubWordAction implements IUpdate {
	public DeletePreviousSubWordAction() {
		super(ST.DELETE_WORD_PREVIOUS);
		setActionDefinitionId(DELETE_PREVIOUS_WORD);
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
			viewer.getDocument().replace(position, length, "");
		} catch (BadLocationException exception) {
			// Should not happen
		}
	}
	public void update() {
		setEnabled(isEditorInputModifiable());
	}
}

    protected void initializeKeyBindingScopes() {
        setKeyBindingScopes(new String[] { PLUGIN_ID + ".context" });
    }

    //private QuickMenuAction fQuickAccessAction;
    //private IHandlerActivation fQuickAccessHandlerActivation;
    //private IHandlerService fHandlerService;

    //private static final String QUICK_MENU_ID= "org.eclipse.imp.runtime.editor.refactor.quickMenu";

    /*private class RefactorQuickAccessAction extends QuickMenuAction {
        public RefactorQuickAccessAction() {
            super(QUICK_MENU_ID);
        }
        protected void fillMenu(IMenuManager menu) {
            //contributeRefactoringActions(menu);
        }
    }*/

    /*private void installQuickAccessAction() {
        fHandlerService= (IHandlerService) getSite().getService(IHandlerService.class);
        if (fHandlerService != null) {
            fQuickAccessAction= new RefactorQuickAccessAction();
            fQuickAccessHandlerActivation= fHandlerService.activateHandler(fQuickAccessAction.getActionDefinitionId(), 
            		new ActionHandler(fQuickAccessAction));
        }
    }*/


    protected boolean isOverviewRulerVisible() {
        return true;
    }

    protected void rulerContextMenuAboutToShow(IMenuManager menu) {
        addDebugActions(menu);

        super.rulerContextMenuAboutToShow(menu);

        IMenuManager foldingMenu= new MenuManager("Folding", "projection");

        menu.appendToGroup(GROUP_RULERS, foldingMenu);

        IAction action;
//      action= getAction("FoldingToggle");
//      foldingMenu.add(action);
        action= getAction("FoldingExpandAll");
        foldingMenu.add(action);
        action= getAction("FoldingCollapseAll");
        foldingMenu.add(action);
        action= getAction("FoldingRestore");
        foldingMenu.add(action);
        action= getAction("FoldingCollapseMembers");
        foldingMenu.add(action);
        action= getAction("FoldingCollapseComments");
        foldingMenu.add(action);
    }

    private void addDebugActions(IMenuManager menu) {
        menu.add(toggleBreakpointAction);
        menu.add(enableDisableBreakpointAction);
    }

    /**
     * Sets the given message as error message to this editor's status line.
     *
     * @param msg message to be set
     */
    protected void setStatusLineErrorMessage(String msg) {
        IEditorStatusLine statusLine= (IEditorStatusLine) getAdapter(IEditorStatusLine.class);
        if (statusLine != null)
            statusLine.setMessage(true, msg, null);
    }

    /**
     * Sets the given message as message to this editor's status line.
     *
     * @param msg message to be set
     * @since 3.0
     */
    protected void setStatusLineMessage(String msg) {
        IEditorStatusLine statusLine= (IEditorStatusLine) getAdapter(IEditorStatusLine.class);
        if (statusLine != null)
            statusLine.setMessage(false, msg, null);
    }

    public ProblemMarkerManager getProblemMarkerManager() {
        return problemMarkerManager;
    }

    public void updatedTitleImage(Image image) {
        setTitleImage(image);
    }

    /**
     * Jumps to the next enabled annotation according to the given direction.
     * An annotation type is enabled if it is configured to be in the
     * Next/Previous tool bar drop down menu and if it is checked.
     *
     * @param forward <code>true</code> if search direction is forward, <code>false</code> if backward
     */
    public Annotation gotoAnnotation(boolean forward) {
        ITextSelection selection= (ITextSelection) getSelectionProvider().getSelection();
        Position position= new Position(0, 0);
        Annotation annotation= getNextAnnotation(selection.getOffset(), 
        		selection.getLength(), forward, position);
        setStatusLineErrorMessage(null);
        setStatusLineMessage(null);
        if (annotation != null) {
        	updateAnnotationViews(annotation);
        	selectAndReveal(position.getOffset(), position.getLength());
        	setStatusLineMessage(annotation.getText());
        }
        return annotation;
    }
    
    
    /**
     * Returns the annotation closest to the given range respecting the given
     * direction. If an annotation is found, the annotations current position
     * is copied into the provided annotation position.
     *
     * @param offset the region offset	
     * @param length the region length
     * @param forward <code>true</code> for forwards, <code>false</code> for backward
     * @param annotationPosition the position of the found annotation
     * @return the found annotation
     */
    private Annotation getNextAnnotation(final int offset, final int length, boolean forward, 
    		Position annotationPosition) {
    	
        Annotation nextAnnotation= null;
        Position nextAnnotationPosition= null;
        Annotation containingAnnotation= null;
        Position containingAnnotationPosition= null;
        boolean currentAnnotation= false;

        IDocument document= getDocumentProvider().getDocument(getEditorInput());
        int endOfDocument= document.getLength();
        int distance= Integer.MAX_VALUE;

        IAnnotationModel model= getDocumentProvider().getAnnotationModel(getEditorInput());
        for (Iterator iter= model.getAnnotationIterator(); iter.hasNext();) {
            Annotation a= (Annotation) iter.next();
            if (!(a instanceof MarkerAnnotation) && 
            		!isParseAnnotation(a))
                continue;

            Position p= model.getPosition(a);
            if (p == null)
                continue;

            if (forward && p.offset==offset || 
            		!forward && p.offset+p.getLength()==offset+length) {// || p.includes(offset)) {
                if (containingAnnotation == null
                        || (forward && p.length>=containingAnnotationPosition.length || 
                            !forward && p.length>=containingAnnotationPosition.length)) {
                    containingAnnotation= a;
                    containingAnnotationPosition= p;
                    currentAnnotation= p.length==length;
                }
            } 
            else {
                int currentDistance= forward ? p.getOffset()-offset : 
                	offset+length-p.getOffset()-p.length;

                if (currentDistance<0)
                    currentDistance= endOfDocument + currentDistance;

                if (currentDistance<distance || 
                		currentDistance==distance && 
                		    p.length<nextAnnotationPosition.length) {
                    distance= currentDistance;
                    nextAnnotation= a;
                    nextAnnotationPosition= p;
                }
            }
        }
        if (containingAnnotationPosition!=null && 
        		(!currentAnnotation || nextAnnotation==null)) {
            annotationPosition.setOffset(containingAnnotationPosition.getOffset());
            annotationPosition.setLength(containingAnnotationPosition.getLength());
            return containingAnnotation;
        }
        if (nextAnnotationPosition!=null) {
            annotationPosition.setOffset(nextAnnotationPosition.getOffset());
            annotationPosition.setLength(nextAnnotationPosition.getLength());
        }

        return nextAnnotation;
    }

    /**
     * Updates the annotation views that show the given annotation.
     *
     * @param annotation the annotation
     */
    private void updateAnnotationViews(Annotation annotation) {
        /*IMarker marker= null;
        if (annotation instanceof MarkerAnnotation)
            marker= ((MarkerAnnotation) annotation).getMarker();
        else if (marker != null) {
            try {
                boolean isProblem= marker.isSubtypeOf(IMarker.PROBLEM);
                IWorkbenchPage page= getSite().getPage();
                IViewPart view= page.findView(isProblem ? IPageLayout.ID_PROBLEM_VIEW : IPageLayout.ID_TASK_LIST);
                if (view != null) {
                    Method method= view.getClass().getMethod("setSelection", new Class[] { IStructuredSelection.class, boolean.class });
                    method.invoke(view, new Object[] { new StructuredSelection(marker), Boolean.TRUE });
                }
            } catch (CoreException x) {
            } catch (NoSuchMethodException x) {
            } catch (IllegalAccessException x) {
            } catch (InvocationTargetException x) {
            }
            // ignore exceptions, don't update any of the lists, just set status line
        }*/
    }

    @Override
    public IDocumentProvider getDocumentProvider() {
        if (SourceArchiveDocumentProvider.canHandle(getEditorInput())) {
            if (sourceArchiveDocumentProvider == null) {
                sourceArchiveDocumentProvider= new SourceArchiveDocumentProvider();
            }
            return sourceArchiveDocumentProvider;
        }
        IEditorInput editorInput= getEditorInput();
        if (ZipStorageEditorDocumentProvider.canHandle(editorInput)) {
            if (zipDocProvider == null) {
                zipDocProvider= new ZipStorageEditorDocumentProvider();
            }
            return zipDocProvider;
        }
    	return super.getDocumentProvider();
    }

    public void createPartControl(Composite parent) {
        
    	// Initialize the parse controller first, since the 
    	// initialization of other things (like the context 
    	// help support) might depend on it.
        initializeParseController();

        // Not sure why the "run the spell checker" pref would 
        // get set, but it does seem to, which gives lots of 
        // annoying squigglies all over the place...
        getPreferenceStore().setValue(PREFERENCE_SPELLING_ENABLED, false);

        super.createPartControl(parent);

        initiateServiceControllers();

        setTitleImageFromLanguageIcon();
        setSourceFontFromPreference();
        setupBracketCloser();
        
        /*((IContextService) getSite().getService(IContextService.class))
                .activateContext(PLUGIN_ID + ".context");*/
        
        //CeylonPlugin.getInstance().getPreferenceStore().addPropertyChangeListener(colorChangeListener);
        currentTheme.getColorRegistry().addListener(colorChangeListener);
        updateFontAndCaret();
        currentTheme.getFontRegistry().addListener(fontChangeListener);
    }

    private void watchForSourceBuild() {        
        getWorkspace().addResourceChangeListener(buildListener= new IResourceChangeListener() {
            public void resourceChanged(IResourceChangeEvent event) {
                if (event.getType()==POST_BUILD && event.getBuildKind()!=CLEAN_BUILD) {
                	scheduleParsing();
                }
            }
        }, IResourceChangeEvent.POST_BUILD);
    }

    public void scheduleParsing() {
    	CeylonParserScheduler scheduler = parserScheduler;
    	if (scheduler!=null) {
    		scheduler.cancel();
    		scheduler.schedule(0);
    	}
    }

    CeylonParseController parseController;
    private PresentationController presentationController;
    
    private void initializeParseController() {
        IEditorInput editorInput= getEditorInput();
        IFile file = getFile(editorInput);
        IPath filePath = getPath(editorInput);
        try {
        	parseController = new CeylonParseController();
            IProject project= file!=null && file.exists() ? file.getProject() : null;
            ISourceProject srcProject= project!=null ? ModelFactory.open(project) : null;
            parseController.initialize(filePath, srcProject, annotationCreator);
        } 
        catch (ModelException e) {
            e.printStackTrace();
        }
    }

    private void watchDocument() {
        getSourceViewer().getDocument()
                .addDocumentListener(documentListener= new IDocumentListener() {
            public void documentAboutToBeChanged(DocumentEvent event) {}
            public void documentChanged(DocumentEvent event) {
                parserScheduler.cancel();
                parserScheduler.schedule(REPARSE_SCHEDULE_DELAY);
            }
        });
    }

    /*private class BracketInserter implements VerifyKeyListener {
        private final Map<String,String> fFencePairs= new HashMap<String, String>();
        private final String fOpenFences;
        private final Map<Character,Boolean> fCloseFenceMap= new HashMap<Character, Boolean>();
//      private final String CATEGORY= toString();
//      private IPositionUpdater fUpdater= new ExclusivePositionUpdater(CATEGORY);

        public BracketInserter() {
            String[][] pairs= fLanguageServiceManager.getParseController().getSyntaxProperties().getFences();
            StringBuilder sb= new StringBuilder();
            for(int i= 0; i < pairs.length; i++) {
                sb.append(pairs[i][0]);
                fFencePairs.put(pairs[i][0], pairs[i][1]);
            }
            fOpenFences= sb.toString();
        }

        public void setCloseFenceEnabled(char openingFence, boolean enabled) {
            fCloseFenceMap.put(openingFence, enabled);
        }

        public void setCloseFencesEnabled(boolean enabled) {
            for(int i= 0; i < fOpenFences.length(); i++) {
                fCloseFenceMap.put(fOpenFences.charAt(i), enabled);
            }
        }

        public void verifyKey(VerifyEvent event) {
            // early pruning to slow down normal typing as little as possible
            if (!event.doit || getInsertMode() != SMART_INSERT)
                return;

            if (fOpenFences.indexOf(event.character) < 0) {
                return;
            }

            final ISourceViewer sourceViewer= getSourceViewer();
            IDocument document= sourceViewer.getDocument();

            final Point selection= sourceViewer.getSelectedRange();
            final int offset= selection.x;
            final int length= selection.y;

            try {
//              IRegion startLine= document.getLineInformationOfOffset(offset);
//              IRegion endLine= document.getLineInformationOfOffset(offset + length);

                // TODO Ask the parser/scanner whether the close fence is valid here
                // (i.e. whether it would recover by inserting the matching close fence character)
                // Right now, naively insert the closing fence regardless.

                ITypedRegion partition= TextUtilities.getPartition(document, getSourceViewerConfiguration().getConfiguredDocumentPartitioning(sourceViewer), offset, true);
                if (!IDocument.DEFAULT_CONTENT_TYPE.equals(partition.getType()))
                    return;

                if (!validateEditorInputState())
                    return;

                final String inputStr= new String(new char[] { event.character });
                final String closingFence= fFencePairs.get(inputStr);
                final StringBuffer buffer= new StringBuffer();
                buffer.append(inputStr);
                buffer.append(closingFence);

                document.replace(offset, length, buffer.toString());
                sourceViewer.setSelectedRange(offset + inputStr.length(), 0);

                event.doit= false;
            } 
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }*/

    //private BracketInserter fBracketInserter;
    //private final String CLOSE_FENCES= PreferenceConstants.EDITOR_CLOSE_FENCES;

    private void setupBracketCloser() {
        // Bug #536: Disable for now, until we can be more intelligent 
        //about when to overwrite an existing (subsequent) close-fence char.
        /*IParseController parseController = fLanguageServiceManager.getParseController();
        if (parseController == null || parseController.getSyntaxProperties() == null || 
        		parseController.getSyntaxProperties().getFences() == null) {
            return;
        }

        //Preference key for automatically closing brackets and parenthesis 
        boolean closeFences= fLangSpecificPrefs.getBooleanPreference(CLOSE_FENCES); // false if no lang-specific setting

        fBracketInserter= new BracketInserter();
        fBracketInserter.setCloseFencesEnabled(closeFences);

        ISourceViewer sourceViewer= getSourceViewer();
        if (sourceViewer instanceof ITextViewerExtension)
            ((ITextViewerExtension) sourceViewer).prependVerifyKeyListener(fBracketInserter);*/
    }

    /**
     * The following listener is intended to detect when the document associated
     * with this editor changes its identity, which happens when, e.g., the
     * underlying resource gets moved or renamed.
     */
    private IPropertyListener fEditorInputPropertyListener = new IPropertyListener() {
        public void propertyChanged(Object source, int propId) {
            if (source == CeylonEditor.this && propId == IEditorPart.PROP_INPUT) {
                IDocument oldDoc= getParseController().getDocument();
                IDocument curDoc= getDocumentProvider().getDocument(getEditorInput()); 
                if (curDoc!=oldDoc) {
                    // Need to unwatch the old document and watch the new document
                    oldDoc.removeDocumentListener(documentListener);
                    curDoc.addDocumentListener(documentListener);
                }
            }
        }
    };

    private void watchForSourceMove() {
        // We need to see when the editor input changes, so we can watch the new document
        addPropertyListener(fEditorInputPropertyListener);
        getWorkspace().addResourceChangeListener(moveListener= new IResourceChangeListener() {
        	public void resourceChanged(IResourceChangeEvent event) {
        		if (event.getType()==IResourceChangeEvent.POST_CHANGE) {
        			ISourceProject project = parseController.getProject();
        			if (project!=null) { //things extrenal to the workspace don't move
        				IPath oldWSRelPath= project.getRawProject().getFullPath().append(parseController.getPath());
        				IResourceDelta rd= event.getDelta().findMember(oldWSRelPath);
        				if (rd != null) {
        					if ((rd.getFlags() & IResourceDelta.MOVED_TO) == IResourceDelta.MOVED_TO) {
        						// The net effect of the following is to re-initialize() the IParseController with the new path
        						IPath newPath= rd.getMovedToPath();
        						IPath newProjRelPath= newPath.removeFirstSegments(1);
        						String newProjName= newPath.segment(0);
        						boolean sameProj= project.getRawProject()
        								.getName().equals(newProjName);

        						try {
        							ISourceProject proj= sameProj ? project : 
        								ModelFactory.open(ResourcesPlugin.getWorkspace().getRoot()
        										.getProject(newProjName));
        							// Tell the IParseController about the move - it caches the path
        							// fParserScheduler.cancel(); // avoid a race condition if ParserScheduler was starting/in the middle of a run
        							parseController.initialize(newProjRelPath, proj, annotationCreator);
        						} 
        						catch (ModelException e) {
        							e.printStackTrace();
        						}
        					}
        				}
        			}
        		}
            }
        });
    }

    private void setSourceFontFromPreference() {
        String fontName = WorkbenchPlugin.getDefault().getPreferenceStore()
        		.getString(JFaceResources.TEXT_FONT);
        FontRegistry fontRegistry= CeylonPlugin.getInstance().getFontRegistry();
        if (!fontRegistry.hasValueFor(fontName)) {
            fontRegistry.put(fontName, PreferenceConverter.readFontData(fontName));
        }
        Font sourceFont= fontRegistry.get(fontName);
        if (sourceFont!=null) {
            getSourceViewer().getTextWidget().setFont(sourceFont);
        }
    }

    private void initiateServiceControllers() {
        try {
            final CeylonSourceViewer sourceViewer = (CeylonSourceViewer) getSourceViewer();
                        
            annotationUpdater= new IProblemChangedListener() {
                public void problemsChanged(IResource[] changedResources, 
                		boolean isMarkerChange) {
                    // Remove annotations that were resolved by changes to 
                	// other resources. 
                	// TODO: It would be better to match the markers to the 
                	// annotations, and decide which annotations to remove.
                    scheduleParsing();
                }
            };
            problemMarkerManager.addListener(annotationUpdater);
            
            editorIconUpdater= new EditorIconUpdater(this);
            problemMarkerManager.addListener(editorIconUpdater);

			parserScheduler= new CeylonParserScheduler(parseController, this, 
					getDocumentProvider(), annotationCreator);

            //add this guy first in the list of model listeners so he
            //gets notified first out of everyone
            presentationController = new PresentationController(getSourceViewer());
            presentationController.damage(new Region(0, sourceViewer.getDocument().getLength()));
            addModelListener(presentationController);
            
            addModelListener(new EditorAnnotationService(this));

            // The source viewer configuration has already been asked for its ITextHover,
            // but before we actually instantiated the relevant controller class. So update
            // the source viewer, now that we actually have the hover provider.
            HoverHelpController hover = new HoverHelpController(this);
			sourceViewer.setTextHover(hover, DEFAULT_CONTENT_TYPE);
            addModelListener(hover);

            new ProjectionSupport(sourceViewer, getAnnotationAccess(), getSharedColors()).install();
            sourceViewer.doOperation(ProjectionViewer.TOGGLE);
            ProjectionAnnotationModel projectionAnnotationModel = sourceViewer.getProjectionAnnotationModel();
            if (projectionAnnotationModel!=null) {
            	addModelListener(new FoldingController(projectionAnnotationModel));
            }

            if (isEditable()) {
                addModelListener(new AnnotationCreatorListener());
            }

            watchDocument();
            watchForSourceMove();
            watchForSourceBuild();
            
            parserScheduler.schedule();
            
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setTitleImageFromLanguageIcon() {
    	IEditorInput editorInput= getEditorInput();
    	Object fileOrPath= getFile(editorInput);
    	if (fileOrPath==null) {
    		fileOrPath = getParseController().getPath();
    	}
    	try {
    		setTitleImage(new CeylonLabelProvider().getImage(fileOrPath));
    	} 
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
	/**
	 * Makes sure that menu items and status bar items disappear as the editor
	 * is out of focus, and reappear when it gets focus again. This does
	 * not work for toolbar items for unknown reasons, they stay visible.
	 *
	 */
	/*private void registerEditorContributionsActivator() {
		fRefreshContributions = new DefaultPartListener() {
			private UniversalEditor editor = UniversalEditor.this;

			@Override
			public void partActivated(IWorkbenchPart part) {
				if (part == editor) {
					editor.fActionBars.activate();
					editor.fActionBars.updateActionBars();
				}

				if (part instanceof UniversalEditor) {
					part.getSite().getPage().showActionSet(IMP_CODING_ACTION_SET);
					part.getSite().getPage().showActionSet(IMP_OPEN_ACTION_SET);
				} else {
					part.getSite().getPage().hideActionSet(IMP_CODING_ACTION_SET);
					part.getSite().getPage().hideActionSet(IMP_OPEN_ACTION_SET);
				}
			}

			@Override
			public void partDeactivated(IWorkbenchPart part) {
				if (part == editor) {
					editor.fActionBars.deactivate();
					editor.fActionBars.updateActionBars();
				}
			}
		};
		getSite().getPage().addPartListener(fRefreshContributions);
	}*/
	
    public void dispose() {
        if (editorIconUpdater!=null) {
        	editorIconUpdater.dispose();
        	editorIconUpdater = null;
        }
        if (annotationUpdater!=null) {
            problemMarkerManager.removeListener(annotationUpdater);
            annotationUpdater = null;
        }
        
        /*if (fActionBars!=null) {
          fActionBars.dispose();
          fActionBars = null;
        }*/

        /*if (fDocumentListener!=null) {
        	getSourceViewer().getDocument()
        	    .removeDocumentListener(fDocumentListener);
        }*/
        
        if (buildListener!=null) {
        	getWorkspace().removeResourceChangeListener(buildListener);
        	buildListener = null;
        }
        if (moveListener!=null) {
        	getWorkspace().removeResourceChangeListener(moveListener);
        	moveListener = null;
        }
        
        toggleBreakpointAction.dispose(); // this holds onto the IDocument
        foldingActionGroup.dispose();

        if (parserScheduler!=null) {
        	parserScheduler.cancel(); // avoid unnecessary work after the editor is asked to close down
        }
        parserScheduler= null;
        parseController = null;

        super.dispose();

        /*if (fResourceListener != null) {
        	ResourcesPlugin.getWorkspace().removeResourceChangeListener(fResourceListener);
        }*/
        //CeylonPlugin.getInstance().getPreferenceStore().removePropertyChangeListener(colorChangeListener);
        currentTheme.getColorRegistry().removeListener(colorChangeListener);
        currentTheme.getFontRegistry().removeListener(fontChangeListener);
    }

    private static final String TEXT_FONT_PREFERENCE = PLUGIN_ID + ".editorFont";

    private void updateFontAndCaret() {
        Font font = currentTheme.getFontRegistry().get(TEXT_FONT_PREFERENCE);
        getSourceViewer().getTextWidget().setFont(font);
        try {
            Method updateCaretMethod = AbstractTextEditor.class.getDeclaredMethod("updateCaret");
            updateCaretMethod.setAccessible(true);
            updateCaretMethod.invoke(this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private IPropertyChangeListener colorChangeListener = new IPropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getProperty().startsWith(PLUGIN_ID + ".theme.color.")) {
                getSourceViewer().invalidateTextPresentation();
            }
        }
    };
    
    IPropertyChangeListener fontChangeListener = new IPropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getProperty().equals(TEXT_FONT_PREFERENCE)) {
                updateFontAndCaret();
            }
        }
    };
    private final ITheme currentTheme = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme();
    
        
    /**
     * Override creation of the normal source viewer with one that supports source folding.
     */
    protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
        //	if (fFoldingUpdater == null)
        //	    return super.createSourceViewer(parent, ruler, styles);

        fAnnotationAccess= createAnnotationAccess();
        fOverviewRuler= createOverviewRuler(getSharedColors());

        ISourceViewer viewer= new CeylonSourceViewer(parent, ruler, 
        		getOverviewRuler(), isOverviewRulerVisible(), styles);
        // ensure decoration support has been created and configured.
        getSourceViewerDecorationSupport(viewer);
        /*if (fLanguageServiceManager != null && fLanguageServiceManager.getParseController() != null) {
        	IMPHelp.setHelp(fLanguageServiceManager, this, viewer.getTextWidget(), IMP_EDITOR_CONTEXT);
        }*/
	
        return viewer;
    }

    private void setupMatchingBrackets() {
        IPreferenceStore store = getPreferenceStore();
        store.setDefault(MATCHING_BRACKETS, true);
        Color color = currentTheme.getColorRegistry()
                    .get(PLUGIN_ID + ".theme.matchingBracketsColor");
        store.setDefault(MATCHING_BRACKETS_COLOR, 
                color.getRed() +"," + color.getGreen() + "," + color.getBlue());
    }
    
    protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {
        setupMatchingBrackets();

        CeylonLanguageSyntaxProperties syntaxProps= new CeylonLanguageSyntaxProperties();
        getPreferenceStore().setValue(MATCHING_BRACKETS, true);
        if (syntaxProps != null) {
            String[][] fences= syntaxProps.getFences();
            if (fences != null) {
                StringBuilder sb= new StringBuilder();
                for(int i= 0; i < fences.length; i++) {
                    sb.append(fences[i][0]);
                    sb.append(fences[i][1]);
                }
                bracketMatcher= new DefaultCharacterPairMatcher(sb.toString().toCharArray());
                support.setCharacterPairMatcher(bracketMatcher);
                support.setMatchingCharacterPainterPreferenceKeys(MATCHING_BRACKETS, MATCHING_BRACKETS_COLOR);
            }
        }
        super.configureSourceViewerDecorationSupport(support);
    }

    /**
     * Jumps to the matching bracket.
     */
    public void gotoMatchingFence() {
        ISourceViewer sourceViewer= getSourceViewer();
        IDocument document= sourceViewer.getDocument();
        if (document == null)
            return;

        IRegion selection= getSignedSelection(sourceViewer);
        int selectionLength= Math.abs(selection.getLength());

        if (selectionLength > 1) {
            setStatusLineErrorMessage("Invalid selection");
            sourceViewer.getTextWidget().getDisplay().beep();
            return;
        }

        // #26314
        int sourceCaretOffset= selection.getOffset() + selection.getLength();
        if (isSurroundedByBrackets(document, sourceCaretOffset))
            sourceCaretOffset -= selection.getLength();

        IRegion region= bracketMatcher.match(document, sourceCaretOffset);
        if (region == null) {
            setStatusLineErrorMessage("No matching fence!");
            sourceViewer.getTextWidget().getDisplay().beep();
            return;
        }

        int offset= region.getOffset();
        int length= region.getLength();

        if (length < 1)
            return;

        int anchor= bracketMatcher.getAnchor();
        // http://dev.eclipse.org/bugs/show_bug.cgi?id=34195
        int targetOffset= (ICharacterPairMatcher.RIGHT == anchor) ? offset + 1: offset + length;

        boolean visible= false;
        if (sourceViewer instanceof ITextViewerExtension5) {
            ITextViewerExtension5 extension= (ITextViewerExtension5) sourceViewer;
            visible= (extension.modelOffset2WidgetOffset(targetOffset) > -1);
        } else {
            IRegion visibleRegion= sourceViewer.getVisibleRegion();
            // http://dev.eclipse.org/bugs/show_bug.cgi?id=34195
            visible= (targetOffset >= visibleRegion.getOffset() && targetOffset <= visibleRegion.getOffset() + visibleRegion.getLength());
        }

        if (!visible) {
            setStatusLineErrorMessage("Matching fence is outside the currently selected element.");
            sourceViewer.getTextWidget().getDisplay().beep();
            return;
        }

        if (selection.getLength() < 0)
            targetOffset -= selection.getLength();

        sourceViewer.setSelectedRange(targetOffset, selection.getLength());
        sourceViewer.revealRange(targetOffset, selection.getLength());
    }

    private boolean isBracket(char character) {
    	CeylonLanguageSyntaxProperties syntaxProps= CeylonLanguageSyntaxProperties.INSTANCE;
        String[][] fences= syntaxProps.getFences();
        for(int i= 0; i != fences.length; ++i) {
            if (fences[i][0].indexOf(character) >= 0)
                return true;
            if (fences[i][1].indexOf(character) >= 0)
                return true;
        }
        return false;
    }

    private boolean isSurroundedByBrackets(IDocument document, int offset) {
        if (offset == 0 || offset == document.getLength())
            return false;

        try {
            return isBracket(document.getChar(offset - 1)) &&
                   isBracket(document.getChar(offset));
        } catch (BadLocationException e) {
                return false;
        }
    }

    /**
     * Returns the signed current selection.
     * The length will be negative if the resulting selection
     * is right-to-left (RtoL).
     * <p>
     * The selection offset is model based.
     * </p>
     *
     * @param sourceViewer the source viewer
     * @return a region denoting the current signed selection, for a resulting RtoL selections length is < 0
     */
    public IRegion getSignedSelection(ISourceViewer sourceViewer) {
            StyledText text= sourceViewer.getTextWidget();
            Point selection= text.getSelectionRange();

            if (text.getCaretOffset() == selection.x) {
                    selection.x= selection.x + selection.y;
                    selection.y= -selection.y;
            }

            selection.x= widgetOffset2ModelOffset(sourceViewer, selection.x);

            return new Region(selection.x, selection.y);
    }

    public IRegion getSelectedRegion() {
        StyledText text= getSourceViewer().getTextWidget();
        Point selection= text.getSelectionRange();
        return new Region(selection.x, selection.y);
    }

    private Map<IMarker, Annotation> markerParseAnnotations = new HashMap<IMarker, Annotation>();
    private Map<IMarker, MarkerAnnotation> markerMarkerAnnotations = new HashMap<IMarker, MarkerAnnotation>();

    /**
     * Refresh the marker annotations on the input document by removing any
     * that do not map to current parse annotations.  Do this for problem
     * markers, specifically; ignore other types of markers.
     * 
     * SMS 25 Apr 2007
     */
    public void refreshMarkerAnnotations(String problemMarkerType) {
    	// Get current marker annotations
		IAnnotationModel model = getDocumentProvider().getAnnotationModel(getEditorInput());
		List<MarkerAnnotation> markerAnnotations = new ArrayList<MarkerAnnotation>();
		for (Iterator iter = model.getAnnotationIterator(); iter.hasNext();) {
			Object ann = iter.next();
			if (ann instanceof MarkerAnnotation) {
				markerAnnotations.add((MarkerAnnotation) ann);
			} 
		}

		// For the current marker annotations, if any lacks a corresponding
		// parse annotation, delete the marker annotation from the document's
		// annotation model (but leave the marker on the underlying resource,
		// which presumably hasn't been changed, despite changes to the document)
		for (int i = 0; i < markerAnnotations.size(); i++) {
			IMarker marker = markerAnnotations.get(i).getMarker();
			try {
				String markerType = marker.getType();
				if (!markerType.endsWith(problemMarkerType))
					continue;
			} 
			catch (CoreException e) {
				// If we get a core exception here, probably something is wrong with the
				// marker, and we probably don't want to keep any annotation that may be
				// associated with it (I don't think)
				model.removeAnnotation(markerAnnotations.get(i));
				continue;
			}
			if (markerParseAnnotations.get(marker) != null) {
				continue;
			} 
			else {
				model.removeAnnotation(markerAnnotations.get(i));
			}	
		}
    }
    
    
    /**
     * This is a type of listener whose purpose is to monitor changes to a document
     * annotation model and to maintain at a mapping from markers on the underlying
     * resource to parse annotations on the document.
     * 
     * The association of markers to annotations is determined by a subroutine that
     * may be more or less sophisticated in how it identifies associations.  The
     * accuracy of the map depends on the implementation of this routine.  (The
     * current implementation of the method simply compares text ranges of annotations
     * and markers.)
     * 
     * The motivating purpose of the mapping is to enable the identification of marker
     * annotations that are (or are not) associated with a current parse annotation.
     * Then, for instance, marker annotations that are not associated with current parse 
     * annotations might be removed from the document.
     * 
     * No assumptions are made here about the type (or types) of marker annotation of
     * interest; all types of marker annotation are considered.
     * 
     * SMS 25 Apr 2007
     */
    protected class InputAnnotationModelListener implements IAnnotationModelListener {
    	public void modelChanged(IAnnotationModel model) {
    		List<Annotation> currentParseAnnotations = new ArrayList<Annotation>();
    		List<IMarker> currentMarkers = new ArrayList<IMarker>();

    		markerParseAnnotations = new HashMap<IMarker,Annotation>();
    		markerMarkerAnnotations = new HashMap<IMarker,MarkerAnnotation>();
    		
    		// Collect the current set of markers and parse annotations;
    		// also maintain a map of markers to marker annotations (as	
    		// there doesn't seem to be a way to get from a marker to the
    		// annotations that may represent it)
    		
    		for (Iterator iter = model.getAnnotationIterator(); iter.hasNext();) {
    			Object ann = iter.next();
    			if (ann instanceof MarkerAnnotation) {
    				IMarker marker = ((MarkerAnnotation)ann).getMarker();
    				if (marker.exists()) {
    				    currentMarkers.add(marker);
    				}
    				markerMarkerAnnotations.put(marker, (MarkerAnnotation) ann);
    			} 
    			else if (ann instanceof Annotation) {
    				Annotation annotation = (Annotation) ann;

    				if (isParseAnnotation(annotation)) {
    					currentParseAnnotations.add(annotation);
    				}
    			}
    		}

    		// Create a mapping between current markers and parse annotations
    		for (int i = 0; i < currentMarkers.size(); i++) {
    			IMarker marker = currentMarkers.get(i);
				Annotation annotation = findParseAnnotationForMarker(model, marker, 
						currentParseAnnotations);
				if (annotation!=null) {
					markerParseAnnotations.put(marker, annotation);
				}
    		}
    	}

    	public Annotation findParseAnnotationForMarker(IAnnotationModel model, IMarker marker, 
    			List<Annotation> parseAnnotations) {
    		Integer markerStartAttr = null;
    		Integer markerEndAttr = null;
    		try {
				// SMS 22 May 2007:  With markers created through the editor the CHAR_START
				// and CHAR_END attributes are null, giving rise to NPEs here.  Not sure
				// why this happens, but it seems to help down the line to trap the NPE.
				markerStartAttr = ((Integer) marker.getAttribute(IMarker.CHAR_START));
				markerEndAttr = ((Integer) marker.getAttribute(IMarker.CHAR_END));
				if (markerStartAttr == null || markerEndAttr == null) {
					return null;
				}
			} 
    		catch (Exception e) {
			    e.printStackTrace();
				return null;
    		}
			
   			int markerStart = markerStartAttr.intValue();
			int markerEnd = markerEndAttr.intValue();
			int markerLength = markerEnd - markerStart;
			for (int j = 0; j < parseAnnotations.size(); j++) {
				Annotation parseAnnotation = parseAnnotations.get(j);
				Position pos = model.getPosition(parseAnnotation);
				if (pos!=null) {
					int annotationStart = pos.offset;
					int annotationLength = pos.length;
					if (markerStart==annotationStart && 
							markerLength==annotationLength) {
						return parseAnnotation;
					}
				}
			}

			return null;
    	}   	
    }

    public static boolean isParseAnnotation(Annotation a) {
        String type= a.getType();
        return type.equals(PARSE_ANNOTATION_TYPE) || type.equals(PARSE_ANNOTATION_TYPE_ERROR) ||
               type.equals(PARSE_ANNOTATION_TYPE_WARNING) || type.equals(PARSE_ANNOTATION_TYPE_INFO);
    }

    protected void doSetInput(IEditorInput input) throws CoreException {
        // Catch CoreExceptions here, since it's possible that things like IOExceptions occur
        // while retrieving the input's contents, e.g., if the given input doesn't exist.
    	try {
    		super.doSetInput(input);
    	} 
    	catch (CoreException e) {
    	    if (e.getCause() instanceof IOException) {
    	        throw new CoreException(new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID, 0, "Unable to read source text", e.getStatus().getException()));
    	    }
    	}
    	setInsertMode(SMART_INSERT);
	
    	// SMS 25 Apr 2007
    	// Added for maintenance of associations between marker annotations
    	// and parse annotations	
    	IAnnotationModel annotationModel = getDocumentProvider().getAnnotationModel(input);
    	// RMF 6 Jun 2007 - Not sure why annotationModel is null for files outside the
    	// workspace, but they are, so make sure we don't cause an NPE here.
    	if (annotationModel != null)
    	    annotationModel.addAnnotationModelListener(new InputAnnotationModelListener());
    }

    /**
     * Add a Model listener to this editor. Any time the underlying AST is recomputed, the listener is notified.
     * 
     * @param listener the listener to notify of Model changes
     */
    public void addModelListener(TreeLifecycleListener listener) {
        parserScheduler.addModelListener(listener);
    }

    /**
     * Remove a Model listener from this editor.
     * 
     * @param listener the listener to remove
     */
    public void removeModelListener(TreeLifecycleListener listener) {
        parserScheduler.removeModelListener(listener);
    }

    private MessageHandler annotationCreator= new AnnotationCreator(this);
    private EditorIconUpdater editorIconUpdater;
    private IProblemChangedListener annotationUpdater;

    private class AnnotationCreatorListener implements TreeLifecycleListener {
        public Stage getStage() {
        	//TODO: post the lex/parse errors earlier,
        	//      before type checking is complete?
            return TYPE_ANALYSIS;
        }
        public void update(CeylonParseController parseController, IProgressMonitor monitor) {
            // SMS 25 Apr 2007
            // Since parsing has finished, check whether the marker annotations
            // are up-to-date with the most recent parse annotations.
            // Assuming that's often enough--i.e., don't refresh the marker
            // annotations after every update to the document annotation model
            // since there will be many of these, including possibly many that
            // don't relate to problem markers.
            final IAnnotationTypeInfo annotationTypeInfo= parseController.getAnnotationTypeInfo();
            if (annotationTypeInfo != null) {
                List<String> problemMarkerTypes = annotationTypeInfo.getProblemMarkerTypes();
                for (int i = 0; i < problemMarkerTypes.size(); i++) {
                    refreshMarkerAnnotations(problemMarkerTypes.get(i));
                }
            }
        }
    }

    public String getSelectionText() {
        Point sel= getSelection();
        IFileEditorInput fileEditorInput= (IFileEditorInput) getEditorInput();
        IDocument document= getDocumentProvider().getDocument(fileEditorInput);

        try {
            return document.get(sel.x, sel.y);
        } catch (BadLocationException e) {
            e.printStackTrace();
            return "";
        }
    }

    public Point getSelection() {
        ISelection sel= this.getSelectionProvider().getSelection();
        ITextSelection textSel= (ITextSelection) sel;

        return new Point(textSel.getOffset(), textSel.getLength());
    }

    public boolean canPerformFind() {
        return true;
    }

	PresentationController getPresentationController() {
		return presentationController;
	}

    public CeylonParseController getParseController() {
        return parseController;
    }

    public String toString() {
        return "Ceylon Editor for " + getEditorInput().getName();
    }
}

/*class GotoAnnotationAction extends TextEditorAction {
    public static final String PREFIX= RuntimePlugin.IMP_RUNTIME + '.';

    private static final String nextAnnotationContextID= PREFIX + "goto_next_error_action";

    private static final String prevAnnotationContextID= PREFIX + "goto_previous_error_action";

    private boolean fForward;

    public GotoAnnotationAction(String prefix, boolean forward) {
        super(CeylonEditor.fgBundleForConstructedKeys, prefix, null);
        fForward= forward;
        if (forward)
            PlatformUI.getWorkbench().getHelpSystem().setHelp(this, nextAnnotationContextID);
        else
            PlatformUI.getWorkbench().getHelpSystem().setHelp(this, prevAnnotationContextID);
    }

    public void run() {
    	CeylonEditor e= (CeylonEditor) getTextEditor();

        e.gotoAnnotation(fForward);
    }

    public void setEditor(ITextEditor editor) {
        if (editor instanceof CeylonEditor)
            super.setEditor(editor);
        update();
    }

    public void update() {
        setEnabled(getTextEditor() instanceof CeylonEditor);
    }
}*/

