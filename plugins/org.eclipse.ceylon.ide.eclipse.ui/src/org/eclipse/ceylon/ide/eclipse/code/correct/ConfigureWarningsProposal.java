/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CONFIG_WARNING;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.dialogs.PreferencesUtil;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonWarningsPropertiesPage;

final class ConfigureWarningsProposal implements ICompletionProposal {

    private final CeylonEditor editor;

    ConfigureWarningsProposal(CeylonEditor editor) {
        this.editor = editor;
    }

    @Override
    public Point getSelection(IDocument document) {
        return null;
    }

    @Override
    public Image getImage() {
        return CONFIG_WARNING;
    }

    @Override
    public String getDisplayString() {
        return "Configure compiler warnings";
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public void apply(IDocument document) {
        PreferencesUtil.createPropertyDialogOn(
                editor.getSite().getShell(), 
                editor.getParseController()
                    .getProject(), //TODO: is this correct? 
                CeylonWarningsPropertiesPage.ID, 
                new String[] { CeylonWarningsPropertiesPage.ID }, 
                null).open();
    }
}