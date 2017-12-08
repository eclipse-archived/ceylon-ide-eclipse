/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.model;

import org.eclipse.ceylon.ide.eclipse.core.typechecker.IdePhasedUnit;
import org.eclipse.ceylon.ide.eclipse.core.typechecker.ModifiablePhasedUnit;

public abstract class ModifiableSourceFile extends SourceFile implements IResourceAware {

    public ModifiableSourceFile(IdePhasedUnit phasedUnit) {
        super(phasedUnit);
    }

    @Override
    public ModifiablePhasedUnit getPhasedUnit() {
        return (ModifiablePhasedUnit) super.getPhasedUnit();
    }
}
