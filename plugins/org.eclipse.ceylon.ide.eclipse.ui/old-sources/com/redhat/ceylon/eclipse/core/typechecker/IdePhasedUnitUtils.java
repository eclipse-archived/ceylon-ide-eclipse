/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.typechecker;

import org.eclipse.ceylon.ide.eclipse.core.model.CeylonUnit;
import org.eclipse.ceylon.ide.eclipse.core.model.ProjectSourceFile;
import org.eclipse.ceylon.ide.eclipse.util.SingleSourceUnitPackage;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Unit;

public class IdePhasedUnitUtils {

    public static boolean isCentralModelDeclaration(Declaration declaration) {
        return declaration == null ||
                IdePhasedUnitUtils.isCentralModelUnit(declaration.getUnit());
    }

    public static boolean isCentralModelUnit(Unit unit) {
        return ! (unit instanceof CeylonUnit) ||
                    unit instanceof ProjectSourceFile ||
                    !(unit.getPackage() instanceof SingleSourceUnitPackage);
    }

}
