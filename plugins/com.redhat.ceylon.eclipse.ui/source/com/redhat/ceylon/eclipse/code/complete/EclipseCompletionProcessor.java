package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getDecoratedImage;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.eclipse.ui.CeylonResources;

public interface EclipseCompletionProcessor extends IContentAssistProcessor {
    void sessionStarted(boolean isAutoActivated);
    
    static final Image LARGE_CORRECTION_IMAGE = 
            getDecoratedImage(CeylonResources.CEYLON_CORRECTION, 0, false);
    static ICompletionProposal[] NO_COMPLETIONS = new ICompletionProposal[0];
}
