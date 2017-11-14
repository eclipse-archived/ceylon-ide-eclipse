/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IEditingSupport;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.widgets.Shell;

public class FocusEditingSupport implements IEditingSupport {
    private final CeylonEditor editor;

    public FocusEditingSupport(CeylonEditor editor) {
        this.editor = editor;
    }

    public boolean ownsFocusShell() {
        Shell editorShell = editor.getSite().getShell();
        Shell activeShell = editorShell.getDisplay().getActiveShell();
        return editorShell == activeShell;
    }

    public boolean isOriginator(DocumentEvent event, IRegion subjectRegion) {
        return false; //leave on external modification outside positions
    }
}