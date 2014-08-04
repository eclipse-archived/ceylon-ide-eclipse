package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.util.EditorUtil.getDocument;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getEditorInput;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.eclipse.code.editor.Navigation;

public class MarkerResolutionGenerator implements IMarkerResolutionGenerator,
        IMarkerResolutionGenerator2 {

    private static final IMarkerResolution[] NO_RESOLUTIONS = new IMarkerResolution[0];

    private static class CorrectionMarkerResolution implements
            IMarkerResolution, IMarkerResolution2 {

        private int fOffset;
        private int fLength;
        private ICompletionProposal fProposal;
        private final IDocument fDocument;

        public CorrectionMarkerResolution(int offset, int length, 
                ICompletionProposal proposal, IMarker marker,
                IDocument document) {
            fOffset = offset;
            fLength = length;
            fProposal = proposal;
            fDocument = document;
        }

        public String getLabel() {
            return fProposal.getDisplayString();
        }

        public void run(IMarker marker) {
            try {
                IEditorPart part = Navigation.openInEditor(marker.getResource());
                if (part instanceof ITextEditor) {
                    ((ITextEditor) part).selectAndReveal(fOffset, fLength);
                }
                if (fDocument != null) {
                    fProposal.apply(fDocument);
                }
            } catch (CoreException e) {
                // JavaPlugin.log(e);
            }
        }

        public String getDescription() {
            return fProposal.getAdditionalProposalInfo();
        }

        public Image getImage() {
            return fProposal.getImage();
        }
    }

    public IMarkerResolution[] getResolutions(final IMarker marker) {
        if (!hasResolutions(marker)) {
            return NO_RESOLUTIONS;
        }

        try {
            IQuickAssistInvocationContext quickAssistContext = 
                    new IQuickAssistInvocationContext() {
                public ISourceViewer getSourceViewer() { return null; }
                public int getOffset() {
                    return marker.getAttribute(IMarker.CHAR_START, 0);
                }
                public int getLength() {
                    return marker.getAttribute(IMarker.CHAR_END, 0) 
                            - getOffset();
                }
            };

            ArrayList<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
            IDocument doc = getDocument(getEditorInput(marker.getResource()));
            new CeylonCorrectionProcessor(marker).collectCorrections(quickAssistContext, 
                    new ProblemLocation(marker), proposals);

            IMarkerResolution[] resolutions = new IMarkerResolution[proposals.size()];
            int i = 0;
            for (ICompletionProposal proposal: proposals) {
                resolutions[i++] = new CorrectionMarkerResolution(
                        quickAssistContext.getOffset(), quickAssistContext.getLength(), 
                        proposal, marker, doc);
            }
            return resolutions;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return NO_RESOLUTIONS;
    }

    public boolean hasResolutions(IMarker marker) {
        return CeylonCorrectionProcessor.canFix(marker);
    }
}