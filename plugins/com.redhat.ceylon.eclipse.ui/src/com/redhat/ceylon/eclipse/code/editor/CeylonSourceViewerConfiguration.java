package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.AUTO_ACTIVATION;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.AUTO_ACTIVATION_DELAY;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.AUTO_INSERT;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.AUTO_INSERT_PREFIX;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.completionJ2C;
import static com.redhat.ceylon.eclipse.util.EditorUtil.createColor;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getPopupStyle;
import static com.redhat.ceylon.eclipse.util.Highlights.DOC_BACKGROUND;
import static com.redhat.ceylon.eclipse.util.Highlights.getCurrentThemeColor;
import static org.eclipse.jdt.ui.PreferenceConstants.APPEARANCE_JAVADOC_FONT;
import static org.eclipse.jface.dialogs.DialogSettings.getOrCreateSection;
import static org.eclipse.jface.text.AbstractInformationControlManager.ANCHOR_GLOBAL;
import static org.eclipse.jface.text.IDocument.DEFAULT_CONTENT_TYPE;
import static org.eclipse.ui.texteditor.AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT;
import static org.eclipse.ui.texteditor.AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND;
import static org.eclipse.ui.texteditor.AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT;

import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.IInformationProviderExtension;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;

import com.redhat.ceylon.eclipse.code.browser.BrowserInformationControl;
import com.redhat.ceylon.eclipse.code.complete.EclipseCompletionProcessor;
import com.redhat.ceylon.eclipse.code.correct.CeylonCorrectionProcessor;
import com.redhat.ceylon.eclipse.code.hover.AnnotationHover;
import com.redhat.ceylon.eclipse.code.hover.BestMatchHover;
import com.redhat.ceylon.eclipse.code.hover.CeylonInformationControlCreator;
import com.redhat.ceylon.eclipse.code.hover.CeylonInformationProvider;
import com.redhat.ceylon.eclipse.code.hover.CeylonSourceHover;
import com.redhat.ceylon.eclipse.code.outline.HierarchyPopup;
import com.redhat.ceylon.eclipse.code.outline.OutlinePopup;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.resolve.CeylonHyperlinkDetector;
import com.redhat.ceylon.eclipse.code.resolve.CeylonJavaBackendHyperlinkDetector;
import com.redhat.ceylon.eclipse.code.resolve.CeylonJavascriptBackendHyperlinkDetector;
import com.redhat.ceylon.eclipse.code.resolve.CeylonNativeHeaderHyperlinkDetector;
import com.redhat.ceylon.eclipse.code.resolve.JavaHyperlinkDetector;
import com.redhat.ceylon.eclipse.code.resolve.ReferencesHyperlinkDetector;
import com.redhat.ceylon.eclipse.code.search.ReferencesPopup;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonSourceViewerConfiguration 
        extends TextSourceViewerConfiguration {
    
    protected final CeylonEditor editor;
    
    public CeylonSourceViewerConfiguration(CeylonEditor editor) {
        super(EditorsUI.getPreferenceStore());
        this.editor = editor;
    }
    
    public PresentationReconciler getPresentationReconciler(
            ISourceViewer sourceViewer) {
        PresentationReconciler reconciler = 
                new PresentationReconciler();
        //make sure we pass the sourceViewer we get as an argument here
        //otherwise it breaks syntax highlighting in Code popup
        PresentationDamageRepairer damageRepairer = 
                new PresentationDamageRepairer(sourceViewer, 
                        editor);
        reconciler.setRepairer(damageRepairer, 
                DEFAULT_CONTENT_TYPE);
        reconciler.setDamager(damageRepairer, 
                DEFAULT_CONTENT_TYPE);
        return reconciler;
    }
    
    private static final class CompletionListener 
            implements ICompletionListener {
        
        private CeylonEditor editor;
        private EclipseCompletionProcessor processor;
//        private CeylonCompletionProcessor processor;
        
        private CompletionListener(CeylonEditor editor,
//                CeylonCompletionProcessor processor) {
                EclipseCompletionProcessor processor) {
            this.editor = editor;
            this.processor = processor;
            
        }
        @Override
        public void selectionChanged(
                ICompletionProposal proposal,
                boolean smartToggle) {}
        
        @Override
        public void assistSessionStarted(
                ContentAssistEvent event) {
            if (editor!=null) {
                editor.pauseBackgroundParsing();
            }
            processor.sessionStarted();
            /*try {
                editor.getSite().getWorkbenchWindow().run(true, true, new Warmup());
            } 
            catch (Exception e) {}*/
        }
        
        @Override
        public void assistSessionEnded(
                ContentAssistEvent event) {
            if (editor!=null) {
                editor.unpauseBackgroundParsing();
                editor.scheduleParsing(false);
            }
        }
    }

    public ContentAssistant getContentAssistant(
            ISourceViewer sourceViewer) {
        if (editor==null) return null;
        ContentAssistant contentAssistant = 
                new ContentAssistant();
        contentAssistant.setRestoreCompletionProposalSize(
                getOrCreateSection(getSettings(),
                "completion_proposal_popup"));
        EclipseCompletionProcessor completionProcessor = 
                completionJ2C().newCompletionProcessor(editor);
//        CeylonCompletionProcessor completionProcessor =
//            new CeylonCompletionProcessor(editor);
        CompletionListener listener = 
                new CompletionListener(editor, 
                        completionProcessor);
        contentAssistant.addCompletionListener(listener);
        contentAssistant.setContentAssistProcessor(
                completionProcessor, 
                DEFAULT_CONTENT_TYPE);
        configCompletionPopup(contentAssistant);
        contentAssistant.enableColoredLabels(true);
        contentAssistant.setRepeatedInvocationMode(true);
        KeyStroke key = 
                KeyStroke.getInstance(SWT.CTRL, SWT.SPACE);
        contentAssistant.setRepeatedInvocationTrigger(
                KeySequence.getInstance(key));
        contentAssistant.setStatusMessage(key.format() + 
                " to toggle second-level completions");
        contentAssistant.setStatusLineVisible(true);
        contentAssistant.setInformationControlCreator(
                new CeylonInformationControlCreator(editor, 
                        "Tab or click for focus"));
        contentAssistant.setContextInformationPopupOrientation(
                IContentAssistant.CONTEXT_INFO_ABOVE);
        return contentAssistant;
    }

    static void configCompletionPopup(
            ContentAssistant contentAssistant) {
        IPreferenceStore preferenceStore = 
        		CeylonPlugin.getPreferences();
        if (preferenceStore!=null) {
            contentAssistant.enableAutoInsert(
                    preferenceStore.getBoolean(AUTO_INSERT));
            contentAssistant.enableAutoActivation(
                    preferenceStore.getBoolean(AUTO_ACTIVATION));
            contentAssistant.setAutoActivationDelay(
                    preferenceStore.getInt(AUTO_ACTIVATION_DELAY));
            contentAssistant.enablePrefixCompletion(
                    preferenceStore.getBoolean(AUTO_INSERT_PREFIX));
        }
    }
    
    @Override
    public IQuickAssistAssistant getQuickAssistAssistant(
            ISourceViewer sourceViewer) {
        if (editor==null) return null;
        CeylonCorrectionProcessor quickAssist = 
                new CeylonCorrectionProcessor(editor);
        quickAssist.setRestoreCompletionProposalSize(
                getOrCreateSection(getSettings(), 
                "quickassist_proposal_popup"));
        quickAssist.enableColoredLabels(true);
        return quickAssist;
    }

    public AnnotationHover getAnnotationHover(
            ISourceViewer sourceViewer) {
        return new AnnotationHover(editor, true);
    }

    public AnnotationHover getOverviewRulerAnnotationHover(
            ISourceViewer sourceViewer) {
        return new AnnotationHover(editor, true);
    }

    public IAutoEditStrategy[] getAutoEditStrategies(
            ISourceViewer sourceViewer, String contentType) {
        return new IAutoEditStrategy[] { 
                new CeylonAutoEditStrategy() };
    }
        
    public ITextDoubleClickStrategy getDoubleClickStrategy(
            ISourceViewer sourceViewer, String contentType) {
        return new DoubleClickStrategy(); 
    }

    public IHyperlinkDetector[] getHyperlinkDetectors(
            ISourceViewer sourceViewer) {
        CeylonParseController controller = 
                getParseController();
        if (controller==null) {
            return new IHyperlinkDetector[0];
        }
        else {
            return new IHyperlinkDetector[] { 
                    new CeylonHyperlinkDetector(
                            editor, controller), 
                    new CeylonNativeHeaderHyperlinkDetector(
                            editor, controller), 
                    new CeylonJavaBackendHyperlinkDetector(
                            editor, controller), 
                    new CeylonJavascriptBackendHyperlinkDetector(
                            editor, controller), 
                    new JavaHyperlinkDetector(controller),
                    new ReferencesHyperlinkDetector(
                            editor, controller)
                };
        }
    }
    
    //TODO: We need a CeylonParseControllerProvider 
    //      which CeylonEditor implements - since
    //      having to extend this class and override
    //      is just sucky.
    protected CeylonParseController getParseController() {
        return editor==null ? null : 
            editor.getParseController();
    }
    
    @Override
    public IInformationPresenter getInformationPresenter(
            ISourceViewer sourceViewer) {
        InformationPresenter presenter = 
                new InformationPresenter(
                        new CeylonInformationControlCreator(
                                editor, "F2 for focus"));
        presenter.setDocumentPartitioning(
                getConfiguredDocumentPartitioning(sourceViewer));
        presenter.setInformationProvider(
                new CeylonInformationProvider(editor), 
                DEFAULT_CONTENT_TYPE);
        // sizes: see org.eclipse.jface.text.TextViewer.TEXT_HOVER_*_CHARS
        presenter.setSizeConstraints(100, 40, false, true);
        return presenter;
    }
    
    /**
     * Used to present hover help (anything else?)
     */
    public IInformationControlCreator 
    getInformationControlCreator(ISourceViewer sourceViewer) {
        return new IInformationControlCreator() {
            @Override
            public IInformationControl 
            createInformationControl(Shell parent) {
                BrowserInformationControl control = 
                		new BrowserInformationControl(parent, 
                				APPEARANCE_JAVADOC_FONT, false);
                IPreferenceStore editorPreferenceStore = 
                		EditorsPlugin.getDefault().getPreferenceStore();
        		if (!editorPreferenceStore.getBoolean(
        				PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT)) {
        			control.setBackgroundColor(
        			    getCurrentThemeColor(DOC_BACKGROUND));
//                		createColor(CeylonPlugin.getPreferences(), 
//                				PREFERENCE_COLOR_BACKGROUND));
        		}
        		if (!editorPreferenceStore.getBoolean(
        				PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT)) {
        			control.setForegroundColor(
        					createColor(editorPreferenceStore, 
                				PREFERENCE_COLOR_FOREGROUND));
        		}
				return control;
            }
        };
    }
    
    private static final int[] STATE_MASKS =
            new int[] { ITextViewerExtension2.DEFAULT_HOVER_STATE_MASK, SWT.SHIFT };
    
    @Override
    public int[] getConfiguredTextHoverStateMasks(
            ISourceViewer sourceViewer, String contentType) {
        return STATE_MASKS;
    }
    
    @Override
    public ITextHover getTextHover(
            ISourceViewer sourceViewer, String contentType, 
            int stateMask) {
        if (stateMask==SWT.SHIFT) {
            return new CeylonSourceHover(editor);
        }
        else {
            return getTextHover(sourceViewer, contentType);
        }
    }

    public ITextHover getTextHover(
            ISourceViewer sourceViewer, String contentType) {
        if (editor==null) return null;
        return new BestMatchHover(editor);
    }

    private IDialogSettings getSettings() {
        return CeylonPlugin.getInstance()
                .getDialogSettings(); 
    }
    
    private InformationPresenter createPresenter(
            ISourceViewer sourceViewer, 
            IInformationControlCreator creator) {
        InformationPresenter presenter = 
                new InformationPresenter(creator);
        String partitioning = 
                getConfiguredDocumentPartitioning(sourceViewer);
        presenter.setDocumentPartitioning(partitioning);
        presenter.setAnchor(ANCHOR_GLOBAL);
        presenter.setInformationProvider(
                new DummyInformationProvider(), 
                DEFAULT_CONTENT_TYPE);
        return presenter;
    }
    
    public IInformationPresenter getOutlinePresenter(
            ISourceViewer sourceViewer) {
        if (editor==null) {
            return null;
        }
        OutlinePresenterControlCreator creator = 
                new OutlinePresenterControlCreator(editor);
        InformationPresenter presenter = 
                createPresenter(sourceViewer, creator);
        presenter.setSizeConstraints(60, 20, true, false);
        presenter.setRestoreInformationControlBounds(
                getOrCreateSection(getSettings(),
                        "outline_presenter_bounds"), 
                true, true);
        return presenter;
    }

    public IInformationPresenter getHierarchyPresenter(
            ISourceViewer sourceViewer) {
        if (editor==null) {
            return null;
        }
        HierarchyPresenterControlCreator creator = 
                new HierarchyPresenterControlCreator(editor);
        InformationPresenter presenter = 
                createPresenter(sourceViewer, creator);
        presenter.setSizeConstraints(80, 20, true, false);
        presenter.setRestoreInformationControlBounds(
                getOrCreateSection(getSettings(),
                        "hierarchy_presenter_bounds"), 
                true, true);
        return presenter;
    }
    
    public IInformationPresenter getDefinitionPresenter(
            ISourceViewer sourceViewer) {
        if (editor==null) {
            return null;
        }
        DefinitionPresenterControlCreator creator = 
                new DefinitionPresenterControlCreator(editor);
        InformationPresenter presenter = 
                createPresenter(sourceViewer, creator);
        presenter.setSizeConstraints(80, 25, true, false);
        presenter.setRestoreInformationControlBounds(
                getOrCreateSection(getSettings(),
                        "code_presenter_bounds"), 
                true, true);
        return presenter;
    }
    
    public IInformationPresenter getReferencesPresenter(
            ISourceViewer sourceViewer) {
        if (editor==null) {
            return null;
        }
        ReferencesPresenterControlCreator creator = 
                new ReferencesPresenterControlCreator(editor);
        InformationPresenter presenter = 
                createPresenter(sourceViewer, creator);
        presenter.setSizeConstraints(80, 20, true, false);
        presenter.setRestoreInformationControlBounds(
                getOrCreateSection(getSettings(),
                        "refs_presenter_bounds"), 
                true, true);
        return presenter;
    }
    
    private static final class OutlinePresenterControlCreator 
            implements IInformationControlCreator {
        private CeylonEditor editor;
        private OutlinePresenterControlCreator(
                CeylonEditor editor) {
            this.editor = editor;
        }
        @Override
        public IInformationControl createInformationControl(
                Shell parent) {
            return new OutlinePopup(editor, parent, 
                    getPopupStyle());
        }
    }
    
    private static final class HierarchyPresenterControlCreator
            implements IInformationControlCreator {
        private CeylonEditor editor;
        private HierarchyPresenterControlCreator(
                CeylonEditor editor) {
            this.editor = editor;
        }
        @Override
        public IInformationControl createInformationControl(
                Shell parent) {
            return new HierarchyPopup(editor, parent, 
                    getPopupStyle());
        }
    }
    
    private static final class DefinitionPresenterControlCreator 
            implements IInformationControlCreator {
        private CeylonEditor editor;
        private DefinitionPresenterControlCreator(
                CeylonEditor editor) {
            this.editor = editor;
        }
        @Override
        public IInformationControl createInformationControl(
                Shell parent) {
            return new PeekDefinitionPopup(parent, 
                    getPopupStyle(), editor);
        }
    }
    
    private static final class ReferencesPresenterControlCreator 
            implements IInformationControlCreator {
        private CeylonEditor editor;
        private ReferencesPresenterControlCreator(
                CeylonEditor editor) {
            this.editor = editor;
        }
        @Override
        public IInformationControl createInformationControl(
                Shell parent) {
            return new ReferencesPopup(parent, 
                    getPopupStyle(), editor);
        }
    }
    
    private static final class DummyInformationProvider 
            implements IInformationProvider, 
                       IInformationProviderExtension {
        @Override
        public IRegion getSubject(
                ITextViewer textViewer, int offset) {
            return new Region(offset, 0); // Could be anything, since it's ignored below in getInformation2()...
        }
        @Override
        public String getInformation(
                ITextViewer textViewer, IRegion subject) {
            // shouldn't be called, given IInformationProviderExtension???
            throw new UnsupportedOperationException();
        }
        @Override
        public Object getInformation2(
                ITextViewer textViewer, IRegion subject) {
            return new Object();
        }
    }
    
    @Override
    public IReconciler getReconciler(ISourceViewer sourceViewer) {
        //don't spell-check!
        return null;
    }
    
}
