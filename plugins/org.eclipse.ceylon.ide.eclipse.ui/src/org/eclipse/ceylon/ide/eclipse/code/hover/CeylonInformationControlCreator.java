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

import static org.eclipse.jdt.ui.PreferenceConstants.APPEARANCE_JAVADOC_FONT;

import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.ceylon.ide.eclipse.code.browser.BrowserInformationControl;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;

public final class CeylonInformationControlCreator 
        extends AbstractReusableInformationControlCreator {

    private final CeylonEditor editor;
    private final String statusLineMessage;

    public CeylonInformationControlCreator(CeylonEditor editor, String statusLineMessage) {
        this.editor = editor;
        this.statusLineMessage = statusLineMessage;
    }

    @Override
    public IInformationControl doCreateInformationControl(Shell parent) {
        BrowserInformationControl control = 
                new BrowserInformationControl(parent, 
                        APPEARANCE_JAVADOC_FONT, 
                        statusLineMessage) {
            /**
             * Create the "enriched" control when 
             * the hover receives focus
             */
            @Override
            public IInformationControlCreator getInformationPresenterControlCreator() {
                return new CeylonEnrichedInformationControlCreator(editor);
            }
        };
        control.addLocationListener(new CeylonLocationListener(editor, control));
        return control;
    }
}