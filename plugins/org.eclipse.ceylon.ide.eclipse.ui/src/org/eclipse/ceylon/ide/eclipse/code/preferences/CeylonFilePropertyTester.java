/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.preferences;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonNature;

public class CeylonFilePropertyTester extends PropertyTester {

    @Override
    public boolean test(Object receiver, String property, Object[] args,
            Object expectedValue) {
        if (property.equals("module") && 
                receiver instanceof IFile) {
            IFile file = (IFile) receiver;
            if (!CeylonNature.isEnabled(file.getProject()) ||
                    !file.getName().equals("module.ceylon")) {
                return false;
            }
            IJavaElement parent = JavaCore.create(file.getParent());
            return parent instanceof IPackageFragment || 
                    parent instanceof IPackageFragmentRoot;
        }
        return false;
    }

}
