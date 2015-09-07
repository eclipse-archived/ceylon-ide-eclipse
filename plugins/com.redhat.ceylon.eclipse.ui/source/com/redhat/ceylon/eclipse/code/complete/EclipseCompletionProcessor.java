package com.redhat.ceylon.eclipse.code.complete;

import org.eclipse.jface.text.contentassist.IContentAssistProcessor;

public interface EclipseCompletionProcessor extends IContentAssistProcessor {
    void sessionStarted();
}
