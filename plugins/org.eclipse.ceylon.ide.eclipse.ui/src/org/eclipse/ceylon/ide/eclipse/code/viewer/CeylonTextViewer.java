/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.viewer;

import static org.eclipse.ceylon.ide.eclipse.code.outline.CeylonStructureCreator.readString;

import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonSourceViewerConfiguration;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;

class CeylonTextViewer extends Viewer {

    private SourceViewer sourceViewer;
    private Object input;


    CeylonTextViewer(Composite parent) {
        sourceViewer = new SourceViewer(parent, null, SWT.LEFT_TO_RIGHT | SWT.H_SCROLL | SWT.V_SCROLL);
//        JavaTextTools tools= JavaCompareUtilities.getJavaTextTools();
//        if (tools != null) {
//            IPreferenceStore store= JavaPlugin.getDefault().getCombinedPreferenceStore();
        CeylonSourceViewerConfiguration configuration = new CeylonSourceViewerConfiguration(null);
        sourceViewer.configure(configuration);
//        }

        sourceViewer.setEditable(false);
        
        Font font = CeylonPlugin.getEditorFont();
        if (font!=null) {
            sourceViewer.getTextWidget().setFont(font);
        }
        
    }

    @Override
    public Control getControl() {
        return sourceViewer.getControl();
    }

    @Override
    public void setInput(Object input) {
        if (input instanceof IStreamContentAccessor) {
            Document document= new Document(getString(input));
//            JavaCompareUtilities.setupDocument(document);
            sourceViewer.setDocument(document);
        }
        this.input = input;
    }

    @Override
    public Object getInput() {
        return input;
    }

    @Override
    public ISelection getSelection() {
        return null;
    }

    @Override
    public void setSelection(ISelection s, boolean reveal) {}

    @Override
    public void refresh() {}

    /**
     * A helper method to retrieve the contents of the given object
     * if it implements the IStreamContentAccessor interface.
     */
    private static String getString(Object input) {
        if (input instanceof IStreamContentAccessor) {
            IStreamContentAccessor sca= (IStreamContentAccessor) input;
            try {
                return readString(sca);
            } 
            catch (CoreException ex) {
                ex.printStackTrace();;
            }
        }
        return "";
    }
}