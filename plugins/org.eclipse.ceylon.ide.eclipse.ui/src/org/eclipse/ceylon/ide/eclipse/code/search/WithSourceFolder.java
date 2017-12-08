/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.search;

import org.eclipse.jdt.core.IPackageFragmentRoot;

class WithSourceFolder {
    
    Object element;
    IPackageFragmentRoot sourceFolder;
    WithSourceFolder(Object element, IPackageFragmentRoot sourceFolder) {
        this.element = element;
        this.sourceFolder = sourceFolder;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WithSourceFolder) {
            WithSourceFolder that = (WithSourceFolder) obj;
            if (sourceFolder==null) {
                if (that.sourceFolder!=null) {
                    return false;
                }
            }
            else {
                if (that.sourceFolder==null ||
                        !that.sourceFolder.equals(sourceFolder)) {
                    return false;
                }
            }
            return element.equals(that.element);
        }
        else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return element.hashCode();
    }
    
    @Override
    public String toString() {
        return element.toString();
    }
}