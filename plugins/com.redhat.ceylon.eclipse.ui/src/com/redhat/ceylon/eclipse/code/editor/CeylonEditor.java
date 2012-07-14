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

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.core.resources.IResourceChangeEvent.POST_BUILD;
import static org.eclipse.core.resources.IncrementalProjectBuilder.AUTO_BUILD;
import static org.eclipse.imp.preferences.PreferenceConstants.P_SOURCE_FONT;
import static org.eclipse.imp.preferences.PreferenceConstants.P_SPACES_FOR_TABS;
import static org.eclipse.imp.preferences.PreferenceConstants.P_TAB_WIDTH;
import static org.eclipse.jface.text.IDocument.DEFAULT_CONTENT_TYPE;
import static org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS;
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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.debug.ui.actions.ToggleBreakpointAction;
import org.eclipse.imp.actions.RulerEnableDisableBreakpointAction;
import org.eclipse.imp.editor.EditorInputUtils;
import org.eclipse.imp.editor.FoldingActionGroup;
import org.eclipse.imp.editor.IEditorActionDefinitionIds;
import org.eclipse.imp.editor.IProblemChangedListener;
import org.eclipse.imp.editor.IRegionSelectionService;
import org.eclipse.imp.editor.IResourceDocumentMapListener;
import org.eclipse.imp.editor.ParserScheduler;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.imp.parser.IModelListener;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.IPreferencesService.BooleanPreferenceListener;
import org.eclipse.imp.preferences.IPreferencesService.IntegerPreferenceListener;
import org.eclipse.imp.preferences.IPreferencesService.PreferenceServiceListener;
import org.eclipse.imp.preferences.IPreferencesService.StringPreferenceListener;
import org.eclipse.imp.preferences.PreferencesService;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.imp.services.IASTFindReplaceTarget;
import org.eclipse.imp.services.IAnnotationTypeInfo;
import org.eclipse.imp.services.IFoldingUpdater;
import org.eclipse.imp.services.ILanguageSyntaxProperties;
import org.eclipse.imp.services.INavigationTargetFinder;
import org.eclipse.imp.services.IOccurrenceMarker;
import org.eclipse.imp.services.ITokenColorer;
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
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
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
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.SubActionBars;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IEditorStatusLine;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.texteditor.TextNavigationAction;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.texteditor.spelling.SpellingService;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.redhat.ceylon.eclipse.code.hover.HoverHelpController;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.outline.CeylonOutlineBuilder;
import com.redhat.ceylon.eclipse.code.outline.CeylonOutlinePage;
import com.redhat.ceylon.eclipse.code.parse.CeylonLanguageSyntaxProperties;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

/**
 * An Eclipse editor, which is not enhanced using API; rather, we publish extension
 * points for outline, content assist, hover help, etc.
 * 
 * @author Chris Laffra
 * @author Robert M. Fuhrer
 */
public class CeylonEditor extends TextEditor implements IASTFindReplaceTarget {
    public static final String MESSAGE_BUNDLE= "org.eclipse.imp.editor.messages";

    public static final String EDITOR_ID= RuntimePlugin.IMP_RUNTIME + ".impEditor";

    public static final String PARSE_ANNOTATION_TYPE= "org.eclipse.imp.editor.parseAnnotation";

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

    private ParserScheduler fParserScheduler;

    private IDocumentProvider fZipDocProvider;

    private ProjectionAnnotationModel fAnnotationModel;

    private ProblemMarkerManager fProblemMarkerManager;

    private ICharacterPairMatcher fBracketMatcher;

    private SubActionBars fActionBars;

    //private DefaultPartListener fRefreshContributions;

    private IPreferencesService fLangSpecificPrefs;

    private PreferenceServiceListener fFontListener;

    private PreferenceServiceListener fTabListener;

    private PreferenceServiceListener fSpacesForTabsListener;

    private IPropertyChangeListener fPropertyListener;

    private ToggleBreakpointAction fToggleBreakpointAction;

    private IAction fEnableDisableBreakpointAction;

    private IResourceChangeListener fResourceListener;

    private IDocumentListener fDocumentListener;

    private FoldingActionGroup fFoldingActionGroup;

	private static final String BUNDLE_FOR_CONSTRUCTED_KEYS= MESSAGE_BUNDLE;//$NON-NLS-1$

    public static ResourceBundle fgBundleForConstructedKeys= ResourceBundle.getBundle(BUNDLE_FOR_CONSTRUCTED_KEYS);
    
    public static final String IMP_CODING_ACTION_SET = RuntimePlugin.IMP_RUNTIME + ".codingActionSet";

    public static final String IMP_OPEN_ACTION_SET = RuntimePlugin.IMP_RUNTIME + ".openActionSet";

    public CeylonEditor() {
        // SMS 4 Apr 2007
        // Do not set preference store with store obtained from plugin; one is
        // already defined for the parent text editor and populated with relevant
        // preferences
        // setPreferenceStore(RuntimePlugin.getInstance().getPreferenceStore());
        setSourceViewerConfiguration(createSourceViewerConfiguration());
        configureInsertMode(SMART_INSERT, true);
        setInsertMode(SMART_INSERT);
        fProblemMarkerManager= new ProblemMarkerManager();
	}

    /**
     * Sub-classes may override this method to extend the behavior provided by IMP's
     * standard StructuredSourceViewerConfiguration.
     * @return the StructuredSourceViewerConfiguration to use with this editor
     */
    protected StructuredSourceViewerConfiguration createSourceViewerConfiguration() {
    	return new StructuredSourceViewerConfiguration(getPreferenceStore(), this);
    }

    public IPreferencesService getLanguageSpecificPreferences() {
        return fLangSpecificPrefs;
    }

    public IPreferenceStore getPrefStore() {
    	return super.getPreferenceStore();
    }

    private SourceArchiveDocumentProvider sourceArchiveDocumentProvider;
    private ToggleBreakpointAdapter toggleBreakpointTarget;
    private CeylonOutlinePage myOutlinePage;
    
    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class required) {
        if (IContentOutlinePage.class.equals(required)) {
            if (myOutlinePage == null) {
                myOutlinePage = new CeylonOutlinePage(getParseController(),
                        new CeylonOutlineBuilder(), new CeylonLabelProvider());
                fParserScheduler.addModelListener(myOutlinePage);
             }
             return myOutlinePage;
        }
        if (IToggleBreakpointsTarget.class.equals(required)) {
            if (toggleBreakpointTarget == null) {
                toggleBreakpointTarget = new ToggleBreakpointAdapter();
            }
            return toggleBreakpointTarget;
        }
        if (IRegionSelectionService.class.equals(required)) {
            return fRegionSelector;
        }
        /*if (IContextProvider.class.equals(required)) {
            return IMPHelp.getHelpContextProvider(this, fLanguageServiceManager, IMP_EDITOR_CONTEXT);
        }*/
        // This was intended to simplify a bit of test code. Unfortunately, it breaks the editor
        // in the presence of search hits, since the search UI classes actually look for an editor
        // that adapts to IAnnotationModel, and behave differently, and this interacts badly with
        // the projection (i.e. folding) support. Go figure.
//      if (IAnnotationModel.class.equals(required)) {
//          return fAnnotationModel;
//      }
        return super.getAdapter(required);
    }

    protected void createActions() {
        super.createActions();

        final ResourceBundle bundle= ResourceBundle.getBundle(MESSAGE_BUNDLE);
        Action action= new ContentAssistAction(bundle, "ContentAssistProposal.", this);
        action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
        setAction("ContentAssistProposal", action);
        markAsStateDependentAction("ContentAssistProposal", true);

        // Not sure how to hook this up - the following class has all the right enablement logic,
        // but it doesn't implement IAction... How to register it as an action here???
//        fToggleBreakpointAction= new AbstractRulerActionDelegate() {
//            protected IAction createAction(ITextEditor editor, IVerticalRulerInfo rulerInfo) {
//                return new ToggleBreakpointAction(UniversalEditor.this, getDocumentProvider().getDocument(getEditorInput()), getVerticalRuler());
//            }
//        }
        fToggleBreakpointAction= new ToggleBreakpointAction(this, getDocumentProvider().getDocument(getEditorInput()), getVerticalRuler());
        setAction("ToggleBreakpoint", action);
        fEnableDisableBreakpointAction= new RulerEnableDisableBreakpointAction(this, getVerticalRuler());
        setAction("ToggleBreakpoint", action);

        action= new TextOperationAction(bundle, "Format.", this, ISourceViewer.FORMAT); //$NON-NLS-1$
        action.setActionDefinitionId(IEditorActionDefinitionIds.FORMAT);
        setAction("Format", action); //$NON-NLS-1$
        markAsStateDependentAction("Format", true); //$NON-NLS-1$
        markAsSelectionDependentAction("Format", true); //$NON-NLS-1$
//      PlatformUI.getWorkbench().getHelpSystem().setHelp(action, IJavaHelpContextIds.FORMAT_ACTION);

        action= new TextOperationAction(bundle, "ShowOutline.", this, StructuredSourceViewer.SHOW_OUTLINE, true /* runsOnReadOnly */); //$NON-NLS-1$
        action.setActionDefinitionId(IEditorActionDefinitionIds.SHOW_OUTLINE);
        setAction(IEditorActionDefinitionIds.SHOW_OUTLINE, action); //$NON-NLS-1$
//      PlatformUI.getWorkbench().getHelpSystem().setHelp(action, IJavaHelpContextIds.SHOW_OUTLINE_ACTION);

        action= new TextOperationAction(bundle, "ToggleComment.", this, StructuredSourceViewer.TOGGLE_COMMENT); //$NON-NLS-1$
        action.setActionDefinitionId(IEditorActionDefinitionIds.TOGGLE_COMMENT);
        setAction(IEditorActionDefinitionIds.TOGGLE_COMMENT, action); //$NON-NLS-1$
//      PlatformUI.getWorkbench().getHelpSystem().setHelp(action, IJavaHelpContextIds.TOGGLE_COMMENT_ACTION);

        action= new TextOperationAction(bundle, "CorrectIndentation.", this, StructuredSourceViewer.CORRECT_INDENTATION); //$NON-NLS-1$
        action.setActionDefinitionId(IEditorActionDefinitionIds.CORRECT_INDENTATION);
        setAction(IEditorActionDefinitionIds.CORRECT_INDENTATION, action); //$NON-NLS-1$

        action= new GotoMatchingFenceAction(this);
        action.setActionDefinitionId(IEditorActionDefinitionIds.GOTO_MATCHING_FENCE);
        setAction(IEditorActionDefinitionIds.GOTO_MATCHING_FENCE, action);

        action= new GotoPreviousTargetAction(this);
        action.setActionDefinitionId(IEditorActionDefinitionIds.GOTO_PREVIOUS_TARGET);
        setAction(IEditorActionDefinitionIds.GOTO_PREVIOUS_TARGET, action);

        action= new GotoNextTargetAction(this);
        action.setActionDefinitionId(IEditorActionDefinitionIds.GOTO_NEXT_TARGET);
        setAction(IEditorActionDefinitionIds.GOTO_NEXT_TARGET, action);

        action= new SelectEnclosingAction(this);
        action.setActionDefinitionId(IEditorActionDefinitionIds.SELECT_ENCLOSING);
        setAction(IEditorActionDefinitionIds.SELECT_ENCLOSING, action);

        fFoldingActionGroup= new FoldingActionGroup(this, this.getSourceViewer());
        
        //installQuickAccessAction();

    	action= new TextOperationAction(bundle, "ShowHierarchy.", this, 
    			StructuredSourceViewer.SHOW_HIERARCHY, true); //$NON-NLS-1$
        action.setActionDefinitionId("com.redhat.ceylon.eclipse.ui.action.hierarchy");
        setAction("com.redhat.ceylon.eclipse.ui.action.hierarchy", action); //$NON-NLS-1$
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
			viewer.getDocument().replace(caret, length, ""); //$NON-NLS-1$
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
			viewer.getDocument().replace(position, length, ""); //$NON-NLS-1$
		} catch (BadLocationException exception) {
			// Should not happen
		}
	}
	public void update() {
		setEnabled(isEditorInputModifiable());
	}
}

    protected void initializeKeyBindingScopes() {
        setKeyBindingScopes(new String[] { RuntimePlugin.SOURCE_EDITOR_SCOPE });
    }

    //private QuickMenuAction fQuickAccessAction;
    //private IHandlerActivation fQuickAccessHandlerActivation;
    //private IHandlerService fHandlerService;

    //private static final String QUICK_MENU_ID= "org.eclipse.imp.runtime.editor.refactor.quickMenu"; //$NON-NLS-1$

    private final class AnnotationUpdater implements IProblemChangedListener {
        public void problemsChanged(IResource[] changedResources, boolean isMarkerChange) {
            // TODO Work-around to remove annotations that were resolved by changes to other resources.
            // It would be better to match the markers to the annotations, and decide which
            // annotations to remove.
            if (fParserScheduler != null) {
                fParserScheduler.schedule(50);
            }
        }
    }

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

        IMenuManager foldingMenu= new MenuManager("Folding", "projection"); //$NON-NLS-1$

        menu.appendToGroup(ITextEditorActionConstants.GROUP_RULERS, foldingMenu);

        IAction action;
//      action= getAction("FoldingToggle"); //$NON-NLS-1$
//      foldingMenu.add(action);
        action= getAction("FoldingExpandAll"); //$NON-NLS-1$
        foldingMenu.add(action);
        action= getAction("FoldingCollapseAll"); //$NON-NLS-1$
        foldingMenu.add(action);
        action= getAction("FoldingRestore"); //$NON-NLS-1$
        foldingMenu.add(action);
        action= getAction("FoldingCollapseMembers"); //$NON-NLS-1$
        foldingMenu.add(action);
        action= getAction("FoldingCollapseComments"); //$NON-NLS-1$
        foldingMenu.add(action);
    }

    private void addDebugActions(IMenuManager menu) {
        menu.add(fToggleBreakpointAction);
        menu.add(fEnableDisableBreakpointAction);
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
        return fProblemMarkerManager;
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
        Annotation annotation= getNextAnnotation(selection.getOffset(), selection.getLength(), forward, position);
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

        for(Iterator<Annotation> e= model.getAnnotationIterator(); e.hasNext(); ) {
            Annotation a= (Annotation) e.next();

            if (!(a instanceof MarkerAnnotation) && !isParseAnnotation(a))
                continue;

            Position p= model.getPosition(a);

            if (p == null)
                continue;

            if (forward && p.offset == offset || !forward && p.offset + p.getLength() == offset + length) {// || p.includes(offset)) {
                if (containingAnnotation == null
                        || (forward && p.length >= containingAnnotationPosition.length || 
                            !forward && p.length >= containingAnnotationPosition.length)) {
                    containingAnnotation= a;
                    containingAnnotationPosition= p;
                    currentAnnotation= p.length == length;
                }
            } else {
                int currentDistance= forward ? p.getOffset() - offset : offset + length - (p.getOffset() + p.length);

                if (currentDistance < 0)
                    currentDistance= endOfDocument + currentDistance;

                if (currentDistance < distance || currentDistance == distance && p.length < nextAnnotationPosition.length) {
                    distance= currentDistance;
                    nextAnnotation= a;
                    nextAnnotationPosition= p;
                }
            }
        }
        if (containingAnnotationPosition != null && (!currentAnnotation || nextAnnotation == null)) {
            annotationPosition.setOffset(containingAnnotationPosition.getOffset());
            annotationPosition.setLength(containingAnnotationPosition.getLength());
            return containingAnnotation;
        }
        if (nextAnnotationPosition != null) {
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
                IViewPart view= page.findView(isProblem ? IPageLayout.ID_PROBLEM_VIEW : IPageLayout.ID_TASK_LIST); //$NON-NLS-1$  //$NON-NLS-2$
                if (view != null) {
                    Method method= view.getClass().getMethod("setSelection", new Class[] { IStructuredSelection.class, boolean.class }); //$NON-NLS-1$
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
            if (fZipDocProvider == null) {
                fZipDocProvider= new ZipStorageEditorDocumentProvider();
            }
            return fZipDocProvider;
        }
    	return super.getDocumentProvider();
    }

    public void createPartControl(Composite parent) {
        String esft = getPreferenceStore().getString(EDITOR_SPACES_FOR_TABS);
        initializeParseController();
        findLanguageSpecificPreferences();

        // RMF 07 June 2010 - Not sure why the "run the spell checker" pref would get set, but
        // it does seem to, which gives lots of annoying squigglies all over the place...
        getPreferenceStore().setValue(SpellingService.PREFERENCE_SPELLING_ENABLED, false);

        super.createPartControl(parent);

        initiateServiceControllers();

        // SMS 4 Apr 2007:  Call no longer needed because preferences for the
        // overview ruler are now obtained from appropriate preference store directly
        //setupOverviewRulerAnnotations();

        // SMS 4 Apr 2007:  Also should not need this, since we're not using
        // the plugin's store (for this purpose)
        //AbstractDecoratedTextEditorPreferenceConstants.initializeDefaultValues(RuntimePlugin.getInstance().getPreferenceStore());

        setTitleImageFromLanguageIcon();
        setSourceFontFromPreference();
        setupBracketCloser();
        setupSourcePrefListeners();

        //initializeEditorContributors();

        watchForSourceMove();

        if (isEditable() && getResourceDocumentMapListener() != null) {
            IResourceDocumentMapListener rdml = getResourceDocumentMapListener();
            rdml.registerDocument(getDocumentProvider().getDocument(getEditorInput()), EditorInputUtils.getFile(getEditorInput()), this);
        }

        getPreferenceStore().setValue(EDITOR_SPACES_FOR_TABS, esft);
        //getPreferenceStore().setValue(EDITOR_TAB_WIDTH, etw);
        
        watchForSourceBuild();
        
        ((IContextService) getSite().getService(IContextService.class)).activateContext(PLUGIN_ID + ".context");
        
        //CeylonPlugin.getInstance().getPreferenceStore().addPropertyChangeListener(colorChangeListener);
        currentTheme.getColorRegistry().addListener(colorChangeListener);
        updateFontAndCaret();
        currentTheme.getFontRegistry().addListener(fontChangeListener);
    }

    private void watchForSourceBuild() {        
        ResourcesPlugin.getWorkspace().addResourceChangeListener(fResourceListener= new IResourceChangeListener() {
            public void resourceChanged(IResourceChangeEvent event) {
                if (event.getType()==POST_BUILD && event.getBuildKind()==AUTO_BUILD) {
                	CeylonParseController pc = getParseController();
                	if (pc!=null) {
                		IPath oldWSRelPath= pc.getProject().getRawProject()
                				.getFullPath().append(pc.getPath());
                		IResourceDelta rd= event.getDelta().findMember(oldWSRelPath);
                		if (rd != null) {
                			scheduleParsing();
                		}
                	}
                }
            }
        }, IResourceChangeEvent.POST_BUILD);
    }

    public void scheduleParsing() {
    	ParserScheduler scheduler = (ParserScheduler) fParserScheduler;
    	if (scheduler!=null) {
    		scheduler.cancel();
    		scheduler.schedule(50);
    	}
    }

    private CeylonParseController parseController;
    
    private void initializeParseController() {
    	
    	// Initialize the parse controller now, since the initialization of other things (like the context help support) might depend on it being so.
        IEditorInput editorInput= getEditorInput();
        IFile file = null;
        IPath filePath = null;

		//IEditorInputResolver editorInputResolver= fLanguageServiceManager.getEditorInputResolver();

		/*if (fLanguageServiceManager != null && editorInputResolver != null) {
			file = editorInputResolver.getFile(editorInput);
			filePath = editorInputResolver.getPath(editorInput);
		} else {*/
        file = EditorInputUtils.getFile(editorInput);
        filePath = EditorInputUtils.getPath(editorInput);
		//}
        
        try {
        	parseController = new CeylonParseController();
            IProject project= (file != null && file.exists()) ? file.getProject() : null;
            ISourceProject srcProject= (project != null) ? ModelFactory.open(project) : null;
            parseController.initialize(filePath, srcProject, fAnnotationCreator);
            // TODO Need to do the following to give the strategy access to project-specific preference settings
//          if (fLanguageServiceManager.getAutoEditStrategies().size() > 0) {
//              Set<org.eclipse.imp.services.IAutoEditStrategy> strategies= fLanguageServiceManager.getAutoEditStrategies();
//              for(org.eclipse.imp.services.IAutoEditStrategy strategy: strategies) {
//                  strategy.setProject(project);
//              }
//          }
        } 
        catch (ModelException e) {
            e.printStackTrace();
        }
    }

    private void findLanguageSpecificPreferences() {
        ISourceProject srcProject = getParseController().getProject();
        if (srcProject!=null) {
        	IProject project= srcProject.getRawProject();
        	fLangSpecificPrefs= new PreferencesService(project, CeylonPlugin.LANGUAGE_ID);
        } 
        else {
            fLangSpecificPrefs= new PreferencesService(null, CeylonPlugin.LANGUAGE_ID);
        }
        // Now propagate the setting of "spaces for tabs" from either the language-specific preference store,
        // or the IMP runtime's preference store to the UniversalEditor's preference store, where
        // AbstractDecoratedTextEditor.isTabsToSpacesConversionEnabled() will look.
        boolean spacesForTabs= RuntimePlugin.getInstance().getPreferenceStore()
        		.getBoolean(P_SPACES_FOR_TABS);

        getPreferenceStore().setValue(EDITOR_SPACES_FOR_TABS, spacesForTabs);
    }

    private void setupSourcePrefListeners() {
        // If there are no language-specific preferences, use the settings on the IMP preferences page
        if (fLangSpecificPrefs == null ||
                !fLangSpecificPrefs.isDefined(P_SOURCE_FONT) ||
                !fLangSpecificPrefs.isDefined(P_TAB_WIDTH) ||
                !fLangSpecificPrefs.isDefined(P_SPACES_FOR_TABS)) {
            fPropertyListener= new IPropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent event) {
                    if (event.getProperty().equals(P_SOURCE_FONT) &&
                        !fLangSpecificPrefs.isDefined(P_SOURCE_FONT)) {
                        FontData[] newValue= (FontData[]) event.getNewValue();
                        String fontDescriptor= newValue[0].toString();

                        handleFontChange(newValue, fontDescriptor);
                    } else if (event.getProperty().equals(P_TAB_WIDTH) &&
                               !fLangSpecificPrefs.isDefined(P_TAB_WIDTH)) {
                        handleTabsChange(((Integer) event.getNewValue()).intValue());
                    } else if (event.getProperty().equals(P_SPACES_FOR_TABS) &&
                               !fLangSpecificPrefs.isDefined(P_SPACES_FOR_TABS)) {
                        handleSpacesForTabsChange(((Boolean) event.getNewValue()).booleanValue());
                    }
                }
            };
            RuntimePlugin.getInstance().getPreferenceStore().addPropertyChangeListener(fPropertyListener);
        }
        // TODO Perhaps add a flavor of IMP PreferenceListener that notifies for a change to any preference key?
        // Then the following listeners could become just one, at the expense of casting the pref values.
        if (fLangSpecificPrefs != null) {
            fFontListener= new StringPreferenceListener(fLangSpecificPrefs, P_SOURCE_FONT) {
                @Override
                public void changed(String oldValue, String newValue) {
                    FontData[] fontData= PreferenceConverter.readFontData(newValue);
                    handleFontChange(fontData, newValue);
                }
            };
        }
        if (fLangSpecificPrefs != null) {
            fTabListener= new IntegerPreferenceListener(fLangSpecificPrefs, P_TAB_WIDTH) {
                @Override
                public void changed(int oldValue, int newValue) {
                    handleTabsChange(newValue);
                }
            };
        }
        if (fLangSpecificPrefs != null) {
            fSpacesForTabsListener= new BooleanPreferenceListener(fLangSpecificPrefs, P_SPACES_FOR_TABS) {
                @Override
                public void changed(boolean oldValue, boolean newValue) {
                    handleSpacesForTabsChange(newValue);
                }
            };
        }
    }

    private void handleTabsChange(int newTab) {
        if (getSourceViewer() != null) {
            getSourceViewer().getTextWidget().setTabs(newTab);
        }
    }

    private void handleSpacesForTabsChange(boolean newValue) {
        if (getSourceViewer() == null) {
            return;
        }
        // RMF 13 Oct 2010 - The base class tabs-to-spaces converter even translates tabs to
        // spaces before the auto-edit strategy sees the document change commands, which makes
        // handling auto-indent nearly impossible (it never actually sees a tab). Anyway, the
        // auto-edit strategy provides the desired behavior itself, so this isn't even needed.
//        if (newValue) {
//            installTabsToSpacesConverter();
//        } else {
//            uninstallTabsToSpacesConverter();
//        }
        // Apparently un/installing the tabs-to-spaces converter isn't enough - shift left/right needs
        // the "indent prefixes" to be computed properly, which relies on the preference store having
        // the right value for AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS.
        getPreferenceStore().setValue(EDITOR_SPACES_FOR_TABS, newValue);
    }

    private void handleFontChange(FontData[] fontData, String fontDescriptor) {
        FontRegistry fontRegistry= RuntimePlugin.getInstance().getFontRegistry();

        if (!fontRegistry.hasValueFor(fontDescriptor)) {
            fontRegistry.put(fontDescriptor, fontData);
        }
        Font sourceFont= fontRegistry.get(fontDescriptor);

        if (sourceFont != null && getSourceViewer() != null) {
            getSourceViewer().getTextWidget().setFont(sourceFont);
        }
    }

    private void watchDocument(final long reparse_schedule_delay) {
        IDocument doc= getDocumentProvider().getDocument(getEditorInput());
        doc.addDocumentListener(fDocumentListener= new IDocumentListener() {
            public void documentAboutToBeChanged(DocumentEvent event) {}
            public void documentChanged(DocumentEvent event) {
                fParserScheduler.cancel();
                fParserScheduler.schedule(reparse_schedule_delay);
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
     * Sub-classes may override this method. It's intended to allow language-
     * specific editor document-aware services like indexing to get notified
     * when the resource/document association changes.
     */
    protected IResourceDocumentMapListener getResourceDocumentMapListener() {
        return null; // base behavior - nothing to do
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

                if (curDoc != oldDoc) {
                    // Need to unwatch the old document and watch the new document
                    oldDoc.removeDocumentListener(fDocumentListener);
                    curDoc.addDocumentListener(fDocumentListener);

                    // Now notify anyone else who needs to know that the document's
                    // identity changed.
                    IResourceDocumentMapListener rdml = getResourceDocumentMapListener();

                    if (rdml != null) {
                        if (oldDoc != null) {
                            rdml.unregisterDocument(oldDoc);
                        }
                        rdml.updateResourceDocumentMap(curDoc, 
                        		EditorInputUtils.getFile(getEditorInput()), 
                        		CeylonEditor.this);
                    }
                }
            }
        }
    };

    private void watchForSourceMove() {
        // We need to see when the editor input changes, so we can watch the new document
        addPropertyListener(fEditorInputPropertyListener);
        ResourcesPlugin.getWorkspace().addResourceChangeListener(fResourceListener= new IResourceChangeListener() {
            public void resourceChanged(IResourceChangeEvent event) {
                if (event.getType() != IResourceChangeEvent.POST_CHANGE)
                    return;
                CeylonParseController pc= getParseController();
                IPath oldWSRelPath= pc.getProject().getRawProject().getFullPath().append(pc.getPath());
                IResourceDelta rd= event.getDelta().findMember(oldWSRelPath);

                if (rd != null) {
                    if ((rd.getFlags() & IResourceDelta.MOVED_TO) == IResourceDelta.MOVED_TO) {
                        // The net effect of the following is to re-initialize() the IParseController with the new path
                        IPath newPath= rd.getMovedToPath();
                        IPath newProjRelPath= newPath.removeFirstSegments(1);
                        String newProjName= newPath.segment(0);
                        boolean sameProj= pc.getProject().getRawProject().getName().equals(newProjName);

                        try {
                            ISourceProject proj= sameProj ? pc.getProject() : 
                            	ModelFactory.open(ResourcesPlugin.getWorkspace().getRoot().getProject(newProjName));

                            // Tell the IParseController about the move - it caches the path
//                          fParserScheduler.cancel(); // avoid a race condition if ParserScheduler was starting/in the middle of a run
                            pc.initialize(newProjRelPath, proj, fAnnotationCreator);
                        } 
                        catch (ModelException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void setSourceFontFromPreference() {
        String fontName= null;
        if (fLangSpecificPrefs != null) {
            fontName= fLangSpecificPrefs.getStringPreference(P_SOURCE_FONT);
        }
        if (fontName == null) {
            // Don't use the IMP SourceFont pref key on the IMP RuntimePlugin's preference
            // store; use the JFaceResources TEXT_FONT pref key on the WorkbenchPlugin's
            // preference store. This way, the workbench-wide setting in "General" ->
            // "Appearance" => "Colors and Fonts" will have the desired effect, in the
            // absence of a language-specific setting.
//          IPreferenceStore prefStore= RuntimePlugin.getInstance().getPreferenceStore();
//
//          fontName= prefStore.getString(PreferenceConstants.P_SOURCE_FONT);

            fontName= WorkbenchPlugin.getDefault().getPreferenceStore().getString(JFaceResources.TEXT_FONT);
        }
        FontRegistry fontRegistry= RuntimePlugin.getInstance().getFontRegistry();

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
            StructuredSourceViewer sourceViewer= (StructuredSourceViewer) getSourceViewer();

            fEditorErrorTickUpdater= new EditorErrorTickUpdater(this);
            fProblemMarkerManager.addListener(fEditorErrorTickUpdater);
            fAnnotationUpdater= new AnnotationUpdater();
            fProblemMarkerManager.addListener(fAnnotationUpdater);
            
			fParserScheduler= new CeylonParserScheduler(parseController, this, 
					getDocumentProvider(), fAnnotationCreator);

            // The source viewer configuration has already been asked for its ITextHover,
            // but before we actually instantiated the relevant controller class. So update
            // the source viewer, now that we actually have the hover provider.
            HoverHelpController hover = new HoverHelpController(this);
			sourceViewer.setTextHover(hover, DEFAULT_CONTENT_TYPE);
            addModelListener(hover);

            // The source viewer configuration has already been asked for its IContentFormatter,
            // but before we actually instantiated the relevant controller class. So update the
            // source viewer, now that we actually have the IContentFormatter.
            /*ContentFormatter formatter= new ContentFormatter();

            formatter.setFormattingStrategy(fServiceControllerManager.getFormattingController(), 
            		DEFAULT_CONTENT_TYPE);
            sourceViewer.setFormatter(formatter);*/

            try {
            	new PresentationController(getSourceViewer(), parseController)
            	        .damage(new Region(0, sourceViewer.getDocument().getLength()));
            } 
            catch (Exception e) {
            	e.printStackTrace();
            }
            
            ProjectionSupport projectionSupport= new ProjectionSupport(sourceViewer, getAnnotationAccess(),
            		getSharedColors());
            projectionSupport.install();
            sourceViewer.doOperation(ProjectionViewer.TOGGLE);
            fAnnotationModel= sourceViewer.getProjectionAnnotationModel();
            if (fAnnotationModel != null) {
            	fParserScheduler.addModelListener(new FoldingController(fAnnotationModel, 
            			new CeylonFoldingUpdater()));
            }

            if (isEditable()) {
                fParserScheduler.addModelListener(new AnnotationCreatorListener());
            }
            //fServiceControllerManager.setupModelListeners(fParserScheduler);

            // TODO RMF 8/6/2007 - Disable "Mark Occurrences" if no occurrence marker exists for this language
            // The following doesn't work b/c getAction() doesn't find the Mark Occurrences action (why?)
            // if (this.fOccurrenceMarker == null)
            //   getAction("org.eclipse.imp.runtime.actions.markOccurrencesAction").setEnabled(false);

            EditorAnnotationService editorService = new EditorAnnotationService(this);
            fParserScheduler.addModelListener(editorService);

            watchDocument(REPARSE_SCHEDULE_DELAY);
            fParserScheduler.schedule();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static final int REPARSE_SCHEDULE_DELAY= 100;

    private void setTitleImageFromLanguageIcon() {
    	IEditorInput editorInput= getEditorInput();
    	Object fileOrPath= EditorInputUtils.getFile(editorInput);
    	if (fileOrPath == null) {
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
	 * Adds elements to toolbars, menubars and statusbars
	 * 
	 */
	/*private void initializeEditorContributors() {
		if (fLanguage != null) {
			addEditorActions();
			registerEditorContributionsActivator();
		}
	}*/

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

	/*private void unregisterEditorContributionsActivator() {
	    if (fRefreshContributions != null) {
	        getSite().getPage().removePartListener(fRefreshContributions);
	    }
        fRefreshContributions= null;
    }*/

	/**
	 * Uses the LanguageActionsContributor extension point to add
	 * elements to (sub) action bars. 
	 *
	 */
	/*private void addEditorActions() {
		final IActionBars allActionBars = getEditorSite().getActionBars();
		if (fActionBars == null) {
			Set<ILanguageActionsContributor> contributors = ServiceFactory
					.getInstance().getLanguageActionsContributors(fLanguage);

			fActionBars = new SubActionBars(allActionBars);

			IStatusLineManager status = fActionBars.getStatusLineManager();
			IToolBarManager toolbar = fActionBars.getToolBarManager();
			IMenuManager menu = fActionBars.getMenuManager();

			if (!contributors.isEmpty()) {
				throw new RuntimeException();
			}
			
			for (ILanguageActionsContributor c : contributors) {
				c.contributeToStatusLine(this, status);
				c.contributeToToolBar(this, toolbar);
				c.contributeToMenuBar(this, menu);
			}

			fActionBars.updateActionBars();
			allActionBars.updateActionBars();
		}
		allActionBars.updateActionBars();
	}*/
	
    public void dispose() {
        if (fFontListener != null) {
            fFontListener.dispose();
        }
        if (fTabListener != null) {
            fTabListener.dispose();
        }
        if (fSpacesForTabsListener != null) {
            fSpacesForTabsListener.dispose();
        }
        if (fPropertyListener != null) {
            RuntimePlugin.getInstance().getPreferenceStore().removePropertyChangeListener(fPropertyListener);
        }

        //unregisterEditorContributionsActivator();
        if (fEditorErrorTickUpdater != null) {
        	fProblemMarkerManager.removeListener(fEditorErrorTickUpdater);
        }
        if (fAnnotationUpdater != null) {
            fProblemMarkerManager.removeListener(fAnnotationUpdater);
        }
        
        if (fActionBars != null) {
          fActionBars.dispose();
          fActionBars = null;
        }

        if (fDocumentListener != null) {
        	getDocumentProvider().getDocument(getEditorInput()).removeDocumentListener(fDocumentListener);
        }
        
        if (fResourceListener != null) {
        	ResourcesPlugin.getWorkspace().removeResourceChangeListener(fResourceListener);
        }
        if (isEditable() && getResourceDocumentMapListener() != null) {
            getResourceDocumentMapListener().unregisterDocument(getDocumentProvider().getDocument(getEditorInput()));
        }

        fToggleBreakpointAction.dispose(); // this holds onto the IDocument
        fFoldingActionGroup.dispose();

        if (fParserScheduler != null) {
        	fParserScheduler.cancel(); // avoid unnecessary work after the editor is asked to close down
        }
        fParserScheduler= null;
        super.dispose();
        parseController = null;

        if (fResourceListener != null) {
        	ResourcesPlugin.getWorkspace().removeResourceChangeListener(fResourceListener);
        }
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

        ISourceViewer viewer= new StructuredSourceViewer(parent, ruler, 
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

        ILanguageSyntaxProperties syntaxProps= new CeylonLanguageSyntaxProperties();
        getPreferenceStore().setValue(MATCHING_BRACKETS, true);
        if (syntaxProps != null) {
//          fBracketMatcher.setSourceVersion(getPreferenceStore().getString(JavaCore.COMPILER_SOURCE));
            String[][] fences= syntaxProps.getFences();
            if (fences != null) {
                StringBuilder sb= new StringBuilder();
                for(int i= 0; i < fences.length; i++) {
                    sb.append(fences[i][0]);
                    sb.append(fences[i][1]);
                }
                fBracketMatcher= new DefaultCharacterPairMatcher(sb.toString().toCharArray());
                support.setCharacterPairMatcher(fBracketMatcher);
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

        IRegion region= fBracketMatcher.match(document, sourceCaretOffset);
        if (region == null) {
            setStatusLineErrorMessage("No matching fence!");
            sourceViewer.getTextWidget().getDisplay().beep();
            return;
        }

        int offset= region.getOffset();
        int length= region.getLength();

        if (length < 1)
            return;

        int anchor= fBracketMatcher.getAnchor();
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

    public final String PARSE_ANNOTATION = "Parse_Annotation";

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
		Iterator annIter = model.getAnnotationIterator();
		List<MarkerAnnotation> markerAnnotations = new ArrayList<MarkerAnnotation>();
		while (annIter.hasNext()) {
			Object ann = annIter.next();
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
			} catch (CoreException e) {
				// If we get a core exception here, probably something is wrong with the
				// marker, and we probably don't want to keep any annotation that may be
				// associated with it (I don't think)
				model.removeAnnotation(markerAnnotations.get(i));
				continue;
			}
			if (markerParseAnnotations.get(marker) != null) {
				continue;
			} else {
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
    		Iterator annotations = model.getAnnotationIterator();
    		while (annotations.hasNext()) {
    			Object ann = annotations.next();
    			if (ann instanceof MarkerAnnotation) {
    				IMarker marker = ((MarkerAnnotation)ann).getMarker();
    				if (marker.exists()) {
    				    currentMarkers.add(marker);
    				}
    				markerMarkerAnnotations.put(marker, (MarkerAnnotation) ann);
    			} else if (ann instanceof Annotation) {
    				Annotation annotation = (Annotation) ann;

    				if (isParseAnnotation(annotation)) {
    					currentParseAnnotations.add(annotation);
    				}
    			}
    		}

    		// Create a mapping between current markers and parse annotations
    		for (int i = 0; i < currentMarkers.size(); i++) {
    			IMarker marker = currentMarkers.get(i);
				Annotation annotation = findParseAnnotationForMarker(model, marker, currentParseAnnotations);
				if (annotation != null) {
					markerParseAnnotations.put(marker, annotation);
				}
    		}
    	}

    	public Annotation findParseAnnotationForMarker(IAnnotationModel model, IMarker marker, List parseAnnotations) {
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
				Annotation parseAnnotation = (Annotation) parseAnnotations.get(j);
				Position pos = model.getPosition(parseAnnotation);
				if (pos == null)
					// And this would be why?
					continue;

				int annotationStart = pos.offset;
				int annotationLength = pos.length;
				//System.out.println("\tfindParseAnnotationForMarker: Checking annotation offset and length = " + annotationStart + ", " + annotationLength);
				
				if (markerStart == annotationStart && markerLength == annotationLength) {
					//System.out.println("\tfindParseAnnotationForMarker: Returning annotation at offset = " + markerStart);
					return parseAnnotation;
				} 
				else {
  					//System.out.println("\tfindParseAnnotationForMarker: Not returning annotation at offset = " + markerStart);
				}
			}
			
			//System.out.println("  findParseAnnotationForMarker: No corresponding annotation found; returning null");
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
    	        throw new CoreException(new Status(IStatus.ERROR, RuntimePlugin.IMP_RUNTIME, 0, "Unable to read source text", e.getStatus().getException()));
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
    public void addModelListener(IModelListener listener) {
        fParserScheduler.addModelListener(listener);
    }

    /**
     * Remove a Model listener from this editor.
     * 
     * @param listener the listener to remove
     */
    public void removeModelListener(IModelListener listener) {
        fParserScheduler.removeModelListener(listener);
    }

    class PresentationDamager implements IPresentationDamager {
        public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent event, boolean documentPartitioningChanged) {
            // Ask the language's token colorer how much of the document presentation needs to be recomputed.
            final ITokenColorer tokenColorer= new CeylonTokenColorer();
            if (tokenColorer != null)
                return tokenColorer.calculateDamageExtent(partition, getParseController());
            else
                return partition;
        }
        public void setDocument(IDocument document) {}
    }
    
    class PresentationRepairer implements IPresentationRepairer {
	    // For checking whether the damage region has changed
	    ITypedRegion previousDamage= null;

	    final PresentationController pc =  new PresentationController(getSourceViewer(), 
	    		getParseController());

        public void createPresentation(TextPresentation presentation, ITypedRegion damage) {
            boolean hyperlinkRestore= false;

//          try {
//              throw new Exception();
//          } catch (Exception e) {
//              System.out.println("Entered PresentationRepairer.createPresentation()");
//              e.printStackTrace(System.out);
//          }
            // If the given damage region is the same as the previous one, assume it's 
            //due to removing a hyperlink decoration
		    if (previousDamage != null && damage.getOffset() == previousDamage.getOffset() 
		    		&& damage.getLength() == previousDamage.getLength()) {
		        hyperlinkRestore= true;
		    }

		    // BUG Should we really just ignore the presentation passed in???
            // JavaDoc says we're responsible for "merging" our changes in...
            try {
                pc.damage(damage);
                if (hyperlinkRestore) {
                	pc.update(getParseController(), new NullProgressMonitor());
                }
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
            previousDamage= damage;
        }

        public void setDocument(IDocument document) { }
    }

    private IMessageHandler fAnnotationCreator= new AnnotationCreator(this);

    private final IRegionSelectionService fRegionSelector= new IRegionSelectionService() {
        public void selectAndReveal(int startOffset, int length) {
            IEditorPart activeEditor= PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            		.getActivePage().getActiveEditor();
            AbstractTextEditor textEditor= (AbstractTextEditor) activeEditor;
            textEditor.selectAndReveal(startOffset, length);
        }
    };

    private EditorErrorTickUpdater fEditorErrorTickUpdater;

    private IProblemChangedListener fAnnotationUpdater;

    private class AnnotationCreatorListener implements IModelListener {
        public AnalysisRequired getAnalysisRequired() {
            return AnalysisRequired.NONE; // Even if it doesn't scan, it's ok - this posts the error annotations!
        }
        public void update(IParseController parseController, IProgressMonitor monitor) {
            // SMS 25 Apr 2007
            // Since parsing has finished, check whether the marker annotations
            // are up-to-date with the most recent parse annotations.
            // Assuming that's often enough--i.e., don't refresh the marker
            // annotations after every update to the document annotation model
            // since there will be many of these, including possibly many that
            // don't relate to problem markers.
            final IAnnotationTypeInfo annotationTypeInfo= parseController.getAnnotationTypeInfo();
            if (annotationTypeInfo != null) {
                List problemMarkerTypes = annotationTypeInfo.getProblemMarkerTypes();
                for (int i = 0; i < problemMarkerTypes.size(); i++) {
                    refreshMarkerAnnotations((String)problemMarkerTypes.get(i));
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

    public CeylonParseController getParseController() {
        return parseController;
    }
    
    public IOccurrenceMarker getOccurrenceMarker() {
        return new CeylonOccurrenceMarker();
    }

    // SMS 4 May 2006:
    // Added this as the only way I could think of (so far) to
    // remove parser annotations that I expect to be duplicated
    // if a save triggers a build that leads to the creation
    // of markers and another set of annotations.
	/*public void doSave(IProgressMonitor progressMonitor) {
		// SMS 25 Apr 2007:  Removing parser annotations here
		// may not hurt but also doesn't seem to be necessary
		removeParserAnnotations();
		super.doSave(progressMonitor);
	}

    public void removeParserAnnotations() {
    	IAnnotationModel model= getDocumentProvider().getAnnotationModel(getEditorInput());
    	for(Iterator i= model.getAnnotationIterator(); i.hasNext(); ) {
    	    Annotation a= (Annotation) i.next();
    	    if (a.getType().equals(PARSE_ANNOTATION_TYPE))
    	    	model.removeAnnotation(a);
    	}
    }*/

    public String toString() {
        return "Ceylon Editor for " + getEditorInput();
    }
}

class GotoMatchingFenceAction extends Action {
    private final CeylonEditor fEditor;

    public GotoMatchingFenceAction(CeylonEditor editor) {
            super("Go to Matching Fence");
            Assert.isNotNull(editor);
            fEditor= editor;
            setEnabled(true);
//          PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.GOTO_MATCHING_BRACKET_ACTION);
    }

    public void run() {
            fEditor.gotoMatchingFence();
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

abstract class TargetNavigationAction extends Action {
    protected CeylonEditor fEditor;
    protected INavigationTargetFinder fNavTargetFinder;

    protected abstract Object getNavTarget(Object o, Object astRoot);

    protected TargetNavigationAction(String title, String actionDefID) {
        this(null, title, actionDefID);
    }

    public TargetNavigationAction(CeylonEditor editor, String title, String actionDefID) {
        setEditor(editor);
        setText(title);
        setActionDefinitionId(actionDefID);
    }

    public void setEditor(ITextEditor editor) {
        fNavTargetFinder= null;
        if (editor instanceof CeylonEditor) {
            fEditor= (CeylonEditor) editor;
            fNavTargetFinder= null; //TODO??
        } 
        else {
            fEditor= null;
        }
        setEnabled(fNavTargetFinder != null);
    }

    @Override
    public void run() {
        IRegion selection= fEditor.getSelectedRegion();
        CeylonParseController pc= fEditor.getParseController();
        CeylonSourcePositionLocator locator= pc.getSourcePositionLocator();
        Object curNode= locator.findNode(pc.getCurrentAst(), selection.getOffset(), selection.getOffset() + selection.getLength() - 1);
        if (curNode == null || selection.getOffset() == 0) {
            curNode= pc.getCurrentAst();
        }
        Object prev= getNavTarget(curNode, pc.getCurrentAst());
    
        if (prev != null) {
            int prevOffset= locator.getStartOffset(prev);
    
            fEditor.selectAndReveal(prevOffset, 0);
        }
    }
}

class GotoNextTargetAction extends TargetNavigationAction {
    public GotoNextTargetAction() {
        this(null);
    }

    public GotoNextTargetAction(CeylonEditor editor) {
        super(editor, "Go to Next Navigation Target", IEditorActionDefinitionIds.GOTO_NEXT_TARGET);
    }

    @Override
    protected Object getNavTarget(Object o, Object astRoot) {
        return fNavTargetFinder.getNextTarget(o, astRoot);
    }
}

class GotoPreviousTargetAction extends TargetNavigationAction {
    public GotoPreviousTargetAction() {
        this(null);
    }

    public GotoPreviousTargetAction(CeylonEditor editor) {
        super(editor, "Go to Previous Navigation Target", IEditorActionDefinitionIds.GOTO_PREVIOUS_TARGET);
    }

    @Override
    protected Object getNavTarget(Object o, Object astRoot) {
        return fNavTargetFinder.getPreviousTarget(o, astRoot);
    }
}

class SelectEnclosingAction extends Action {
    private CeylonEditor fEditor;
    private INavigationTargetFinder fNavTargetFinder;

    public SelectEnclosingAction() {
        this(null);
    }

    public SelectEnclosingAction(CeylonEditor editor) {
        super("Select Enclosing");
        setActionDefinitionId(IEditorActionDefinitionIds.SELECT_ENCLOSING);
        setEditor(editor);
    }

    public void setEditor(ITextEditor editor) {
        fNavTargetFinder= null;
        if (editor instanceof CeylonEditor) {
            fEditor= (CeylonEditor) editor;
            fNavTargetFinder= null; //TODO???
        } 
        else {
            fEditor= null;
        }
        setEnabled(fNavTargetFinder != null);
    }

    @Override
    public void run() {
        IRegion selection= fEditor.getSelectedRegion();
        CeylonParseController pc= fEditor.getParseController();
        CeylonSourcePositionLocator locator= pc.getSourcePositionLocator();
        Object curNode= locator.findNode(pc.getCurrentAst(), selection.getOffset(), selection.getOffset() + selection.getLength() - 1);
        if (curNode == null || selection.getOffset() == 0) {
            curNode= pc.getCurrentAst();
        }
        Object enclosing= fNavTargetFinder.getEnclosingConstruct(curNode, pc.getCurrentAst());
    
        if (enclosing != null) {
            int enclOffset= locator.getStartOffset(enclosing);
            int enclEnd= locator.getEndOffset(enclosing);

            fEditor.selectAndReveal(enclOffset, enclEnd - enclOffset + 1);
        }
    }
}

class FoldingController implements IModelListener {
    private final ProjectionAnnotationModel fAnnotationModel;
    private final IFoldingUpdater fFoldingUpdater;

    public FoldingController(ProjectionAnnotationModel annotationModel, IFoldingUpdater foldingUpdater) {
        super();
        this.fAnnotationModel= annotationModel;
        this.fFoldingUpdater= foldingUpdater;
    }

    public AnalysisRequired getAnalysisRequired() {
        return AnalysisRequired.SYNTACTIC_ANALYSIS;
    }

    public void update(IParseController parseController, IProgressMonitor monitor) {
        if (fAnnotationModel != null) { // can be null if file is outside workspace
            try {
                fFoldingUpdater.updateFoldingStructure(parseController, fAnnotationModel);
            } catch (Exception e) {
                RuntimePlugin.getInstance().logException("Error while updating folding annotations for " + parseController.getPath(), e);
            }
        }
    }
}
