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

import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.ADD_BLOCK_COMMENT;
import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.CORRECT_INDENTATION;
import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.GOTO_MATCHING_FENCE;
import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.GOTO_NEXT_TARGET;
import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.GOTO_PREVIOUS_TARGET;
import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.REMOVE_BLOCK_COMMENT;
import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.RESTORE_PREVIOUS;
import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.SELECT_ENCLOSING;
import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.SHOW_OUTLINE;
import static com.redhat.ceylon.eclipse.code.editor.EditorActionIds.TOGGLE_COMMENT;
import static com.redhat.ceylon.eclipse.code.editor.EditorInputUtils.getFile;
import static com.redhat.ceylon.eclipse.code.editor.EditorInputUtils.getPath;
import static com.redhat.ceylon.eclipse.code.editor.SourceArchiveDocumentProvider.isSrcArchive;
import static com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage.TYPE_ANALYSIS;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.TASK_MARKER_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static java.util.ResourceBundle.getBundle;
import static org.eclipse.core.resources.IResourceChangeEvent.POST_BUILD;
import static org.eclipse.core.resources.IncrementalProjectBuilder.CLEAN_BUILD;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static org.eclipse.ui.texteditor.ITextEditorActionConstants.GROUP_RULERS;
import static org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS;
import static org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds.DELETE_NEXT_WORD;
import static org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds.DELETE_PREVIOUS_WORD;
import static org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds.SELECT_WORD_NEXT;
import static org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds.SELECT_WORD_PREVIOUS;
import static org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds.WORD_NEXT;
import static org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds.WORD_PREVIOUS;

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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.debug.ui.actions.ToggleBreakpointAction;
import org.eclipse.jdt.internal.ui.viewsupport.IProblemChangedListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.preference.IPreferenceStore;
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
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
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
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IEditorStatusLine;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.texteditor.TextNavigationAction;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import ceylon.language.StringBuilder;

import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.outline.CeylonOutlineBuilder;
import com.redhat.ceylon.eclipse.code.outline.CeylonOutlinePage;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.CeylonParserScheduler;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener;
import com.redhat.ceylon.eclipse.code.refactor.RefactorMenuItems;
import com.redhat.ceylon.eclipse.code.search.FindMenuItems;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;

/**
 * An editor for Ceylon source code.
 * 
 * @author Gavin King
 * @author Chris Laffra
 * @author Robert M. Fuhrer
 */
public class CeylonEditor extends TextEditor {
	
	public static final String MESSAGE_BUNDLE= "com.redhat.ceylon.eclipse.code.editor.EditorActionMessages";

	private static final int REPARSE_SCHEDULE_DELAY= 100;

    /** 
     * Parent annotation ID
     */
    public static final String PARSE_ANNOTATION_TYPE = PLUGIN_ID + 
    		".parseAnnotation";

    /**
     * Annotation ID for a parser annotation w/ severity = error.
     * Must match the ID of the corresponding annotationTypes 
     * extension in the plugin.xml.
     */
    public static final String PARSE_ANNOTATION_TYPE_ERROR= PARSE_ANNOTATION_TYPE + 
    		".error";

    /**
     * Annotation ID for a parser annotation w/ severity = warning. 
     * Must match the ID of the corresponding annotationTypes 
     * extension in the plugin.xml.
     */
    public static final String PARSE_ANNOTATION_TYPE_WARNING= PARSE_ANNOTATION_TYPE + 
    		".warning";

    /**
     * Annotation ID for a parser annotation w/ severity = info. 
     * Must match the ID of the corresponding annotationTypes 
     * extension in the plugin.xml.
     */
    public static final String PARSE_ANNOTATION_TYPE_INFO= PARSE_ANNOTATION_TYPE + 
    		".info";

    /** Preference key for matching brackets */
    public final static String MATCHING_BRACKET= "matchingBrackets";

    /** Preference key for matching brackets color */
    public final static String MATCHING_BRACKETS_COLOR= "matchingBracketsColor";
    
    public final static String SELECTED_BRACKET= "highlightBracketAtCaretLocation";
    
    public final static String ENCLOSING_BRACKETS= "enclosingBrackets";

    private CeylonParserScheduler parserScheduler;
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
    private boolean backgroundParsingPaused;
    private CeylonParseController parseController;
    private ProjectionSupport projectionSupport;
        
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
        setRangeIndicator(new CeylonRangeIndicator());
        configureInsertMode(SMART_INSERT, true);
        setInsertMode(SMART_INSERT);
        problemMarkerManager= new ProblemMarkerManager();
	}

	static String[][] getFences() {
		return new String[][] { { "(", ")" }, { "[", "]" }, { "{", "}" } };
	}
	
	public synchronized void pauseBackgroundParsing() {
		backgroundParsingPaused = true;
	}
    
	public synchronized void unpauseBackgroundParsing() {
		backgroundParsingPaused = false;
	}
	
	public synchronized boolean isBackgroundParsingPaused() {
		return backgroundParsingPaused;
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
		    getSourceViewer().getTextWidget().addCaretListener(outlinePage);
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

//        action= new TextOperationAction(bundle, "Format.", this, 
//        		CeylonSourceViewer.FORMAT);
//        action.setActionDefinitionId(FORMAT);
//        setAction("Format", action);
//        markAsStateDependentAction("Format", true);
//        markAsSelectionDependentAction("Format", true);
        //getWorkbench().getHelpSystem().setHelp(action, IJavaHelpContextIds.FORMAT_ACTION);

        action= new TextOperationAction(bundle, "AddBlockComment.", this,
                CeylonSourceViewer.ADD_BLOCK_COMMENT);
        action.setActionDefinitionId(ADD_BLOCK_COMMENT);
        setAction(ADD_BLOCK_COMMENT, action); 
        markAsStateDependentAction(ADD_BLOCK_COMMENT, true); 
        markAsSelectionDependentAction(ADD_BLOCK_COMMENT, true); 
        //PlatformUI.getWorkbench().getHelpSystem().setHelp(action, IJavaHelpContextIds.ADD_BLOCK_COMMENT_ACTION);

        action= new TextOperationAction(bundle, "RemoveBlockComment.", this,
                CeylonSourceViewer.REMOVE_BLOCK_COMMENT);
        action.setActionDefinitionId(REMOVE_BLOCK_COMMENT);
        setAction(REMOVE_BLOCK_COMMENT, action); 
        markAsStateDependentAction(REMOVE_BLOCK_COMMENT, true); 
        markAsSelectionDependentAction(REMOVE_BLOCK_COMMENT, true); 
        //PlatformUI.getWorkbench().getHelpSystem().setHelp(action, IJavaHelpContextIds.REMOVE_BLOCK_COMMENT_ACTION);
        
        action= new TextOperationAction(bundle, "ShowOutline.", this, 
        		CeylonSourceViewer.SHOW_OUTLINE, true /* runsOnReadOnly */);
        action.setActionDefinitionId(SHOW_OUTLINE);
        setAction(SHOW_OUTLINE, action);
        //getWorkbench().getHelpSystem().setHelp(action, IJavaHelpContextIds.SHOW_OUTLINE_ACTION);

        action= new TextOperationAction(bundle, "ToggleComment.", this, 
        		CeylonSourceViewer.TOGGLE_COMMENT);
        action.setActionDefinitionId(TOGGLE_COMMENT);
        setAction(TOGGLE_COMMENT, action);
        //getWorkbench().getHelpSystem().setHelp(action, IJavaHelpContextIds.TOGGLE_COMMENT_ACTION);

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

        action= new RestorePreviousSelectionAction(this);
        action.setActionDefinitionId(RESTORE_PREVIOUS);
        setAction(RESTORE_PREVIOUS, action);

    	action= new TextOperationAction(bundle, "ShowHierarchy.", this, 
    			CeylonSourceViewer.SHOW_HIERARCHY, true);
        action.setActionDefinitionId(EditorActionIds.SHOW_CEYLON_HIERARCHY);
        setAction(EditorActionIds.SHOW_CEYLON_HIERARCHY, action);

    	action= new TextOperationAction(bundle, "ShowCode.", this, 
    			CeylonSourceViewer.SHOW_CODE, true);
        action.setActionDefinitionId(EditorActionIds.SHOW_CEYLON_CODE);
        setAction(EditorActionIds.SHOW_CEYLON_CODE, action);
        
        action= new TerminateStatementAction(this);
        action.setActionDefinitionId(EditorActionIds.TERMINATE_STATEMENT);
        setAction(EditorActionIds.TERMINATE_STATEMENT, action);

        foldingActionGroup= new FoldingActionGroup(this, this.getSourceViewer());
        
        getAction(ITextEditorActionConstants.SHIFT_LEFT)
            .setImageDescriptor(CeylonPlugin.getInstance().getImageRegistry()
            		.getDescriptor(CeylonResources.SHIFT_LEFT));
        getAction(ITextEditorActionConstants.SHIFT_RIGHT)
            .setImageDescriptor(CeylonPlugin.getInstance().getImageRegistry()
        		.getDescriptor(CeylonResources.SHIFT_RIGHT));
        
        IAction qaa=getAction(ITextEditorActionConstants.QUICK_ASSIST);
        qaa.setImageDescriptor(CeylonPlugin.getInstance().getImageRegistry()
    		    .getDescriptor(CeylonResources.QUICK_ASSIST));
        qaa.setText("Quick Fix/Assist");
        
        installQuickAccessAction();
        
    }
    
    @Override
    protected String[] collectContextMenuPreferencePages() {
        String[] pages = super.collectContextMenuPreferencePages();
        String[] result = new String[pages.length+1];
        System.arraycopy(pages, 0, result, 0, pages.length);
        result[pages.length] = "com.redhat.ceylon.eclipse.ui.preferences.editor";
        return result;
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

    private IHandlerActivation fFindQuickAccessHandlerActivation;
    private IHandlerActivation fRefactorQuickAccessHandlerActivation;
    private IHandlerService fHandlerService;

    public static final String REFACTOR_MENU_ID = "com.redhat.ceylon.eclipse.ui.menu.refactorQuickMenu";
    public static final String FIND_MENU_ID = "com.redhat.ceylon.eclipse.ui.menu.findQuickMenu";
    
    private class RefactorQuickAccessAction extends QuickMenuAction {
        public RefactorQuickAccessAction() {
            super(REFACTOR_MENU_ID);
        }
        protected void fillMenu(IMenuManager menu) {
            IContributionItem[] cis = new RefactorMenuItems().getContributionItems();
            for (IContributionItem ci: cis) {
                menu.add(ci);
            }
        }
    }
    
    private class FindQuickAccessAction extends QuickMenuAction {
        public FindQuickAccessAction() {
            super(FIND_MENU_ID);
        }
        protected void fillMenu(IMenuManager menu) {
            IContributionItem[] cis = new FindMenuItems().getContributionItems();
            for (IContributionItem ci: cis) {
                menu.add(ci);
            }
        }
    }
    
    private void installQuickAccessAction() {
        fHandlerService= (IHandlerService) getSite().getService(IHandlerService.class);
        if (fHandlerService != null) {
            QuickMenuAction refactorQuickAccessAction= new RefactorQuickAccessAction();
            fRefactorQuickAccessHandlerActivation= fHandlerService.activateHandler(refactorQuickAccessAction.getActionDefinitionId(), 
            		new ActionHandler(refactorQuickAccessAction));
            QuickMenuAction findQuickAccessAction= new FindQuickAccessAction();
            fRefactorQuickAccessHandlerActivation= fHandlerService.activateHandler(findQuickAccessAction.getActionDefinitionId(), 
                    new ActionHandler(findQuickAccessAction));
        }
    }
    
    private void uninstallQuickAccessAction() {
        if (fHandlerService != null) {
            fHandlerService.deactivateHandler(fRefactorQuickAccessHandlerActivation); 
            fHandlerService.deactivateHandler(fFindQuickAccessHandlerActivation); 
        }
    }

    protected boolean isOverviewRulerVisible() {
        return true;
    }

    protected void rulerContextMenuAboutToShow(IMenuManager menu) {
        addDebugActions(menu);

        super.rulerContextMenuAboutToShow(menu);

        //IMenuManager foldingMenu= new MenuManager("Folding", "projection");

        //menu.appendToGroup(GROUP_RULERS, foldingMenu);

        menu.appendToGroup(GROUP_RULERS, new Separator());
        
        IAction action;
//      action= getAction("FoldingToggle");
//      foldingMenu.add(action);
        action= getAction("FoldingExpandAll");
        menu.appendToGroup(GROUP_RULERS, action);
        //foldingMenu.add(action);
        action= getAction("FoldingCollapseAll");
        //foldingMenu.add(action);
        menu.appendToGroup(GROUP_RULERS, action);
        /*action= getAction("FoldingRestore");
        foldingMenu.add(action);
        action= getAction("FoldingCollapseMembers");
        foldingMenu.add(action);
        action= getAction("FoldingCollapseComments");
        foldingMenu.add(action);*/
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
    
    @Override
    protected void setTitleImage(Image titleImage) {
    	super.setTitleImage(titleImage);
    }

    public IDocumentProvider getDocumentProvider() {
    	if (isSrcArchive(getEditorInput())) {
    		//Note: I would prefer to register the
    		//document provider in plugin.xml but
    		//I don't know how to uniquely identity
    		//that a IURIEditorInput is a source
    		//archive there
    		if (sourceArchiveDocumentProvider==null) {
    			sourceArchiveDocumentProvider = new SourceArchiveDocumentProvider();
    		}
    		return sourceArchiveDocumentProvider;
    	}
    	else {
    		return super.getDocumentProvider();
    	}
    }
    
    public CeylonSourceViewer getCeylonSourceViewer() {
    	return (CeylonSourceViewer) super.getSourceViewer();
    }

    public void createPartControl(Composite parent) {
        
    	// Initialize the parse controller first, since the 
    	// initialization of other things (like the context 
    	// help support) might depend on it.
        initializeParseController();

        // Not sure why the "run the spell checker" pref would 
        // get set, but it does seem to, which gives lots of 
        // annoying squigglies all over the place...
        //getPreferenceStore().setValue(SpellingService.PREFERENCE_SPELLING_ENABLED, false);

        super.createPartControl(parent);

        initiateServiceControllers();

        setTitleImageFromLanguageIcon();
        //setSourceFontFromPreference();
        
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

    public synchronized void scheduleParsing() {
    	if (parserScheduler!=null && !backgroundParsingPaused) {
    		parserScheduler.cancel();
    		parserScheduler.schedule(REPARSE_SCHEDULE_DELAY);
    	}
    }

    private void initializeParseController() {
        IEditorInput editorInput= getEditorInput();
        IFile file = getFile(editorInput);
        IPath filePath = getPath(editorInput);
        parseController = new CeylonParseController();
        IProject project= file!=null && file.exists() ? file.getProject() : null;
        parseController.initialize(filePath, project, annotationCreator);
    }

    private void watchDocument() {
        getSourceViewer().getDocument()
                .addDocumentListener(documentListener= new IDocumentListener() {
            public void documentAboutToBeChanged(DocumentEvent event) {}
            public void documentChanged(DocumentEvent event) {
            	synchronized (CeylonEditor.this) {
            		if (parserScheduler!=null && !backgroundParsingPaused) {
            			parserScheduler.cancel();
            			parserScheduler.schedule(REPARSE_SCHEDULE_DELAY);
            		}
				}
            }
        });
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
        			IProject project = parseController.getProject();
        			if (project!=null) { //things extrenal to the workspace don't move
        				IPath oldWSRelPath= project.getFullPath().append(parseController.getPath());
        				IResourceDelta rd= event.getDelta().findMember(oldWSRelPath);
        				if (rd != null) {
        					if ((rd.getFlags() & IResourceDelta.MOVED_TO) == IResourceDelta.MOVED_TO) {
        						// The net effect of the following is to re-initialize() the IParseController with the new path
        						IPath newPath= rd.getMovedToPath();
        						IPath newProjRelPath= newPath.removeFirstSegments(1);
        						String newProjName= newPath.segment(0);
        						IProject proj= project.getName().equals(newProjName) ? 
        								project : project.getWorkspace().getRoot()
        							            .getProject(newProjName);
        						// Tell the IParseController about the move - it caches the path
        						// fParserScheduler.cancel(); // avoid a race condition if ParserScheduler was starting/in the middle of a run
        						parseController.initialize(newProjRelPath, proj, annotationCreator);
        					}
        				}
        			}
        		}
            }
        });
    }

    /*private void setSourceFontFromPreference() {
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
    }*/

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
					annotationCreator);
            
            addModelListener(new AdditionalAnnotationCreator(this));
            
            // The source viewer configuration has already been asked for its ITextHover,
            // but before we actually instantiated the relevant controller class. So update
            // the source viewer, now that we actually have the hover provider.
            //HoverHelpController hover = new HoverHelpController(this);
			//sourceViewer.setTextHover(hover, DEFAULT_CONTENT_TYPE);
            //addModelListener(hover);
            
            projectionSupport = new ProjectionSupport(sourceViewer, getAnnotationAccess(), getSharedColors());
            MarkerAnnotationPreferences markerAnnotationPreferences = (MarkerAnnotationPreferences) getAdapter(MarkerAnnotationPreferences.class);
            if (markerAnnotationPreferences != null) {
                List<AnnotationPreference> annPrefs = markerAnnotationPreferences.getAnnotationPreferences();
                for (Iterator<AnnotationPreference> e = annPrefs.iterator(); e.hasNext();) {
                    Object annotationType = e.next().getAnnotationType();
                    if (annotationType instanceof String) {
                        projectionSupport.addSummarizableAnnotationType((String) annotationType);
                    }
                }
            } 
            /*else {
                projectionSupport.addSummarizableAnnotationType(PARSE_ANNOTATION_TYPE_ERROR);
                projectionSupport.addSummarizableAnnotationType(PARSE_ANNOTATION_TYPE_WARNING);
                projectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.error");
                projectionSupport.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.warning");
            }*/
            projectionSupport.install();
            sourceViewer.doOperation(ProjectionViewer.TOGGLE);
            ProjectionAnnotationModel projectionAnnotationModel = sourceViewer.getProjectionAnnotationModel();
            if (projectionAnnotationModel!=null) {
            	addModelListener(new FoldingController(projectionAnnotationModel));
            }
            
            if (isEditable()) {
                addModelListener(new MarkerAnnotationUpdater());
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

        /*if (documentListener!=null) {
        	getSourceViewer().getDocument()
        	    .removeDocumentListener(documentListener);
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
        
        if (projectionSupport!=null) {
            projectionSupport.dispose();
            projectionSupport = null;
        }

        if (parserScheduler!=null) {
        	parserScheduler.cancel(); // avoid unnecessary work after the editor is asked to close down
        }
        parserScheduler= null;
        parseController = null;
        
        uninstallQuickAccessAction();

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
    
        
    protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
        
        fAnnotationAccess= getAnnotationAccess();
        fOverviewRuler= createOverviewRuler(getSharedColors());

        ISourceViewer viewer= new CeylonSourceViewer(parent, ruler, 
        		getOverviewRuler(), isOverviewRulerVisible(), styles);
        
        // ensure decoration support has been created and configured.
        getSourceViewerDecorationSupport(viewer);
        
        viewer.getTextWidget().addCaretListener(new CaretListener() {
            @Override
            public void caretMoved(CaretEvent event) {
                Object adapter = getAdapter(IVerticalRulerInfo.class);
                if (adapter instanceof CompositeRuler) {
                    // redraw initializer annotations according to cursor position
                    ((CompositeRuler) adapter).update();
                }
            }
        });
	
        return viewer;
    }

    protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {
        IPreferenceStore store = getPreferenceStore();
		store.setDefault(MATCHING_BRACKET, true);
		Color color = currentTheme.getColorRegistry()
		            .get(PLUGIN_ID + ".theme.matchingBracketsColor");
		store.setDefault(MATCHING_BRACKETS_COLOR, 
		        color.getRed() +"," + color.getGreen() + "," + color.getBlue());
        store.setDefault(MATCHING_BRACKET, true);
        store.setDefault(ENCLOSING_BRACKETS, false);
        store.setDefault(SELECTED_BRACKET, false);
        String[][] fences= getFences();
        if (fences != null) {
        	StringBuilder sb= new StringBuilder();
        	for (int i=0; i<fences.length; i++) {
        		sb.append(fences[i][0]);
        		sb.append(fences[i][1]);
        	}
        	bracketMatcher= new DefaultCharacterPairMatcher(sb.toString().toCharArray());
        	support.setCharacterPairMatcher(bracketMatcher);
        	support.setMatchingCharacterPainterPreferenceKeys(
        	        MATCHING_BRACKET, MATCHING_BRACKETS_COLOR, 
        	        SELECTED_BRACKET, ENCLOSING_BRACKETS);
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
        String[][] fences= getFences();
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
			MarkerAnnotation markerAnnotation = markerAnnotations.get(i);
			IMarker marker = markerAnnotation.getMarker();
			try {
				if (marker.getType().equals(problemMarkerType)) {
					if (markerParseAnnotations.get(marker)==null) {
						model.removeAnnotation(markerAnnotation);
					}	
				}
			} 
			catch (CoreException e) {
				// If we get a core exception here, probably something is wrong with the
				// marker, and we probably don't want to keep any annotation that may be
				// associated with it (I don't think)
				model.removeAnnotation(markerAnnotation);
				continue;
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
        return a.getType().startsWith(PARSE_ANNOTATION_TYPE);
    }

    protected void doSetInput(IEditorInput input) throws CoreException {
        // Catch CoreExceptions here, since it's possible that things like IOExceptions occur
        // while retrieving the input's contents, e.g., if the given input doesn't exist.
    	try {
    		super.doSetInput(input);
    	} 
    	catch (CoreException e) {
    	    if (e.getCause() instanceof IOException) {
    	        throw new CoreException(new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID, 
    	        		0, "Unable to read source text", e.getStatus().getException()));
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

    private AnnotationCreator annotationCreator = new AnnotationCreator(this);
    private EditorIconUpdater editorIconUpdater;
    private IProblemChangedListener annotationUpdater;

    private class MarkerAnnotationUpdater implements TreeLifecycleListener {
        public Stage getStage() {
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
            refreshMarkerAnnotations(PROBLEM_MARKER_ID);
            refreshMarkerAnnotations(TASK_MARKER_ID);
        }
    }

    public String getSelectionText() {
        IRegion sel= getSelection();
        IFileEditorInput fileEditorInput= (IFileEditorInput) getEditorInput();
        IDocument document= getDocumentProvider().getDocument(fileEditorInput);
        try {
            return document.get(sel.getOffset(), sel.getLength());
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
            return "";
        }
    }

    public IRegion getSelection() {
        ITextSelection ts= (ITextSelection) getSelectionProvider().getSelection();
        return new Region(ts.getOffset(), ts.getLength());
    }

    public boolean canPerformFind() {
        return true;
    }

    public CeylonParseController getParseController() {
        return parseController;
    }

    public String toString() {
        return "Ceylon Editor for " + getEditorInput().getName();
    }
}


