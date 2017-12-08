/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;

public abstract class Refactoring 
        extends org.eclipse.ltk.core.refactoring.Refactoring implements org.eclipse.ceylon.ide.common.refactoring.Refactoring {
    public abstract String getName();
}
