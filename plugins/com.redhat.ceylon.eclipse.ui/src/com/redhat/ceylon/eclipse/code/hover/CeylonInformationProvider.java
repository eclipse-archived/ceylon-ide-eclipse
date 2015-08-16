package com.redhat.ceylon.eclipse.code.hover;

import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.IInformationProviderExtension;
import org.eclipse.jface.text.information.IInformationProviderExtension2;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;


public class CeylonInformationProvider 
        implements IInformationProvider, 
                   IInformationProviderExtension, 
                   IInformationProviderExtension2 {

	protected BestMatchHover bestMatchHover;

	public CeylonInformationProvider(CeylonEditor editor) {
	    bestMatchHover = new BestMatchHover(editor);
	}

	public IRegion getSubject(ITextViewer textViewer, int offset) {
		return SourceInfoHover.findWord(textViewer, offset);
	}
	
	@Deprecated
	public String getInformation(ITextViewer textViewer, IRegion subject) {
		String text = bestMatchHover.getHoverInfo(textViewer, subject);
		if (text!=null && !text.trim().isEmpty()) {
			return text;
		}
		else {
		    return null;
		}
	}

	public Object getInformation2(ITextViewer textViewer, IRegion subject) {
		return bestMatchHover.getHoverInfo2(textViewer, subject, true);
	}

	public IInformationControlCreator getInformationPresenterControlCreator() {
		return bestMatchHover.getInformationPresenterControlCreator();
	}
}
