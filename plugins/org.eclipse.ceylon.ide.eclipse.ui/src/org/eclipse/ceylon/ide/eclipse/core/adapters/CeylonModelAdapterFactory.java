/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.adapters;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jdt.core.IJavaElement;

import org.eclipse.ceylon.ide.common.model.IJavaModelAware;
import org.eclipse.ceylon.ide.common.model.IResourceAware;
import org.eclipse.ceylon.ide.common.model.IUnit;


public class CeylonModelAdapterFactory implements IAdapterFactory {

    private static Class<?>[] ADAPTER_LIST= new Class[] {
        IFile.class,
        IJavaElement.class,
    };

    public Class<?>[] getAdapterList() {
        return ADAPTER_LIST;
    }

    public Object getAdapter(Object element, 
            @SuppressWarnings("rawtypes") Class key) {
        IUnit unit = (IUnit) element;

        if (IFile.class.equals(key) 
                && unit instanceof IResourceAware) {
            IResourceAware ra = (IResourceAware) unit;
            return ra.getResourceFile();
        }
        if (IJavaElement.class.equals(key)  
                && unit instanceof IJavaModelAware) {
            IJavaModelAware ja = (IJavaModelAware) unit;
            return ja.getTypeRoot();
        }
        return null;
    }
}
