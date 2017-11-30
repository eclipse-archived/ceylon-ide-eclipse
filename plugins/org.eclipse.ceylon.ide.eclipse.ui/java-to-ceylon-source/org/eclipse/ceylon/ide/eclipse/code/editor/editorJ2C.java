/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import org.eclipse.jface.action.Action;

import org.eclipse.ceylon.ide.eclipse.java2ceylon.EditorJ2C;
import org.eclipse.ceylon.ide.common.editor.formatAction_;

public class editorJ2C implements EditorJ2C {
    @Override
    public formatAction_ eclipseFormatAction() {
        return formatAction_.get_();
    }
    
    @Override
    public Action newEclipseTerminateStatementAction(CeylonEditor editor) {
        return new EclipseTerminateStatementAction(editor);
    }

}
