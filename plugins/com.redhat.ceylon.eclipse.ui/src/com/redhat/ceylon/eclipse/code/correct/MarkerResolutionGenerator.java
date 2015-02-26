package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.openInEditor;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.CEYLON_CONFIG_NOT_IN_SYNC_MARKER;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.CHARSET_PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getDocument;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getEditorInput;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.PreferenceDialog;
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
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.eclipse.code.preferences.CeylonBuildPathsPropertiesPage;
import com.redhat.ceylon.eclipse.core.builder.CeylonProjectConfig;
import com.redhat.ceylon.eclipse.ui.CeylonEncodingSynchronizer;
import com.redhat.ceylon.eclipse.ui.CeylonResources;

public class MarkerResolutionGenerator 
        implements IMarkerResolutionGenerator, IMarkerResolutionGenerator2 {

    private static class OpenBuildPathsCorrection implements IMarkerResolution {
        @Override
        public String getLabel() {
            return "Resolve the conflict in the 'Ceylon Build Paths' properties page";
        }

        @Override
        public void run(IMarker marker) {
            IProject project = (IProject) marker.getResource();
            PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(null, 
                    project, CeylonBuildPathsPropertiesPage.ID, new String[0], null);
            dialog.open();
        }

    }

    private static final IMarkerResolution[] NO_RESOLUTIONS = 
            new IMarkerResolution[0];

    private static final class CharsetCorrection 
            implements IMarkerResolution, IMarkerResolution2 {
        
        private final IProject project;
        private final String encoding;

        private CharsetCorrection(IProject project, String encoding) {
            this.project = project;
            this.encoding = encoding;
        }

        @Override
        public void run(IMarker marker) {
            CeylonEncodingSynchronizer.getInstance()
                    .updateEncoding(project, encoding);
        }

        @Override
        public String getLabel() {
            return "change project character encoding to " + 
                    encoding;
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public Image getImage() {
            return CeylonResources.MINOR_CHANGE;
        }
    }

    private static class CorrectionMarkerResolution 
            implements IMarkerResolution, IMarkerResolution2 {

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
                IEditorPart part = openInEditor(marker.getResource());
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
            if (marker.getType().equals(CHARSET_PROBLEM_MARKER_ID)) {
                IProject project = (IProject) marker.getResource();
                String encoding = project.getDefaultCharset();
                String ceylonEncoding = 
                        CeylonProjectConfig.get(project).getEncoding();
                return new IMarkerResolution[] {
                    new CharsetCorrection(project, encoding),
                    new CharsetCorrection(project, ceylonEncoding),
                };
            }

            if (marker.getType().equals(CEYLON_CONFIG_NOT_IN_SYNC_MARKER)) {
                return new IMarkerResolution[] {
                    new OpenBuildPathsCorrection()
                };
            }

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

            ArrayList<ICompletionProposal> proposals = 
                    new ArrayList<ICompletionProposal>();
            IDocument doc = 
                    getDocument(getEditorInput(marker.getResource()));
            new CeylonCorrectionProcessor(marker)
                    .collectCorrections(quickAssistContext, 
                            new ProblemLocation(marker), proposals);

            IMarkerResolution[] resolutions = 
                    new IMarkerResolution[proposals.size()];
            int i = 0;
            for (ICompletionProposal proposal: proposals) {
                resolutions[i++] = 
                        new CorrectionMarkerResolution(
                                quickAssistContext.getOffset(), 
                                quickAssistContext.getLength(), 
                                proposal, marker, doc);
            }
            return resolutions;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return NO_RESOLUTIONS;
    }

    public boolean hasResolutions(IMarker marker) {
        try {
            return CeylonCorrectionProcessor.canFix(marker) ||
                    marker.getType().equals(CHARSET_PROBLEM_MARKER_ID) ||
                    marker.getType().equals(CEYLON_CONFIG_NOT_IN_SYNC_MARKER);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}