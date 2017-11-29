/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.search;

import org.eclipse.jface.viewers.IStructuredContentProvider;

interface CeylonStructuredContentProvider extends IStructuredContentProvider {
    public void elementsChanged(Object[] updatedElements);
    public void clear();
    public void setLevel(int grouping);
    public void setShowCategories(boolean showCategories);
}
