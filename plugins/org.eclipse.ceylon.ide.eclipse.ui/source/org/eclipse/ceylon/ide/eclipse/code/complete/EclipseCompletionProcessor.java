/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
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
