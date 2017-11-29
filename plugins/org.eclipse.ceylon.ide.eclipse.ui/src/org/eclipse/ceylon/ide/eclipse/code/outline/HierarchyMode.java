/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.outline;

import org.eclipse.ceylon.ide.eclipse.ui.CeylonResources;

public enum HierarchyMode implements CeylonResources { 
    HIERARCHY, SUPERTYPES, SUBTYPES; 
    HierarchyMode next() {
        switch (this) {
        case HIERARCHY:
            return SUPERTYPES;
        case SUPERTYPES:
            return SUBTYPES;
        case SUBTYPES:
            return HIERARCHY;
        default:
            throw new RuntimeException();
        }
    }
    String image() {
        switch (this) {
        case HIERARCHY:
            return CEYLON_HIER;
        case SUPERTYPES:
            return CEYLON_SUP;
        case SUBTYPES:
            return CEYLON_SUB;
        default:
            throw new RuntimeException();
        }
    }
}