/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.navigator;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;


public class CeylonNavigatorAdapterFactory implements IAdapterFactory {

    private static Class<?>[] ADAPTER_LIST= new Class[] {
        IFolder.class,
        IPackageFragment.class,
        IResource.class,
        IJavaElement.class,
    };

    @Override
    public Class<?>[] getAdapterList() {
        return ADAPTER_LIST;
    }

    @Override
    public Object getAdapter(Object element, @SuppressWarnings("rawtypes") Class key) {
        SourceModuleNode sourceModule = (SourceModuleNode) element;

        IPackageFragment packageFragment = sourceModule.getMainPackageFragment();
        
        if (IJavaElement.class.equals(key)  || IPackageFragment.class.equals(key)) {
            return packageFragment;
        }

        if (IFolder.class.equals(key) || IResource.class.equals(key)) {
            if (packageFragment != null) {
                try {
                    return packageFragment.getCorrespondingResource();
                } catch (JavaModelException e) {
                }
            }
        }
        return null;
    }
}
