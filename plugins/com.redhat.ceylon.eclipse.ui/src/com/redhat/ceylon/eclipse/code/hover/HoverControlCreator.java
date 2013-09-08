package com.redhat.ceylon.eclipse.code.hover;

import static com.redhat.ceylon.eclipse.code.hover.BrowserInformationControl.isAvailable;
import static org.eclipse.jdt.ui.PreferenceConstants.APPEARANCE_JAVADOC_FONT;

import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.swt.widgets.Shell;

/**
 * Hover control creator.
 */
public final class HoverControlCreator extends AbstractReusableInformationControlCreator {
	
	private final CeylonHover docHover;
	private String statusLineMessage;
	private final IInformationControlCreator enrichedControlCreator;

	public HoverControlCreator(CeylonHover docHover, 
			IInformationControlCreator enrichedControlCreator,
			String statusLineMessage) {
		this.docHover = docHover;
		this.enrichedControlCreator = enrichedControlCreator;
		this.statusLineMessage = statusLineMessage;
	}
	
	@Override
	public IInformationControl doCreateInformationControl(Shell parent) {
		if (enrichedControlCreator!=null && isAvailable(parent)) {
			BrowserInformationControl control= new BrowserInformationControl(parent, 
					APPEARANCE_JAVADOC_FONT, statusLineMessage) {
				@Override
				public IInformationControlCreator getInformationPresenterControlCreator() {
					return enrichedControlCreator;
				}
			};
			if (docHover!=null) {
				docHover.addLinkListener(control);
			}
			return control;
		} 
		else {
			return new DefaultInformationControl(parent, statusLineMessage);
		}
	}
	
}