package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.AUTO_ACTIVATION;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.AUTO_ACTIVATION_DELAY;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.AUTO_INSERT;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.AUTO_INSERT_PREFIX;
import static org.eclipse.jdt.ui.PreferenceConstants.APPEARANCE_JAVADOC_FONT;
import static org.eclipse.jface.dialogs.DialogSettings.getOrCreateSection;
import static org.eclipse.jface.text.AbstractInformationControlManager.ANCHOR_GLOBAL;
import static org.eclipse.jface.text.IDocument.DEFAULT_CONTENT_TYPE;

import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
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

import com.redhat.ceylon.eclipse.code.browser.BrowserInformationControl;
import com.redhat.ceylon.eclipse.code.complete.CeylonCompletionProcessor;
import com.redhat.ceylon.eclipse.code.correct.CeylonCorrectionProcessor;
import com.redhat.ceylon.eclipse.code.hover.AnnotationHover;
import com.redhat.ceylon.eclipse.code.hover.BestMatchHover;
import com.redhat.ceylon.eclipse.code.hover.DocumentationHover;
import com.redhat.ceylon.eclipse.code.html.HTMLTextPresenter;
import com.redhat.ceylon.eclipse.code.outline.HierarchyPopup;
import com.redhat.ceylon.eclipse.code.outline.OutlinePopup;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.resolve.CeylonHyperlinkDetector;
import com.redhat.ceylon.eclipse.code.resolve.JavaHyperlinkDetector;
import com.redhat.ceylon.eclipse.code.search.ReferencesPopup;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EditorUtil;

public class CeylonSourceViewerConfiguration extends TextSourceViewerConfiguration {
    
    protected final CeylonEditor editor;
    
    public CeylonSourceViewerConfiguration(CeylonEditor editor) {
        super(EditorsUI.getPreferenceStore());
        this.editor = editor;
    }
    
    public PresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        PresentationReconciler reconciler = new PresentationReconciler();
        //make sure we pass the sourceViewer we get as an argument here
        //otherwise it breaks syntax highlighting in Code popup
        PresentationDamageRepairer damageRepairer = 
                new PresentationDamageRepairer(sourceViewer, editor);
        reconciler.setRepairer(damageRepairer, DEFAULT_CONTENT_TYPE);
        reconciler.setDamager(damageRepairer, DEFAULT_CONTENT_TYPE);
        return reconciler;
    }
    
    private static final class CompletionListener 
            implements ICompletionListener {
        
        private CeylonEditor editor;
        private CeylonCompletionProcessor processor;
        
        private CompletionListener(CeylonEditor editor,
                CeylonCompletionProcessor processor) {
            this.editor = editor;
            this.processor = processor;
            
        }
        @Override
        public void selectionChanged(ICompletionProposal proposal,
                boolean smartToggle) {}
        
        @Override
        public void assistSessionStarted(ContentAssistEvent event) {
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
        public void assistSessionEnded(ContentAssistEvent event) {
            if (editor!=null) {
                editor.unpauseBackgroundParsing();
                editor.scheduleParsing();
            }
        }
    }

    public ContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
        if (editor==null) return null;
        ContentAssistant contentAssistant = new ContentAssistant();
        contentAssistant.setRestoreCompletionProposalSize(getOrCreateSection(getSettings(),
                "completion_proposal_popup"));
        CeylonCompletionProcessor completionProcessor = new CeylonCompletionProcessor(editor);
        contentAssistant.addCompletionListener(new CompletionListener(editor, completionProcessor));
        contentAssistant.setContentAssistProcessor(completionProcessor, DEFAULT_CONTENT_TYPE);
        configCompletionPopup(contentAssistant);
        contentAssistant.enableColoredLabels(true);
        contentAssistant.setRepeatedInvocationMode(true);
        KeyStroke key = KeyStroke.getInstance(SWT.CTRL, SWT.SPACE);
        contentAssistant.setRepeatedInvocationTrigger(KeySequence.getInstance(key));
        contentAssistant.setStatusMessage(key.format() + " to toggle second-level completions");
        contentAssistant.setStatusLineVisible(true);
        contentAssistant.setInformationControlCreator(new DocumentationHover(editor).getHoverControlCreator("Click for focus"));
        contentAssistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
//      contentAssistant.setContextInformationPopupBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        return contentAssistant;
    }

    static void configCompletionPopup(ContentAssistant contentAssistant) {
        IPreferenceStore preferenceStore = EditorUtil.getPreferences();
        contentAssistant.enableAutoInsert(preferenceStore.getBoolean(AUTO_INSERT));
        contentAssistant.enableAutoActivation(preferenceStore.getBoolean(AUTO_ACTIVATION));
        contentAssistant.setAutoActivationDelay(preferenceStore.getInt(AUTO_ACTIVATION_DELAY));
        contentAssistant.enablePrefixCompletion(preferenceStore.getBoolean(AUTO_INSERT_PREFIX));
    }

    @Override
    public IQuickAssistAssistant getQuickAssistAssistant(ISourceViewer sourceViewer) {
        if (editor==null) return null;
        CeylonCorrectionProcessor quickAssist = new CeylonCorrectionProcessor(editor);
        quickAssist.setRestoreCompletionProposalSize(getOrCreateSection(getSettings(), 
                "quickassist_proposal_popup"));
        quickAssist.enableColoredLabels(true);
        return quickAssist;
    }

    public AnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
        return new AnnotationHover(editor, true);
    }

    public AnnotationHover getOverviewRulerAnnotationHover(ISourceViewer sourceViewer) {
        return new AnnotationHover(editor, true);
    }

    public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
        return new IAutoEditStrategy[] { new CeylonAutoEditStrategy() };
    }
        
    public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
        return new DoubleClickStrategy(); 
    }

    public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
        CeylonParseController pc = getParseController();
        if (pc==null) {
            return new IHyperlinkDetector[0];
        }
        else {
            return new IHyperlinkDetector[] { 
                    new CeylonHyperlinkDetector(pc), 
                    new JavaHyperlinkDetector(pc) 
                };
        }
    }
    
    //TODO: We need a CeylonParseControllerProvider 
    //      which CeylonEditor implements - since
    //      having to extend this class and override
    //      is just sucky.
    protected CeylonParseController getParseController() {
        if (editor==null) {
            return null;
        }
        else {
            return editor.getParseController();
        }
    }
    
    /**
     * The PeekDefinitionPopup shares the editor that it is
     * servicing, but it has its own CeylonParseController
     * for the file it is actually displaying. Therefore, 
     * the hyperlink detector must be redirected to that
     * parse controller.
     */
    private static final class PopupSourceViewerConfiguration 
            extends CeylonSourceViewerConfiguration {
        private final PeekDefinitionPopup popup;
        private PopupSourceViewerConfiguration(CeylonEditor editor,
                PeekDefinitionPopup popup) {
            super(editor);
            this.popup = popup;
        }
        
        @Override
        protected CeylonParseController getParseController() {
            return popup.getParseController();
        }
    }

    /**
     * Used to present hover help (anything else?)
     */
    public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
        return new BrowserControlCreator();
    }

    public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
        if (editor==null) return null;
        return new BestMatchHover(editor);
    }

    /*public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {
        if (infoPresenter == null) {
            infoPresenter= new InformationPresenter(getInformationControlCreator(sourceViewer));
            infoPresenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
            infoPresenter.setAnchor(ANCHOR_GLOBAL);

            IInformationProvider provider= new HoverInformationProvider();
            infoPresenter.setInformationProvider(provider, IDocument.DEFAULT_CONTENT_TYPE);
            //infoPresenter.setSizeConstraints(500, 100, true, false);
            //infoPresenter.setRestoreInformationControlBounds(getSettings("outline_presenter_bounds"), true, true); //$NON-NLS-1$
        }
        return infoPresenter;
    }

    private final class HoverInformationProvider implements IInformationProvider {
        private IAnnotationModel annotationModel= editor.getDocumentProvider()
                .getAnnotationModel(editor.getEditorInput());

        private List<Annotation> getParserAnnotationsAtOffset(int offset) {
            List<Annotation> result= new LinkedList<Annotation>();
            if (annotationModel != null) {
                for(Iterator<Annotation> iter= annotationModel.getAnnotationIterator(); 
                        iter.hasNext(); ) {
                    Annotation ann= iter.next();
                    if (annotationModel.getPosition(ann).includes(offset) && 
                            isParseAnnotation(ann)) {
                        result.add(ann);
                    }
                }
            }
            return result;
        }

        public IRegion getSubject(ITextViewer textViewer, int offset) {
            List<Annotation> parserAnnsAtOffset = getParserAnnotationsAtOffset(offset);
            if (!parserAnnsAtOffset.isEmpty()) {
                Annotation ann= parserAnnsAtOffset.get(0);
                Position pos= annotationModel.getPosition(ann);
                return new Region(pos.offset, pos.length);
            }
            Node selNode= findNode(editor.getParseController().getRootNode(), offset);
            return new Region(getStartOffset(selNode), getLength(selNode));
        }

        public String getInformation(ITextViewer textViewer, IRegion subject) {
            List<Annotation> parserAnnsAtOffset = getParserAnnotationsAtOffset(subject.getOffset());
            if (!parserAnnsAtOffset.isEmpty()) {
                return parserAnnsAtOffset.get(0).getText();
            }

            CeylonParseController pc = editor.getParseController();
            Node selNode= findNode(pc.getRootNode(), subject.getOffset());
            return new CeylonDocumentationProvider().getDocumentation(selNode, pc);
            return null;
        }
    }*/

    private IDialogSettings getSettings() {
        return CeylonPlugin.getInstance().getDialogSettings(); 
    }
    
    public IInformationPresenter getOutlinePresenter(ISourceViewer sourceViewer) {
        if (editor==null) return null;
        InformationPresenter presenter = 
                new InformationPresenter(new OutlinePresenterControlCreator(editor));
        presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
        presenter.setAnchor(ANCHOR_GLOBAL);
        presenter.setInformationProvider(new DummyInformationProvider(), 
                DEFAULT_CONTENT_TYPE);
        presenter.setSizeConstraints(50, 10, true, false);
        presenter.setRestoreInformationControlBounds(getOrCreateSection(getSettings(),
                "outline_presenter_bounds"), true, true);
        return presenter;
    }
    
    public IInformationPresenter getHierarchyPresenter(ISourceViewer sourceViewer) {
        if (editor==null) return null;
        InformationPresenter presenter = 
                new InformationPresenter(new HierarchyPresenterControlCreator(editor));
        presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
        presenter.setAnchor(ANCHOR_GLOBAL);
        presenter.setInformationProvider(new DummyInformationProvider(), 
                DEFAULT_CONTENT_TYPE);
        presenter.setSizeConstraints(80, 15, true, false);
        presenter.setRestoreInformationControlBounds(getOrCreateSection(getSettings(),
                "hierarchy_presenter_bounds"), true, true);
        return presenter;
    }
    
    public IInformationPresenter getDefinitionPresenter(ISourceViewer sourceViewer) {
        if (editor==null) return null;
        InformationPresenter presenter = new InformationPresenter(new DefinitionPresenterControlCreator(editor));
        presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
        presenter.setAnchor(ANCHOR_GLOBAL);
        presenter.setInformationProvider(new DummyInformationProvider(), 
                DEFAULT_CONTENT_TYPE);
        presenter.setSizeConstraints(80, 25, true, false);
        presenter.setRestoreInformationControlBounds(getOrCreateSection(getSettings(),
                "code_presenter_bounds"), true, true);
        return presenter;
    }
    
    public IInformationPresenter getReferencesPresenter(ISourceViewer sourceViewer) {
        if (editor==null) return null;
        InformationPresenter presenter = new InformationPresenter(new ReferencesPresenterControlCreator(editor));
        presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
        presenter.setAnchor(ANCHOR_GLOBAL);
        presenter.setInformationProvider(new DummyInformationProvider(), 
                DEFAULT_CONTENT_TYPE);
        presenter.setSizeConstraints(80, 10, true, false);
        presenter.setRestoreInformationControlBounds(getOrCreateSection(getSettings(),
                "refs_presenter_bounds"), true, true);
        return presenter;
    }
    
    private static final class BrowserControlCreator 
            implements IInformationControlCreator {
        @Override
        public IInformationControl createInformationControl(Shell parent) {
            try {
                return new BrowserInformationControl(parent, 
                        APPEARANCE_JAVADOC_FONT, 
                        (String) null);
            }
            catch(org.eclipse.swt.SWTError x){
                return new DefaultInformationControl(parent, 
                        "Press 'F2' for focus", 
                        new HTMLTextPresenter(true));
            }
        }
    }
    
    private static int getPopupStyle() {
        String platform = SWT.getPlatform();
        int resize = platform.equals("carbon") || platform.equals("cocoa") ? SWT.RESIZE : SWT.NONE;
        return /*SWT.V_SCROLL | SWT.H_SCROLL |*/ resize;
    }
    
    private static final class OutlinePresenterControlCreator implements
            IInformationControlCreator {
        private CeylonEditor editor;
        private OutlinePresenterControlCreator(CeylonEditor editor) {
            this.editor = editor;
        }
        @Override
        public IInformationControl createInformationControl(Shell parent) {
            return new OutlinePopup(editor, parent, getPopupStyle());
        }
    }
    
    private static final class HierarchyPresenterControlCreator
    implements IInformationControlCreator {
        private CeylonEditor editor;
        private HierarchyPresenterControlCreator(CeylonEditor editor) {
            this.editor = editor;
        }
        @Override
        public IInformationControl createInformationControl(Shell parent) {
            return new HierarchyPopup(editor, parent, getPopupStyle());
        }
    }
    
    private static final class DefinitionPresenterControlCreator 
            implements IInformationControlCreator {
        private CeylonEditor editor;
        private DefinitionPresenterControlCreator(CeylonEditor editor) {
            this.editor = editor;
        }
        @Override
        public IInformationControl createInformationControl(Shell parent) {
            PeekDefinitionPopup popup = 
                    new PeekDefinitionPopup(parent, getPopupStyle(), editor);
            popup.getViewer().configure(new PopupSourceViewerConfiguration(editor, popup));
            return popup;
        }
    }
    
    private static final class ReferencesPresenterControlCreator 
            implements IInformationControlCreator {
        private CeylonEditor editor;
        private ReferencesPresenterControlCreator(CeylonEditor editor) {
            this.editor = editor;
        }
        @Override
        public IInformationControl createInformationControl(Shell parent) {
            return new ReferencesPopup(parent, getPopupStyle(), editor);
        }
    }
    
    private static final class DummyInformationProvider 
            implements IInformationProvider, IInformationProviderExtension {
//        private CeylonParseController parseController;
//        DummyInformationProvider(CeylonParseController parseController) {
//            this.parseController = parseController;
//        }
        @Override
        public IRegion getSubject(ITextViewer textViewer, int offset) {
            return new Region(offset, 0); // Could be anything, since it's ignored below in getInformation2()...
        }
        @Override
        public String getInformation(ITextViewer textViewer, IRegion subject) {
            // shouldn't be called, given IInformationProviderExtension???
            throw new UnsupportedOperationException();
        }
        @Override
        public Object getInformation2(ITextViewer textViewer, IRegion subject) {
            return new Object();
        }
    }
    
    @Override
    public IReconciler getReconciler(ISourceViewer sourceViewer) {
        //don't spell-check!
        return null;
    }
    
}
