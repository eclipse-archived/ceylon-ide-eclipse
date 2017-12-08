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

import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;

public abstract class AbstractLinkedModeListener 
        implements ILinkedModeListener {
    
    private final CeylonEditor editor;
    private final Object linkedModeOwner;
    
    public AbstractLinkedModeListener(CeylonEditor editor,
            Object linkedModeOwner) {
        this.editor = editor;
        this.linkedModeOwner = linkedModeOwner;
    }
    
    @Override
    public void suspend(LinkedModeModel model) {
        editor.clearLinkedMode();
    }
    
    @Override
    public void resume(LinkedModeModel model, int flags) {
        editor.setLinkedMode(model, linkedModeOwner);
    }
}