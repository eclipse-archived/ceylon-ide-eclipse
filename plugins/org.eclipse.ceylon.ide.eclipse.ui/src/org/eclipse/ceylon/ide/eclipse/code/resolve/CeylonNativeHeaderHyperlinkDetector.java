/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.resolve;

import org.eclipse.ceylon.common.Backends;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;

public class CeylonNativeHeaderHyperlinkDetector extends
        CeylonHyperlinkDetector {

    public CeylonNativeHeaderHyperlinkDetector(CeylonEditor editor,
            CeylonParseController controller) {
        super(editor, controller);
    }

    @Override
    public Backends supportedBackends() {
        return Backends.HEADER;
    }
}
