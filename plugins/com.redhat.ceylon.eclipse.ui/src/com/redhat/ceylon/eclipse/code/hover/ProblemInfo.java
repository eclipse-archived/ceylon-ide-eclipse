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
import org.eclipse.jface.text.source.TextInvocationContext;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import com.redhat.ceylon.eclipse.code.correct.CeylonCorrectionProcessor;
import com.redhat.ceylon.eclipse.code.correct.ProblemLocation;
import com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

class ProblemInfo extends AnnotationInfo {
    
    private static final ICompletionProposal[] NO_PROPOSALS = new ICompletionProposal[0];
    
    private final CeylonEditor editor;

    ProblemInfo(CeylonEditor editor, Map<Annotation,Position> annotationPositions,
            ITextViewer textViewer) {
        super(annotationPositions, textViewer);
        this.editor = editor;
    }

    ICompletionProposal[] getCompletionProposals() {
        List<ICompletionProposal> list = new ArrayList<ICompletionProposal>();
        for (Map.Entry<Annotation,Position> e: 
                getAnnotationPositions().entrySet()) {
            Annotation annotation = e.getKey();
            Position position = e.getValue();
            if (annotation instanceof CeylonAnnotation) {
                for (ICompletionProposal p: 
                    getAnnotationFixes((CeylonAnnotation) annotation, position)) {
                    list.add(p);
                }
            }
            if (annotation instanceof MarkerAnnotation) {
                for (ICompletionProposal p:
                    getMarkerAnnotationFixes((MarkerAnnotation) annotation, position)) {
                    list.add(p);
                }
            }
        }
        return list.toArray(NO_PROPOSALS);
    }

    private ICompletionProposal[] getAnnotationFixes(CeylonAnnotation annotation, Position position) {
        final ProblemLocation location = 
                new ProblemLocation(position.getOffset(), 
                        position.getLength(), annotation);
        
        IQuickAssistInvocationContext quickAssistContext = 
                new IQuickAssistInvocationContext() {
            public ISourceViewer getSourceViewer() {
                if (getViewer() instanceof ISourceViewer)
                    return (ISourceViewer) getViewer();
                return null;
            }

            public int getOffset() {
                return location.getOffset();
            }

            public int getLength() {
                return location.getLength();
            }
        };
        
        ArrayList<ICompletionProposal> proposals = 
                new ArrayList<ICompletionProposal>();
        new CeylonCorrectionProcessor(editor)
                .collectCorrections(quickAssistContext, location, proposals);
        
        return (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
    }

    private ICompletionProposal[] getMarkerAnnotationFixes(MarkerAnnotation markerAnnotation, Position position) {
        if (markerAnnotation.isQuickFixableStateSet() && 
                !markerAnnotation.isQuickFixable()) {
            return NO_PROPOSALS;
        }

        TextInvocationContext context = new TextInvocationContext(
                ((ISourceViewer) this.getViewer()), 
                position.getOffset(),
                position.getLength());
        return new CeylonCorrectionProcessor(markerAnnotation.getMarker())
                .computeQuickAssistProposals(context);
    }

}