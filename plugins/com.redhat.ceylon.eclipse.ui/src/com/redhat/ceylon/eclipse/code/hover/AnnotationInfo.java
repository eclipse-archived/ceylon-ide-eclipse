package com.redhat.ceylon.eclipse.code.hover;

import java.util.Map;

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
    
    private final Map<Annotation,Position> annotationPositions;
    private final ITextViewer viewer;

    AnnotationInfo(Map<Annotation,Position> annotationPositions, ITextViewer textViewer) {
        this.annotationPositions = annotationPositions;
        this.viewer = textViewer;
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
        Annotation first = annotationPositions.keySet().iterator().next();
        manager.add(new ConfigureAnnotationsAction(first, infoControl));
    }

    Map<Annotation,Position> getAnnotationPositions() {
        return annotationPositions;
    }

    ITextViewer getViewer() {
        return viewer;
    }
}