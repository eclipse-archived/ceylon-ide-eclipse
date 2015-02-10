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
package com.redhat.ceylon.eclipse.code.hover;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.core.debug.hover.CeylonDebugHover;

/**
 * Caution: this implementation is a layer breaker and contains some "shortcuts"
 */
public class BestMatchHover 
        implements ITextHover, ITextHoverExtension, 
                   ITextHoverExtension2 {
    
    private CeylonEditor editor;

    private List<ITextHover> fInstantiatedTextHovers;
    private ITextHover fBestHover;

    public BestMatchHover(CeylonEditor editor) {
        this.editor=editor;
        installTextHovers();
    }

    /**
     * Installs all text hovers.
     */
    private void installTextHovers() {
        fInstantiatedTextHovers= new ArrayList<ITextHover>(2);
        fInstantiatedTextHovers.add(new CeylonDebugHover(editor));
        fInstantiatedTextHovers.add(new AnnotationHover(editor, false));
        fInstantiatedTextHovers.add(new DocumentationHover(editor));
    }
    
    @Override
    public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {

        fBestHover= null;

        if (fInstantiatedTextHovers == null)
            return null;

        for (ITextHover hover: fInstantiatedTextHovers) {
            if (hover instanceof CeylonDebugHover) {
                if (((CeylonDebugHover)hover).isEnabled()) {
                    continue;
                }
            }
            @SuppressWarnings("deprecation")
            String s= hover.getHoverInfo(textViewer, hoverRegion);
            if (s!=null && !s.trim().isEmpty()) {
                fBestHover= hover;
                return s;
            }
        }

        return null;
    }
    
    @Override
    public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {

        fBestHover= null;

        if (fInstantiatedTextHovers == null)
            return null;

        for (ITextHover hover: fInstantiatedTextHovers) {
            if (hover instanceof CeylonDebugHover) {
                if (! ((CeylonDebugHover)hover).isEnabled()) {
                    continue;
                }
            }
            if (hover instanceof ITextHoverExtension2) {
                Object info= ((ITextHoverExtension2) hover).getHoverInfo2(textViewer, hoverRegion);
                if (info != null) {
                    fBestHover= hover;
                    return info;
                }
            } 
            else {
                @SuppressWarnings("deprecation")
                String s= hover.getHoverInfo(textViewer, hoverRegion);
                if (s!=null && !s.isEmpty()) {
                    fBestHover= hover;
                    return s;
                }
            }
        }

        return null;
    }

    @Override
    public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
        return new Region(offset, 0);
    }
    
    @Override
    public IInformationControlCreator getHoverControlCreator() {
        if (fBestHover instanceof ITextHoverExtension)
            return ((ITextHoverExtension)fBestHover).getHoverControlCreator();
        return null;
    }

    /*public IInformationControlCreator getInformationPresenterControlCreator() {
        if (fBestHover instanceof IInformationProviderExtension2) // this is wrong, but left here for backwards compatibility
            return ((IInformationProviderExtension2)fBestHover).getInformationPresenterControlCreator();

        return null;
    }*/
}

