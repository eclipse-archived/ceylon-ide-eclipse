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

import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.hoverJ2C;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.core.debug.hover.CeylonDebugHover;
import com.redhat.ceylon.ide.common.doc.DocGenerator;

/**
 * Caution: this implementation is a layer breaker and 
 * contains some "shortcuts"
 */
public class BestMatchHover 
        implements ITextHover, ITextHoverExtension, 
                   ITextHoverExtension2 {
    
    private CeylonEditor editor;

    private List<ITextHover> hovers;
    private ITextHover bestHover;

    public BestMatchHover(CeylonEditor editor) {
        this.editor = editor;
        installTextHovers();
    }
    
    private void installTextHovers() {
        hovers = new ArrayList<ITextHover>(2);
        hovers.add(new CeylonDebugHover(editor));
        hovers.add(new AnnotationHover(editor, false));
        //fInstantiatedTextHovers.add(new DocumentationHover(editor));
        hovers.add(hoverJ2C()
                .newEclipseDocGeneratorAsSourceInfoHover(editor));
    }
    
    @Override
    @Deprecated
    public String getHoverInfo(ITextViewer textViewer, 
            IRegion hoverRegion) {

        bestHover = null;

        if (hovers == null) {
            return null;
        }

        for (ITextHover hover: hovers) {
            if (hover instanceof CeylonDebugHover) {
                CeylonDebugHover debugHover = 
                        (CeylonDebugHover) hover;
                if (debugHover.isEnabled()) {
                    continue;
                }
            }
            String string = 
                    hover.getHoverInfo(textViewer, 
                            hoverRegion);
            if (string!=null && !string.trim().isEmpty()) {
                bestHover = hover;
                return string;
            }
        }

        return null;
    }
    
    @Override
    public Object getHoverInfo2(ITextViewer textViewer, 
            IRegion hoverRegion) {
        return getHoverInfo2(textViewer, hoverRegion, false);
    }

    public Object getHoverInfo2(ITextViewer textViewer, 
            IRegion hoverRegion, boolean forInformationProvider) {
        
        bestHover = null;

        if (hovers == null) {
            return null;
        }

        for (ITextHover hover: hovers) {
            if (hover instanceof CeylonDebugHover) {
                CeylonDebugHover debugHover = 
                        (CeylonDebugHover) hover;
                if (!debugHover.isEnabled()) {
                    continue;
                }
            }
            if (hover instanceof ITextHoverExtension2) {
                ITextHoverExtension2 hoverExt2 = 
                        (ITextHoverExtension2) hover;
                Object info = 
                        hoverExt2.getHoverInfo2(textViewer, 
                                hoverRegion);
                if (info!=null) {
                    if (!forInformationProvider || 
                            getInformationPresenterControlCreator(hover)!=null) {
                        bestHover = hover;
                        return info;
                    }
                }
            }
            else {
                @SuppressWarnings("deprecation")
                String text =
                        hover.getHoverInfo(textViewer, 
                                hoverRegion);
                if (text!=null && text.trim().length()>0) {
                    bestHover = hover;
                    return text;
                }
            }
        }

        return null;
    }

    @Override
    public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
        return SourceInfoHover.findWord(textViewer, offset);
    }
    
    @Override
    public IInformationControlCreator getHoverControlCreator() {
        if (bestHover instanceof ITextHoverExtension) {
            ITextHoverExtension hoverExt = 
                    (ITextHoverExtension) bestHover;
            return hoverExt.getHoverControlCreator();
        }
        else {
            return null;
        }
    }
    
    public IInformationControlCreator getInformationPresenterControlCreator() {
        return getInformationPresenterControlCreator(bestHover);
    }

    private static IInformationControlCreator 
    getInformationPresenterControlCreator(ITextHover hover) {
//        if (hover instanceof DocumentationHover) {
//            DocumentationHover documentationHover = 
//                    (DocumentationHover) hover;
//            return documentationHover.getInformationPresenterControlCreator();
//        }
        if (hover instanceof DocGenerator) {
            DocGenerator docGenerator =
                    (DocGenerator) hover;
            return hoverJ2C()
                    .getInformationPresenterControlCreator(
                            docGenerator);
        }
        else {
            return null;
        }
    }

}

