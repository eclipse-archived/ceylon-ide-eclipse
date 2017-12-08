/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.hover;

import org.eclipse.jface.text.IInformationControlCreator;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.java2ceylon.HoverJ2C;
import org.eclipse.ceylon.ide.common.doc.DocGenerator;

public class hoverJ2C implements HoverJ2C {

    @Override
    public DocGenerator getDocGenerator() {
        return eclipseDocGenerator_.get_();
    }
    
    @Override
    public SourceInfoHover newEclipseDocGeneratorAsSourceInfoHover(CeylonEditor editor) {
        return new EclipseDocGenerator(editor);
    }
    
    @Override
    public DocGenerator newEclipseDocGenerator(CeylonEditor editor) {
        return new EclipseDocGenerator(editor);
    }
    
    @Override
    public IInformationControlCreator getInformationPresenterControlCreator(DocGenerator docGenerator) {
        if (docGenerator instanceof EclipseDocGenerator) {
            EclipseDocGenerator eclipseDocGenerator =
                    (EclipseDocGenerator) docGenerator;
            return eclipseDocGenerator.getInformationPresenterControlCreator();
        }
        else {
            return null;
        }
    }

}
