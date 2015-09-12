package com.redhat.ceylon.eclipse.code.correct;

/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import static com.redhat.ceylon.eclipse.ui.CeylonResources.MINOR_CHANGE;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;

public class MarkerResolutionProposal implements ICompletionProposal {

    private IMarkerResolution fResolution;
    private IMarker fMarker;

    public MarkerResolutionProposal(IMarkerResolution resolution, IMarker marker) {
        fResolution = resolution;
        fMarker = marker;
    }

    public void apply(IDocument document) {
        fResolution.run(fMarker);
    }

    public String getAdditionalProposalInfo() {
        if (fResolution instanceof IMarkerResolution2) {
            return ((IMarkerResolution2) fResolution).getDescription();
        }
        if (fResolution instanceof ICompletionProposal) {
            return ((ICompletionProposal) fResolution)
                    .getAdditionalProposalInfo();
        }
        try {
            return "Problem description: " + fMarker.getAttribute(IMarker.MESSAGE);
        } catch (CoreException e) {
            // JavaPlugin.log(e);
        }
        return null;
    }

    public IContextInformation getContextInformation() {
        return null;
    }

    public String getDisplayString() {
        return fResolution.getLabel();
    }

    public Image getImage() {
        if (fResolution instanceof IMarkerResolution2) {
            return ((IMarkerResolution2) fResolution).getImage();
        }
        if (fResolution instanceof ICompletionProposal) {
            return ((ICompletionProposal) fResolution).getImage();
        }
        return MINOR_CHANGE;
    }

    public Point getSelection(IDocument document) {
        if (fResolution instanceof ICompletionProposal) {
            return ((ICompletionProposal) fResolution).getSelection(document);
        }
        return null;
    }

}
