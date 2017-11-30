/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.correctJ2C;


public class ImportProposals {
    private final static org.eclipse.ceylon.ide.common.correct.importProposals_ importProposals
        = correctJ2C().importProposals();
    
    public static org.eclipse.ceylon.ide.common.correct.importProposals_ importProposals() {
        return importProposals;
    }

}
