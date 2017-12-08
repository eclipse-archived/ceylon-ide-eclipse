/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.debug.hover;

import org.eclipse.debug.core.model.IVariable;

class DebugHoverInput {

    private String text;
    private IVariable variable;

    DebugHoverInput(IVariable variable, String text) {
        this.text = text;
        this.variable = variable;
    }
    
    String getText() {
        return text;
    }
    
    IVariable getVariable() {
        return variable;
    }

}
