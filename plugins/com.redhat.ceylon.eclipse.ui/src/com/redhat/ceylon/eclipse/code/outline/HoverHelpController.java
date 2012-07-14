package com.redhat.ceylon.eclipse.code.outline;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.core.ErrorHandler;
import org.eclipse.imp.editor.HoverHelper;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.ServiceFactory;
import org.eclipse.imp.parser.IModelListener;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IHoverHelper;
import org.eclipse.imp.services.base.HoverHelperBase;
import org.eclipse.imp.utils.AnnotationUtils;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.information.IInformationProviderExtension2;
import org.eclipse.jface.text.source.ISourceViewer;

public class HoverHelpController implements ITextHover, ITextHoverExtension, ITextHoverExtension2, IModelListener {
    private IParseController controller;

    private IHoverHelper hoverHelper;
    
    BestMatchHover fHover;

    public HoverHelpController(Language language) {
        hoverHelper= ServiceFactory.getInstance().getHoverHelper(language);
        if (hoverHelper == null)
        {
            hoverHelper= new HoverHelper(language);
            fHover = new BestMatchHover();
        }
        else if (hoverHelper instanceof HoverHelperBase) {
            ((HoverHelperBase) hoverHelper).setLanguage(language);
        }
    }

    public AnalysisRequired getAnalysisRequired() {
        return AnalysisRequired.NAME_ANALYSIS;
    }

    public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
        return new Region(offset, 0);
    }

    public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
        try {
            final int offset= hoverRegion.getOffset();
            String help= null;

            if (controller != null && hoverHelper != null)
                help= hoverHelper.getHoverHelpAt(controller, (ISourceViewer) textViewer, offset);
            if (help == null)
                help= AnnotationUtils.formatAnnotationList(AnnotationUtils.getAnnotationsForOffset((ISourceViewer) textViewer, offset));

            return help;
        } catch (Throwable e) {
            ErrorHandler.reportError("Hover help service implementation threw an exception", e);
        }
        return null;
    }

    /*
	 * @see org.eclipse.jface.text.ITextHoverExtension2#getHoverInfo2(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 * @since 3.4
	 */
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		if (fHover instanceof ITextHoverExtension2) {
			Object info = ((ITextHoverExtension2) fHover).getHoverInfo2(
					textViewer, hoverRegion);
			return (info == null) ? getHoverInfo(textViewer, hoverRegion)
					: info;
		} else
			return fHover.getHoverInfo(textViewer, hoverRegion);
	}
	
    public void update(IParseController controller, IProgressMonitor monitor) {
        this.controller= controller;
    }
    
    /*
	 * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
	 * @since 3.0
	 */
	public IInformationControlCreator getHoverControlCreator() {
		if (fHover instanceof ITextHoverExtension)
			return ((ITextHoverExtension)fHover).getHoverControlCreator();

		return null;
	}

	/*
	 * @see org.eclipse.jface.text.information.IInformationProviderExtension2#getInformationPresenterControlCreator()
	 */
	public IInformationControlCreator getInformationPresenterControlCreator() {
		if (fHover instanceof IInformationProviderExtension2) // this is wrong, but left here for backwards compatibility
			return ((IInformationProviderExtension2) fHover).getInformationPresenterControlCreator();

		return null;
	}
}
