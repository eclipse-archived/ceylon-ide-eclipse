package com.redhat.ceylon.eclipse.code.hover;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;

import com.redhat.ceylon.eclipse.code.correct.CeylonCorrectionProcessor;
import com.redhat.ceylon.eclipse.code.correct.ProblemLocation;
import com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

/**
 * An annotation info contains information about an 
 * {@link Annotation} It's used as input for the 
 * {@link AnnotationInformationControl}
 *
 * @since 3.4
 */
class AnnotationInfo {
    
    private final Map<Annotation,Position> annotationPositions;
    private final ITextViewer viewer;

    private static final ICompletionProposal[] NO_PROPOSALS = 
            new ICompletionProposal[0];
    
    private final CeylonEditor editor;
    private ICompletionProposal[] proposals = null;

    AnnotationInfo(CeylonEditor editor, 
            Map<Annotation,Position> annotationPositions,
            ITextViewer textViewer) {
        this.annotationPositions = annotationPositions;
        this.viewer = textViewer;
        this.editor = editor;
    }
    
    public CeylonEditor getEditor() {
        return editor;
    }

    /**
     * Create completion proposals which can resolve the 
     * given annotation at the given position. Returns an 
     * empty array if no such proposals exist.
     *
     * @return the proposals or an empty array
     */
    ICompletionProposal[] getCompletionProposals() {
        List<ICompletionProposal> list = 
                new ArrayList<ICompletionProposal>();
        for (Map.Entry<Annotation,Position> e: 
                getAnnotationPositions().entrySet()) {
            Annotation annotation = e.getKey();
            Position position = e.getValue();
            if (annotation instanceof CeylonAnnotation) {
                CeylonAnnotation ca = 
                        (CeylonAnnotation) annotation;
                collectAnnotationFixes(ca, position, list);
            }
        }
        return list.toArray(NO_PROPOSALS);
    }

    private void collectAnnotationFixes(
            CeylonAnnotation annotation, 
            Position position, 
            List<ICompletionProposal> proposals) {
        final ProblemLocation location = 
                new ProblemLocation(
                        position.getOffset(), 
                        position.getLength(), 
                        annotation);
        
        IQuickAssistInvocationContext quickAssistContext = 
                new IQuickAssistInvocationContext() {
            public ISourceViewer getSourceViewer() {
                ITextViewer viewer = getViewer();
                if (viewer instanceof ISourceViewer) {
                    return (ISourceViewer) viewer;
                }
                return null;
            }

            public int getOffset() {
                return location.getOffset();
            }

            public int getLength() {
                return location.getLength();
            }
        };
        
        CeylonCorrectionProcessor cp = 
                new CeylonCorrectionProcessor(editor);
        cp.collectCorrections(quickAssistContext, 
                location, proposals);
        cp.collectAnnotationCorrections(annotation, 
                quickAssistContext, location, proposals);
    }
    
    Map<Annotation,Position> getAnnotationPositions() {
        return annotationPositions;
    }

    ITextViewer getViewer() {
        return viewer;
    }

    public ICompletionProposal[] getProposals() {
        return proposals;
    }

    public void setProposals(ICompletionProposal[] proposals) {
        this.proposals = proposals;
    }

}