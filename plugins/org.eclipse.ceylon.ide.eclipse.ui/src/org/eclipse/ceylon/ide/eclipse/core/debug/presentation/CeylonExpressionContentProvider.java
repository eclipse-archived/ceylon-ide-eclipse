/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.debug.presentation;

import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenCountUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IHasChildrenUpdate;
import org.eclipse.jdt.internal.debug.ui.variables.JavaExpressionContentProvider;

public class CeylonExpressionContentProvider extends JavaExpressionContentProvider {

    @Override
    public void update(IChildrenCountUpdate[] updates) {
        super.update(updates);
    }

    @Override
    public void update(IChildrenUpdate[] updates) {
        super.update(updates);
    }

    @Override
    public void update(IHasChildrenUpdate[] updates) {
        super.update(updates);
    }

}
