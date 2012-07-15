package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.CeylonEditor.isParseAnnotation;
import static org.eclipse.imp.editor.IEditorActionDefinitionIds.SHOW_OUTLINE;
import static org.eclipse.jface.text.AbstractInformationControlManager.ANCHOR_GLOBAL;
import static org.eclipse.jface.text.IDocument.DEFAULT_CONTENT_TYPE;
import static org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.IInformationProviderExtension;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.hover.CeylonAnnotationHover;
import com.redhat.ceylon.eclipse.code.hover.CeylonDocumentationProvider;
import com.redhat.ceylon.eclipse.code.hover.HoverHelpController;
import com.redhat.ceylon.eclipse.code.outline.CeylonOutlineBuilder;
import com.redhat.ceylon.eclipse.code.outline.HierarchyPopup;
import com.redhat.ceylon.eclipse.code.outline.OutlinePopup;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator;
import com.redhat.ceylon.eclipse.code.propose.CompletionProcessor;
import com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixController;
import com.redhat.ceylon.eclipse.code.resolve.CeylonHyperlinkDetector;
import com.redhat.ceylon.eclipse.code.resolve.JavaHyperlinkDetector;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonSourceViewerConfiguration extends TextSourceViewerConfiguration {
	
    protected final CeylonEditor fEditor;
    private final CompletionProcessor processor;

    public CeylonSourceViewerConfiguration(IPreferenceStore prefStore, CeylonEditor editor) {
        super(prefStore);
        fEditor = editor;
        processor = new CompletionProcessor();
    }
    
    @Override
    public int getTabWidth(ISourceViewer sourceViewer) {
        return fEditor.getPrefStore().getInt(EDITOR_TAB_WIDTH);
    }

    public PresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        // BUG Perhaps we shouldn't use a PresentationReconciler; its JavaDoc says it runs in the UI thread!
        PresentationReconciler reconciler= new PresentationReconciler();
        reconciler.setRepairer(fEditor.new PresentationRepairer(), DEFAULT_CONTENT_TYPE);
        reconciler.setDamager(fEditor.new PresentationDamager(), DEFAULT_CONTENT_TYPE);
        return reconciler;
    }

    public ContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
        ContentAssistant ca= new ContentAssistant();
		ca.setContentAssistProcessor(processor, DEFAULT_CONTENT_TYPE);
        ca.setInformationControlCreator(getInformationControlCreator(sourceViewer));
        return ca;
    }

    public CeylonAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
        return new CeylonAnnotationHover();
    }

    public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
        return new IAutoEditStrategy[] { getAutoEditStrategy() };
    }
        
    public CeylonAutoEditStrategy getAutoEditStrategy() {
        return new CeylonAutoEditStrategy();
    }

    public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
        return new DoubleClickStrategy(fEditor.getParseController()); 
    }

    public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
    	return new IHyperlinkDetector[] { new CeylonHyperlinkDetector(fEditor), 
    			new JavaHyperlinkDetector(fEditor) };
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

                	CeylonParseController pc = fEditor.getParseController();
                	CeylonSourcePositionLocator locator= pc.getSourcePositionLocator();
                    Node selNode= locator.findNode(pc.getCurrentAst(), offset);
                    return new Region(locator.getStartOffset(selNode), locator.getLength(selNode));
                }

                public String getInformation(ITextViewer textViewer, IRegion subject) {
                	List<Annotation> parserAnnsAtOffset = getParserAnnotationsAtOffset(subject.getOffset());
                	if (parserAnnsAtOffset.size() > 0) {
                		Annotation theAnn= parserAnnsAtOffset.get(0);
                		return theAnn.getText();
                	}

                	CeylonParseController pc = fEditor.getParseController();
                	Node selNode= pc.getSourcePositionLocator()
                			.findNode(pc.getCurrentAst(), subject.getOffset());
                    return new CeylonDocumentationProvider().getDocumentation(selNode, pc);
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
