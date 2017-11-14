/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.hover;

import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.part.IWorkbenchPartOrientation;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;

public class CeylonSourceHover extends SourceInfoHover {

    public CeylonSourceHover(CeylonEditor editor) {
        super(editor);
    }

    @Override
    public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
        return null;
    }

    @Override
    public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
        return hoverRegion;
    }

    @Override
    public IInformationControlCreator getHoverControlCreator() {
        return new IInformationControlCreator() {
            public IInformationControl createInformationControl(Shell parent) {
                final int orientation = 
                        editor instanceof IWorkbenchPartOrientation ?
                            ((IWorkbenchPartOrientation) editor).getOrientation() :
                            SWT.NONE;
                return new SourceViewerInformationControl(editor, 
                        parent, false, orientation, 
                        EditorsUI.getTooltipAffordanceString());
            }
        };
    }

}
