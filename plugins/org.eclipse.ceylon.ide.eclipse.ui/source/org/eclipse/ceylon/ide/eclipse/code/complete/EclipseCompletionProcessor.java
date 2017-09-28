package org.eclipse.ceylon.ide.eclipse.code.complete;

import static org.eclipse.ceylon.ide.eclipse.code.outline.CeylonLabelProvider.getDecoratedImage;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.swt.graphics.Image;

import org.eclipse.ceylon.ide.eclipse.ui.CeylonResources;

public interface EclipseCompletionProcessor extends IContentAssistProcessor {
    void sessionStarted(boolean isAutoActivated);
    
    static final Image LARGE_CORRECTION_IMAGE = 
            getDecoratedImage(CeylonResources.CEYLON_CORRECTION, 0, false);
    static ICompletionProposal[] NO_COMPLETIONS = new ICompletionProposal[0];
}
