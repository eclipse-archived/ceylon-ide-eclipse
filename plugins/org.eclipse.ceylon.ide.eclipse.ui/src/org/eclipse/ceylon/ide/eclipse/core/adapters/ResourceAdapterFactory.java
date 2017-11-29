/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.adapters;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;

import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder;
import org.eclipse.ceylon.ide.common.model.BaseIdeModule;
import org.eclipse.ceylon.ide.common.model.IResourceAware;
import org.eclipse.ceylon.ide.common.model.IUnit;


public class ResourceAdapterFactory implements IAdapterFactory {

    private static Class<?>[] ADAPTER_LIST= new Class[] {
        IUnit.class,
        IResourceAware.class,
        BaseIdeModule.class,
        Package.class,
    };

    public Class<?>[] getAdapterList() {
        return ADAPTER_LIST;
    }

    public Object getAdapter(Object element, @SuppressWarnings("rawtypes") Class key) {
        IResource resource = (IResource) element;

        if (IUnit.class.equals(key) && resource instanceof IFile) {
            return CeylonBuilder.getUnit((IFile) resource);
        }
        if (IResourceAware.class.equals(key) && resource instanceof IFile) {
            return CeylonBuilder.getUnit((IFile) resource);
        }
        if (Package.class.equals(key) && element instanceof IFolder) {
            return CeylonBuilder.getPackage((IFolder) element);
        }
        if (BaseIdeModule.class.equals(key) && element instanceof IFolder) {
            return CeylonBuilder.asSourceModule((IFolder) element);
        }
        return null;
    }
}
