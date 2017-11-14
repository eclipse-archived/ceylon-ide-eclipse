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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.texteditor.AbstractRulerActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;

public class SelectRulerAction extends AbstractRulerActionDelegate {
    @Override
    protected IAction createAction(ITextEditor editor, IVerticalRulerInfo rulerInfo) {
        return new CeylonSelectAnnotationRulerAction(
                EditorActionMessages.ResBundle, "SelectRulerAction.", editor, rulerInfo);
    }
}
