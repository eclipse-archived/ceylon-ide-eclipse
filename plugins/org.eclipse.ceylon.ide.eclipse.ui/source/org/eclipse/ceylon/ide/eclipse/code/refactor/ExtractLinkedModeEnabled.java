/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;

import org.eclipse.jface.text.IRegion;

public interface ExtractLinkedModeEnabled {
    IRegion getTypeRegion();
    IRegion getDecRegion();
    IRegion getRefRegion();
    
    String[] getNameProposals();
}
