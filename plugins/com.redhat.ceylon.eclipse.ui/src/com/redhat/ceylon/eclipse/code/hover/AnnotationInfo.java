package com.redhat.ceylon.eclipse.code.hover;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.Annotation;

import com.redhat.ceylon.eclipse.code.hover.AbstractAnnotationHover.ConfigureAnnotationsAction;

/**
 * An annotation info contains information about an {@link Annotation}
 * It's used as input for the {@link AnnotationInformationControl}
 *
 * @since 3.4
 */
class AnnotationInfo {
    
	public final Annotation annotation;
	public final Position position;
	public final ITextViewer viewer;

	AnnotationInfo(Annotation annotation, Position position, ITextViewer textViewer) {
		this.annotation= annotation;
		this.position= position;
		this.viewer= textViewer;
	}

	/**
	 * Create completion proposals which can resolve the given annotation at
	 * the given position. Returns an empty array if no such proposals exist.
	 *
	 * @return the proposals or an empty array
	 */
	ICompletionProposal[] getCompletionProposals() {
		return new ICompletionProposal[0];
	}

	/**
	 * Adds actions to the given toolbar.
	 *
	 * @param manager the toolbar manager to add actions to
	 * @param infoControl the information control
	 */
	void fillToolBar(ToolBarManager manager, IInformationControl infoControl) {
		ConfigureAnnotationsAction configureAnnotationsAction= new ConfigureAnnotationsAction(annotation, infoControl);
		manager.add(configureAnnotationsAction);
	}
}