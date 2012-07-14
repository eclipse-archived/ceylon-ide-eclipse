package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.CeylonEditor.isParseAnnotation;
import static org.eclipse.imp.editor.IEditorActionDefinitionIds.SHOW_OUTLINE;
import static org.eclipse.jface.text.AbstractInformationControlManager.ANCHOR_GLOBAL;
import static org.eclipse.jface.text.IDocument.DEFAULT_CONTENT_TYPE;
import static org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.imp.editor.LanguageServiceManager;
import org.eclipse.imp.editor.ServiceControllerManager;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.services.IDocumentationProvider;
import org.eclipse.imp.services.base.DefaultAnnotationHover;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.IInformationProviderExtension;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

import com.redhat.ceylon.eclipse.code.hover.HoverHelpController;
import com.redhat.ceylon.eclipse.code.outline.CeylonOutlineBuilder;
import com.redhat.ceylon.eclipse.code.outline.HierarchyPopup;
import com.redhat.ceylon.eclipse.code.outline.OutlinePopup;
import com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixController;
import com.redhat.ceylon.eclipse.code.resolve.JavaReferenceResolver;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class StructuredSourceViewerConfiguration extends TextSourceViewerConfiguration {
	
    protected final CeylonEditor fEditor;
    private ServiceControllerManager fServiceControllerManager;
    private LanguageServiceManager fLanguageServiceManager;
    private IPreferencesService fLangSpecificPrefs;
    private CompletionProcessor processor;

    public StructuredSourceViewerConfiguration(IPreferenceStore prefStore, CeylonEditor editor) {
        super(prefStore);
        fEditor = editor;
        processor = new CompletionProcessor();
        // Can't cache the ServiceControllerManager, LangaugeServiceManager, or the IPreferencesService
        // yet, b/c they haven't been set up by the editor yet. Retrieve them lazily.
    }

    protected ServiceControllerManager getServiceControllerManager() {
        if (fServiceControllerManager == null) {
            fServiceControllerManager= fEditor.fServiceControllerManager;
        }
        return fServiceControllerManager;
    }

    protected LanguageServiceManager getLanguageServiceManager() {
        if (fLanguageServiceManager == null) {
            fLanguageServiceManager= fEditor.getLanguageServiceManager();
        }
        return fLanguageServiceManager;
    }

    protected IPreferencesService getLangSpecificPrefs() {
        if (fLangSpecificPrefs == null) {
            fLangSpecificPrefs= fEditor.getLanguageSpecificPreferences();
        }
        return fLangSpecificPrefs;
    }

    @Override
    public int getTabWidth(ISourceViewer sourceViewer) {
        return fEditor.getPrefStore().getInt(EDITOR_TAB_WIDTH);
    }

    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        // BUG Perhaps we shouldn't use a PresentationReconciler; its JavaDoc says it runs in the UI thread!
        PresentationReconciler reconciler= new PresentationReconciler();
        reconciler.setRepairer(fEditor.new PresentationRepairer(), DEFAULT_CONTENT_TYPE);
        reconciler.setDamager(fEditor.new PresentationDamager(), DEFAULT_CONTENT_TYPE);
        return reconciler;
    }

    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
        ContentAssistant ca= new ContentAssistant();
		ca.setContentAssistProcessor(processor, DEFAULT_CONTENT_TYPE);
        ca.setInformationControlCreator(getInformationControlCreator(sourceViewer));
        return ca;
    }

    public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
        IAnnotationHover hover= getLanguageServiceManager().getAnnotationHover();
        if (hover==null) hover= new DefaultAnnotationHover();
        return hover;
    }

    public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
        Set<org.eclipse.imp.services.IAutoEditStrategy> autoEdits= getLanguageServiceManager().getAutoEditStrategies();
        if (autoEdits == null || autoEdits.size() == 0) {
            return super.getAutoEditStrategies(sourceViewer, contentType);
        }
        return autoEdits.toArray(new IAutoEditStrategy[autoEdits.size()]);
    }

    public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
        LanguageServiceManager lsm= getLanguageServiceManager();
        return lsm!=null ? new DoubleClickStrategy(lsm.getParseController()) : 
        	    super.getDoubleClickStrategy(sourceViewer, contentType);
    }

    public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
    	IHyperlinkDetector[] detectors;
        if (getServiceControllerManager() != null && getServiceControllerManager().getHyperLinkController() != null)
        	detectors = new IHyperlinkDetector[] { getServiceControllerManager().getHyperLinkController() };
        else
        	detectors = super.getHyperlinkDetectors(sourceViewer);
        IHyperlinkDetector[] result = new IHyperlinkDetector[detectors.length+1];
        for (int i=0; i<detectors.length; i++) {
            result[i]=detectors[i];
        }
        result[detectors.length] = new JavaReferenceResolver(fEditor);
        return result;
    }

    /**
     * Used to present hover help (anything else?)
     */
    public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
        return new IInformationControlCreator() {
            public IInformationControl createInformationControl(Shell parent) {
                return new DefaultInformationControl(parent, "Press 'F2' for focus", 
                		new HTMLTextPresenter(true));
            }
        };
    }

    private InformationPresenter fInfoPresenter;

    public IInformationPresenter getInformationPresenter(ISourceViewer sourceViewer) {
        if (getLanguageServiceManager() == null) {
            return super.getInformationPresenter(sourceViewer);
        }
        if (fInfoPresenter == null) {
            fInfoPresenter= new InformationPresenter(getInformationControlCreator(sourceViewer));
            fInfoPresenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
            fInfoPresenter.setAnchor(ANCHOR_GLOBAL);

            IInformationProvider provider= new IInformationProvider() {
            	private IAnnotationModel fAnnotationModel= fEditor.getDocumentProvider()
            			.getAnnotationModel(fEditor.getEditorInput());

                private List<Annotation> getParserAnnotationsAtOffset(int offset) {
                    List<Annotation> result= new LinkedList<Annotation>();
                    if (fAnnotationModel != null) {
                        for(Iterator<Annotation> iter= fAnnotationModel.getAnnotationIterator(); iter.hasNext(); ) {
                            Annotation ann= iter.next();
                            if (fAnnotationModel.getPosition(ann).includes(offset) && 
                            		isParseAnnotation(ann)) {
                                result.add(ann);
                            }
                        }
                    }
                    return result;
                }

            	public IRegion getSubject(ITextViewer textViewer, int offset) {
                	List<Annotation> parserAnnsAtOffset = getParserAnnotationsAtOffset(offset);

                	if (parserAnnsAtOffset.size() > 0) {
                		Annotation theAnn= parserAnnsAtOffset.get(0);
                		Position pos= fAnnotationModel.getPosition(theAnn);
                		return new Region(pos.offset, pos.length);
                	}

                	IParseController pc= getLanguageServiceManager().getParseController();
                    ISourcePositionLocator locator= pc.getSourcePositionLocator();

                    if (locator == null) {
                        return new Region(offset, 0);
                    }
                    Object selNode= locator.findNode(pc.getCurrentAst(), offset);
                    return new Region(locator.getStartOffset(selNode), locator.getLength(selNode));
                }

                public String getInformation(ITextViewer textViewer, IRegion subject) {
                	List<Annotation> parserAnnsAtOffset = getParserAnnotationsAtOffset(subject.getOffset());

                	if (parserAnnsAtOffset.size() > 0) {
                		Annotation theAnn= parserAnnsAtOffset.get(0);
                		return theAnn.getText();
                	}

                	IParseController pc= getLanguageServiceManager().getParseController();
                    ISourcePositionLocator locator= pc.getSourcePositionLocator();

                    if (locator == null) {
                        return "";
                    }
                    IDocumentationProvider docProvider= getLanguageServiceManager().getDocProvider();
                    Object selNode= locator.findNode(pc.getCurrentAst(), subject.getOffset());
                    return (docProvider != null) ? docProvider.getDocumentation(selNode, pc) : null;
                }
            };
            fInfoPresenter.setInformationProvider(provider, IDocument.DEFAULT_CONTENT_TYPE);
            fInfoPresenter.setSizeConstraints(60, 10, true, false);
            fInfoPresenter.setRestoreInformationControlBounds(getSettings("outline_presenter_bounds"), true, true); //$NON-NLS-1$
        }
        return fInfoPresenter;
    }

    public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
        return new HoverHelpController(fEditor);
    }

    private static final CeylonOutlineBuilder builder = new CeylonOutlineBuilder();

    private class OutlineInformationProvider 
    implements IInformationProvider, IInformationProviderExtension {
    	public IRegion getSubject(ITextViewer textViewer, int offset) {
    		return new Region(offset, 0); // Could be anything, since it's ignored below in getInformation2()...
    	}
    	public String getInformation(ITextViewer textViewer, IRegion subject) {
    		// shouldn't be called, given IInformationProviderExtension???
    		throw new UnsupportedOperationException();
    	}
    	public Object getInformation2(ITextViewer textViewer, IRegion subject) {
    		return builder.buildTree(fEditor.getParseController().getRootNode());
    	}
    }

    private IInformationControlCreator getOutlinePresenterControlCreator(ISourceViewer sourceViewer, final String commandId) {
    	return new IInformationControlCreator() {
    		@Override
    		public IInformationControl createInformationControl(Shell parent) {
    			return new OutlinePopup(parent, SWT.RESIZE, SWT.V_SCROLL | SWT.H_SCROLL, commandId);
    		}
    	};
    }

    private IInformationControlCreator getHierarchyPresenterControlCreator(ISourceViewer sourceViewer, final String commandId) {
    	return new IInformationControlCreator() {
    		@Override
    		public IInformationControl createInformationControl(Shell parent) {
    			return new HierarchyPopup(parent, SWT.RESIZE, SWT.V_SCROLL | SWT.H_SCROLL, commandId);
    		}
    	};
    }

    public IInformationPresenter getOutlinePresenter(ISourceViewer sourceViewer) {
        InformationPresenter presenter = new InformationPresenter(getOutlinePresenterControlCreator(sourceViewer, SHOW_OUTLINE));
        presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
        presenter.setAnchor(ANCHOR_GLOBAL);
        presenter.setInformationProvider(new OutlineInformationProvider(), DEFAULT_CONTENT_TYPE);
        presenter.setSizeConstraints(50, 20, true, false);
        //presenter.setRestoreInformationControlBounds(getSettings("outline_presenter_bounds"), true, true);
        return presenter;
    }
    
    public IInformationPresenter getHierarchyPresenter(ISourceViewer sourceViewer, boolean b) {
        InformationPresenter presenter = new InformationPresenter(getHierarchyPresenterControlCreator(sourceViewer, 
        		"com.redhat.ceylon.eclipse.ui.action.hierarchy"));
        presenter.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
        presenter.setAnchor(ANCHOR_GLOBAL);
        presenter.setInformationProvider(new HierarchyInformationProvider(), DEFAULT_CONTENT_TYPE);
        presenter.setSizeConstraints(100, 20, true, false);
        //presenter.setRestoreInformationControlBounds(getSettings("outline_presenter_bounds"), true, true);
        return presenter;
    }

    private class HierarchyInformationProvider 
    implements IInformationProvider, IInformationProviderExtension {
    	public IRegion getSubject(ITextViewer textViewer, int offset) {
    		return new Region(offset, 0); // Could be anything, since it's ignored below in getInformation2()...
    	}
    	public String getInformation(ITextViewer textViewer, IRegion subject) {
    		// shouldn't be called, given IInformationProviderExtension???
    		throw new UnsupportedOperationException();
    	}
    	public Object getInformation2(ITextViewer textViewer, IRegion subject) {
    		return fEditor;
    	}
    }

    /**
     * Returns the settings for the given section.
     * 
     * @param sectionName
     *            the section name
     * @return the settings
     * @since 3.0
     */
    private IDialogSettings getSettings(String sectionName) {
        IDialogSettings dialogSettings = CeylonPlugin.getInstance().getDialogSettings();
		IDialogSettings settings= dialogSettings.getSection(sectionName);
        if (settings==null)
            settings = dialogSettings.addNewSection(sectionName);
        return settings;
    }

    @Override
    public IQuickAssistAssistant getQuickAssistAssistant(ISourceViewer sourceViewer) {
        return new CeylonQuickFixController(fEditor);
    }
}
