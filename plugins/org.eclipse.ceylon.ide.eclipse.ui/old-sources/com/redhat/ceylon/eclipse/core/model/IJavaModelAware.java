/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.model;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;

import org.eclipse.ceylon.model.typechecker.model.Declaration;

public interface IJavaModelAware extends IProjectAware {
    ITypeRoot getTypeRoot();
    IJavaElement toJavaElement(Declaration ceylonDeclaration, IProgressMonitor monitor);
    IJavaElement toJavaElement(Declaration ceylonDeclaration);
}
