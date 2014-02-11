package com.redhat.ceylon.eclipse.code.hover;

import java.util.ArrayList;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.TextInvocationContext;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import com.redhat.ceylon.eclipse.code.correct.CeylonCorrectionProcessor;
import com.redhat.ceylon.eclipse.code.correct.ProblemLocation;
import com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

class ProblemInfo extends AnnotationInfo {
    
    private static final ICompletionProposal[] NO_PROPOSALS = new ICompletionProposal[0];
    
    private final CeylonEditor editor;

    ProblemInfo(CeylonEditor editor, Annotation annotation, Position position,
            ITextViewer textViewer) {
        super(annotation, position, textViewer);
        this.editor = editor;
    }

    ICompletionProposal[] getCompletionProposals() {
        if (annotation instanceof CeylonAnnotation) {
            ICompletionProposal[] result = getAnnotationFixes((CeylonAnnotation) annotation);
            if (result.length > 0) {
                return result;
            }
        }
        if (annotation instanceof MarkerAnnotation) {
            return getMarkerAnnotationFixes((MarkerAnnotation) annotation);
        }
        return NO_PROPOSALS;
    }

    private ICompletionProposal[] getAnnotationFixes(CeylonAnnotation annotation) {
        final ProblemLocation location = new ProblemLocation(position.getOffset(), 
                position.getLength(), annotation);
        
        IQuickAssistInvocationContext quickAssistContext = new IQuickAssistInvocationContext() {
            public ISourceViewer getSourceViewer() {
                if (viewer instanceof ISourceViewer)
                    return (ISourceViewer) viewer;
                return null;
            }

            public int getOffset() {
                return location.getOffset();
            }

            public int getLength() {
                return location.getLength();
            }
        };
        
        ArrayList<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
        new CeylonCorrectionProcessor(editor).collectCorrections(quickAssistContext, location, proposals);
        
        return (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
    }

    private ICompletionProposal[] getMarkerAnnotationFixes(MarkerAnnotation markerAnnotation) {
        if (markerAnnotation.isQuickFixableStateSet() && 
                !markerAnnotation.isQuickFixable()) {
            return NO_PROPOSALS;
        }

        TextInvocationContext context = new TextInvocationContext(
                ((ISourceViewer) this.viewer), 
                position.getOffset(),
                position.getLength());
        return new CeylonCorrectionProcessor(markerAnnotation.getMarker())
                .computeQuickAssistProposals(context);
    }

}