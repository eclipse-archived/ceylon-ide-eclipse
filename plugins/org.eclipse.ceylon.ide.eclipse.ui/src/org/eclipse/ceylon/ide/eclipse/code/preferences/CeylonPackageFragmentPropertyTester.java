/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.preferences;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IPackageFragment;

import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonNature;

public class CeylonPackageFragmentPropertyTester extends PropertyTester {

    @Override
    public boolean test(Object receiver, String property, Object[] args,
            Object expectedValue) {
        if (property.equals("module") && 
            receiver instanceof IPackageFragment) {
            IPackageFragment pkg = (IPackageFragment) receiver;
            IResource resource = pkg.getResource();
            if (resource instanceof IFolder) {
                IFolder folder = (IFolder) resource;
                return CeylonNature.isEnabled(folder.getProject()) &&
                        folder.getFile("module.ceylon").exists();
            }
        }
        return false;
    }

}
